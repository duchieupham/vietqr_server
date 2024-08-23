package com.vietqr.org.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vietqr.org.dto.*;
import com.vietqr.org.entity.*;
import com.vietqr.org.service.*;
import com.vietqr.org.util.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class TerminalSyncController {

    private static final Logger logger = Logger.getLogger(TerminalSyncController.class);

    @Autowired
    public MerchantSyncService merchantSyncService;

    @Autowired
    private AccountBankReceiveService accountBankReceiveService;

    @Autowired
    private AccountCustomerService accountCustomerService;

    @Autowired
    private TerminalBankService terminalBankService;

    @Autowired
    private BankReceiveConnectionService bankReceiveConnectionService;

    @Autowired
    private MerchantConnectionService merchantConnectionService;

    @Autowired
    private TerminalService terminalService;

    @Autowired
    private TerminalBankReceiveService terminalBankReceiveService;

    @Autowired
    private BankTypeService bankTypeService;

    @Autowired
    SocketHandler socketHandler;

    @Autowired
    private SystemSettingService systemSettingService;

    @PostMapping("tid/synchronize/v1")
    public ResponseEntity<Object> syncTidExternal(@RequestHeader("Authorization") String token,
                                                  @RequestBody TidSyncDTO dto) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            String username = getUsernameFromToken(token);
            if (!StringUtil.isNullOrEmpty(username)) {
                String checkExistMerchantSync = accountCustomerService.checkExistMerchantSyncByUsername(username);
                String accessKey = accountCustomerService.getAccessKeyByUsername(username);
                if (!StringUtil.isNullOrEmpty(checkExistMerchantSync)) {
                    ResponseMessageDTO validateDto = validateTidSync(accessKey, dto);
                    if ("SUCCESS".equals(validateDto.getStatus())) {
                        // check all bank is_authenticated = true;
                        List<BankAccountSyncDTO> bankDto = getUniqueBankAccountSyncDTOs(dto.getTerminals());
                        ResponseObjectDTO responseCheckCode = checkRawTerminalCodeUnique(dto.getTerminals().stream()
                                .map(TidSynchronizeDTO::getTerminalCode)
                                .collect(Collectors.toList()));
                        if ("SUCCESS".equals(responseCheckCode.getStatus()) ||
                                "CHECK".equals(responseCheckCode.getStatus())) {
                            Set<String> terminalCodeDup = new HashSet<>();
                            if ("CHECK".equals(responseCheckCode.getStatus())) {
                                terminalCodeDup = (Set<String>) responseCheckCode.getData();
                            }
                            ResponseObjectDTO validateBankAccount = validateBankAccountAuthenticated(bankDto);
                            if ("SUCCESS".equals(validateBankAccount.getStatus())) {
                                Map<BankAccountSyncDTO, AccountBankReceiveEntity> accountBankReceiveMap
                                        = (Map<BankAccountSyncDTO, AccountBankReceiveEntity>) validateBankAccount.getData();
                                Map<String, MidBankAccountSyncDTO> midBankAccountSyncDTOMap = new HashMap<>();
                                Map<String, MerchantSyncEntity> merchantSyncEntityMapByMid = new HashMap<>();
                                Map<String, MerchantSyncEntity> merchantSyncEntityMapByName = new HashMap<>();
                                boolean validateMerchant = true;
                                // validate MERCHANT
                                for (TidSynchronizeDTO item : dto.getTerminals()) {
                                    MerchantSyncEntity merchantSyncEntity = null;
                                    // false = mid, true = name
                                    boolean isNameOrMid = false;
                                    if (!StringUtil.isNullOrEmpty(item.getMid())) {
                                        merchantSyncEntity = merchantSyncEntityMapByMid.get(item.getMid());
                                    } else if (!StringUtil.isNullOrEmpty(item.getMerchantName())) {
                                        isNameOrMid = true;
                                        merchantSyncEntity = merchantSyncEntityMapByName.get(item.getMerchantName());
                                    }
                                    // Neu chua lay ra truoc do moi connect database de lay du lieu
                                    if (Objects.isNull(merchantSyncEntity)) {
                                        merchantSyncEntity = isNameOrMid ? merchantSyncService.getMerchantSyncByName(item.getMerchantName()) :
                                                merchantSyncService.getMerchantSyncByPublishId(item.getMid());
                                        if (Objects.nonNull(merchantSyncEntity)) {
                                            AccountBankReceiveEntity accountBankReceiveEntity = accountBankReceiveMap
                                                    .get(new BankAccountSyncDTO(item.getBankAccount(), item.getBankCode()));
                                            MidBankAccountSyncDTO midBankAccountSyncDTO
                                                    = new MidBankAccountSyncDTO(merchantSyncEntity.getId(), item.getBankAccount(),
                                                    item.getBankCode(), accountBankReceiveEntity.getId());
                                            midBankAccountSyncDTOMap.put(merchantSyncEntity.getId(), midBankAccountSyncDTO);
                                            merchantSyncEntityMapByMid.put(merchantSyncEntity.getPublishId(), merchantSyncEntity);
                                            merchantSyncEntityMapByName.put(merchantSyncEntity.getName(), merchantSyncEntity);
                                        } else {
                                            validateMerchant = false;
                                            break;
                                        }
                                    }
                                }

                                if (validateMerchant) {
                                    // validate bank account nếu đã tồn tại ở merchant khac
                                    ResponseMessageDTO validateBankAccountMerchant = validateBankAccountMerchant(midBankAccountSyncDTOMap);
                                    if ("SUCCESS".equals(validateBankAccountMerchant.getStatus())) {
                                        List<TidSyncResponseDTO> tidSyncResponseDTOs = new ArrayList<>();
                                        List<TerminalEntity> terminalEntities = new ArrayList<>();
                                        List<TerminalBankReceiveEntity> terminalBankReceiveEntities = new ArrayList<>();
                                        for (TidSynchronizeDTO item : dto.getTerminals()) {
                                            String terminalCode = getRandomUniqueCodeInTerminalCode();
                                            TerminalEntity terminalEntity = new TerminalEntity();
                                            UUID uuid = UUID.randomUUID();
                                            terminalEntity.setId(uuid.toString());
                                            terminalEntity.setCode(terminalCode);
                                            terminalEntity.setRawTerminalCode(item.getTerminalCode());
                                            terminalEntity.setAddress(item.getTerminalAddress());
                                            terminalEntity.setName(item.getTerminalName());
                                            terminalEntity.setDefault(false);
                                            terminalEntity.setTimeCreated(DateTimeUtil.getCurrentDateTimeUTC());
                                            MerchantSyncEntity merchantSyncEntity = !StringUtil.isNullOrEmpty(item.getMid()) ?
                                                    merchantSyncEntityMapByMid.get(item.getMid()) : merchantSyncEntityMapByName.get(item.getMerchantName());
                                            terminalEntity.setMerchantId(merchantSyncEntity.getId());
                                            String publicId = generateRandomTerPublishId();
                                            terminalEntity.setPublicId(publicId);

                                            AccountBankReceiveEntity accountBankReceiveEntity =
                                                    accountBankReceiveMap
                                                            .get(new BankAccountSyncDTO(item.getBankAccount(), item.getBankCode()));
                                            terminalEntity.setUserId(accountBankReceiveEntity.getUserId());

                                            TerminalBankReceiveEntity terminalBankReceiveEntity = new TerminalBankReceiveEntity();
                                            terminalBankReceiveEntity.setId(UUID.randomUUID().toString());
                                            terminalBankReceiveEntity.setTerminalCode("");
                                            terminalBankReceiveEntity.setRawTerminalCode("");
                                            terminalBankReceiveEntity.setTerminalId(uuid.toString());
                                            terminalBankReceiveEntity.setSubTerminalAddress("");
                                            terminalBankReceiveEntity.setTypeOfQR(0);
                                            terminalBankReceiveEntity.setBankId(accountBankReceiveEntity.getId());
                                            terminalBankReceiveEntity.setData1("");
                                            terminalBankReceiveEntity.setData2("");
                                            terminalBankReceiveEntity.setTraceTransfer("");

                                            TidSyncResponseDTO tidSyncResponseDTO = new TidSyncResponseDTO();
                                            tidSyncResponseDTO.setTid(terminalEntity.getPublicId());
                                            tidSyncResponseDTO.setTerminalName(terminalEntity.getName());
                                            tidSyncResponseDTO.setTerminalCode(terminalEntity.getRawTerminalCode());
                                            tidSyncResponseDTO.setBankAccount(item.getBankAccount());
                                            tidSyncResponseDTO.setBankCode(item.getBankCode());
                                            tidSyncResponseDTOs.add(tidSyncResponseDTO);

                                            terminalEntities.add(terminalEntity);
                                            terminalBankReceiveEntities.add(terminalBankReceiveEntity);
                                        }
                                        List<BankReceiveConnectionEntity> bankReceiveConnectionEntities = new ArrayList<>();

                                        for (Map.Entry<String, MidBankAccountSyncDTO> midBankItem :
                                                midBankAccountSyncDTOMap.entrySet()) {
                                            String mid = midBankItem.getKey();
                                            MidBankAccountSyncDTO midBankAccountSyncDTO = midBankItem.getValue();
                                            BankReceiveConnectionEntity bankReceiveConnectionEntity =
                                                    bankReceiveConnectionService
                                                            .getBankReceiveConnectionByBankIdAndMid(
                                                                    midBankAccountSyncDTO.getBankId(),
                                                                    mid);
                                            AccountBankReceiveEntity accountBankReceiveEntity = accountBankReceiveMap
                                                    .get(new BankAccountSyncDTO(midBankAccountSyncDTO.getBankAccount(), midBankAccountSyncDTO.getBankCode()));
                                            if (Objects.isNull(bankReceiveConnectionEntity)) {
                                                List<String> midConnectIds = merchantConnectionService
                                                        .getIdMerchantConnectionByMid(checkExistMerchantSync);
                                                String terminalBankId = "";
                                                if (accountBankReceiveEntity.isMmsActive()) {
                                                    TerminalBankEntity terminalBankEntity =
                                                            terminalBankService.getTerminalBankByBankAccount(accountBankReceiveEntity.getBankAccount());
                                                    if (Objects.nonNull(terminalBankEntity)) {
                                                        terminalBankId = terminalBankEntity.getId();
                                                    }
                                                }
                                                for (String midConnected : midConnectIds) {
                                                    bankReceiveConnectionEntity = new BankReceiveConnectionEntity();
                                                    bankReceiveConnectionEntity.setId(UUID.randomUUID().toString());
                                                    bankReceiveConnectionEntity.setBankId(accountBankReceiveEntity.getId());
                                                    bankReceiveConnectionEntity.setMid(mid);
                                                    bankReceiveConnectionEntity.setActive(true);
                                                    bankReceiveConnectionEntity.setData("{}");
                                                    bankReceiveConnectionEntity.setMidConnectId(midConnected);
                                                    bankReceiveConnectionEntity.setTerminalBankId(terminalBankId);
                                                    bankReceiveConnectionEntities.add(bankReceiveConnectionEntity);
                                                }
                                            }
                                        }

                                        if (!bankReceiveConnectionEntities.isEmpty()) {
                                            bankReceiveConnectionService.insertAll(bankReceiveConnectionEntities);
                                        }
                                        terminalService.insertAllTerminal(terminalEntities);
                                        terminalBankReceiveService.insertAll(terminalBankReceiveEntities);
                                        httpStatus = HttpStatus.OK;
                                        result = new ResponseObjectDTO("SUCCESS", tidSyncResponseDTOs);
                                    } else {
                                        httpStatus = HttpStatus.BAD_REQUEST;
                                        result = validateBankAccountMerchant;
                                    }
                                } else {
                                    // Khong ton tai merchant trong request
                                    logger.error("syncTidExternal: MERCHANT IS NOT EXISTED");
                                    httpStatus = HttpStatus.BAD_REQUEST;
                                    result = new ResponseMessageDTO("FAILED", "E104");
                                }
                            } else {
                                httpStatus = HttpStatus.BAD_REQUEST;
                                result = validateBankAccount;
                            }
                        } else {
                            httpStatus = HttpStatus.BAD_REQUEST;
                            result = responseCheckCode;
                        }

                    } else {
                        // Invalid Validated
                        httpStatus = HttpStatus.BAD_REQUEST;
                        result = validateDto;
                    }
                } else {
                    logger.error("syncTidExternal: MERCHANT IS NOT EXISTED");
                    result = new ResponseMessageDTO("FAILED", "E104");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                logger.error("syncTidExternal: INVALID TOKEN");
                result = new ResponseMessageDTO("FAILED", "E74");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("syncTidExternal: ERROR: " + e.getMessage() + " at: " + System.currentTimeMillis());
            httpStatus = HttpStatus.BAD_REQUEST;
            result = new ResponseMessageDTO("FAILED", "E05");
        }

        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("tid/synchronize/v2")
    public ResponseEntity<Object> syncTidExternalV2(@RequestHeader("Authorization") String token,
                                                    @RequestBody TidSyncV2DTO dto) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            String username = getUsernameFromToken(token);
            if (!StringUtil.isNullOrEmpty(username)) {
                String checkExistMerchantSync = accountCustomerService.checkExistMerchantSyncByUsernameV2(username);
                String accessKey = accountCustomerService.getAccessKeyByUsername(username);
                if (!StringUtil.isNullOrEmpty(checkExistMerchantSync)) {
                    ResponseMessageDTO validateDto = validateTidSyncV2(accessKey, dto);
                    if ("SUCCESS".equals(validateDto.getStatus())) {
                        // check all bank is_authenticated = true;
                        List<BankAccountSyncDTO> bankDto = getUniqueBankAccountSyncV2DTOs(dto.getTerminals());
                        ResponseObjectDTO responseCheckCode = checkRawTerminalCodeUnique(dto.getTerminals().stream()
                                .map(TidSynchronizeV2DTO::getTerminalCode)
                                .collect(Collectors.toList()));
                        if ("SUCCESS".equals(responseCheckCode.getStatus()) ||
                                "CHECK".equals(responseCheckCode.getStatus())) {
                            Set<String> terminalCodeDup = new HashSet<>();
                            if ("CHECK".equals(responseCheckCode.getStatus())) {
                                terminalCodeDup = (Set<String>) responseCheckCode.getData();
                            }
                            ResponseObjectDTO validateBankAccount = validateBankAccountAuthenticated(bankDto);
                            if ("SUCCESS".equals(validateBankAccount.getStatus())) {
                                Map<BankAccountSyncDTO, AccountBankReceiveEntity> accountBankReceiveMap
                                        = (Map<BankAccountSyncDTO, AccountBankReceiveEntity>) validateBankAccount.getData();
                                Map<String, MidBankAccountSyncDTO> midBankAccountSyncDTOMap = new HashMap<>();
                                Map<String, MerchantSyncEntity> merchantSyncEntityMapByMid = new HashMap<>();
                                Map<String, MerchantSyncEntity> merchantSyncEntityMapByName = new HashMap<>();
                                boolean validateMerchant = true;
                                // validate MERCHANT
                                for (TidSynchronizeV2DTO item : dto.getTerminals()) {
                                    MerchantSyncEntity merchantSyncEntity = null;
                                    // false = mid, true = name
                                    boolean isNameOrMid = false;
                                    if (!StringUtil.isNullOrEmpty(item.getMid())) {
                                        merchantSyncEntity = merchantSyncEntityMapByMid.get(item.getMid());
                                    } else if (!StringUtil.isNullOrEmpty(item.getMerchantName())) {
                                        isNameOrMid = true;
                                        merchantSyncEntity = merchantSyncEntityMapByName.get(item.getMerchantName());
                                    }
                                    // Neu chua lay ra truoc do moi connect database de lay du lieu
                                    if (Objects.isNull(merchantSyncEntity)) {
                                        merchantSyncEntity = isNameOrMid ? merchantSyncService.getMerchantSyncByName(item.getMerchantName()) :
                                                merchantSyncService.getMerchantSyncByPublishId(item.getMid());
                                        if (Objects.nonNull(merchantSyncEntity)) {
                                            AccountBankReceiveEntity accountBankReceiveEntity = accountBankReceiveMap
                                                    .get(new BankAccountSyncDTO(item.getBankAccount(), item.getBankCode()));
                                            MidBankAccountSyncDTO midBankAccountSyncDTO
                                                    = new MidBankAccountSyncDTO(merchantSyncEntity.getId(), item.getBankAccount(),
                                                    item.getBankCode(), accountBankReceiveEntity.getId());
                                            midBankAccountSyncDTOMap.put(merchantSyncEntity.getId(), midBankAccountSyncDTO);
                                            merchantSyncEntityMapByMid.put(merchantSyncEntity.getPublishId(), merchantSyncEntity);
                                            merchantSyncEntityMapByName.put(merchantSyncEntity.getName(), merchantSyncEntity);
                                        } else {
                                            validateMerchant = false;
                                            break;
                                        }
                                    }
                                }

                                if (validateMerchant) {
                                    // validate bank account nếu đã tồn tại ở merchant khac
                                    ResponseMessageDTO validateBankAccountMerchant = validateBankAccountMerchant(midBankAccountSyncDTOMap);
                                    if ("SUCCESS".equals(validateBankAccountMerchant.getStatus())) {
                                        List<TidSyncResponseV2DTO> tidSyncResponseDTOs = new ArrayList<>();
                                        List<TerminalEntity> terminalEntities = new ArrayList<>();
                                        List<TerminalBankReceiveEntity> terminalBankReceiveEntities = new ArrayList<>();
                                        for (TidSynchronizeV2DTO item : dto.getTerminals()) {
                                            String terminalCode = getRandomUniqueCodeInTerminalCode();
                                            TerminalEntity terminalEntity = new TerminalEntity();
                                            UUID uuid = UUID.randomUUID();
                                            terminalEntity.setId(uuid.toString());
                                            terminalEntity.setCode(terminalCode);
                                            terminalEntity.setRawTerminalCode(item.getTerminalCode());
                                            terminalEntity.setAddress(item.getTerminalAddress());
                                            terminalEntity.setName(item.getTerminalName());
                                            terminalEntity.setDefault(false);
                                            terminalEntity.setTimeCreated(DateTimeUtil.getCurrentDateTimeUTC());
                                            MerchantSyncEntity merchantSyncEntity = !StringUtil.isNullOrEmpty(item.getMid()) ?
                                                    merchantSyncEntityMapByMid.get(item.getMid()) : merchantSyncEntityMapByName.get(item.getMerchantName());
                                            terminalEntity.setMerchantId(merchantSyncEntity.getId());
                                            String publicId = generateRandomTerPublishId();
                                            terminalEntity.setPublicId(publicId);

                                            AccountBankReceiveEntity accountBankReceiveEntity =
                                                    accountBankReceiveMap
                                                            .get(new BankAccountSyncDTO(item.getBankAccount(), item.getBankCode()));
                                            terminalEntity.setUserId(accountBankReceiveEntity.getUserId());

                                            TerminalBankReceiveEntity terminalBankReceiveEntity = new TerminalBankReceiveEntity();
                                            terminalBankReceiveEntity.setId(UUID.randomUUID().toString());
                                            terminalBankReceiveEntity.setTerminalCode("");
                                            terminalBankReceiveEntity.setRawTerminalCode("");
                                            terminalBankReceiveEntity.setTerminalId(uuid.toString());
                                            terminalBankReceiveEntity.setSubTerminalAddress("");
                                            terminalBankReceiveEntity.setTypeOfQR(0);
                                            terminalBankReceiveEntity.setBankId(accountBankReceiveEntity.getId());
                                            terminalBankReceiveEntity.setData1("");
                                            terminalBankReceiveEntity.setData2("");
                                            terminalBankReceiveEntity.setTraceTransfer("");

                                            TidSyncResponseV2DTO tidSyncResponseDTO = new TidSyncResponseV2DTO();
                                            tidSyncResponseDTO.setTid(terminalEntity.getPublicId());
                                            tidSyncResponseDTO.setTerminalName(terminalEntity.getName());
                                            tidSyncResponseDTO.setTerminalCode(terminalEntity.getRawTerminalCode());
                                            tidSyncResponseDTO.setBankAccount(item.getBankAccount());
                                            tidSyncResponseDTO.setBankCode(item.getBankCode());
                                            tidSyncResponseDTOs.add(tidSyncResponseDTO);

                                            terminalEntities.add(terminalEntity);
                                            terminalBankReceiveEntities.add(terminalBankReceiveEntity);

                                            // push webSocket
                                            Map<String, String> data = new HashMap<>();
                                            data.put("notificationType", NotificationUtil.getNotiSyncTidV2());
                                            data.put("merchantId", item.getMid());
                                            data.put("merchantName", item.getMerchantName());
                                            data.put("terminalName", item.getTerminalName());
                                            data.put("terminalCode", item.getTerminalCode());
                                            data.put("terminalAddress", item.getTerminalAddress());
                                            data.put("bankCode", item.getBankCode());
                                            data.put("bankAccount", item.getBankAccount());
                                            data.put("checkSum", item.getCheckSum());
                                            socketHandler.sendMessageToClientId(merchantSyncEntity.getClientId(), data);
                                        }
                                        List<BankReceiveConnectionEntity> bankReceiveConnectionEntities = new ArrayList<>();

                                        for (Map.Entry<String, MidBankAccountSyncDTO> midBankItem :
                                                midBankAccountSyncDTOMap.entrySet()) {
                                            String mid = midBankItem.getKey();
                                            MidBankAccountSyncDTO midBankAccountSyncDTO = midBankItem.getValue();
                                            BankReceiveConnectionEntity bankReceiveConnectionEntity =
                                                    bankReceiveConnectionService
                                                            .getBankReceiveConnectionByBankIdAndMid(
                                                                    midBankAccountSyncDTO.getBankId(),
                                                                    mid);
                                            AccountBankReceiveEntity accountBankReceiveEntity = accountBankReceiveMap
                                                    .get(new BankAccountSyncDTO(midBankAccountSyncDTO.getBankAccount(), midBankAccountSyncDTO.getBankCode()));
                                            if (Objects.isNull(bankReceiveConnectionEntity)) {
                                                List<String> midConnectIds = merchantConnectionService
                                                        .getIdMerchantConnectionByMid(checkExistMerchantSync);
                                                String terminalBankId = "";
                                                if (accountBankReceiveEntity.isMmsActive()) {
                                                    TerminalBankEntity terminalBankEntity =
                                                            terminalBankService.getTerminalBankByBankAccount(accountBankReceiveEntity.getBankAccount());
                                                    if (Objects.nonNull(terminalBankEntity)) {
                                                        terminalBankId = terminalBankEntity.getId();
                                                    }
                                                }
                                                for (String midConnected : midConnectIds) {
                                                    bankReceiveConnectionEntity = new BankReceiveConnectionEntity();
                                                    bankReceiveConnectionEntity.setId(UUID.randomUUID().toString());
                                                    bankReceiveConnectionEntity.setBankId(accountBankReceiveEntity.getId());
                                                    bankReceiveConnectionEntity.setMid(mid);
                                                    bankReceiveConnectionEntity.setActive(true);
                                                    bankReceiveConnectionEntity.setData("{}");
                                                    bankReceiveConnectionEntity.setMidConnectId(midConnected);
                                                    bankReceiveConnectionEntity.setTerminalBankId(terminalBankId);
                                                    bankReceiveConnectionEntities.add(bankReceiveConnectionEntity);
                                                }
                                            }
                                        }

                                        if (!bankReceiveConnectionEntities.isEmpty()) {
                                            bankReceiveConnectionService.insertAll(bankReceiveConnectionEntities);
                                        }
                                        terminalService.insertAllTerminal(terminalEntities);
                                        terminalBankReceiveService.insertAll(terminalBankReceiveEntities);

                                        httpStatus = HttpStatus.OK;
                                        result = new ResponseObjectDTO("SUCCESS", tidSyncResponseDTOs);
                                    } else {
                                        httpStatus = HttpStatus.BAD_REQUEST;
                                        result = validateBankAccountMerchant;
                                    }
                                } else {
                                    // Khong ton tai merchant trong request
                                    logger.error("syncTidExternal: MERCHANT IS NOT EXISTED");
                                    httpStatus = HttpStatus.BAD_REQUEST;
                                    result = new ResponseMessageDTO("FAILED", "E104");
                                }
                            } else {
                                httpStatus = HttpStatus.BAD_REQUEST;
                                result = validateBankAccount;
                            }
                        } else {
                            httpStatus = HttpStatus.BAD_REQUEST;
                            result = responseCheckCode;
                        }

                    } else {
                        // Invalid Validated
                        httpStatus = HttpStatus.BAD_REQUEST;
                        result = validateDto;
                    }
                } else {
                    logger.error("syncTidExternal: MERCHANT IS NOT EXISTED");
                    result = new ResponseMessageDTO("FAILED", "E104");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                logger.error("syncTidExternal: INVALID TOKEN");
                result = new ResponseMessageDTO("FAILED", "E74");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("syncTidExternal: ERROR: " + e.getMessage() + " at: " + System.currentTimeMillis());
            httpStatus = HttpStatus.BAD_REQUEST;
            result = new ResponseMessageDTO("FAILED", "E05");
        }

        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("tid/list-tid")
    public ResponseEntity<Object> getListTid(@RequestHeader("Authorization") String token,
                                             @RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "20") int size,
                                             @RequestParam String mid) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            String username = getUsernameFromToken(token);
            String midForSearch = "";
            if (!StringUtil.isNullOrEmpty(username)) {
                String checkExistMerchantSync = accountCustomerService.checkExistMerchantSyncByUsername(username);
                if (!StringUtil.isNullOrEmpty(checkExistMerchantSync)) {
                    midForSearch = merchantSyncService.getIdByPublicId(mid);
                    int offset = (page - 1) * size;
                    int totalElement = 0;
                    PageResDTO response = new PageResDTO();
                    totalElement = terminalService.countTerminalByMidSync(midForSearch);
                    List<ITerminalSyncDTO> iTerminalSyncDTOs = terminalService
                            .getTerminalByMidSync(midForSearch, offset, size);
                    PageDTO pageDTO = new PageDTO();
                    pageDTO.setPage(page);
                    pageDTO.setSize(size);
                    pageDTO.setTotalPage(StringUtil.getTotalPage(totalElement, size));
                    pageDTO.setTotalElement(totalElement);
                    response.setMetadata(pageDTO);

                    response.setData(iTerminalSyncDTOs);
                    result = response;
                    httpStatus = HttpStatus.OK;
                } else {
                    logger.error("getListTid: MERCHANT IS NOT EXISTED");
                    result = new ResponseMessageDTO("FAILED", "E104");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                logger.error("getListTid: INVALID TOKEN");
                result = new ResponseMessageDTO("FAILED", "E74");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            httpStatus = HttpStatus.BAD_REQUEST;
            result = new ResponseMessageDTO("FAILED", "E05");
        }

        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("mid/synchronize/v1")
    public ResponseEntity<Object> syncMidExternal(@RequestHeader("Authorization") String token,
                                                  @RequestBody MidSyncDTO dto) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            String username = getUsernameFromToken(token);
            if (!StringUtil.isNullOrEmpty(username)) {
                String checkExistMerchantSync = accountCustomerService.checkExistMerchantSyncByUsername(username);
                String accessKey = accountCustomerService.getAccessKeyByUsername(username);
                if (!StringUtil.isNullOrEmpty(checkExistMerchantSync)) {
                    MerchantSyncEntity merchantSyncAdmin = merchantSyncService.getMerchantSyncById(checkExistMerchantSync);
                    if (Objects.nonNull(merchantSyncAdmin) && merchantSyncAdmin.getIsMaster()) {
                        ResponseMessageDTO validateMidSync = validateMidSync(accessKey, dto.getMerchants());
                        if ("SUCCESS".equals(validateMidSync.getStatus())) {
                            List<MidSyncResponseDTO> midSyncResponseDTOs = new ArrayList<>();
                            List<MerchantSyncEntity> merchantSyncEntities = new ArrayList<>();
                            for (MidSynchronizeDTO item : dto.getMerchants()) {
                                String publishId = generateRandomMerPublishId();
                                String merchantName = item.getMerchantName().toUpperCase();
                                MerchantSyncEntity merchantSyncEntity = new MerchantSyncEntity();
                                merchantSyncEntity.setId(UUID.randomUUID().toString());
                                merchantSyncEntity.setName(merchantName);
                                merchantSyncEntity.setFullName(item.getMerchantFullName());
                                merchantSyncEntity.setVso("");
                                merchantSyncEntity.setBusinessType("");
                                merchantSyncEntity.setCareer("");
                                merchantSyncEntity.setAddress(item.getMerchantAddress());
                                merchantSyncEntity.setNationalId(item.getMerchantIdentity());
                                merchantSyncEntity.setIsActive(true);
                                merchantSyncEntity.setUserId("");
                                merchantSyncEntity.setAccountCustomerId("");
                                merchantSyncEntity.setEmail(item.getContactEmail());
                                merchantSyncEntity.setPhoneNo(item.getContactPhone());
                                merchantSyncEntity.setPublishId(publishId);
                                merchantSyncEntity.setRefId(checkExistMerchantSync);

                                MidSyncResponseDTO midSyncResponseDTO = new MidSyncResponseDTO();
                                midSyncResponseDTO.setMid(publishId);
                                midSyncResponseDTO.setMerchantName(merchantName);

                                midSyncResponseDTOs.add(midSyncResponseDTO);
                                merchantSyncEntities.add(merchantSyncEntity);
                            }

                            merchantSyncService.insertAll(merchantSyncEntities);
                            result = new ResponseObjectDTO("SUCCESS", midSyncResponseDTOs);
                            httpStatus = HttpStatus.OK;
                        } else {
                            result = validateMidSync;
                            httpStatus = HttpStatus.BAD_REQUEST;
                        }
                    } else {
                        result = new ResponseMessageDTO("FAILED", "E150");
                        httpStatus = HttpStatus.BAD_REQUEST;
                    }
                } else {
                    logger.error("getListTid: MERCHANT IS NOT EXISTED");
                    result = new ResponseMessageDTO("FAILED", "E104");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                logger.error("getListTid: INVALID TOKEN");
                result = new ResponseMessageDTO("FAILED", "E74");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            httpStatus = HttpStatus.BAD_REQUEST;
            result = new ResponseMessageDTO("FAILED", "E05");
        }

        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("mid/synchronize/v2") // sửa lại update data merchant-syn theo merchantId lúc tạo certificate
    public ResponseEntity<Object> syncMidExternalV2(@RequestHeader("Authorization") String token,
                                                    @RequestBody MidSyncV2DTO dto) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            String username = getUsernameFromToken(token);

            if (!StringUtil.isNullOrEmpty(username)) {
                String checkExistMerchantSync = accountCustomerService.checkExistMerchantSyncByUsername(username);
                String accessKey = accountCustomerService.getAccessKeyByUsername(username);
                if (!StringUtil.isNullOrEmpty(checkExistMerchantSync)) {
                    MerchantSyncEntity merchantSyncAdmin = merchantSyncService.getMerchantSyncById(checkExistMerchantSync);
                    if (Objects.nonNull(merchantSyncAdmin) && merchantSyncAdmin.getIsMaster()) {
                        ResponseMessageDTO validateMidSync = validateMidSyncV2(accessKey, dto.getMerchants());
                        if ("SUCCESS".equals(validateMidSync.getStatus())) {
                            List<MerchantSyncEntity> merchantSyncEntities = new ArrayList<>();
                            ResponseMerchantV2DTO responseMerchantV2DTO = new ResponseMerchantV2DTO();
                            for (MidSynchronizeV2DTO item : dto.getMerchants()) {
                                String publishId = generateRandomMerPublishId();
                                String merchantName = item.getMerchantName().toUpperCase();
                                String merchantIdentity = item.getMerchantIdentity().toUpperCase();
                                String certificate = EnvironmentUtil.getVietQrMerchantPrefix()
                                        + MerchantRefUtil.encryptMerchantId(publishId + merchantIdentity);
                                // update lại data trong merchant-sync theo merchantId truyền vào
                                MerchantSyncEntity merchantSyncEntity =
                                        merchantSyncService.getMerchantSyncByPublishId(item.getMerchantId());
                                if (merchantSyncEntity != null) {
                                    // update data merchant-sync
                                    merchantSyncService.updateMerchantV2(item.getMerchantId(), "", "", item.getCareer(),
                                            "", merchantName, "", "", item.getContactEmail(),
                                            checkExistMerchantSync, item.getMerchantFullName(), item.getContactPhone());

                                    // trả response
                                    String mid = checkExistMerchantSync;
                                    List<IMerchantSyncPublicDTO> iTerminalSyncDTOs = merchantSyncService
                                            .getMerchantByMidSyncV2(mid);
                                    for (IMerchantSyncPublicDTO iMerchantSyncPublicDTO : iTerminalSyncDTOs) {
                                        if (iMerchantSyncPublicDTO.getIsMaster() == true) {
//                                      set data master merchant and merchant
                                            MasterDataDTO masterData = new MasterDataDTO();
                                            masterData.setMid(StringUtil.getValueNullChecker(iMerchantSyncPublicDTO.getMid()));
                                            masterData.setMerchantName(StringUtil.getValueNullChecker(iMerchantSyncPublicDTO.getMerchantName()));
                                            masterData.setMerchantFullName(StringUtil.getValueNullChecker(iMerchantSyncPublicDTO.getMerchantFullName()));
                                            masterData.setMerchantAddress(StringUtil.getValueNullChecker(iMerchantSyncPublicDTO.getMerchantAddress()));
                                            masterData.setContactEmail(StringUtil.getValueNullChecker(iMerchantSyncPublicDTO.getContactEmail()));
                                            masterData.setContactPhone(StringUtil.getValueNullChecker(iMerchantSyncPublicDTO.getContactPhone()));
                                            masterData.setMerchantIdentify(StringUtil.getValueNullChecker(iMerchantSyncPublicDTO.getMerchantIdentify()));
                                            masterData.setCertificate(StringUtil.getValueNullChecker(iMerchantSyncPublicDTO.getCertificate()));
                                            masterData.setWebhook(StringUtil.getValueNullChecker(iMerchantSyncPublicDTO.getWebhook()));
                                            masterData.setClientId(StringUtil.getValueNullChecker(iMerchantSyncPublicDTO.getClientId()));
                                            responseMerchantV2DTO.setMasterData(masterData);
                                        }
                                    }
                                    // set merchants data
                                    List<MidSynchronizeV2DTO> merchantDataDTOS = new ArrayList<>();
                                    for (MidSynchronizeV2DTO merchantDataDTO : dto.getMerchants()) {
                                        merchantDataDTO.setMerchantFullName(item.getMerchantFullName());
                                        merchantDataDTO.setMerchantName(merchantName);
                                        merchantDataDTO.setMerchantAddress(item.getMerchantAddress());
                                        merchantDataDTO.setMerchantIdentity(item.getMerchantIdentity());
                                        merchantDataDTO.setContactPhone(item.getContactPhone());
                                        merchantDataDTO.setContactEmail(item.getContactEmail());
                                        merchantDataDTO.setCertificate(certificate);
                                        merchantDataDTO.setWebhook(item.getWebhook());
                                        merchantDataDTO.setClientId(merchantSyncEntity.getClientId());
                                        merchantDataDTOS.add(merchantDataDTO);
                                    }

                                    // push webSocket
                                    Map<String, String> data = new HashMap<>();
                                    data.put("notificationType", NotificationUtil.getNotiSyncMidV2());
                                    data.put("merchantFullName", item.getMerchantFullName());
                                    data.put("merchantName", item.getMerchantName());
                                    data.put("merchantAddress", item.getMerchantAddress());
                                    data.put("merchantIdentity", item.getMerchantIdentity());
                                    data.put("contactEmail", item.getContactEmail());
                                    data.put("contactPhone", item.getContactPhone());
                                    data.put("merchantId", item.getMerchantId());
                                    data.put("career", item.getCareer());
                                    data.put("webhook", item.getWebhook());
                                    data.put("certificate", item.getCertificate());
                                    socketHandler.sendMessageToClientId(merchantSyncEntity.getClientId(), data);

                                    responseMerchantV2DTO.setData(merchantDataDTOS);
                                    merchantSyncEntities.add(merchantSyncEntity);

                                    responseMerchantV2DTO.setStatus("SUCCESS");
                                    result = responseMerchantV2DTO;
                                    httpStatus = HttpStatus.OK;
                                } else {
                                    result = new ResponseMessageDTO("FAILED", "E161");
                                    httpStatus = HttpStatus.BAD_REQUEST;
                                    break;
                                }
                            }

//                            merchantSyncService.insertAll(merchantSyncEntities);
//                            responseMerchantV2DTO.setStatus("SUCCESS");
//                            result = responseMerchantV2DTO;
//                            httpStatus = HttpStatus.OK;
                        } else {
                            result = validateMidSync;
                            httpStatus = HttpStatus.BAD_REQUEST;
                        }
                    } else {
                        result = new ResponseMessageDTO("FAILED", "E150");
                        httpStatus = HttpStatus.BAD_REQUEST;
                    }
                } else {
                    logger.error("getListTid: MERCHANT IS NOT EXISTED");
                    result = new ResponseMessageDTO("FAILED", "E104");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                logger.error("getListTid: INVALID TOKEN");
                result = new ResponseMessageDTO("FAILED", "E74");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            httpStatus = HttpStatus.BAD_REQUEST;
            result = new ResponseMessageDTO("FAILED", "E05");
        }

        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("mid/list-mid/v2")
    public ResponseEntity<Object> getMidsExternalV2(@RequestHeader("Authorization") String token,
                                                    @RequestParam(defaultValue = "1") int page,
                                                    @RequestParam(defaultValue = "20") int size) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            String username = getUsernameFromToken(token);
            String mid = "";
            if (!StringUtil.isNullOrEmpty(username)) {
                String checkExistMerchantSync = accountCustomerService.checkExistMerchantSyncByUsername(username);
                if (!StringUtil.isNullOrEmpty(checkExistMerchantSync)) {
                    mid = checkExistMerchantSync;
                    int offset = (page - 1) * size;
                    int totalElement = 0;
                    totalElement = merchantSyncService.countMerchantByMidSync(mid);
                    List<IMerchantSyncPublicDTO> iTerminalSyncDTOs = merchantSyncService
                            .getMerchantByMidSync(mid, offset, size);

                    PageMerchantDTO pageMerchantDTO = new PageMerchantDTO();
                    PageDTO pageDTO = new PageDTO();
                    pageDTO.setPage(page);
                    pageDTO.setSize(size);
                    pageDTO.setTotalPage(StringUtil.getTotalPage(totalElement, size));
                    pageDTO.setTotalElement(totalElement);
                    pageMerchantDTO.setMetadata(pageDTO);

                    for (IMerchantSyncPublicDTO iMerchantSyncPublicDTO : iTerminalSyncDTOs) {
                        if (iMerchantSyncPublicDTO.getIsMaster() == true) {
//                         set data master merchant and merchant
                            MasterDataDTO masterData = new MasterDataDTO();
                            masterData.setMid(StringUtil.getValueNullChecker(iMerchantSyncPublicDTO.getMid()));
                            masterData.setMerchantName(StringUtil.getValueNullChecker(iMerchantSyncPublicDTO.getMerchantName()));
                            masterData.setMerchantFullName(StringUtil.getValueNullChecker(iMerchantSyncPublicDTO.getMerchantFullName()));
                            masterData.setMerchantAddress(StringUtil.getValueNullChecker(iMerchantSyncPublicDTO.getMerchantAddress()));
                            masterData.setContactEmail(StringUtil.getValueNullChecker(iMerchantSyncPublicDTO.getContactEmail()));
                            masterData.setContactPhone(StringUtil.getValueNullChecker(iMerchantSyncPublicDTO.getContactPhone()));
                            masterData.setMerchantIdentify(StringUtil.getValueNullChecker(iMerchantSyncPublicDTO.getMerchantIdentify()));
                            masterData.setCertificate(StringUtil.getValueNullChecker(iMerchantSyncPublicDTO.getCertificate()));
                            masterData.setWebhook(StringUtil.getValueNullChecker(iMerchantSyncPublicDTO.getWebhook()));
                            masterData.setClientId(StringUtil.getValueNullChecker(iMerchantSyncPublicDTO.getClientId()));
                            pageMerchantDTO.setMasterData(masterData);
                        }
                    }

                    pageMerchantDTO.setData(iTerminalSyncDTOs);
                    result = pageMerchantDTO;
                    httpStatus = HttpStatus.OK;
                } else {
                    logger.error("getMidsExternal: MERCHANT IS NOT EXISTED");
                    result = new ResponseMessageDTO("FAILED", "E104");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                logger.error("getMidsExternal: INVALID TOKEN");
                result = new ResponseMessageDTO("FAILED", "E74");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            httpStatus = HttpStatus.BAD_REQUEST;
            result = new ResponseMessageDTO("FAILED", "E05");
        }

        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("mid/certificate/v2")
    public ResponseEntity<Object> getQrCertificate(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody QrCertificateDTO dto
    ) {
        Object result = null;
        HttpStatus httpStatus = null;
        QrCertificateResponseDTO response = new QrCertificateResponseDTO();
        try {

            String publishId = generateRandomMerPublishId(); // trả về cái này
            // check user từ token
            // nếu là master merchant thì sẽ tạo Certificate syncMID là MCS
            // nếu là merchant thì sẽ tạo Certificate syncTID Prefix sẽ là MCT
            String username = getUsernameFromToken(token);
            String certificate = "";
            boolean checkIsMasterMerchant = merchantSyncService.getMerchantSyncByUsername(username);
            if (checkIsMasterMerchant == true) {
                certificate = EnvironmentUtil.getVietQrMerchantPrefix()
                        + MerchantRefUtil.encryptMerchantId(publishId + dto.getMerchantIdentify());
            } else {
                certificate = EnvironmentUtil.getVietQrMasterMerchantPrefix()
                        + MerchantRefUtil.encryptMerchantId(publishId + dto.getMerchantIdentify());
            }

            // sinh ra cho KH 1 URL webSocket
            String clientId = MerchantRefUtil.encryptMerchantId(publishId);
            // kiểm tra đã có record nào dưới DB đã có publishId chưa
            MerchantSyncEntity checkEntity = merchantSyncService.getMerchantSyncByPublishId(publishId);
            if (checkEntity != null) {
                clientId = MerchantRefUtil.encryptMerchantId(checkEntity.getPublishId());
            } else {
                // tạo 1 records để lưu vào bảng merchant-sync
                checkEntity = new MerchantSyncEntity();
                UUID idMerchantSync = UUID.randomUUID();
                checkEntity.setId(idMerchantSync.toString());
                checkEntity.setName("");
                checkEntity.setFullName("");
                checkEntity.setVso("");
                checkEntity.setBusinessType("");
                checkEntity.setAddress("");
                checkEntity.setCareer("");
                checkEntity.setNationalId(dto.getMerchantIdentify());
                checkEntity.setIsActive(true);
                checkEntity.setUserId("");
                checkEntity.setAccountCustomerId("");
                checkEntity.setEmail("");
                checkEntity.setPhoneNo("");
                checkEntity.setPublishId(publishId);
                checkEntity.setRefId("");
                checkEntity.setCertificate(certificate);
                checkEntity.setWebhook(dto.getWebhook());
                // generate webSocket
                checkEntity.setClientId(clientId);
            }
            merchantSyncService.insert(checkEntity);

            // trả về response
            response.setMerchantId(publishId);
            response.setCertificate(certificate);
            response.setWebhook(dto.getWebhook());
            response.setClientId(clientId);

            result = response;
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED" + e.getMessage(), "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("mid/list-mid")
    public ResponseEntity<Object> getMidsExternal(@RequestHeader("Authorization") String token,
                                                  @RequestParam(defaultValue = "1") int page,
                                                  @RequestParam(defaultValue = "20") int size) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            String username = getUsernameFromToken(token);
            String mid = "";
            if (!StringUtil.isNullOrEmpty(username)) {
                String checkExistMerchantSync = accountCustomerService.checkExistMerchantSyncByUsername(username);
                if (!StringUtil.isNullOrEmpty(checkExistMerchantSync)) {
                    mid = checkExistMerchantSync;
                    int offset = (page - 1) * size;
                    int totalElement = 0;
                    PageResDTO response = new PageResDTO();
                    totalElement = merchantSyncService.countMerchantByMidSync(mid);
                    List<IMerchantSyncPublicDTO> iTerminalSyncDTOs = merchantSyncService
                            .getMerchantByMidSync(mid, offset, size);
                    PageDTO pageDTO = new PageDTO();
                    pageDTO.setPage(page);
                    pageDTO.setSize(size);
                    pageDTO.setTotalPage(StringUtil.getTotalPage(totalElement, size));
                    pageDTO.setTotalElement(totalElement);
                    response.setMetadata(pageDTO);
                    response.setData(iTerminalSyncDTOs);
                    result = response;
                    httpStatus = HttpStatus.OK;
                } else {
                    logger.error("getMidsExternal: MERCHANT IS NOT EXISTED");
                    result = new ResponseMessageDTO("FAILED", "E104");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                logger.error("getMidsExternal: INVALID TOKEN");
                result = new ResponseMessageDTO("FAILED", "E74");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            httpStatus = HttpStatus.BAD_REQUEST;
            result = new ResponseMessageDTO("FAILED", "E05");
        }

        return new ResponseEntity<>(result, httpStatus);
    }


    private ResponseMessageDTO validateMidSync(String accessKey, List<MidSynchronizeDTO> dtos) {
        ResponseMessageDTO result = new ResponseMessageDTO();
        if (Objects.nonNull(dtos)) {
            for (MidSynchronizeDTO item : dtos) {
                String checkSum = BankEncryptUtil.generateMD5SyncMidChecksum(accessKey, item.getMerchantName(),
                        item.getMerchantIdentity());
                if (BankEncryptUtil.isMatchChecksum(checkSum, item.getCheckSum())) {
                    if (ObjectUtils.allNotNull(item.getMerchantFullName(), item.getMerchantName(), item.getMerchantAddress(),
                            item.getMerchantIdentity(), item.getContactEmail(), item.getContactPhone())) {
                        String merchantId = merchantSyncService.getMerchantIdSyncByName(item.getMerchantName().toUpperCase());
                        if (StringUtil.isNullOrEmpty(merchantId)) {
                            result = new ResponseMessageDTO("SUCCESS", "");
                        } else {
                            logger.error(
                                    "validateMidSync: ERROR: INVALID Mid Sync DTO: " + item.toString()
                                            + " at: " + System.currentTimeMillis());
                            result = new ResponseMessageDTO("FAILED", "E154");
                            break;
                        }
                    } else {
                        logger.error(
                                "validateMidSync: ERROR: INVALID Mid Sync DTO: " + item.toString()
                                        + " at: " + System.currentTimeMillis());
                        result = new ResponseMessageDTO("FAILED", "E46");
                        break;
                    }
                } else {
                    logger.error(
                            "validateMidSync: ERROR: INVALID CHECKSUM");
                    result = new ResponseMessageDTO("FAILED", "E39");
                    break;
                }
            }
        } else {
            result = new ResponseMessageDTO("FAILED", "E46");
        }
        return result;
    }

    private ResponseMessageDTO validateMidSyncV2(String accessKey, List<MidSynchronizeV2DTO> dtos) {
        ResponseMessageDTO result = new ResponseMessageDTO();
        if (Objects.nonNull(dtos)) {
            for (MidSynchronizeDTO item : dtos) {
                String checkSum = BankEncryptUtil.generateMD5SyncMidChecksum(accessKey, item.getMerchantName(),
                        item.getMerchantIdentity());
                System.out.println(checkSum);
                if (BankEncryptUtil.isMatchChecksum(checkSum, item.getCheckSum())) {
                    if (ObjectUtils.allNotNull(item.getMerchantFullName(), item.getMerchantName(), item.getMerchantAddress(),
                            item.getMerchantIdentity(), item.getContactEmail(), item.getContactPhone())) {
                        String merchantId = merchantSyncService.getMerchantIdSyncByName(item.getMerchantName().toUpperCase());
                        if (StringUtil.isNullOrEmpty(merchantId)) {
                            result = new ResponseMessageDTO("SUCCESS", "");
                        } else {
                            logger.error(
                                    "validateMidSync: ERROR: INVALID Mid Sync DTO: " + item.toString()
                                            + " at: " + System.currentTimeMillis());
                            result = new ResponseMessageDTO("FAILED", "E154");
                            break;
                        }
                    } else {
                        logger.error(
                                "validateMidSync: ERROR: INVALID Mid Sync DTO: " + item.toString()
                                        + " at: " + System.currentTimeMillis());
                        result = new ResponseMessageDTO("FAILED", "E46");
                        break;
                    }
                } else {
                    logger.error(
                            "validateMidSync: ERROR: INVALID CHECKSUM");
                    result = new ResponseMessageDTO("FAILED", "E39");
                    break;
                }
            }
        } else {
            result = new ResponseMessageDTO("FAILED", "E46");
        }
        return result;
    }

    private ResponseMessageDTO validateTidSync(String accessKey, TidSyncDTO dto) {
        ResponseMessageDTO result = new ResponseMessageDTO();
        if (Objects.nonNull(dto)) {
            for (TidSynchronizeDTO item : dto.getTerminals()) {
                String checkSum = BankEncryptUtil.generateMD5SyncTidChecksum(accessKey, item.getBankCode(),
                        item.getBankAccount());
                if (BankEncryptUtil.isMatchChecksum(checkSum, item.getCheckSum())) {
                    if (ObjectUtils.allNotNull(item.getBankAccount(), item.getBankCode(), item.getTerminalCode(),
                            item.getTerminalAddress(), item.getTerminalName())) {
                        if (!StringUtil.isNullOrEmpty(item.getMid()) || !StringUtil.isNullOrEmpty(item.getMerchantName())) {
                            result = new ResponseMessageDTO("SUCCESS", "");
                        } else {
                            logger.error(
                                    "validateTidSync: ERROR: INVALID Tid Sync DTO: " + item.toString()
                                            + " at: " + System.currentTimeMillis());
                            result = new ResponseMessageDTO("FAILED", "E46");
                        }
                    } else {
                        logger.error(
                                "validateTidSync: ERROR: INVALID Tid Sync DTO: " + item.toString()
                                        + " at: " + System.currentTimeMillis());
                        result = new ResponseMessageDTO("FAILED", "E46");
                        break;
                    }
                } else {
                    logger.error(
                            "validateTidSync: ERROR: INVALID CHECKSUM");
                    result = new ResponseMessageDTO("FAILED", "E39");
                    break;
                }
            }
        } else {
            result = new ResponseMessageDTO("FAILED", "E46");
        }
        return result;
    }

    private ResponseMessageDTO validateTidSyncV2(String accessKey, TidSyncV2DTO dto) {
        ResponseMessageDTO result = new ResponseMessageDTO();
        if (Objects.nonNull(dto)) {
            for (TidSynchronizeV2DTO item : dto.getTerminals()) {
                String checkSum = BankEncryptUtil.generateMD5SyncTidChecksum(accessKey, item.getBankCode(),
                        item.getBankAccount());
                System.out.println(checkSum);
                if (BankEncryptUtil.isMatchChecksum(checkSum, item.getCheckSum())) {
                    if (ObjectUtils.allNotNull(item.getBankAccount(), item.getBankCode(), item.getTerminalCode(),
                            item.getTerminalAddress(), item.getTerminalName())) {
                        if (!StringUtil.isNullOrEmpty(item.getMid()) || !StringUtil.isNullOrEmpty(item.getMerchantName())) {
                            result = new ResponseMessageDTO("SUCCESS", "");
                        } else {
                            logger.error(
                                    "validateTidSync: ERROR: INVALID Tid Sync DTO: " + item.toString()
                                            + " at: " + System.currentTimeMillis());
                            result = new ResponseMessageDTO("FAILED", "E46");
                        }
                    } else {
                        logger.error(
                                "validateTidSync: ERROR: INVALID Tid Sync DTO: " + item.toString()
                                        + " at: " + System.currentTimeMillis());
                        result = new ResponseMessageDTO("FAILED", "E46");
                        break;
                    }
                } else {
                    logger.error(
                            "validateTidSync: ERROR: INVALID CHECKSUM");
                    result = new ResponseMessageDTO("FAILED", "E39");
                    break;
                }
            }
        } else {
            result = new ResponseMessageDTO("FAILED", "E46");
        }
        return result;
    }

    private List<BankAccountSyncDTO> getUniqueBankAccountSyncDTOs(List<TidSynchronizeDTO> dtoList) {
        return dtoList.stream()
                .map(item -> {
                    BankAccountSyncDTO accountSyncDTO = new BankAccountSyncDTO();
                    accountSyncDTO.setBankAccount(item.getBankAccount());
                    accountSyncDTO.setBankCode(item.getBankCode());
                    return accountSyncDTO;
                }).distinct().collect(Collectors.toList());
    }

    private List<BankAccountSyncDTO> getUniqueBankAccountSyncV2DTOs(List<TidSynchronizeV2DTO> dtoList) {
        return dtoList.stream()
                .map(item -> {
                    BankAccountSyncDTO accountSyncDTO = new BankAccountSyncDTO();
                    accountSyncDTO.setBankAccount(item.getBankAccount());
                    accountSyncDTO.setBankCode(item.getBankCode());
                    return accountSyncDTO;
                }).distinct().collect(Collectors.toList());
    }

    private ResponseObjectDTO checkRawTerminalCodeUnique(List<String> dtoList) {
        ResponseObjectDTO result = null;
        Set<String> terminalCodes = new HashSet<>(dtoList);
        List<String> checkDuplicated = terminalService.checkExistedTerminalRawCodes(dtoList);
        if (terminalCodes.size() == dtoList.size()) {
            if (Objects.isNull(checkDuplicated) || checkDuplicated.isEmpty()) {
                result = new ResponseObjectDTO("SUCCESS", "");
            } else {
                Set<String> codes = new HashSet<>(checkDuplicated);
                result = new ResponseObjectDTO("CHECK", codes);
            }
        } else {
            result = new ResponseObjectDTO("FAILED", "E153");
        }
        return result;
    }

    private String generateRandomMerPublishId() {
        String result = "";
        String checkExistedCode = "";
        String code = "";
        try {
            do {
                code = EnvironmentUtil.getMerchantPrefixPublic() +
                        RandomCodeUtil.generateOTP(8);
                checkExistedCode = merchantSyncService
                        .checkExistedPublishId(code);
                if (checkExistedCode == null || checkExistedCode.trim().isEmpty()) {
                    checkExistedCode = merchantSyncService.checkExistedPublishId(code);
                }
            } while (!StringUtil.isNullOrEmpty(checkExistedCode));
            result = code;
        } catch (Exception e) {
            logger.error("getRandomUniqueCodeInTerminalCode: ERROR: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
        }
        return result;
    }

    private String generateRandomTerPublishId() {
        String result = "";
        String checkExistedCode = "";
        String code = "";
        try {
            do {
                code = EnvironmentUtil.getPrefixTerminalExternal() +
                        RandomCodeUtil.generateOTP(8);
                checkExistedCode = terminalService
                        .checkExistedPublishId(code);
                if (checkExistedCode == null || checkExistedCode.trim().isEmpty()) {
                    checkExistedCode = terminalService.checkExistedPublishId(code);
                }
            } while (!StringUtil.isNullOrEmpty(checkExistedCode));
            result = code;
        } catch (Exception e) {
            logger.error("generateRandomTerPublishId: ERROR: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
        }
        return result;
    }

    private ResponseMessageDTO validateBankAccountMerchant(Map<String, MidBankAccountSyncDTO> midBankAccountSyncDTOMap) {
        ResponseMessageDTO result = null;
        try {
            result = new ResponseMessageDTO("SUCCESS", "");
            for (Map.Entry<String, MidBankAccountSyncDTO> midBankItem :
                    midBankAccountSyncDTOMap.entrySet()) {
                String mid = midBankItem.getKey();
                MidBankAccountSyncDTO bankAccountSyncDTO = midBankItem.getValue();
                String checkBankAccountInOtherMid = bankReceiveConnectionService
                        .checkBankAccountByBankIdAndMid(bankAccountSyncDTO.getBankId(), mid);
                if (!StringUtil.isNullOrEmpty(checkBankAccountInOtherMid)) {
                    result = new ResponseMessageDTO("FAILED", "E155");
                    break;
                }
            }
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "E05");
        }
        return result;
    }

    private ResponseObjectDTO validateBankAccountAuthenticated(List<BankAccountSyncDTO> dtos) {
        ResponseObjectDTO result = null;
        try {
            Map<BankAccountSyncDTO, AccountBankReceiveEntity> accountBankReceiveEntities = new HashMap<>();
            for (BankAccountSyncDTO item : dtos) {
                BankTypeEntity bankTypeEntity = bankTypeService.getBankTypeByBankCode(item.getBankCode());
                if (Objects.nonNull(bankTypeEntity)) {
                    AccountBankReceiveEntity accountBankReceiveEntity = accountBankReceiveService
                            .getAccountBankReceiveByBankAccountAndBankCode(item.getBankAccount(), item.getBankCode());
                    if (Objects.nonNull(accountBankReceiveEntity)) {
                        accountBankReceiveEntities.put(item, accountBankReceiveEntity);
                    } else {
                        result = new ResponseObjectDTO("FAILED", "E101");
                        break;
                    }
                } else {
                    result = new ResponseObjectDTO("FAILED", "E24");
                    break;
                }
            }
            if (Objects.isNull(result)) {
                result = new ResponseObjectDTO("SUCCESS", accountBankReceiveEntities);
            }
        } catch (Exception e) {
            logger.error("validateBankAccountAuthenticated: ERROR: " + e.getMessage() +
                    " at: " + System.currentTimeMillis());
            result = new ResponseObjectDTO("FAILED", "E05");
        }
        return result;
    }

    private String getUsernameFromToken(String token) {
        String result = "";
        try {
            if (token != null && !token.trim().isEmpty()) {
                String secretKey = "mySecretKey";
                String jwtToken = token.substring(7); // remove "Bearer " from the beginning
                Claims claims = Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(jwtToken).getBody();
                String userId = (String) claims.get("user");
                if (userId != null) {
                    result = new String(Base64.getDecoder().decode(userId));
                }
            }
        } catch (Exception e) {
            logger.info("TerminalSyncController: ERROR: getUsernameFromToken: "
                    + e.getMessage() + " at: " + System.currentTimeMillis());
        }
        return result;
    }

    private String getRandomUniqueCodeInTerminalCode() {
        String result = "";
        String checkExistedCode = "";
        String code = "";
        try {
            do {
                code = getTerminalCode();
                checkExistedCode = terminalBankReceiveService
                        .checkExistedTerminalCode(code);
                if (checkExistedCode == null || checkExistedCode.trim().isEmpty()) {
                    checkExistedCode = terminalService.checkExistedTerminal(code);
                }
            } while (!StringUtil.isNullOrEmpty(checkExistedCode));
            result = code;
        } catch (Exception e) {
            logger.error("getRandomUniqueCodeInTerminalCode: ERROR: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
        }
        return result;
    }

    private String getTerminalCode() {
        return RandomCodeUtil.generateRandomId(10);
    }
}
