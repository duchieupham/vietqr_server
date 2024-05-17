package com.vietqr.org.controller;

import com.vietqr.org.dto.*;
import com.vietqr.org.dto.mb.VietQRStaticMMSRequestDTO;
import com.vietqr.org.entity.*;
import com.vietqr.org.service.*;
import com.vietqr.org.util.*;
import com.vietqr.org.util.bank.mb.MBTokenUtil;
import com.vietqr.org.util.bank.mb.MBVietQRUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

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
    private SocketHandler socketHandler;

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
            if (accountBankInfoResById != null) {
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
                    data.put("terminalCode", terminalBankReceiveEntity.getTerminalCode());
                    data.put("terminalName", dto.getTerminalName() != null ? dto.getTerminalName() : "");
                    data.put("boxId", boxId);
                    data.put("boxAddress", dto.getBoxAddress());
                    data.put("boxCode", boxCode);
                    data.put("bankCode", accountBankInfoResById.getBankCode());
                    socketHandler.sendMessageToBoxId(boxId, data);
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
        return BoxTerminalRefIdUtil.decryptTransactionId(boxCode);
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
            String certificate = BoxTerminalRefIdUtil.encryptQrBoxId(qrBoxCode + macAddr);
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
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping(value = "tid/dynamic-qr")
    public ResponseEntity<Object> generateDynamicQr(@Valid @RequestBody VietQrDynamicQrDTO dto) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            UUID transactionUUID = UUID.randomUUID();
            String boxId = BoxTerminalRefIdUtil.encryptQrBoxId(dto.getBoxCode());

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

            if (checkMMS == false) {
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
                    if (dto.getTransType() != null && dto.getTransType().trim().toUpperCase().equals("D")) {
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
                        if (dto.getTransType() != null && dto.getTransType().trim().toUpperCase().equals("D")) {
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
            }
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
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
                if (item.getStatus()) {
                    dto.setStatus(1);
                } else {
                    dto.setStatus(0);
                }
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

    @PostMapping("tid/socket-test")
    public ResponseEntity<Object> getTerminalInternalInfo(@Valid @RequestBody TransactionDataSuccessDTO dto) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            Map<String, String> data = new HashMap<>();
            data.put("bankAccount", dto.getBankAccount());
            data.put("traceId", "");
            data.put("bankCode", "MB");
            data.put("amount", dto.getAmount() + "");
            data.put("orderId", "");
            data.put("bankName", "Ngân hàng TMCP Quân đội");
            data.put("notificationType", "N05");
            data.put("type", "2");
            data.put("content", "SQR" + dto.getTerminalCode() + ".test Ma giao dich  Trace444952 Trace 444952");
            data.put("terminalName", "");
            data.put("bankId", "9dea5eac-6fc9-4b92-a6ec-22a7f3fa8178");
            data.put("timePaid", "1714843161");
            data.put("transType", "C");
            data.put("rawTerminalCode", "");
            data.put("referenceNumber", "FT2439624023480075");
            data.put("notificationId", "0faa8b54-03bb-4d8a-b320-d91965a9dabc");
            data.put("terminalCode", "");
            data.put("time", "1714843161");
            data.put("refId", "FT24396240234875");
            if (dto.getTransactionReceiveId() != null || "".equals(dto.getTransactionReceiveId())) {
                data.put("transactionReceiveId", dto.getTransactionReceiveId());
            } else {
                data.put("transactionReceiveId", "e509d941-8f67-4e37-9a22-d6ea94ae4a77");
            }
            data.put("urlLink", "");
            data.put("status", "1");
            socketHandler.sendMessageToBoxId(dto.getBoxId(), data);
            result = new ResponseMessageDTO("SUCCESS", "");
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
                transactionEntity.setTerminalCode(dto.getTerminalCode());
                transactionEntity.setQrCode(qr);
                transactionEntity.setUserId(accountBankEntity.getUserId());
                transactionEntity.setOrderId(orderId);
                transactionEntity.setNote(dto.getNote() != null ? dto.getNote() : "");
                transactionEntity.setTransStatus(0);
                transactionEntity.setUrlLink(dto.getUrlLink());
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
                data.put("amount", dto.getAmount());
                data.put("imgId", "");
                data.put("qrCode", qr);
                data.put("qrType", "0");
                data.put("boxId", boxId);
                data.put("boxCode", boxCode);
                data.put("terminalCode", dto.getTerminalCode() != null ? dto.getTerminalCode() : "");
                data.put("terminalName", terminalName != null ? terminalName : "");
                socketHandler.sendMessageToBoxId(boxId, data);
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
                code = getTerminalCode(TERMINAL_CODE_LENGTH);
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

    private String getTerminalCode(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(randomIndex));
        }
        return code.toString();
    }

}
