package com.vietqr.org.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vietqr.org.dto.*;
import com.vietqr.org.dto.bidv.VietQRVaRequestDTO;
import com.vietqr.org.dto.mb.VietQRStaticMMSRequestDTO;
import com.vietqr.org.entity.*;
import com.vietqr.org.entity.bidv.CustomerInvoiceEntity;
import com.vietqr.org.service.*;
import com.vietqr.org.service.bidv.CustomerInvoiceService;
import com.vietqr.org.service.mqtt.MqttMessagingService;
import com.vietqr.org.util.*;
import com.vietqr.org.util.bank.bidv.CustomerVaUtil;
import com.vietqr.org.util.bank.mb.MBTokenUtil;
import com.vietqr.org.util.bank.mb.MBVietQRUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.security.SecureRandom;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class TidQrInternalController {

    private static final Logger logger = Logger.getLogger(TerminalController.class);
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String NUMBERS = "0123456789";
    private static final int CODE_LENGTH = 6;
    private static final int TERMINAL_CODE_LENGTH = 10;

    @Autowired
    private TerminalService terminalService;

    @Autowired
    private MqttMessagingService mqttMessagingService;

    @Autowired
    private TerminalBankReceiveService terminalBankReceiveService;

    @Autowired
    private TransactionReceiveService transactionReceiveService;

    @Autowired
    private AccountBankReceiveService accountBankReceiveService;

    @Autowired
    private QrBoxSyncService qrBoxSyncService;

    @Autowired
    private TerminalBankService terminalBankService;

    @Autowired
    private BankTypeService bankTypeService;

    @Autowired
    private CaiBankService caiBankService;

    @Autowired
    private SystemSettingService systemSettingService;

    @Autowired
    private SocketHandler socketHandler;

    @Autowired
    private CustomerInvoiceService customerInvoiceService;

    @GetMapping("tid-internal/env-setting")
    public ResponseEntity<Object> getEnvironmentSetting() {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            BoxEnvironmentVarDTO response = new BoxEnvironmentVarDTO();
            BoxEnvironmentResDTO dto = systemSettingService.getSystemSettingBoxEnv();
            if (dto != null) {
                response = getBoxEnv(dto.getBoxEnv());
            }
            result = response;
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("tid-internal/env-setting")
    public ResponseEntity<ResponseMessageDTO> updateEnvironmentSetting(
            @Valid @RequestBody BoxEnvironmentVarDTO dto
    ) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            String data = mapper.writeValueAsString(dto);
            systemSettingService.updateBoxEnvironment(data);
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("tid-internal/sync")
    public ResponseEntity<Object> syncQrBoxInternal(@Valid @RequestBody TerminalSyncInterDTO dto) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            String terminalCode = "";
            terminalCode = getRandomUniqueCodeInTerminalCode();
            IAccountBankReceiveDTO accountBankInfoResById
                    = accountBankReceiveService.getAccountBankInfoResById(dto.getBankAccount(), dto.getBankCode());
            String boxCode = qrBoxSyncService.getByQrCertificate(dto.getQrCertificate());
            if (accountBankInfoResById != null && boxCode != null && !boxCode.isEmpty()) {
                if (accountBankInfoResById.getIsAuthenticated()) {
                    TerminalBankReceiveEntity terminalBankReceiveEntity = terminalBankReceiveService
                            .getTerminalBankReceiveByRawTerminalCode(boxCode);
                    if (terminalBankReceiveEntity == null) {
                        terminalBankReceiveEntity = new TerminalBankReceiveEntity();
                        terminalBankReceiveEntity.setId(UUID.randomUUID().toString());
                    }
                    terminalBankReceiveEntity.setTerminalId("");
                    terminalBankReceiveEntity.setSubTerminalAddress(dto.getBoxAddress());
                    terminalBankReceiveEntity.setBankId(accountBankInfoResById.getBankId());
                    terminalBankReceiveEntity.setRawTerminalCode(boxCode);
                    terminalBankReceiveEntity.setTerminalCode(terminalCode);
                    terminalBankReceiveEntity.setTypeOfQR(2);
                    String qrCode = "";
                    switch (accountBankInfoResById.getBankCode()) {
                        case "MB":
                            if (accountBankInfoResById.getIsMmsActive()) {
                                TerminalBankEntity terminalBankEntity =
                                        terminalBankService.getTerminalBankByBankAccount(accountBankInfoResById.getBankAccount());
                                if (terminalBankEntity != null) {
                                    String qr = MBVietQRUtil.generateStaticVietQRMMS(
                                            new VietQRStaticMMSRequestDTO(MBTokenUtil.getMBBankToken().getAccess_token(),
                                                    terminalBankEntity.getTerminalId(), terminalCode));
                                    terminalBankReceiveEntity.setData2(qr);
                                    qrCode = qr;
                                    String traceTransfer = MBVietQRUtil.getTraceTransfer(qr);
                                    terminalBankReceiveEntity.setTraceTransfer(traceTransfer);
                                    terminalBankReceiveEntity.setData1("");
                                } else {
                                    logger.error("TerminalController: insertTerminal: terminalBankEntity is null or bankCode is not MB");
                                }
                            } else {
                                // luồng thuong
                                String qrCodeContent = "SQR" + terminalCode;
                                String bankAccount = accountBankInfoResById.getBankAccount();
                                String caiValue = accountBankReceiveService.getCaiValueByBankId(accountBankInfoResById.getBankId());
                                VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO(caiValue, "", qrCodeContent, bankAccount);
                                String qr = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);
                                terminalBankReceiveEntity.setData1(qr);
                                qrCode = qr;
                                terminalBankReceiveEntity.setData2("");
                                terminalBankReceiveEntity.setTraceTransfer("");
                            }
                            break;
                        case "BIDV":
                            AccountBankReceiveEntity accountBankEntity = accountBankReceiveService
                                    .getAccountBankById(accountBankInfoResById.getBankId());
                            String qr = "";
                            String billId = getRandomBillId();
                            VietQRCreateDTO vietQRCreateDTO = new VietQRCreateDTO();
                            vietQRCreateDTO.setBankId(accountBankInfoResById.getBankId());
                            vietQRCreateDTO.setAmount("0");
                            vietQRCreateDTO.setContent(billId);
                            vietQRCreateDTO.setUserId(accountBankEntity.getUserId());
                            vietQRCreateDTO.setTerminalCode(terminalCode);

                            ResponseMessageDTO responseMessageDTO =
                                    insertNewCustomerInvoiceTransBIDV(vietQRCreateDTO, accountBankInfoResById, billId);
                            if ("SUCCESS".equals(responseMessageDTO.getStatus())) {
                                VietQRVaRequestDTO vietQRVaRequestDTO = new VietQRVaRequestDTO();
                                vietQRVaRequestDTO.setAmount("0");
                                vietQRVaRequestDTO.setBillId(billId);
                                vietQRVaRequestDTO.setUserBankName(accountBankInfoResById.getUserBankName());
                                vietQRVaRequestDTO.setDescription(StringUtil.getValueNullChecker(billId));
                                ResponseMessageDTO generateVaInvoiceVietQR = CustomerVaUtil
                                        .generateVaInvoiceVietQR(vietQRVaRequestDTO, accountBankEntity.getCustomerId());
                                if ("SUCCESS".equals(generateVaInvoiceVietQR.getStatus())) {
                                    qr = generateVaInvoiceVietQR.getMessage();
                                    terminalBankReceiveEntity.setData1(qr);
                                    qrCode = qr;
                                    terminalBankReceiveEntity.setData2("");
                                    terminalBankReceiveEntity.setTraceTransfer("");
                                }
                            }
                            break;
                    }
                    terminalBankReceiveService.insert(terminalBankReceiveEntity);
                    BoxMachineCreatedDTO response = new BoxMachineCreatedDTO();
                    String boxId = BoxTerminalRefIdUtil
                            .encryptQrBoxId(terminalBankReceiveEntity.getRawTerminalCode());
                    response.setBoxId(boxId);
                    response.setBankAccount(dto.getBankAccount());
                    response.setBankCode(dto.getBankCode());
                    response.setQrCode(qrCode);
                    response.setBoxCode(boxCode);
                    response.setTerminalCode(terminalCode);
                    result = response;

                    Map<String, String> data = new HashMap<>();
                    data.put("notificationType", NotificationUtil.getNotiConnectQrSuccess());
                    data.put("bankAccount", dto.getBankAccount());
                    data.put("bankShortName", accountBankInfoResById.getBankAccount());
                    data.put("userBankName", accountBankInfoResById.getUserBankName());
                    data.put("qrCode", response.getQrCode());
                    data.put("machineId", terminalBankReceiveEntity.getId());
                    data.put("terminalCode", boxCode);
                    data.put("terminalName", dto.getTerminalName() != null ? dto.getTerminalName() : "");
                    data.put("boxId", boxId);
                    data.put("boxAddress", dto.getBoxAddress());
                    data.put("boxCode", boxCode);
                    data.put("bankCode", accountBankInfoResById.getBankCode());
                    String homePage = EnvironmentUtil.getVietQrHomePage();
                    BoxEnvironmentResDTO boxEnvironmentResDTO = systemSettingService.getSystemSettingBoxEnv();
                    if (boxEnvironmentResDTO != null) {
                        try {
                            ObjectMapper mapper = new ObjectMapper();
                            BoxEnvironmentVarDTO boxEnvironmentVarDTO = mapper.readValue(boxEnvironmentResDTO.getBoxEnv(), BoxEnvironmentVarDTO.class);
                            homePage = boxEnvironmentVarDTO.getHomePage();
                        } catch (Exception e) {
                            homePage = EnvironmentUtil.getVietQrHomePage();
                        }
                    }
                    data.put("homePage", homePage);
                    ObjectMapper mapper = new ObjectMapper();
                    socketHandler.sendMessageToBoxId(boxId, data);

                    qrBoxSyncService.updateQrBoxSync(dto.getQrCertificate(), DateTimeUtil.getCurrentDateTimeUTC(),
                            true, dto.getTerminalName());
                    try {
                        SyncTidInternalDTO dto1 = new SyncTidInternalDTO();
                        dto1.setNotificationType(NotificationUtil.getNotiConnectQrSuccess());
                        dto1.setBankAccount(dto.getBankAccount());
                        dto1.setBankShortName(accountBankInfoResById.getBankShortName());
                        dto1.setUserBankName(accountBankInfoResById.getUserBankName());
                        dto1.setQrCode(response.getQrCode());
                        dto1.setTerminalCode(boxCode);
                        dto1.setTerminalName(dto.getTerminalName() != null ? dto.getTerminalName() : "");
                        dto1.setBankCode(accountBankInfoResById.getBankCode());
                        dto1.setImgBank("");
                        dto1.setHomePage(homePage);
                        mqttMessagingService
                                .sendMessageToBoxId(boxId, mapper.writeValueAsString(dto1));
                    } catch (Exception e) {}

                    httpStatus = HttpStatus.OK;
                } else {
                    result = new ResponseMessageDTO("FAILED", "E46");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("/test")
    public String getTerminalInternalInfoHehe(@RequestParam String boxCode) {
        return BoxTerminalRefIdUtil.encryptQrBoxId(boxCode);
    }

    @GetMapping("/test2")
    public String getTerminalInternalInfoHehee(@RequestParam String boxCode) {
        return BoxTerminalRefIdUtil.decryptBoxId(boxCode);
    }

    @GetMapping("tid/info/{boxCode}")
    public ResponseEntity<Object> getTerminalInternalInfo(@PathVariable String boxCode) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            ITerminalInternalDTO dto = terminalBankReceiveService.getTerminalInternalDTOByMachineCode(boxCode);
            if (dto != null) {
                MachineDetailResponseDTO responseDTO = new MachineDetailResponseDTO();
                responseDTO.setBankAccount(dto.getBankAccount());
                responseDTO.setBankShortName(dto.getBankShortName());
                responseDTO.setBoxAddress(dto.getMachineAddress());
                responseDTO.setBoxCode(dto.getMachineCode());
                responseDTO.setTerminalSubCode(dto.getTerminalCode());
                responseDTO.setBoxId(BoxTerminalRefIdUtil.encryptQrBoxId(dto.getMachineId()));
                responseDTO.setMachineId(dto.getMachineId());
                responseDTO.setQrCode(dto.getQrCode());
                responseDTO.setUserBankName(dto.getUserBankName());
                responseDTO.setBankCode(dto.getBankCode());
                result = responseDTO;
                httpStatus = HttpStatus.OK;
            } else {
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.OK;
            }
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping(value = "tid/sync-box")
    public ResponseEntity<Object> getSyncQRBox(@Valid @RequestBody SyncBoxDTO dto) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            String macAddr = dto.getMacAddr().replaceAll("\\:", "");
            dto.setMacAddr(macAddr);
            macAddr = dto.getMacAddr().replaceAll("\\.", "");
            String qrBoxCode = getRandomNumberUniqueQRBox();
            String certificate = EnvironmentUtil.getVietQrBoxInteralPrefix() + BoxTerminalRefIdUtil.encryptQrBoxId(qrBoxCode + macAddr);
            String boxId = BoxTerminalRefIdUtil.encryptQrBoxId(qrBoxCode);
            QrBoxSyncEntity entity = qrBoxSyncService.getByMacAddress(macAddr);
            if (entity != null) {
                boxId = BoxTerminalRefIdUtil.encryptQrBoxId(entity.getQrBoxCode());
            } else {
                entity = new QrBoxSyncEntity();
                entity.setId(UUID.randomUUID().toString());
                entity.setTimeCreated(DateTimeUtil.getCurrentDateTimeUTC());
                entity.setTimeSync(0);
                entity.setQrBoxCode(qrBoxCode);
                entity.setCertificate(certificate);
                entity.setMacAddress(macAddr);
                entity.setIsActive(false);
                entity.setQrName("");
                entity.setLastChecked(0);
                entity.setStatus(0);
            }
            qrBoxSyncService.insert(entity);
            result = new SyncBoxQrDTO(entity.getCertificate(), boxId);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping(value = "tid/cancel-qr")
    public ResponseEntity<Object> cancelDynamicQr(@Valid @RequestBody CancelTransactionQrDTO dto) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            Map<String, String> data = new HashMap<>();
            TransactionReceiveEntity entity = transactionReceiveService
                    .getTransactionReceiveById(dto.getTransactionReceiveId());
            TerminalBankReceiveEntity terminalBankReceiveEntity = terminalBankReceiveService
                    .getTerminalBankReceiveEntityByTerminalCode(entity.getTerminalCode());
            String boxId = BoxTerminalRefIdUtil.encryptQrBoxId(terminalBankReceiveEntity.getRawTerminalCode());
            data.put("notificationType", NotificationUtil.getNotiTypeCancelTransaction());
            data.put("transactionReceiveId", entity.getId());
            data.put("bankAccount", entity.getBankAccount());
            data.put("amount", entity.getAmount() + "");
            data.put("content", entity.getContent());
            data.put("qrCode", entity.getQrCode());
            data.put("qrType", "0");
            data.put("boxId", boxId);
            data.put("boxCode", terminalBankReceiveEntity.getRawTerminalCode());
            socketHandler.sendMessageToBoxId(boxId, data);
            try {
                ObjectMapper mapper = new ObjectMapper();
                CancelQRDTO cancelQRDTO = new CancelQRDTO();
                cancelQRDTO.setNotificationType(NotificationUtil.getNotiTypeCancelTransaction());
                cancelQRDTO.setTransactionReceiveId(entity.getId());
                mqttMessagingService
                        .sendMessageToBoxId(boxId, mapper.writeValueAsString(cancelQRDTO));
            } catch (Exception e) {}
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping(value = "tid/box-list/{bankId}")
    public ResponseEntity<Object> getQrBoxList(@PathVariable String bankId) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            result = terminalBankService.getQrBoxListByBankId(bankId);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping(value = "tid/tid-box/{bankId}")
    public ResponseEntity<Object> getQrBoxForActive(
            @PathVariable String bankId,
            @RequestParam String userId
    ) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            result = terminalBankService.getQrBoxDynamicQrByBankId(bankId);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("tid/active-box")
    public ResponseEntity<Object> activeQrBoxForUser(@Valid @RequestBody ActiveQrBoxDTO dto) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            String terminalCode = "";
            terminalCode = getRandomUniqueCodeInTerminalCode();
            IAccountBankReceiveDTO accountBankInfoResById
                    = accountBankReceiveService.getAccountBankInfoResById(dto.getBankId());
            String boxCode = qrBoxSyncService.getByQrCertificate(dto.getQrCertificate());
            TerminalEntity terminalEntity = terminalService.getTerminalByTerminalId(dto.getTerminalId());
            if (accountBankInfoResById != null && boxCode != null && !boxCode.isEmpty()) {
                if (accountBankInfoResById.getIsAuthenticated()) {
                    TerminalBankReceiveEntity terminalBankReceiveEntity = terminalBankReceiveService
                            .getTerminalBankReceiveByRawTerminalCode(boxCode);
                    if (terminalBankReceiveEntity == null) {
                        terminalBankReceiveEntity = new TerminalBankReceiveEntity();
                        terminalBankReceiveEntity.setId(UUID.randomUUID().toString());
                    }
                    terminalBankReceiveEntity.setTerminalId(dto.getTerminalId());
                    if (terminalEntity != null) {
                        terminalBankReceiveEntity.setSubTerminalAddress(terminalEntity.getAddress());
                    } else {
                        terminalBankReceiveEntity.setSubTerminalAddress("");
                    }
                    terminalBankReceiveEntity.setBankId(accountBankInfoResById.getBankId());
                    terminalBankReceiveEntity.setRawTerminalCode(boxCode);
                    terminalBankReceiveEntity.setTerminalCode(terminalCode);
                    terminalBankReceiveEntity.setTypeOfQR(2);
                    String qrCode = "";
                    switch (accountBankInfoResById.getBankCode()) {
                        case "MB":
                            if (accountBankInfoResById.getIsMmsActive()) {
                                TerminalBankEntity terminalBankEntity =
                                        terminalBankService.getTerminalBankByBankAccount(accountBankInfoResById.getBankAccount());
                                if (terminalBankEntity != null) {
                                    String qr = MBVietQRUtil.generateStaticVietQRMMS(
                                            new VietQRStaticMMSRequestDTO(MBTokenUtil.getMBBankToken().getAccess_token(),
                                                    terminalBankEntity.getTerminalId(), terminalCode));
                                    terminalBankReceiveEntity.setData2(qr);
                                    qrCode = qr;
                                    String traceTransfer = MBVietQRUtil.getTraceTransfer(qr);
                                    terminalBankReceiveEntity.setTraceTransfer(traceTransfer);
                                    terminalBankReceiveEntity.setData1("");
                                } else {
                                    logger.error("TerminalController: insertTerminal: terminalBankEntity is null or bankCode is not MB");
                                }
                            } else {
                                // luồng thuong
                                String qrCodeContent = "SQR" + terminalCode;
                                String bankAccount = accountBankInfoResById.getBankAccount();
                                String caiValue = accountBankReceiveService.getCaiValueByBankId(accountBankInfoResById.getBankId());
                                VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO(caiValue, "", qrCodeContent, bankAccount);
                                String qr = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);
                                terminalBankReceiveEntity.setData1(qr);
                                qrCode = qr;
                                terminalBankReceiveEntity.setData2("");
                                terminalBankReceiveEntity.setTraceTransfer("");
                            }
                            break;
                        case "BIDV":
                            AccountBankReceiveEntity accountBankEntity = accountBankReceiveService
                                    .getAccountBankById(accountBankInfoResById.getBankId());
                            String qr = "";
                            String billId = getRandomBillId();
                            VietQRCreateDTO vietQRCreateDTO = new VietQRCreateDTO();
                            vietQRCreateDTO.setBankId(accountBankInfoResById.getBankId());
                            vietQRCreateDTO.setAmount("0");
                            vietQRCreateDTO.setContent(billId);
                            vietQRCreateDTO.setUserId(accountBankEntity.getUserId());
                            vietQRCreateDTO.setTerminalCode(terminalCode);

                            ResponseMessageDTO responseMessageDTO =
                                    insertNewCustomerInvoiceTransBIDV(vietQRCreateDTO, accountBankInfoResById, billId);
                            if ("SUCCESS".equals(responseMessageDTO.getStatus())) {
                                VietQRVaRequestDTO vietQRVaRequestDTO = new VietQRVaRequestDTO();
                                vietQRVaRequestDTO.setAmount("0");
                                vietQRVaRequestDTO.setBillId(billId);
                                vietQRVaRequestDTO.setUserBankName(accountBankInfoResById.getUserBankName());
                                vietQRVaRequestDTO.setDescription(StringUtil.getValueNullChecker(billId));
                                ResponseMessageDTO generateVaInvoiceVietQR = CustomerVaUtil
                                        .generateVaInvoiceVietQR(vietQRVaRequestDTO, accountBankEntity.getCustomerId());
                                if ("SUCCESS".equals(generateVaInvoiceVietQR.getStatus())) {
                                    qr = generateVaInvoiceVietQR.getMessage();
                                    terminalBankReceiveEntity.setData1(qr);
                                    qrCode = qr;
                                    terminalBankReceiveEntity.setData2("");
                                    terminalBankReceiveEntity.setTraceTransfer("");
                                }
                            }
                            break;
                    }

                    terminalBankReceiveService.insert(terminalBankReceiveEntity);
                    String terminalName = "";
                    String boxAddress = "";
                    if (terminalEntity != null) {
                        terminalName = terminalEntity.getName() != null ? terminalEntity.getName() : "";
                        boxAddress = terminalEntity.getAddress() != null ? terminalEntity.getAddress() : "";
                    }
                    BoxQrMachineResponseDTO response = new BoxQrMachineResponseDTO();
                    String boxId = BoxTerminalRefIdUtil
                            .encryptQrBoxId(terminalBankReceiveEntity.getRawTerminalCode());
                    response.setBoxId(boxId);
                    response.setBankAccount(accountBankInfoResById.getBankAccount());
                    response.setBankCode(accountBankInfoResById.getBankCode());
                    response.setQrCode(qrCode);
                    response.setBoxCode(boxCode);
                    response.setSubTerminalCode(terminalCode);
                    response.setSubTerminalAddress(boxAddress);
                    result = response;

                    Map<String, String> data = new HashMap<>();
                    data.put("notificationType", NotificationUtil.getNotiConnectQrSuccess());
                    data.put("bankAccount", accountBankInfoResById.getBankAccount());
                    data.put("bankShortName", accountBankInfoResById.getBankAccount());
                    data.put("userBankName", accountBankInfoResById.getUserBankName());
                    data.put("qrCode", response.getQrCode());
                    data.put("machineId", terminalBankReceiveEntity.getId());
                    data.put("terminalCode", boxCode);
                    data.put("terminalName", terminalName);
                    data.put("boxId", boxId);
                    data.put("boxAddress", boxAddress);
                    data.put("boxCode", boxCode);
                    data.put("bankCode", accountBankInfoResById.getBankCode());
                    String homePage = EnvironmentUtil.getVietQrHomePage();
                    BoxEnvironmentResDTO boxEnvironmentResDTO = systemSettingService.getSystemSettingBoxEnv();
                    if (boxEnvironmentResDTO != null) {
                        try {
                            ObjectMapper mapper = new ObjectMapper();
                            BoxEnvironmentVarDTO boxEnvironmentVarDTO = mapper.readValue(boxEnvironmentResDTO.getBoxEnv(), BoxEnvironmentVarDTO.class);
                            homePage = boxEnvironmentVarDTO.getHomePage();
                        } catch (Exception e) {
                            homePage = EnvironmentUtil.getVietQrHomePage();
                        }
                    }
                    data.put("homePage", homePage);
                    socketHandler.sendMessageToBoxId(boxId, data);
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        SyncTidInternalDTO dto1 = new SyncTidInternalDTO();
                        dto1.setNotificationType(NotificationUtil.getNotiConnectQrSuccess());
                        dto1.setBankAccount(accountBankInfoResById.getBankAccount());
                        dto1.setBankShortName(accountBankInfoResById.getBankShortName());
                        dto1.setUserBankName(accountBankInfoResById.getUserBankName());
                        dto1.setQrCode(response.getQrCode());
                        dto1.setTerminalCode(boxCode);
                        dto1.setTerminalName(terminalName);
                        dto1.setBankCode(accountBankInfoResById.getBankCode());
                        dto1.setImgBank("");
                        dto1.setHomePage(homePage);
                        mqttMessagingService
                                .sendMessageToBoxId(boxId, mapper.writeValueAsString(dto1));
                    } catch (Exception e) {}
                    qrBoxSyncService.updateQrBoxSync(dto.getQrCertificate(), DateTimeUtil.getCurrentDateTimeUTC(),
                            true, terminalName);
                    httpStatus = HttpStatus.OK;
                } else {
                    result = new ResponseMessageDTO("FAILED", "E46");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("ERROR: activeQrBoxForUser " + e.getMessage() + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    private ResponseMessageDTO insertNewCustomerInvoiceTransBIDV(VietQRCreateDTO dto,
                                                                 IAccountBankReceiveDTO accountBankEntity, String billId) {
        ResponseMessageDTO responseMessageDTO = new ResponseMessageDTO();
        logger.info("QR generate - start insertNewCustomerInvoiceTransBIDV at: " + DateTimeUtil.getCurrentDateTimeUTC());
        try {
            long amount = 0;
            if (Objects.nonNull(accountBankEntity) && !StringUtil.isNullOrEmpty(billId)) {
                AccountBankReceiveEntity accountBankReceiveEntity = accountBankReceiveService.getAccountBankById(accountBankEntity.getBankId());
                if (!StringUtil.isNullOrEmpty(accountBankReceiveEntity.getCustomerId())) {
                    CustomerInvoiceEntity entity = new CustomerInvoiceEntity();
                    entity.setId(UUID.randomUUID().toString());
                    entity.setCustomerId(accountBankReceiveEntity.getCustomerId());
                    try {
                        amount = Long.parseLong(dto.getAmount());
                    } catch (Exception e) {
                        logger.error("VietQRController: ERROR: insertNewCustomerInvoiceTransBIDV: " + e.getMessage());
                    }
                    entity.setAmount(amount);
                    entity.setBillId(billId);
                    entity.setStatus(0);
                    entity.setType(1);
                    entity.setName(dto.getTerminalCode());
                    entity.setTimeCreated(DateTimeUtil.getCurrentDateTimeUTC());
                    entity.setTimePaid(0L);
                    entity.setInquire(0);
                    entity.setQrType(2);
                    customerInvoiceService.insert(entity);
                    responseMessageDTO = new ResponseMessageDTO("SUCCESS", "");
                } else {
                    responseMessageDTO = new ResponseMessageDTO("FAILED", "");
                }
            } else {
                responseMessageDTO = new ResponseMessageDTO("FAILED", "");
            }
        } catch (Exception e) {
            logger.error("Error at insertNewCustomerInvoiceTransBIDV: " + e.toString());
        } finally {
            logger.info("QR generate - end insertNewCustomerInvoiceTransBIDV at: " + DateTimeUtil.getCurrentDateTimeUTC());
        }
        return responseMessageDTO;
    }

    private String getRandomBillId() {
        String result = "";
        try {
            result = EnvironmentUtil.getPrefixBidvBillIdCommon() + DateTimeUtil.getCurrentWeekYear() +
                    StringUtil.convertToHexadecimal(DateTimeUtil.getMinusCurrentDate()) + RandomCodeUtil.generateRandomId(4);
        } catch (Exception e) {
            logger.error("getRandomBillId: ERROR: " + e.getMessage() + " at: " + System.currentTimeMillis());
        }
        return result;
    }

    @PostMapping(value = "tid/dynamic-qr")
    public ResponseEntity<Object> generateDynamicQr(@Valid @RequestBody VietQrDynamicQrDTO dto) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            UUID transactionUUID = UUID.randomUUID();
            String boxId = BoxTerminalRefIdUtil.encryptQrBoxId(dto.getBoxCode());
            String orderId = "VVB" + RandomCodeUtil.generateRandomId(8);
            String qr = "";
            String qrMMS = "";

            boolean checkMMS = false;
            String transType = "C";
            if (dto.getTransType() == null) {
                transType = "C";
            } else {
                transType = dto.getTransType().trim();
            }
            String checkExistedMMSBank = accountBankReceiveService.checkMMSBankAccount(dto.getBankAccount());
            if (checkExistedMMSBank != null && !checkExistedMMSBank.trim().isEmpty() && transType.equals("C")) {
                checkMMS = true;
            }

            if (!checkMMS) {
                // Luồng 1
                UUID transcationUUID = UUID.randomUUID();
                String traceId = "VQR" + RandomCodeUtil.generateRandomUUID();
                String bankTypeId = "";
                if (dto.getTransType() == null || dto.getTransType().trim().toUpperCase().equals("C")) {
                    bankTypeId = bankTypeService.getBankTypeIdByBankCode(dto.getBankCode());
                }
                VietQRDTO vietQRDTO = new VietQRDTO();
                try {
                    if (dto.getContent().length() <= 50) {
                        // check if generate qr with transtype = D or C
                        // if D => generate with customer information
                        // if C => do normal
                        // find bankTypeId by bankcode
                        if (bankTypeId != null && !bankTypeId.isEmpty()) {
                            // find bank by bankAccount and banktypeId

                            AccountBankReceiveEntity accountBankEntity = null;
                            if (dto.getTransType() == null || dto.getTransType().trim().toUpperCase().equals("C")) {
                                accountBankEntity = accountBankReceiveService
                                        .getAccountBankByBankAccountAndBankTypeId(dto.getBankAccount(), bankTypeId);
                            }
                            if (accountBankEntity != null) {
                                // get cai value
                                BankTypeEntity bankTypeEntity = bankTypeService.getBankTypeById(bankTypeId);
                                String caiValue = caiBankService.getCaiValue(bankTypeId);
                                String content = "";
                                content = traceId + " " + dto.getContent();
                                // generate VietQRGenerateDTO
                                VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
                                vietQRGenerateDTO.setCaiValue(caiValue);
                                vietQRGenerateDTO.setAmount(dto.getAmount() + "");
                                vietQRGenerateDTO.setContent(content);
                                vietQRGenerateDTO.setBankAccount(accountBankEntity.getBankAccount());
                                String qrCode = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);
                                qr = qrCode;
                                // generate VietQRDTO
                                vietQRDTO.setBankCode(bankTypeEntity.getBankCode());
                                vietQRDTO.setBankName(bankTypeEntity.getBankName());
                                vietQRDTO.setBankAccount(accountBankEntity.getBankAccount());
                                vietQRDTO.setUserBankName(accountBankEntity.getBankAccountName().toUpperCase());
                                vietQRDTO.setAmount(dto.getAmount() + "");
                                vietQRDTO.setContent(content);
                                vietQRDTO.setQrCode(qrCode);
                                vietQRDTO.setImgId(bankTypeEntity.getImgId());
                                vietQRDTO.setExisting(1);
                                vietQRDTO.setTransactionId(transactionUUID.toString());
                                vietQRDTO.setTerminalCode(dto.getTerminalCode());
                                String refId = TransactionRefIdUtil.encryptTransactionId(transactionUUID.toString());
                                String qrLink = EnvironmentUtil.getQRLink() + refId;
                                vietQRDTO.setTransactionRefId(refId);
                                vietQRDTO.setQrLink(qrLink);
                                //
                                result = vietQRDTO;
                                httpStatus = HttpStatus.OK;
                            } else {
                                String bankAccount = "";
                                String userBankName = "";
                                String content = "";
                                if (dto.getTransType() == null || dto.getTransType().trim().toUpperCase().equals("C")) {
                                    bankAccount = dto.getBankAccount();
                                    userBankName = dto.getUserBankName().trim().toUpperCase();
                                }
                                // get cai value
                                BankTypeEntity bankTypeEntity = bankTypeService.getBankTypeById(bankTypeId);
                                String caiValue = caiBankService.getCaiValue(bankTypeId);
                                // generate VietQRGenerateDTO
                                VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
                                vietQRGenerateDTO.setCaiValue(caiValue);
                                vietQRGenerateDTO.setAmount(dto.getAmount() + "");
                                content = traceId + " " + dto.getContent();
                                vietQRGenerateDTO.setContent(content);
                                vietQRGenerateDTO.setBankAccount(bankAccount);
                                String qrCode = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);
                                qr = qrCode;
                                //
                                // generate VietQRDTO
                                vietQRDTO.setBankCode(bankTypeEntity.getBankCode());
                                vietQRDTO.setBankName(bankTypeEntity.getBankName());
                                vietQRDTO.setBankAccount(bankAccount);
                                vietQRDTO.setUserBankName(userBankName);
                                vietQRDTO.setAmount(dto.getAmount() + "");
                                vietQRDTO.setContent(content);
                                vietQRDTO.setQrCode(qrCode);
                                vietQRDTO.setImgId(bankTypeEntity.getImgId());
                                vietQRDTO.setExisting(0);
                                result = vietQRDTO;
                                httpStatus = HttpStatus.OK;
                            }
                        } else {
                            result = new ResponseMessageDTO("FAILED", "E24");
                            httpStatus = HttpStatus.BAD_REQUEST;
                        }
                    } else {
                        result = new ResponseMessageDTO("FAILED", "E26");
                        httpStatus = HttpStatus.BAD_REQUEST;
                    }
                    return new ResponseEntity<>(result, httpStatus);
                    //
                } catch (Exception e) {
                    logger.error(e.toString());
                    System.out.println(e.toString());
                    result = new ResponseMessageDTO("FAILED", "Unexpected Error");
                    httpStatus = HttpStatus.BAD_REQUEST;
                    return new ResponseEntity<>(result, httpStatus);
                } finally {
                    // insert new transaction with orderId and sign
                    if (dto.getTransType() != null && dto.getTransType().trim().equalsIgnoreCase("D")) {
                        bankTypeId = bankTypeService.getBankTypeIdByBankCode(dto.getBankCode());
                    }
                    AccountBankReceiveEntity accountBankEntity = accountBankReceiveService
                            .getAccountBankByBankAccountAndBankTypeId(dto.getBankAccount(), bankTypeId);
                    if (accountBankEntity != null) {
                        VietQRCreateDTO vietQRCreateDTO = new VietQRCreateDTO();
                        vietQRCreateDTO.setBankId(accountBankEntity.getId());
                        vietQRCreateDTO.setAmount(dto.getAmount() + "");
                        vietQRCreateDTO.setContent(dto.getContent());
                        vietQRCreateDTO.setUserId(accountBankEntity.getUserId());
                        vietQRCreateDTO.setTerminalCode(dto.getTerminalCode());
                        //
                        if (dto.getTransType() != null && dto.getTransType().trim().equalsIgnoreCase("D")) {
                            vietQRCreateDTO.setTransType("D");
                            vietQRCreateDTO.setCustomerBankAccount("");
                            vietQRCreateDTO.setCustomerBankCode("");
                            vietQRCreateDTO.setCustomerName("");
                        } else {
                            vietQRCreateDTO.setTransType("C");
                        }
                        vietQRCreateDTO.setUrlLink("");
                        insertNewTransaction(transactionUUID, traceId, vietQRCreateDTO, dto.getOrderId(), vietQRDTO.getBankAccount(), vietQRDTO.getUserBankName(), qr,
                                boxId, dto.getBoxCode(), dto.getTerminalName());
                    }
                }
            } else {
                LocalDateTime requestLDT = LocalDateTime.now();
                long requestTime = requestLDT.toEpochSecond(ZoneOffset.UTC);
                logger.info("generateDynamicQr: start generate at: " + requestTime);
                String bankTypeMB = "aa4e489b-254e-4351-9cd4-f62e09c63ebc";
                AccountBankReceiveEntity accountBankEntity = null;
                String qrCode = "";
                try {
                    // 1. Validate input (amount, content, bankCode) => E34 if Invalid input data
                    if (checkRequestBodyFlow2(dto)) {
                        // 2. Find terminal bank by bank_account_raw_number
                        accountBankEntity = accountBankReceiveService
                                .getAccountBankByBankAccountAndBankTypeId(dto.getBankAccount(), bankTypeMB);
                        if (accountBankEntity != null) {
                            TerminalBankEntity terminalBankEntity = terminalBankService
                                    .getTerminalBankByBankAccount(dto.getBankAccount());
                            if (terminalBankEntity == null) {
                                // 3.A. If not found => E35 (terminal is not existed)
                                logger.error("generateVietQRMMS: ERROR: Bank account is not existed.");
                                result = new ResponseMessageDTO("FAILED", "E35");
                                httpStatus = HttpStatus.BAD_REQUEST;
                            } else {
                                // 3.B. If found => get bank token => create qr code
                                TokenProductBankDTO tokenBankDTO = MBTokenUtil.getMBBankToken();
                                if (tokenBankDTO != null) {
                                    String content = "";
                                    if (dto.getContent() != null && !dto.getContent().trim().isEmpty()) {
                                        content = dto.getContent();
                                    } else {
                                        String traceId = "VQR" + RandomCodeUtil.generateRandomUUID();
                                        content = traceId;
                                    }
                                    VietQRMMSRequestDTO requestDTO = new VietQRMMSRequestDTO();
                                    requestDTO.setToken(tokenBankDTO.getAccess_token());
                                    requestDTO.setTerminalId(terminalBankEntity.getTerminalId());
                                    requestDTO.setAmount(dto.getAmount() + "");
                                    requestDTO.setContent(content);
                                    requestDTO.setOrderId(orderId);
                                    qrCode = requestVietQRMMS(requestDTO);
                                    if (qrCode != null) {
                                        // VietQRMMSDTO vietQRMMSDTO = new VietQRMMSDTO(qrCode);
                                        qrMMS = qrCode;
                                        String bankTypeId = bankTypeService.getBankTypeIdByBankCode(dto.getBankCode());
                                        if (bankTypeId != null && !bankTypeId.trim().isEmpty()) {
                                            VietQRDTO vietQRDTO = new VietQRDTO();
                                            // get cai value
                                            BankTypeEntity bankTypeEntity = bankTypeService.getBankTypeById(bankTypeId);
                                            //
                                            String bankAccount = "";
                                            String userBankName = "";
                                            bankAccount = dto.getBankAccount();
                                            userBankName = dto.getUserBankName().trim().toUpperCase();
                                            // generate VietQRDTO
                                            vietQRDTO.setBankCode(bankTypeEntity.getBankCode());
                                            vietQRDTO.setBankName(bankTypeEntity.getBankName());
                                            vietQRDTO.setBankAccount(bankAccount);
                                            vietQRDTO.setUserBankName(userBankName);
                                            vietQRDTO.setAmount(dto.getAmount() + "");
                                            vietQRDTO.setContent(content);
                                            vietQRDTO.setQrCode(qrCode);
                                            vietQRDTO.setImgId(bankTypeEntity.getImgId());
                                            vietQRDTO.setExisting(0);
                                            vietQRDTO.setTransactionId("");
                                            vietQRDTO.setTerminalCode(dto.getTerminalCode());
                                            String refId = TransactionRefIdUtil
                                                    .encryptTransactionId(transactionUUID.toString());
                                            String qrLink = EnvironmentUtil.getQRLink() + refId;
                                            vietQRDTO.setTransactionRefId(refId);
                                            vietQRDTO.setQrLink(qrLink);
                                            result = vietQRDTO;
                                            httpStatus = HttpStatus.OK;
                                        } else {
                                            result = new ResponseMessageDTO("FAILED", "E24");
                                            httpStatus = HttpStatus.BAD_REQUEST;
                                        }
                                    } else {
                                        logger.error("generateVietQRMMS: ERROR: Invalid get QR Code");
                                        result = new ResponseMessageDTO("FAILED", "E05");
                                        httpStatus = HttpStatus.BAD_REQUEST;
                                    }
                                } else {
                                    logger.error("generateVietQRMMS: ERROR: Invalid get bank token");
                                    result = new ResponseMessageDTO("FAILED", "E05");
                                    httpStatus = HttpStatus.BAD_REQUEST;
                                }
                            }
                        } else {
                            logger.error("generateVietQRMMS: ERROR: bankAccount is not existed in system");
                            result = new ResponseMessageDTO("FAILED", "E36");
                            httpStatus = HttpStatus.BAD_REQUEST;

                        }
                    } else {
                        logger.error("generateVietQRMMS: ERROR: Invalid request body");
                        result = new ResponseMessageDTO("FAILED", "E34");
                        httpStatus = HttpStatus.BAD_REQUEST;
                    }
                    return new ResponseEntity<>(result, httpStatus);
                } catch (Exception e) {
                    logger.error("generateVietQRMMS: ERROR: " + e.toString());
                    result = new ResponseMessageDTO("FAILED", "E05");
                    httpStatus = HttpStatus.BAD_REQUEST;
                    return new ResponseEntity<>(result, httpStatus);
                } finally {
                    // 4. Insert transaction_receive
                    // (5. Insert notification)
                    if (accountBankEntity != null && qrCode != null && !qrCode.isEmpty()) {
                        String content = "";
                        if (dto.getContent() != null && !dto.getContent().trim().isEmpty()) {
                            content = dto.getContent();
                        } else {
                            String traceId = "VQR" + RandomCodeUtil.generateRandomUUID();
                            content = traceId;
                        }
                        LocalDateTime currentDateTime = LocalDateTime.now();
                        long time = currentDateTime.toEpochSecond(ZoneOffset.UTC);
                        VietQRMMSCreateDTO vietQRMMSCreateDTO = new VietQRMMSCreateDTO();
                        vietQRMMSCreateDTO.setBankAccount(dto.getBankAccount());
                        vietQRMMSCreateDTO.setBankCode(dto.getBankCode());
                        vietQRMMSCreateDTO.setAmount(dto.getAmount() + "");
                        vietQRMMSCreateDTO.setContent(content);
                        vietQRMMSCreateDTO.setOrderId(orderId);
                        vietQRMMSCreateDTO.setSign("");
                        vietQRMMSCreateDTO.setTerminalCode(dto.getTerminalCode());
                        vietQRMMSCreateDTO.setNote(dto.getNote());
                        vietQRMMSCreateDTO.setUrlLink("");
                        insertNewTransactionFlow2(qrMMS, transactionUUID.toString(), accountBankEntity, vietQRMMSCreateDTO,
                                time, dto.getBankAccount(), dto.getUserBankName(), qrMMS, BoxTerminalRefIdUtil.encryptQrBoxId(dto.getBoxCode()),
                                dto.getBoxCode(), dto.getTerminalName());
                    }
                }
            }
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @Async
    protected void insertNewTransactionFlow2(String qrCode, String transcationUUID,
                                             AccountBankReceiveEntity accountBankReceiveEntity,
                                             VietQRMMSCreateDTO dto,
                                             long time, String bankAccount, String userBankName, String qr,
                                             String boxId, String boxCode, String terminalName) {
        try {
            TransactionReceiveEntity transactionEntity = new TransactionReceiveEntity();
            transactionEntity.setId(transcationUUID);
            transactionEntity.setBankAccount(accountBankReceiveEntity.getBankAccount());
            transactionEntity.setBankId(accountBankReceiveEntity.getId());
            transactionEntity.setContent(dto.getContent());
            transactionEntity.setAmount(Long.parseLong(dto.getAmount()));
            transactionEntity.setTime(time);
            transactionEntity.setRefId("");
            transactionEntity.setType(0);
            transactionEntity.setStatus(0);
            transactionEntity.setTraceId("");
            transactionEntity.setTransType("C");
            transactionEntity.setReferenceNumber("");
            transactionEntity.setOrderId(dto.getOrderId());
            transactionEntity.setSign(dto.getSign());
            transactionEntity.setTimePaid(time);
            transactionEntity.setTerminalCode(dto.getTerminalCode() != null ? dto.getTerminalCode() : "");
            transactionEntity.setQrCode(qrCode);
            transactionEntity.setUserId(accountBankReceiveEntity.getUserId());
            transactionEntity.setNote(dto.getNote() != null ? dto.getNote() : "");
            transactionEntity.setTransStatus(0);
            transactionEntity.setUrlLink(dto.getUrlLink() != null ? dto.getUrlLink() : "");
            transactionReceiveService.insertTransactionReceive(transactionEntity);
            LocalDateTime endTime = LocalDateTime.now();
            long endTimeLong = endTime.toEpochSecond(ZoneOffset.UTC);
            logger.info("insertNewTransaction - end generateDynamicQr at: " + endTimeLong);

            Map<String, String> data = new HashMap<>();
            data.put("notificationType", NotificationUtil.getNotiSendDynamicQr());
            data.put("transactionReceiveId", transcationUUID);
            data.put("bankAccount", bankAccount);
            data.put("bankName", "Ngân hàng TMCP Quân đội");
            data.put("bankShortName", "MB Bank");
            data.put("userBankName", userBankName);
            data.put("note", "");
            data.put("content", dto.getContent());
            data.put("amount", StringUtil.formatNumberAsString(dto.getAmount()));
            data.put("imgId", "");
            data.put("qrCode", qr);
            data.put("qrType", "0");
            data.put("boxId", boxId);
            data.put("boxCode", boxCode);
            data.put("terminalCode", boxCode);
            data.put("terminalName", terminalName != null ? terminalName : "");
            socketHandler.sendMessageToBoxId(boxId, data);
            DynamicQRBoxDTO dynamicQRBoxDTO = new DynamicQRBoxDTO();
            dynamicQRBoxDTO.setNotificationType(NotificationUtil.getNotiSendDynamicQr());
            dynamicQRBoxDTO.setTransactionReceiveId(transcationUUID);
            dynamicQRBoxDTO.setBankAccount(bankAccount);
            dynamicQRBoxDTO.setBankShortName("MB Bank");
            dynamicQRBoxDTO.setUserBankName(userBankName);
            dynamicQRBoxDTO.setContent(dto.getContent());
            dynamicQRBoxDTO.setAmount(StringUtil.formatNumberAsString(dto.getAmount()));
            dynamicQRBoxDTO.setQrCode(qr);
            dynamicQRBoxDTO.setQrType(0 + "");
            ObjectMapper mapper = new ObjectMapper();
            mqttMessagingService
                    .sendMessageToBoxId(boxId, mapper.writeValueAsString(dynamicQRBoxDTO));
        } catch (Exception e) {
            logger.error("insertNewTransaction - generateDynamicQr: ERROR: " + e.toString());
        }
    }

    private boolean checkRequestBodyFlow2(VietQrDynamicQrDTO dto) {
        boolean result = false;
        try {
            // content up to 19
            // orderId up to 13
            String content = "";
            String orderId = "";
            if (dto.getContent() != null) {
                content = dto.getContent();
            }
            if (dto.getOrderId() != null) {
                orderId = dto.getOrderId();
            }
            if (content.length() <= 19
                    && orderId.length() <= 13
                    && dto.getAmount() != null && !dto.getBankAccount().trim().isEmpty()

                    && dto.getBankAccount() != null && !dto.getBankAccount().trim().isEmpty()
                    && dto.getBankCode() != null && dto.getBankCode().equals("MB")) {
                result = true;
            }
        } catch (Exception e) {
            logger.error("checkRequestBody: ERROR: " + e.toString());
        }
        return result;
    }

    private String requestVietQRMMS(VietQRMMSRequestDTO dto) {
        String result = null;
        LocalDateTime requestLDT = LocalDateTime.now();
        long requestTime = requestLDT.toEpochSecond(ZoneOffset.UTC);
        logger.info("requestVietQRMMS: start request QR to MB at: " + requestTime);
        try {
            UUID clientMessageId = UUID.randomUUID();
            Map<String, Object> data = new HashMap<>();
            data.put("terminalID", dto.getTerminalId());
            data.put("qrcodeType", 4);
            data.put("partnerType", 2);
            data.put("initMethod", 12);
            data.put("transactionAmount", dto.getAmount());
            data.put("billNumber", "");
            data.put("additionalAddress", 0);
            data.put("additionalMobile", 0);
            data.put("additionalEmail", 0);
            data.put("referenceLabelCode", dto.getOrderId());
            data.put("transactionPurpose", dto.getContent());
            UriComponents uriComponents = UriComponentsBuilder
                    .fromHttpUrl(EnvironmentUtil.getBankUrl()
                            + "ms/offus/public/payment-service/payment/v1.0/createqr")
                    .buildAndExpand(/* add url parameter here */);
            WebClient webClient = WebClient.builder()
                    .baseUrl(
                            EnvironmentUtil.getBankUrl()
                                    + "ms/offus/public/payment-service/payment/v1.0/createqr")
                    .build();
            Mono<ClientResponse> responseMono = webClient.post()
                    .uri(uriComponents.toUri())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("clientMessageId", clientMessageId.toString())
                    .header("secretKey", EnvironmentUtil.getSecretKeyAPI())
                    .header("username", EnvironmentUtil.getUsernameAPI())
                    .header("Authorization", "Bearer " + dto.getToken())
                    .body(BodyInserters.fromValue(data))
                    .exchange();
            ClientResponse response = responseMono.block();
            if (response.statusCode().is2xxSuccessful()) {
                String json = response.bodyToMono(String.class).block();
                logger.info("requestVietQRMMS: RESPONSE: " + json);
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(json);
                if (rootNode.get("data") != null) {
                    if (rootNode.get("data").get("qrcode") != null) {
                        result = rootNode.get("data").get("qrcode").asText();
                        logger.info("requestVietQRMMS: RESPONSE qrcode: " + result);
                    } else {
                        logger.info("requestVietQRMMS: RESPONSE qrcode is null");
                    }
                } else {
                    logger.info("requestVietQRMMS: RESPONSE data is null");
                }
            } else {
                String json = response.bodyToMono(String.class).block();
                logger.info("requestVietQRMMS: RESPONSE: " + response.statusCode().value() + " - " + json);
            }
        } catch (Exception e) {
            logger.error("requestVietQRMMS: ERROR: " + e.toString());
        } finally {
            LocalDateTime responseLDT = LocalDateTime.now();
            long responseTime = responseLDT.toEpochSecond(ZoneOffset.UTC);
            logger.info("requestVietQRMMS: response from MB at: " + responseTime);
        }
        return result;
    }

    @GetMapping("tid-internal/list")
    public ResponseEntity<Object> getTidInternalList(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam int type,
            @RequestParam String value) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            PageResDTO response = new PageResDTO();
            int offset = (page - 1) * size;
            int totalElement = 0;
            List<TidInternalDTO> data = new ArrayList<>();
            List<ITidInternalDTO> dtos = new ArrayList<>();
            switch (type) {
                case 0:
                    dtos = qrBoxSyncService.getQrBoxSyncByBankAccount(value, offset, size);
                    totalElement = qrBoxSyncService.countQrBoxSyncByBankAccount(value);
                    break;
                case 9:
                    dtos = qrBoxSyncService.getQrBoxSync(offset, size);
                    totalElement = qrBoxSyncService.countQrBoxSync();
                    break;
            }
            data = dtos.stream().map(item -> {
                TidInternalDTO dto = new TidInternalDTO();
                dto.setBoxId(StringUtil.getValueNotNull(item.getBoxId()));
                dto.setMacAddr(item.getMacAddr());
                dto.setBoxCode(item.getBoxCode());
                dto.setMerchantName(StringUtil.getValueNotNull(item.getMerchantName()));
                dto.setTerminalName(StringUtil.getValueNotNull(item.getTerminalName()));
                dto.setTerminalId(StringUtil.getValueNotNull(item.getTerminalId()));
                dto.setTerminalCode(StringUtil.getValueNotNull(item.getTerminalCode()));
                dto.setBankAccount(StringUtil.getValueNotNull(item.getBankAccount()));
                dto.setBankShortName(StringUtil.getValueNotNull(item.getBankShortName()));
                dto.setUserBankName(StringUtil.getValueNotNull(item.getUserBankName()));
                if (item.getMmsActive() == 1) {
                    dto.setFeePackage(EnvironmentUtil.getVietQrProPackage());
                } else {
                    dto.setFeePackage(EnvironmentUtil.getVietQrPlusPackage());
                }
                if (item.getBankAccount() == null || item.getBankAccount().trim().isEmpty()) {
                    dto.setFeePackage("");
                }
                dto.setBoxAddress(StringUtil.getValueNotNull(item.getBoxAddress()));
                dto.setCertificate(item.getCertificate());
                dto.setStatus(item.getStatus());
                dto.setLastChecked(StringUtil.getValueNullChecker(item.getLastChecked()));
                return dto;
            }).collect(Collectors.toList());
            PageDTO pageDTO = new PageDTO();
            pageDTO.setPage(page);
            pageDTO.setSize(size);
            pageDTO.setTotalElement(totalElement);
            pageDTO.setTotalPage(StringUtil.getTotalPage(totalElement, size));
            response.setMetadata(pageDTO);
            response.setData(data);
            result = response;
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @Async
    protected void insertNewTransaction(UUID transcationUUID, String traceId, VietQRCreateDTO dto,
                                        String orderId, String bankAccount, String userBankName, String qr, String boxId, String boxCode, String terminalName) {
        LocalDateTime startTime = LocalDateTime.now();
        long startTimeLong = startTime.toEpochSecond(ZoneOffset.UTC);
        try {
            NumberFormat nf = NumberFormat.getInstance(Locale.US);
            AccountBankReceiveEntity accountBankEntity = accountBankReceiveService.getAccountBankById(dto.getBankId());
            if (accountBankEntity != null) {
                // UUID transactionBranchUUID = UUID.randomUUID();
                LocalDateTime currentDateTime = LocalDateTime.now();
                TransactionReceiveEntity transactionEntity = new TransactionReceiveEntity();
                transactionEntity.setId(transcationUUID.toString());
                transactionEntity.setBankAccount(accountBankEntity.getBankAccount());
                transactionEntity.setBankId(dto.getBankId());
                transactionEntity.setContent(traceId + " " + dto.getContent());
                transactionEntity.setAmount(Long.parseLong(dto.getAmount()));
                transactionEntity.setTime(currentDateTime.toEpochSecond(ZoneOffset.UTC));
                transactionEntity.setRefId("");
                transactionEntity.setType(0);
                transactionEntity.setStatus(0);
                transactionEntity.setTraceId(traceId);
                transactionEntity.setTimePaid(0);
                transactionEntity.setTerminalCode(dto.getTerminalCode() != null ? dto.getTerminalCode() : "");
                transactionEntity.setQrCode(qr);
                transactionEntity.setUserId(accountBankEntity.getUserId());
                transactionEntity.setOrderId(orderId);
                transactionEntity.setNote(dto.getNote() != null ? dto.getNote() : "");
                transactionEntity.setTransStatus(0);
                transactionEntity.setUrlLink(dto.getUrlLink() != null ? dto.getUrlLink() : "");
                if (dto.getTransType() != null) {
                    transactionEntity.setTransType(dto.getTransType());
                } else {
                    transactionEntity.setTransType("C");
                }
                transactionEntity.setReferenceNumber("");
                transactionEntity.setOrderId(orderId);
                transactionEntity.setSign("");
                //
                if (dto.getTransType() != null && dto.getTransType().trim().toUpperCase().equals("D")) {
                    transactionEntity.setCustomerBankAccount(dto.getCustomerBankAccount());
                    transactionEntity.setCustomerBankCode(dto.getCustomerBankCode());
                    transactionEntity.setCustomerName(dto.getCustomerName());
                }
                transactionReceiveService.insertTransactionReceive(transactionEntity);
                LocalDateTime afterInsertTransactionTime = LocalDateTime.now();
                long afterInsertTransactionTimeLong = afterInsertTransactionTime.toEpochSecond(ZoneOffset.UTC);
                logger.info("QR generate - after insertTransactionReceive at: " + afterInsertTransactionTimeLong);
                LocalDateTime afterInsertNotificationTransaction = LocalDateTime.now();
                long afterInsertNotificationTransactionLong = afterInsertNotificationTransaction
                        .toEpochSecond(ZoneOffset.UTC);
                logger.info("QR generate - after InsertNotificationTransaction at: "
                        + afterInsertNotificationTransactionLong);

                Map<String, String> data = new HashMap<>();
                data.put("notificationType", NotificationUtil.getNotiSendDynamicQr());
                data.put("transactionReceiveId", transcationUUID.toString());
                data.put("bankAccount", bankAccount);
                data.put("bankName", "Ngân hàng TMCP Quân đội");
                data.put("bankShortName", "MB Bank");
                data.put("userBankName", userBankName);
                data.put("note", "");
                data.put("content", dto.getContent());
                data.put("amount", StringUtil.formatNumberAsString(dto.getAmount()));
                data.put("imgId", "");
                data.put("qrCode", qr);
                data.put("qrType", "0");
                data.put("boxId", boxId);
                data.put("boxCode", boxCode);
                data.put("terminalCode", boxCode);
                data.put("terminalName", terminalName != null ? terminalName : "");
                socketHandler.sendMessageToBoxId(boxId, data);
                DynamicQRBoxDTO dynamicQRBoxDTO = new DynamicQRBoxDTO();
                dynamicQRBoxDTO.setNotificationType(NotificationUtil.getNotiSendDynamicQr());
                dynamicQRBoxDTO.setTransactionReceiveId(transcationUUID.toString());
                dynamicQRBoxDTO.setBankAccount(bankAccount);
                dynamicQRBoxDTO.setBankShortName("MB Bank");
                dynamicQRBoxDTO.setUserBankName(userBankName);
                dynamicQRBoxDTO.setContent(dto.getContent());
                dynamicQRBoxDTO.setAmount(StringUtil.formatNumberAsString(dto.getAmount()));
                dynamicQRBoxDTO.setQrCode(qr);
                dynamicQRBoxDTO.setQrType(0 + "");
                ObjectMapper mapper = new ObjectMapper();
                mqttMessagingService
                        .sendMessageToBoxId(boxId, mapper.writeValueAsString(dynamicQRBoxDTO));
            }
            // }

        } catch (Exception e) {
            logger.error("Error at insertNewTransaction: " + e.toString());
        } finally {
            LocalDateTime endTime = LocalDateTime.now();
            long endTimeLong = endTime.toEpochSecond(ZoneOffset.UTC);
            logger.info("QR generate - end insertNewTransaction at: " + endTimeLong);
        }

    }

    private String getRawTerminalCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            int randomIndex = random.nextInt(NUMBERS.length());
            code.append(NUMBERS.charAt(randomIndex));
        }
        return code.toString();
    }

    private String getRandomNumberUniqueQRBox() {
        String result = "";
        String checkExistedCode = "";
        String code = "";
        try {
            do {
                code = "VVB" + getRawTerminalCode();
                checkExistedCode = qrBoxSyncService.checkExistQRBoxCode(code);
                if (checkExistedCode == null || checkExistedCode.trim().isEmpty()) {
                    checkExistedCode = qrBoxSyncService.checkExistQRBoxCode(code);
                }
            } while (!StringUtil.isNullOrEmpty(checkExistedCode));
            result = code;
        } catch (Exception ignored) {
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
                checkExistedCode = terminalBankReceiveService.checkExistedTerminalCode(code);
                if (checkExistedCode == null || checkExistedCode.trim().isEmpty()) {
                    checkExistedCode = terminalService.checkExistedTerminal(code);
                }
            } while (!StringUtil.isNullOrEmpty(checkExistedCode));
            result = code;
        } catch (Exception ignored) {
        }
        return result;
    }

    private String getTerminalCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder(TidQrInternalController.TERMINAL_CODE_LENGTH);
        for (int i = 0; i < TidQrInternalController.TERMINAL_CODE_LENGTH; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(randomIndex));
        }
        return code.toString();
    }

    private BoxEnvironmentVarDTO getBoxEnv(String data) {
        BoxEnvironmentVarDTO result = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            result = mapper.readValue(data, BoxEnvironmentVarDTO.class);
        } catch (Exception e) {
            result = new BoxEnvironmentVarDTO();
        }
        return result;
    }

}
