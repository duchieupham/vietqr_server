package com.vietqr.org.controller;

import com.vietqr.org.dto.*;
import com.vietqr.org.dto.mb.VietQRStaticMMSRequestDTO;
import com.vietqr.org.entity.*;
import com.vietqr.org.service.AccountBankReceiveService;
import com.vietqr.org.service.AccountBankReceiveShareService;
import com.vietqr.org.service.TerminalBankService;
import com.vietqr.org.service.TerminalService;
import com.vietqr.org.util.FormatUtil;
import com.vietqr.org.util.StringUtil;
import com.vietqr.org.util.VietQRUtil;
import com.vietqr.org.util.bank.mb.MBTokenUtil;
import com.vietqr.org.util.bank.mb.MBVietQRUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class TerminalController {
    private static final Logger logger = Logger.getLogger(TerminalController.class);
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 10;

    @Autowired
    private TerminalService terminalService;

    @Autowired
    private AccountBankReceiveService accountBankReceiveService;

    @Autowired
    private AccountBankReceiveShareService accountBankReceiveShareService;

    @Autowired
    private TerminalBankService terminalBankService;

    @GetMapping("terminal/generate-code")
    public ResponseEntity<ResponseDataDTO> generateTerminalCode() {
        ResponseDataDTO result = null;
        HttpStatus httpStatus = null;
        try {
            String code = getRandomUniqueCode();
            result = new ResponseDataDTO(code);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            result = new ResponseDataDTO("");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("terminal")
    public ResponseEntity<ResponseMessageDTO> insertTerminal(@Valid @RequestBody TerminalInsertDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            UUID uuid = UUID.randomUUID();
            Map<String, QRStaticCreateDTO> qrMap = new HashMap<>();
            //return terminal id if the code is existed
            String checkExistedCode = terminalService.checkExistedTerminal(dto.getCode());
            if (!StringUtil.isNullOrEmpty(checkExistedCode)) {
                result = new ResponseMessageDTO("FAILED", "E110");
                httpStatus = HttpStatus.BAD_REQUEST;
            } else {
                LocalDateTime currentDateTime = LocalDateTime.now();
                long time = currentDateTime.toEpochSecond(ZoneOffset.UTC);
                TerminalEntity entity = new TerminalEntity();
                entity.setId(uuid.toString());
                entity.setName(dto.getName());
                entity.setCode(dto.getCode());
                entity.setAddress(StringUtil.isNullOrEmpty(dto.getAddress()) ? "" : dto.getAddress());
                entity.setMerchantId("");
                entity.setUserId(dto.getUserId());
                entity.setDefault(false);
                entity.setTimeCreated(time);
                terminalService.insertTerminal(entity);
                // insert account-bank-receive-share
                List<AccountBankReceiveShareEntity> entities = new ArrayList<>();
                if (!FormatUtil.isListNullOrEmpty(dto.getUserIds())) {
                    for (String userId : dto.getUserIds()) {
                        AccountBankReceiveShareEntity accountBankReceiveShareEntity = new AccountBankReceiveShareEntity();
                        accountBankReceiveShareEntity.setId(UUID.randomUUID().toString());
                        accountBankReceiveShareEntity.setBankId("");
                        accountBankReceiveShareEntity.setUserId(userId);
                        accountBankReceiveShareEntity.setOwner(false);
                        accountBankReceiveShareEntity.setTerminalId(uuid.toString());
                        accountBankReceiveShareEntity.setQrCode("");
                        accountBankReceiveShareEntity.setTraceTransfer("");
                        entities.add(accountBankReceiveShareEntity);
                    }
                }
                if (!FormatUtil.isListNullOrEmpty(dto.getBankIds())) {
                    for (String bankId : dto.getBankIds()) {
                        AccountBankReceiveShareEntity accountBankReceiveShareEntity = new AccountBankReceiveShareEntity();
                        accountBankReceiveShareEntity.setId(UUID.randomUUID().toString());
                        accountBankReceiveShareEntity.setBankId(bankId);
                        accountBankReceiveShareEntity.setUserId(dto.getUserId());
                        accountBankReceiveShareEntity.setOwner(true);
                        accountBankReceiveShareEntity.setTerminalId(uuid.toString());
                        AccountBankReceiveEntity accountBankReceiveEntity = accountBankReceiveService.getAccountBankById(bankId);
                        if (accountBankReceiveEntity != null) {
                            // luồng ưu tiên
                            if (accountBankReceiveEntity.isMmsActive()) {
                                TerminalBankEntity terminalBankEntity =
                                        terminalBankService.getTerminalBankByBankAccount(accountBankReceiveEntity.getBankAccount());
                                if (terminalBankEntity != null && "MB".equals(terminalBankEntity.getBankCode())) {
                                    String qr = MBVietQRUtil.generateStaticVietQRMMS(
                                            new VietQRStaticMMSRequestDTO(MBTokenUtil.getMBBankToken().getAccess_token(),
                                            terminalBankEntity.getTerminalId(), dto.getCode()));
                                    accountBankReceiveShareEntity.setQrCode(qr);
                                    String traceTransfer = MBVietQRUtil.getTraceTransfer(qr);
                                    accountBankReceiveShareEntity.setTraceTransfer(traceTransfer);
                                    qrMap.put(bankId, new QRStaticCreateDTO(qr, traceTransfer));
                                } else {
                                    logger.error("TerminalController: insertTerminal: terminalBankEntity is null or bankCode is not MB");
                                }
                            } else {
                                // luồng thuong
                                String qrCodeContent = "SQR" + dto.getCode();
                                String bankAccount = accountBankReceiveEntity.getBankAccount();
                                String caiValue = accountBankReceiveService.getCaiValueByBankId(bankId);
                                VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO(caiValue, "", qrCodeContent, bankAccount);
                                String qr = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);
                                accountBankReceiveShareEntity.setQrCode(qr);
                                accountBankReceiveShareEntity.setTraceTransfer("");
                                qrMap.put(bankId, new QRStaticCreateDTO(qr, ""));
                            }
                        }
                        entities.add(accountBankReceiveShareEntity);
                    }
                }

                if (!FormatUtil.isListNullOrEmpty(dto.getUserIds()) && !FormatUtil.isListNullOrEmpty(dto.getBankIds())) {
                    for (String userId : dto.getUserIds()) {
                        for (String bankId : dto.getBankIds()) {
                            AccountBankReceiveShareEntity accountBankReceiveShareEntity = new AccountBankReceiveShareEntity();
                            accountBankReceiveShareEntity.setId(UUID.randomUUID().toString());
                            accountBankReceiveShareEntity.setBankId(bankId);
                            accountBankReceiveShareEntity.setUserId(userId);
                            accountBankReceiveShareEntity.setOwner(false);
                            QRStaticCreateDTO qrStaticCreateDTO = qrMap.get(bankId);
                            if (qrStaticCreateDTO != null) {
                                accountBankReceiveShareEntity.setTraceTransfer(qrStaticCreateDTO.getTraceTransfer());
                                accountBankReceiveShareEntity.setQrCode(qrStaticCreateDTO.getQrCode());
                            }
                            accountBankReceiveShareEntity.setTerminalId(uuid.toString());
                            entities.add(accountBankReceiveShareEntity);
                        }
                    }
                }

                accountBankReceiveShareService.insertAccountBankReceiveShare(entities);
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            }

        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "Unexpected Error.");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("terminal/update")
    public ResponseEntity<ResponseMessageDTO> updateTerminal(@Valid @RequestBody TerminalUpdateDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            //return terminal id if the code is existed
            String checkExistedCode = terminalService.checkExistedTerminal(dto.getCode());
            if (!StringUtil.isNullOrEmpty(checkExistedCode) && !checkExistedCode.equals(dto.getId())) {
                result = new ResponseMessageDTO("FAILED", "E110");
                httpStatus = HttpStatus.BAD_REQUEST;
            } else {
                TerminalEntity entity = terminalService.findTerminalById(dto.getId());
                entity.setName(StringUtil.isNullOrEmpty(dto.getName()) ? entity.getName() : dto.getName());
                entity.setCode(StringUtil.isNullOrEmpty(dto.getCode()) ? entity.getCode() : dto.getCode());
                entity.setAddress(StringUtil.isNullOrEmpty(dto.getAddress()) ? entity.getAddress() : dto.getAddress());
                terminalService.insertTerminal(entity);

                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            }

        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "Unexpected Error.");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("terminal/bank-account")
    public ResponseEntity<List<TerminalBankReceiveDTO>> getBankAccountNotAvailable(
            @Valid @RequestParam String terminalId,
            @Valid @RequestParam String userId) {
        List<TerminalBankReceiveDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            List<TerminalBankReceiveDTO> banks = accountBankReceiveShareService
                    .getAccountBankReceiveShareByTerminalId(userId, terminalId);
            result = banks;
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("terminal/bank")
    public ResponseEntity<TerminalShareResponseDTO> getTerminalsOfBank(
            @Valid @RequestParam String userId,
            @Valid @RequestParam String bankId,
            @Valid @RequestParam int offset) {
        TerminalShareResponseDTO result = null;
        HttpStatus httpStatus = null;
        try {
            TerminalShareResponseDTO dto = new TerminalShareResponseDTO();
            List<TerminalResponseInterfaceDTO> terminalInters = terminalService.getTerminalsByUserIdAndBankId(userId, bankId, offset);
            List<TerminalResponseDTO> terminals = mapInterfToTerminalResponse(terminalInters);
            int total = terminalService.countNumberOfTerminalByUserIdAndBankId(userId, bankId);
            dto.setTotalTerminals(total);
            dto.setUserId(userId);

            // Fetch all banks associated with the terminals in a single database call
            List<ITerminalBankResponseDTO> allBankInters = accountBankReceiveShareService.getTerminalBanksByTerminalIds(
                    terminals.stream().map(TerminalResponseInterfaceDTO::getId).collect(Collectors.toList())
            );
            List<TerminalBankResponseDTO> allBanks = mapInterfTerminalBankToDto(allBankInters);

            // Map the banks to the respective terminals
            Map<String, List<TerminalBankResponseDTO>> terminalBanksMap = allBanks.stream()
                    .collect(Collectors.groupingBy(TerminalBankResponseDTO::getTerminalId));

            terminals.forEach(terminal -> {
                terminal.setBanks(terminalBanksMap.getOrDefault(terminal.getId(), new ArrayList<>()));
            });
            dto.setTerminals(terminals);

            result = dto;
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("terminal/bank-account")
    public ResponseEntity<ResponseMessageDTO> insertBankAccountTerminal(@Valid @RequestBody TerminalBankInsertDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            // get list userIds in terminal
            AccountBankReceiveEntity accountBankReceiveEntity = accountBankReceiveService.getAccountBankById(dto.getBankId());
            String qr = "";
            String traceTransfer = "";
            if (accountBankReceiveEntity != null) {
                // luồng thường
                TerminalEntity terminalEntity = terminalService.findTerminalById(dto.getTerminalId());
                if (accountBankReceiveEntity.isMmsActive() == false) {
                    String qrCodeContent = "SQR" + terminalEntity.getCode();
                    String bankAccount = accountBankReceiveEntity.getBankAccount();
                    String caiValue = accountBankReceiveService.getCaiValueByBankId(dto.getBankId());
                    VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO(caiValue, "", qrCodeContent, bankAccount);
                    qr = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);
                } else {
                    // luồng ưu tien
                    TerminalBankEntity terminalBankEntity =
                            terminalBankService.getTerminalBankByBankAccount(accountBankReceiveEntity.getBankAccount());
                    if (terminalBankEntity != null && "MB".equals(terminalBankEntity.getBankCode())) {
                        qr = MBVietQRUtil.generateStaticVietQRMMS(
                                new VietQRStaticMMSRequestDTO(MBTokenUtil.getMBBankToken().getAccess_token(),
                                        terminalBankEntity.getTerminalId(), ""));
                        traceTransfer = MBVietQRUtil.getTraceTransfer(qr);
                    } else {
                        logger.error("TerminalController: insertTerminal: terminalBankEntity is null or bankCode is not MB");
                    }
                }
            }
            // get all userIds in terminal is_owner = false
            List<String> userIds = accountBankReceiveShareService.getUserIdsFromTerminalId(dto.getTerminalId(), dto.getUserId());
            // insert account-bank-receive-share
            List<AccountBankReceiveShareEntity> entities = new ArrayList<>();
            if (!FormatUtil.isListNullOrEmpty(userIds)) {
                for (String userId : userIds) {
                    AccountBankReceiveShareEntity accountBankReceiveShareEntity = new AccountBankReceiveShareEntity();
                    accountBankReceiveShareEntity.setId(UUID.randomUUID().toString());
                    accountBankReceiveShareEntity.setBankId(dto.getBankId());
                    accountBankReceiveShareEntity.setUserId(userId);
                    accountBankReceiveShareEntity.setOwner(false);
                    accountBankReceiveShareEntity.setTerminalId(dto.getTerminalId());
                    accountBankReceiveShareEntity.setQrCode(qr);
                    accountBankReceiveShareEntity.setTraceTransfer(traceTransfer);
                    entities.add(accountBankReceiveShareEntity);
                }
            }
            UUID uuidShare = UUID.randomUUID();
            AccountBankReceiveShareEntity accountBankReceiveShareEntity = new AccountBankReceiveShareEntity();
            accountBankReceiveShareEntity.setId(uuidShare.toString());
            accountBankReceiveShareEntity.setBankId(dto.getBankId());
            accountBankReceiveShareEntity.setUserId(dto.getUserId());
            accountBankReceiveShareEntity.setOwner(true);
            accountBankReceiveShareEntity.setQrCode(qr);
            accountBankReceiveShareEntity.setTraceTransfer(traceTransfer);
            accountBankReceiveShareEntity.setTerminalId(dto.getTerminalId());
            entities.add(accountBankReceiveShareEntity);
            accountBankReceiveShareService.insertAccountBankReceiveShare(entities);

            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @DeleteMapping("terminal/bank-account")
    public ResponseEntity<ResponseMessageDTO> removeBankAccountTerminal(@Valid @RequestBody TerminalRemoveBankDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            accountBankReceiveShareService.removeBankAccountFromTerminal(dto.getTerminalId(), dto.getBankId());
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @DeleteMapping("terminal/remove")
    public ResponseEntity<ResponseMessageDTO> removeTerminalById(@Valid @RequestBody TerminalRemoveDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            terminalService.removeTerminalById(dto.getTerminalId());
            accountBankReceiveShareService.removeTerminalGroupByTerminalId(dto.getTerminalId());
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("terminal/detail/{terminalId}")
    public ResponseEntity<TerminalDetailResponseDTO> getTerminalById(@PathVariable String terminalId) {
        TerminalDetailResponseDTO result = null;
        HttpStatus httpStatus = null;
        try {
            TerminalDetailResponseDTO responseDTO = new TerminalDetailResponseDTO();
            ITerminalDetailResponseDTO dto = terminalService.getTerminalById(terminalId);
            responseDTO.setId(dto.getId());
            responseDTO.setName(dto.getName());
            responseDTO.setAddress(dto.getAddress());
            responseDTO.setCode(dto.getCode());
            responseDTO.setUserId(dto.getUserId());
            responseDTO.setDefault(dto.getIsDefault());
            responseDTO.setTotalMember(dto.getTotalMember());
//            responseDTO.setQrCode("");
            List<String> terminalIds = new ArrayList<>();
            terminalIds.add(terminalId);
            List<ITerminalBankResponseDTO> iTerminalBankResponseDTOS = accountBankReceiveShareService.getTerminalBanksByTerminalIds(terminalIds);
            List<TerminalBankResponseDTO> banks = mapInterfTerminalBankToDto(iTerminalBankResponseDTOS);
            List<AccountMemberDTO> members = new ArrayList<>();
            members = accountBankReceiveShareService.getMembersFromTerminalId(terminalId);
            responseDTO.setBanks(banks);
            responseDTO.setMembers(members);
            result = responseDTO;
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("terminal")
    public ResponseEntity<Object> getTerminalsByUserId(
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "type") int type,
            @RequestParam(value = "offset") int offset) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            switch (type) {
                // 0: Filter theo ‘Đã chia sẻ - Nhóm chia sẻ’
                // 1: Filter theo ‘Đã chia sẻ - Tài khoản ngân hàng’
                // 2: Filter theo ‘Chia sẻ với tôi - Nhóm chia sẻ’
                // 3: Filter theo ‘Chia sẻ với tôi - Tài khoản ngân hàng’
                case 0:
                    TerminalShareResponseDTO dto = new TerminalShareResponseDTO();
                    List<TerminalResponseInterfaceDTO> terminalInters = terminalService.getTerminalsByUserId(userId, offset);
                    List<TerminalResponseDTO> terminals = mapInterfToTerminalResponse(terminalInters);
                    int total = terminalService.countNumberOfTerminalByUserId(userId);
                    dto.setTotalTerminals(total);
                    dto.setUserId(userId);

                    // Fetch all banks associated with the terminals in a single database call
                    List<ITerminalBankResponseDTO> allBankInters = accountBankReceiveShareService.getTerminalBanksByTerminalIds(
                            terminals.stream().map(TerminalResponseInterfaceDTO::getId).collect(Collectors.toList())
                    );
                    List<TerminalBankResponseDTO> allBanks = mapInterfTerminalBankToDto(allBankInters);

                    // Map the banks to the respective terminals
                    Map<String, List<TerminalBankResponseDTO>> terminalBanksMap = allBanks.stream()
                            .collect(Collectors.groupingBy(TerminalBankResponseDTO::getTerminalId));

                    terminals.forEach(terminal -> {
                        terminal.setBanks(terminalBanksMap.getOrDefault(terminal.getId(), new ArrayList<>()));
                    });
                    dto.setTerminals(terminals);

                    result = dto;
                    httpStatus = HttpStatus.OK;
                    break;
                case 1:
                    TerminalBankShareResponseDTO terminalBankShareResponseDTO = new TerminalBankShareResponseDTO();

                    List<IBankShareResponseDTO> iBankShareResponseDTOS = accountBankReceiveShareService
                            .getTerminalBankByUserId(userId, offset);
                    List<BankShareResponseDTO> bankShareResponseDTOs = mapInterfToBankShareResponse(iBankShareResponseDTOS);
                    int totalBanks = accountBankReceiveShareService.countNumberOfBankShareByUserId(userId);
                    terminalBankShareResponseDTO.setTotalBankShares(totalBanks);
                    terminalBankShareResponseDTO.setUserId(userId);

                    // Fetch all terminals associated with the banks in a single database call
                    List<ITerminalShareDTO> iTerminalShareDTOS = terminalService.getTerminalSharesByBankIds(
                            bankShareResponseDTOs.stream().map(BankShareResponseDTO::getBankId).collect(Collectors.toList()), userId
                    );
                    List<TerminalShareDTO> allTerminals = mapInterfToTerminalShare(iTerminalShareDTOS);

                    // Map the terminals to the respective banks
                    Map<String, List<TerminalShareDTO>> bankTerminalsMap = allTerminals.stream()
                            .collect(Collectors.groupingBy(TerminalShareDTO::getBankId));

                    bankShareResponseDTOs.forEach(bank -> {
                        bank.setTerminals(bankTerminalsMap.getOrDefault(bank.getBankId(), new ArrayList<>()));
                    });
                    terminalBankShareResponseDTO.setBankShares(bankShareResponseDTOs);
                    result = terminalBankShareResponseDTO;
                    httpStatus = HttpStatus.OK;
                    break;
                case 2:
                    TerminalShareResponseDTO terminalShareResponseDTO = new TerminalShareResponseDTO();
                    List<TerminalResponseInterfaceDTO> iTerminalResponseDTOs = terminalService.getTerminalSharesByUserId(userId, offset);
                    List<TerminalResponseDTO> terminalResponseDTOs = mapInterfToTerminalResponse(iTerminalResponseDTOs);
                    int totalTerminalShare = terminalService.countNumberOfTerminalShareByUserId(userId);
                    terminalShareResponseDTO.setTotalTerminals(totalTerminalShare);
                    terminalShareResponseDTO.setUserId(userId);

                    // Fetch all banks associated with the terminals in a single database call
                    List<ITerminalBankResponseDTO> iTerminalBankResponseDTOS = accountBankReceiveShareService.getTerminalBanksByTerminalIds(
                            terminalResponseDTOs.stream().map(TerminalResponseDTO::getId).collect(Collectors.toList())
                    );

                    List<TerminalBankResponseDTO> allBankShares = mapInterfTerminalBankToDto(iTerminalBankResponseDTOS);

//                     Map the banks to the respective terminals
                    Map<String, List<TerminalBankResponseDTO>> terminalBankSharesMap = allBankShares.stream()
                            .collect(Collectors.groupingBy(TerminalBankResponseDTO::getTerminalId));

                    terminalResponseDTOs.forEach(terminal -> {
                        terminal.setBanks(terminalBankSharesMap.getOrDefault(terminal.getId(), new ArrayList<>()));
                    });
                    terminalShareResponseDTO.setTerminals(terminalResponseDTOs);

                    result = terminalShareResponseDTO;
                    httpStatus = HttpStatus.OK;
                    break;
                case 3:
                    TerminalBankShareResponseDTO responseDTO = new TerminalBankShareResponseDTO();

                    List<IBankShareResponseDTO> iBankShareResponseDTOList = accountBankReceiveShareService
                            .getTerminalBankShareByUserId(userId, offset);
                    List<BankShareResponseDTO> shareResponseDTOList = mapInterfToBankShareResponse(iBankShareResponseDTOList);
                    int totalBankShares = accountBankReceiveShareService.countNumberOfTerminalBankShareByUserId(userId);
                    responseDTO.setTotalBankShares(totalBankShares);
                    responseDTO.setUserId(userId);

                    // Fetch all terminals associated with the banks in a single database call
                    List<ITerminalShareDTO> iTerminalShareDTOList = terminalService.getTerminalSharesByBankIds(
                            shareResponseDTOList.stream().map(BankShareResponseDTO::getBankId).collect(Collectors.toList()), userId
                    );
                    List<TerminalShareDTO> terminalShareDTOS = mapInterfToTerminalShare(iTerminalShareDTOList);

                    // Map the terminals to the respective banks
                    Map<String, List<TerminalShareDTO>> listMap = terminalShareDTOS.stream()
                            .collect(Collectors.groupingBy(TerminalShareDTO::getBankId));

                    shareResponseDTOList.forEach(bank -> {
                        bank.setTerminals(listMap.getOrDefault(bank.getBankId(), new ArrayList<>()));
                    });
                    responseDTO.setBankShares(shareResponseDTOList);
                    result = responseDTO;
                    httpStatus = HttpStatus.OK;
                    break;
                default:
                    result = new ResponseMessageDTO("FAILED", "E88");
                    httpStatus = HttpStatus.BAD_REQUEST;
                    break;
            }
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "Unexpected Error.");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    private List<BankShareResponseDTO> mapInterfToBankShareResponse(List<IBankShareResponseDTO> iBankShareResponseDTOList) {
        List<BankShareResponseDTO> shareResponseDTOList = iBankShareResponseDTOList.stream().map(item -> {
            BankShareResponseDTO bankShareResponseDTO = new BankShareResponseDTO();
            bankShareResponseDTO.setBankName(item.getBankName());
            bankShareResponseDTO.setBankId(item.getBankId());
            bankShareResponseDTO.setBankCode(item.getBankCode());
            bankShareResponseDTO.setBankAccount(item.getBankAccount());
            bankShareResponseDTO.setUserBankName(item.getUserBankName());
            bankShareResponseDTO.setBankShortName(item.getBankShortName());
            bankShareResponseDTO.setImgId(item.getImgId());
            return bankShareResponseDTO;
        }).collect(Collectors.toList());
        return shareResponseDTOList;
    }

    private List<TerminalResponseDTO> mapInterfToTerminalResponse(List<TerminalResponseInterfaceDTO> terminalInters) {
        List<TerminalResponseDTO> terminals = terminalInters.stream().map(item -> {
            TerminalResponseDTO terminalResponseDTO = new TerminalResponseDTO();
            terminalResponseDTO.setId(item.getId());
            terminalResponseDTO.setName(item.getName());
            terminalResponseDTO.setAddress(item.getAddress());
            terminalResponseDTO.setCode(item.getCode());
            terminalResponseDTO.setDefault(item.getIsDefault());
            terminalResponseDTO.setUserId(item.getUserId());
            terminalResponseDTO.setTotalMembers(item.getTotalMembers());
            return terminalResponseDTO;
        }).collect(Collectors.toList());
        return terminals;
    }

    private List<TerminalShareDTO> mapInterfToTerminalShare(List<ITerminalShareDTO> iTerminalShareDTOList) {
        List<TerminalShareDTO> terminalShareDTOS = iTerminalShareDTOList.stream().map(item -> {
            TerminalShareDTO terminalShareDTO = new TerminalShareDTO();
            terminalShareDTO.setId(item.getTerminalId());
            terminalShareDTO.setBankId(item.getBankId());
            terminalShareDTO.setTerminalName(item.getTerminalName());
            terminalShareDTO.setTerminalCode(item.getTerminalCode());
            terminalShareDTO.setTerminalAddress(item.getTerminalAddress());
            terminalShareDTO.setTotalMembers(item.getTotalMembers());
            terminalShareDTO.setDefault(item.getIsDefault());
            return terminalShareDTO;
        }).collect(Collectors.toList());
        return terminalShareDTOS;
    }

    private List<TerminalBankResponseDTO> mapInterfTerminalBankToDto(List<ITerminalBankResponseDTO> iTerminalBankResponseDTOS) {
        List<TerminalBankResponseDTO> allBankShares = iTerminalBankResponseDTOS.stream().map(item -> {
            TerminalBankResponseDTO terminalBankResponseDTO = new TerminalBankResponseDTO();
            terminalBankResponseDTO.setBankId(item.getBankId());
            terminalBankResponseDTO.setTerminalId(item.getTerminalId());
            terminalBankResponseDTO.setBankName(item.getBankName());
            terminalBankResponseDTO.setBankCode(item.getBankCode());
            terminalBankResponseDTO.setBankAccount(item.getBankAccount());
            terminalBankResponseDTO.setUserBankName(item.getUserBankName());
            terminalBankResponseDTO.setBankShortName(item.getBankShortName());
            terminalBankResponseDTO.setImgId(item.getImgId());
            terminalBankResponseDTO.setQrCode(item.getQrCode() != null ? item.getQrCode() : "");
            return terminalBankResponseDTO;
        }).collect(Collectors.toList());
        return allBankShares;
    }


    private String getTerminalCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(randomIndex));
        }
        return code.toString();
    }

    private String getRandomUniqueCode() {
        String result = "";
        String checkExistedCode = "";
        String code = "";
        try {
            do {
                code = getTerminalCode();
                checkExistedCode = terminalService.checkExistedTerminal(code);
            } while (!StringUtil.isNullOrEmpty(checkExistedCode));
            result = code;
        } catch (Exception e) {
        }
        return result;
    }
}
