package com.vietqr.org.service.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.vietqr.org.controller.AccountController;
import com.vietqr.org.dto.*;
import com.vietqr.org.dto.bidv.VietQRVaRequestDTO;
import com.vietqr.org.entity.*;
import com.vietqr.org.entity.bidv.CustomerInvoiceEntity;
import com.vietqr.org.service.*;
import com.vietqr.org.service.bidv.CustomerInvoiceService;
import com.vietqr.org.util.*;
import com.vietqr.org.util.bank.bidv.CustomerVaUtil;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static com.vietqr.org.util.RandomCodeUtil.getRandomBillId;

@Component
public class VietQRServer {
    private static final Logger logger = Logger.getLogger(VietQRServer.class);
    private static final String BROKER = "tcp://broker.hivemq.com:1883";
    private static final String CLIENT_ID = "VietQRServer";
    private static final String RESPONSE_TOPIC_BASE = "vietqr/response";

    private MqttClient client;

    @Autowired
    AccountBankReceiveService accountBankReceiveService;
    @Autowired
    BankTypeService bankTypeService;
    @Autowired
    CaiBankService caiBankService;
    @Autowired
    TransactionReceiveService transactionReceiveService;

    @Autowired
    CustomerInvoiceService customerInvoiceService;

    @Autowired
    NotificationService notificationService;

    public VietQRServer() throws MqttException {
        MemoryPersistence persistence = new MemoryPersistence();
        client = new MqttClient(BROKER, CLIENT_ID, persistence);
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        client.connect(connOpts);

        client.subscribe("vietqr/request/#", new IMqttMessageListener() {
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String payload = new String(message.getPayload());
                System.out.println("Received request: " + payload);
                ObjectMapper mapper = new ObjectMapper();
                VietQRCreateCustomerDTO dto = mapper.readValue(payload, VietQRCreateCustomerDTO.class);

                // Xử lý yêu cầu và tạo phản hồi
                VietQRDTO response = (VietQRDTO) generateQRCustomer(dto);
                String responsePayload = mapper.writeValueAsString(response);

                // Lấy thông tin trạm và trụ từ DTO
                String tramId = dto.getAdditionalData().get(0).getAdditionalData1(); // Tram ID từ additionalData1
                String truId = dto.getTerminalCode(); // Terminal ID là mã của trụ xăng

                // Xác định topic phản hồi
                String responseTopic = RESPONSE_TOPIC_BASE + "/" + tramId + "/" + truId;

                // Gửi phản hồi lên đúng topic
                MqttMessage responseMessage = new MqttMessage(responsePayload.getBytes());
                responseMessage.setQos(2);
                client.publish(responseTopic, responseMessage);
                System.out.println("Response sent to topic: " + responseTopic + " Payload: " + responsePayload);
            }
        });
    }

    public Object generateQRCustomer(VietQRCreateCustomerDTO dto) {
        Object result = null;
        int qrType = dto.getQrType() != null ? dto.getQrType() : 0;
        Object response = null;

        if (qrType == 0) {
            response = generateDynamicQrCustomer(dto);
            result = response;
        } else {
            // Invalid QR type
            result = new ResponseMessageDTO("FAILED", "E46");
        }

        return result;
    }

    private Object generateDynamicQrCustomer(VietQRCreateCustomerDTO dto) {
        Object result = null;
        HttpStatus httpStatus = null;
        UUID transactionUUID = UUID.randomUUID();
        String serviceCode = !StringUtil.isNullOrEmpty(dto.getServiceCode()) ? dto.getServiceCode() : "";
        switch (dto.getBankCode().toUpperCase()) {
            case "MB":
                String qrMMS = "";
                String checkExistedMMSBank = accountBankReceiveService.checkMMSBankAccount(dto.getBankAccount());
                boolean checkMMS = false;
                String transType = dto.getTransType() != null ? dto.getTransType().trim() : "C";
                if (checkExistedMMSBank != null && !checkExistedMMSBank.trim().isEmpty() && transType.equals("C")) {
                    checkMMS = true;
                }
                if (!checkMMS) {
                    String traceId = "VQR" + RandomCodeUtil.generateRandomUUID();
                    String bankTypeId = dto.getTransType() == null || dto.getTransType().trim().toUpperCase().equals("C")
                            ? bankTypeService.getBankTypeIdByBankCode(dto.getBankCode())
                            : bankTypeService.getBankTypeIdByBankCode(dto.getCustomerBankCode());
                    VietQRDTO vietQRDTO = new VietQRDTO();
                    try {
                        if (dto.getContent().length() <= 50) {
                            if (bankTypeId != null && !bankTypeId.isEmpty()) {
                                AccountBankReceiveEntity accountBankEntity = dto.getTransType() == null || dto.getTransType().trim().toUpperCase().equals("C")
                                        ? accountBankReceiveService.getAccountBankByBankAccountAndBankTypeId(dto.getBankAccount(), bankTypeId)
                                        : accountBankReceiveService.getAccountBankByBankAccountAndBankTypeId(dto.getCustomerBankAccount(), bankTypeId);
                                if (accountBankEntity != null) {
                                    BankTypeEntity bankTypeEntity = bankTypeService.getBankTypeById(bankTypeId);
                                    String caiValue = caiBankService.getCaiValue(bankTypeId);
                                    String content = dto.getReconciliation() == null || dto.getReconciliation()
                                            ? traceId + " " + dto.getContent()
                                            : dto.getContent();
                                    VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
                                    vietQRGenerateDTO.setCaiValue(caiValue);
                                    vietQRGenerateDTO.setAmount(dto.getAmount() + "");
                                    vietQRGenerateDTO.setContent(content);
                                    vietQRGenerateDTO.setBankAccount(accountBankEntity.getBankAccount());
                                    String qr = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);
                                    vietQRDTO.setBankCode(bankTypeEntity.getBankCode());
                                    vietQRDTO.setBankName(bankTypeEntity.getBankName());
                                    vietQRDTO.setBankAccount(accountBankEntity.getBankAccount());
                                    vietQRDTO.setUserBankName(accountBankEntity.getBankAccountName().toUpperCase());
                                    vietQRDTO.setAmount(dto.getAmount() + "");
                                    vietQRDTO.setContent(content);
                                    vietQRDTO.setQrCode(qr);
                                    vietQRDTO.setImgId(bankTypeEntity.getImgId());
                                    vietQRDTO.setExisting(1);
                                    vietQRDTO.setTransactionId("");
                                    vietQRDTO.setTerminalCode(dto.getTerminalCode());
                                    vietQRDTO.setServiceCode(dto.getServiceCode());
                                    vietQRDTO.setAdditionalData(dto.getAdditionalData());
                                    String refId = TransactionRefIdUtil.encryptTransactionId(transactionUUID.toString());
                                    String qrLink = EnvironmentUtil.getQRLink() + refId;
                                    vietQRDTO.setTransactionRefId(refId);
                                    vietQRDTO.setQrLink(qrLink);
                                    result = vietQRDTO;
                                    httpStatus = HttpStatus.OK;
                                } else {
                                    String bankAccount = dto.getTransType() == null || dto.getTransType().trim().equalsIgnoreCase("C")
                                            ? dto.getBankAccount()
                                            : dto.getCustomerBankAccount();
                                    String userBankName = dto.getTransType() == null || dto.getTransType().trim().equalsIgnoreCase("C")
                                            ? dto.getUserBankName().trim().toUpperCase()
                                            : dto.getCustomerName().trim().toUpperCase();
                                    BankTypeEntity bankTypeEntity = bankTypeService.getBankTypeById(bankTypeId);
                                    String caiValue = caiBankService.getCaiValue(bankTypeId);
                                    VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
                                    vietQRGenerateDTO.setCaiValue(caiValue);
                                    vietQRGenerateDTO.setAmount(dto.getAmount() + "");
                                    String content = dto.getReconciliation() == null || dto.getReconciliation()
                                            ? traceId + " " + dto.getContent()
                                            : dto.getContent();
                                    vietQRGenerateDTO.setContent(content);
                                    vietQRGenerateDTO.setBankAccount(bankAccount);
                                    String qr = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);
                                    vietQRDTO.setBankCode(bankTypeEntity.getBankCode());
                                    vietQRDTO.setBankName(bankTypeEntity.getBankName());
                                    vietQRDTO.setBankAccount(bankAccount);
                                    vietQRDTO.setUserBankName(userBankName);
                                    vietQRDTO.setAmount(dto.getAmount() + "");
                                    vietQRDTO.setContent(content);
                                    vietQRDTO.setQrCode(qr);
                                    vietQRDTO.setImgId(bankTypeEntity.getImgId());
                                    vietQRDTO.setExisting(0);
                                    vietQRDTO.setServiceCode(dto.getServiceCode());
                                    vietQRDTO.setAdditionalData(dto.getAdditionalData());
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
                        return result;
                    } catch (Exception e) {
                        logger.error(e.toString());
                        System.out.println(e.toString());
                        result = new ResponseMessageDTO("FAILED", "Unexpected Error");
                        httpStatus = HttpStatus.BAD_REQUEST;
                        return result;
                    } finally {
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
                            vietQRCreateDTO.setServiceCode(serviceCode);
                            if (dto.getTransType() != null && dto.getTransType().trim().toUpperCase().equals("D")) {
                                vietQRCreateDTO.setTransType("D");
                                vietQRCreateDTO.setCustomerBankAccount(dto.getCustomerBankAccount());
                                vietQRCreateDTO.setCustomerBankCode(dto.getCustomerBankCode());
                                vietQRCreateDTO.setCustomerName(dto.getCustomerName());
                            } else {
                                vietQRCreateDTO.setTransType("C");
                            }
                            vietQRCreateDTO.setUrlLink(dto.getUrlLink() != null && !dto.getUrlLink().trim().isEmpty()
                                    ? dto.getUrlLink()
                                    : "");
                            vietQRCreateDTO.setAdditionalData(dto.getAdditionalData());
                            insertNewTransaction(transactionUUID, traceId, vietQRCreateDTO, vietQRDTO, dto.getOrderId(), dto.getSign(), true);
                        }
                        LocalDateTime currentDateTime = LocalDateTime.now();
                        long time = currentDateTime.toEpochSecond(ZoneOffset.UTC);
                        logger.info("qr/generate-customer - call at " + time);
                    }
                }
                break;
            case "BIDV":
                String qr = "";
                String billId = "";
                AccountBankReceiveEntity accountBankEntity = null;
                BankTypeEntity bankTypeEntity = dto.getTransType() == null || dto.getTransType().trim().equalsIgnoreCase("C")
                        ? bankTypeService.getBankTypeByBankCode(dto.getBankCode())
                        : bankTypeService.getBankTypeByBankCode(dto.getCustomerBankCode());
                VietQRDTO vietQRDTO = new VietQRDTO();
                try {
                    if (dto.getContent().length() <= 50) {
                        if (bankTypeEntity != null) {
                            accountBankEntity = dto.getTransType() == null || dto.getTransType().trim().equalsIgnoreCase("C")
                                    ? accountBankReceiveService.getAccountBankByBankAccountAndBankTypeId(dto.getBankAccount(), bankTypeEntity.getId())
                                    : accountBankReceiveService.getAccountBankByBankAccountAndBankTypeId(dto.getCustomerBankAccount(), bankTypeEntity.getId());
                            if (accountBankEntity != null) {
                                billId = getRandomBillId();
                                String content = billId;
                                VietQRCreateDTO vietQRCreateDTO = new VietQRCreateDTO();
                                vietQRCreateDTO.setBankId(accountBankEntity.getId());
                                vietQRCreateDTO.setAmount(dto.getAmount() + "");
                                vietQRCreateDTO.setContent(billId);
                                vietQRCreateDTO.setUserId(accountBankEntity.getUserId());
                                vietQRCreateDTO.setTerminalCode(dto.getTerminalCode());
                                if (dto.getTransType() != null && dto.getTransType().trim().equalsIgnoreCase("D")) {
                                    vietQRCreateDTO.setTransType("D");
                                    vietQRCreateDTO.setCustomerBankAccount(dto.getCustomerBankAccount());
                                    vietQRCreateDTO.setCustomerBankCode(dto.getCustomerBankCode());
                                    vietQRCreateDTO.setCustomerName(dto.getCustomerName());
                                } else {
                                    vietQRCreateDTO.setTransType("C");
                                }
                                vietQRCreateDTO.setUrlLink(dto.getUrlLink() != null && !dto.getUrlLink().trim().isEmpty() ? dto.getUrlLink() : "");
                                ResponseMessageDTO responseMessageDTO = insertNewCustomerInvoiceTransBIDV(vietQRCreateDTO, accountBankEntity, billId);
                                if ("SUCCESS".equals(responseMessageDTO.getStatus())) {
                                    VietQRVaRequestDTO vietQRVaRequestDTO = new VietQRVaRequestDTO();
                                    vietQRVaRequestDTO.setAmount(dto.getAmount() + "");
                                    vietQRVaRequestDTO.setBillId(billId);
                                    vietQRVaRequestDTO.setUserBankName(accountBankEntity.getBankAccountName());
                                    vietQRVaRequestDTO.setDescription(StringUtil.getValueNullChecker(billId));
                                    ResponseMessageDTO generateVaInvoiceVietQR = CustomerVaUtil.generateVaInvoiceVietQR(vietQRVaRequestDTO, accountBankEntity.getCustomerId());
                                    if ("SUCCESS".equals(generateVaInvoiceVietQR.getStatus())) {
                                        qr = generateVaInvoiceVietQR.getMessage();
                                        vietQRDTO.setBankCode(bankTypeEntity.getBankCode());
                                        vietQRDTO.setBankName(bankTypeEntity.getBankName());
                                        vietQRDTO.setBankAccount(accountBankEntity.getBankAccount());
                                        vietQRDTO.setUserBankName(accountBankEntity.getBankAccountName().toUpperCase());
                                        vietQRDTO.setAmount(dto.getAmount() + "");
                                        vietQRDTO.setContent(content);
                                        vietQRDTO.setQrCode(qr);
                                        vietQRDTO.setImgId(bankTypeEntity.getImgId());
                                        vietQRDTO.setExisting(1);
                                        vietQRDTO.setTransactionId("");
                                        vietQRDTO.setTerminalCode(dto.getTerminalCode());
                                        String refId = TransactionRefIdUtil.encryptTransactionId(transactionUUID.toString());
                                        String qrLink = EnvironmentUtil.getQRLink() + refId;
                                        vietQRDTO.setTransactionRefId(refId);
                                        vietQRDTO.setQrLink(qrLink);
                                        result = vietQRDTO;
                                        httpStatus = HttpStatus.OK;
                                    } else {
                                        result = new ResponseMessageDTO("FAILED", "");
                                        httpStatus = HttpStatus.BAD_REQUEST;
                                    }
                                } else {
                                    httpStatus = HttpStatus.BAD_REQUEST;
                                }
                            } else {
                                String bankAccount = dto.getTransType() == null || dto.getTransType().trim().equalsIgnoreCase("C")
                                        ? dto.getBankAccount()
                                        : dto.getCustomerBankAccount();
                                String userBankName = dto.getTransType() == null || dto.getTransType().trim().equalsIgnoreCase("C")
                                        ? dto.getUserBankName().trim().toUpperCase()
                                        : dto.getCustomerName().trim().toUpperCase();
                                String caiValue = caiBankService.getCaiValue(bankTypeEntity.getId());
                                VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
                                vietQRGenerateDTO.setCaiValue(caiValue);
                                vietQRGenerateDTO.setAmount(dto.getAmount() + "");
                                String content = billId;
                                vietQRGenerateDTO.setContent(content);
                                vietQRGenerateDTO.setBankAccount(bankAccount);
                                qr = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);
                                vietQRDTO.setBankCode(bankTypeEntity.getBankCode());
                                vietQRDTO.setBankName(bankTypeEntity.getBankName());
                                vietQRDTO.setBankAccount(bankAccount);
                                vietQRDTO.setUserBankName(userBankName);
                                vietQRDTO.setAmount(dto.getAmount() + "");
                                vietQRDTO.setContent(content);
                                vietQRDTO.setQrCode(qr);
                                vietQRDTO.setImgId(bankTypeEntity.getImgId());
                                vietQRDTO.setExisting(0);
                                vietQRDTO.setServiceCode(dto.getServiceCode());
                                vietQRDTO.setAdditionalData(dto.getAdditionalData());
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
                    return result;
                } catch (Exception e) {
                    logger.error(e.toString());
                    System.out.println(e.toString());
                    result = new ResponseMessageDTO("FAILED", "Unexpected Error");
                    httpStatus = HttpStatus.BAD_REQUEST;
                } finally {
                    if (accountBankEntity != null && !StringUtil.isNullOrEmpty(qr)) {
                        VietQRBIDVCreateDTO dto1 = new VietQRBIDVCreateDTO();
                        dto1.setContent(billId);
                        dto1.setAmount(dto.getAmount() + "");
                        dto1.setTerminalCode(StringUtil.getValueNullChecker(dto.getTerminalCode()));
                        dto1.setOrderId(StringUtil.getValueNullChecker(dto.getOrderId()));
                        dto1.setNote(StringUtil.getValueNullChecker(dto.getNote()));
                        dto1.setUrlLink(StringUtil.getValueNullChecker(dto.getUrlLink()));
                        dto1.setTransType(StringUtil.getValueNullChecker(dto.getTransType()));
                        dto1.setSign(StringUtil.getValueNullChecker(dto.getSign()));
                        dto1.setBillId(billId);
                        dto1.setCustomerBankAccount(StringUtil.getValueNullChecker(dto.getCustomerBankAccount()));
                        dto1.setCustomerBankCode(StringUtil.getValueNullChecker(dto.getCustomerBankCode()));
                        dto1.setCustomerName(StringUtil.getValueNullChecker(dto.getCustomerName()));
                        dto1.setQr(qr);
                        AccountBankReceiveEntity accountBankReceiveEntity = accountBankEntity;
                        Thread thread = new Thread(() -> {
                            insertNewTransactionBIDV(transactionUUID, dto1, false, accountBankReceiveEntity);
                        });
                        thread.start();
                    }
                }
                break;
            default:
                String traceId = "VQR" + RandomCodeUtil.generateRandomUUID();
                String bankTypeId = dto.getTransType() == null || dto.getTransType().trim().equalsIgnoreCase("C")
                        ? bankTypeService.getBankTypeIdByBankCode(dto.getBankCode())
                        : bankTypeService.getBankTypeIdByBankCode(dto.getCustomerBankCode());
                try {
                    String bankAccount = dto.getTransType() == null || dto.getTransType().trim().equalsIgnoreCase("C")
                            ? dto.getBankAccount()
                            : dto.getCustomerBankAccount();
                    String userBankName = dto.getTransType() == null || dto.getTransType().trim().equalsIgnoreCase("C")
                            ? dto.getUserBankName().trim().toUpperCase()
                            : dto.getCustomerName().trim().toUpperCase();
                    if (dto.getContent().length() <= 50) {
                        bankTypeEntity = bankTypeService.getBankTypeById(bankTypeId);
                        String caiValue = caiBankService.getCaiValue(bankTypeId);
                        VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
                        vietQRGenerateDTO.setCaiValue(caiValue);
                        vietQRGenerateDTO.setAmount(dto.getAmount() + "");
                        String content = dto.getReconciliation() == null || dto.getReconciliation()
                                ? traceId + " " + dto.getContent()
                                : dto.getContent();
                        vietQRGenerateDTO.setContent(content);
                        vietQRGenerateDTO.setBankAccount(bankAccount);
                        qr = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);
                        vietQRDTO = new VietQRDTO();
                        vietQRDTO.setBankCode(bankTypeEntity.getBankCode());
                        vietQRDTO.setBankName(bankTypeEntity.getBankName());
                        vietQRDTO.setBankAccount(bankAccount);
                        vietQRDTO.setUserBankName(userBankName);
                        vietQRDTO.setAmount(dto.getAmount() + "");
                        vietQRDTO.setContent(content);
                        vietQRDTO.setQrCode(qr);
                        vietQRDTO.setImgId(bankTypeEntity.getImgId());
                        vietQRDTO.setExisting(0);
                        vietQRDTO.setServiceCode(dto.getServiceCode());
                        vietQRDTO.setAdditionalData(dto.getAdditionalData());
                        result = vietQRDTO;
                        httpStatus = HttpStatus.OK;
                    } else {
                        result = new ResponseMessageDTO("FAILED", "E26");
                        httpStatus = HttpStatus.BAD_REQUEST;
                    }
                } catch (Exception e) {
                    httpStatus = HttpStatus.BAD_REQUEST;
                    logger.error("VietQRController: ERROR: generateQRCustomer: " + e.getMessage() + " at: " + System.currentTimeMillis());
                }
                break;
        }
        return result;
    }

    @Async
    protected void insertNewTransaction(UUID transcationUUID, String traceId, VietQRCreateDTO dto, VietQRDTO result,
                                        String orderId, String sign, boolean isFromMerchantSync) {
        LocalDateTime startTime = LocalDateTime.now();
        long startTimeLong = startTime.toEpochSecond(ZoneOffset.UTC);
        logger.info("QR generate - start insertNewTransaction at: " + startTimeLong);
        logger.info("QR generate - insertNewTransaction data: " + result.toString());

        MqttClient mqttClient = null;

        try {
            NumberFormat nf = NumberFormat.getInstance(Locale.US);
            AccountBankReceiveEntity accountBankEntity = accountBankReceiveService.getAccountBankById(dto.getBankId());
            if (accountBankEntity != null) {
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
                transactionEntity.setTerminalCode(result.getTerminalCode() != null ? result.getTerminalCode() : "");
                transactionEntity.setQrCode("");
                transactionEntity.setUserId(accountBankEntity.getUserId());
                transactionEntity.setOrderId(orderId);
                transactionEntity.setNote(dto.getNote() != null ? dto.getNote() : "");
                transactionEntity.setTransStatus(0);
                transactionEntity.setUrlLink(dto.getUrlLink() != null ? dto.getUrlLink() : "");
                transactionEntity.setTransType(dto.getTransType() != null ? dto.getTransType() : "C");
                transactionEntity.setReferenceNumber("");
                transactionEntity.setOrderId(orderId);
                transactionEntity.setServiceCode(dto.getServiceCode());
                transactionEntity.setSign(sign != null ? sign :"");
                if (dto.getTransType() != null && dto.getTransType().trim().toUpperCase().equals("D")) {
                    transactionEntity.setCustomerBankAccount(dto.getCustomerBankAccount());
                    transactionEntity.setCustomerBankCode(dto.getCustomerBankCode());
                    transactionEntity.setCustomerName(dto.getCustomerName());
                }


                ZoneOffset offset = ZoneOffset.ofHours(7); // UTC+7
                long timestampUtcPlus7 = currentDateTime.toEpochSecond(offset);
                List<Object> additionalDataList = new ArrayList<>();
                // Lặp qua từng phần tử trong danh sách additionalData của DTO
                for (AdditionalData additionalData : dto.getAdditionalData()) {
                    additionalDataList.add(new AdditionalDataInTransaction(
                            dto.getAmount(),
                            timestampUtcPlus7,
                            result.getServiceCode(),
                            result.getTerminalCode(),
                            additionalData.getAdditionalData1() // Lấy giá trị của additionalData1
                    ));
                }
                // Chuyển đổi danh sách thành JSON và gán vào transactionEntity
                ObjectMapper mapper = new ObjectMapper();
                String additionalDataJson = mapper.writeValueAsString(additionalDataList);
                transactionEntity.setAdditionalData(additionalDataJson);

                transactionReceiveService.insertTransactionReceive(transactionEntity);

//                UUID notificationUUID = UUID.randomUUID();
//                NotificationEntity notiEntity = new NotificationEntity();
//                String message = NotificationUtil.getNotiDescNewTransPrefix2()
//                        + NotificationUtil.getNotiDescNewTransSuffix1()
//                        + nf.format(Double.parseDouble(dto.getAmount()))
//                        + NotificationUtil
//                        .getNotiDescNewTransSuffix2();
//
//                if (isFromMerchantSync) {
//                    // Gửi thông báo qua MQTT
//                    try {
//                        mqttClient = new MqttClient("tcp://broker.hivemq.com:1883", MqttClient.generateClientId(), new MemoryPersistence());
//                        mqttClient.connect();
//
//                        Gson gson = new Gson();
//                        Map<String, String> data = new HashMap<>();
//                        data.put("notificationType", NotificationUtil.getNotiTypeNewTransaction());
//                        data.put("notificationId", notificationUUID.toString());
//                        data.put("bankCode", result.getBankCode());
//                        data.put("bankName", result.getBankName());
//                        data.put("bankAccount", result.getBankAccount());
//                        data.put("userBankName", result.getUserBankName());
//                        data.put("amount", result.getAmount());
//                        data.put("content", result.getContent());
//                        data.put("qrCode", result.getQrCode());
//                        data.put("imgId", result.getImgId());
//                        data.put("serviceCode", result.getServiceCode());
//                        data.put("terminalCode", result.getTerminalCode());
//
//                        String jsonMessage = gson.toJson(data);
//                        MqttMessage mqttMessage = new MqttMessage(jsonMessage.getBytes());
//                        mqttMessage.setQos(2);
//                        mqttClient.publish("notification/topic", mqttMessage);
//                    } catch (MqttException e) {
//                        logger.error("Error sending MQTT message: " + e.toString());
//                    } finally {
//                        if (mqttClient != null && mqttClient.isConnected()) {
//                            try {
//                                mqttClient.disconnect();
//                            } catch (MqttException e) {
//                                logger.error("Error disconnecting MQTT client: " + e.toString());
//                            }
//                        }
//                    }
//                }
//
//                notiEntity.setId(notificationUUID.toString());
//                notiEntity.setRead(false);
//                notiEntity.setMessage(message);
//                notiEntity.setTime(currentDateTime.toEpochSecond(ZoneOffset.UTC));
//                notiEntity.setType(NotificationUtil.getNotiTypeNewTransaction());
//                notiEntity.setUserId(dto.getUserId());
//                notiEntity.setData(transcationUUID.toString());
//                notificationService.insertNotification(notiEntity);

            }
        } catch (Exception e) {
            logger.error("Error at insertNewTransaction: " + e.toString());
        } finally {
            LocalDateTime endTime = LocalDateTime.now();
            long endTimeLong = endTime.toEpochSecond(ZoneOffset.UTC);
            logger.info("QR generate - end insertNewTransaction at: " + endTimeLong);
        }
    }

    private ResponseMessageDTO insertNewCustomerInvoiceTransBIDV(VietQRCreateDTO dto,
                                                                 AccountBankReceiveEntity accountBankEntity, String billId) {
        ResponseMessageDTO responseMessageDTO = new ResponseMessageDTO();
        logger.info("QR generate - start insertNewCustomerInvoiceTransBIDV at: " + DateTimeUtil.getCurrentDateTimeUTC());
        try {
            long amount = 0;
            if (Objects.nonNull(accountBankEntity) && !StringUtil.isNullOrEmpty(billId)) {
                if (!StringUtil.isNullOrEmpty(accountBankEntity.getCustomerId())) {
                    CustomerInvoiceEntity entity = new CustomerInvoiceEntity();
                    entity.setId(UUID.randomUUID().toString());
                    entity.setCustomerId(accountBankEntity.getCustomerId());
                    try {
                        amount = Long.parseLong(dto.getAmount());
                    } catch (Exception e) {
                        logger.error("VietQRController: ERROR: insertNewCustomerInvoiceTransBIDV: " + e.getMessage());
                    }
                    entity.setAmount(amount);
                    entity.setBillId(billId);
                    entity.setStatus(0);
                    entity.setType(1);
                    entity.setName("");
                    entity.setTimeCreated(DateTimeUtil.getCurrentDateTimeUTC());
                    entity.setTimePaid(0L);
                    entity.setInquire(0);
                    entity.setQrType(1);
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
    private void insertNewTransactionBIDV(UUID transcationUUID, VietQRBIDVCreateDTO dto,
                                          boolean isFromMerchantSync,
                                          AccountBankReceiveEntity accountBankEntity) {
        logger.info("QR generate - start insertNewTransactionBIDV at: " + DateTimeUtil.getCurrentDateTimeUTC());
        try {
            if (Objects.nonNull(accountBankEntity)) {
                TransactionReceiveEntity transactionEntity = new TransactionReceiveEntity();
                transactionEntity.setId(transcationUUID.toString());
                transactionEntity.setBankAccount(accountBankEntity.getBankAccount());
                transactionEntity.setBankId(accountBankEntity.getId());
                transactionEntity.setContent(dto.getContent());
                transactionEntity.setAmount(Long.parseLong(dto.getAmount()));
                transactionEntity.setTime(DateTimeUtil.getCurrentDateTimeUTC());
                transactionEntity.setRefId("");
                transactionEntity.setType(0);
                transactionEntity.setStatus(0);
                transactionEntity.setTraceId("");
                transactionEntity.setTimePaid(0);
                transactionEntity.setTerminalCode(dto.getTerminalCode());
                transactionEntity.setQrCode(dto.getQr());
                transactionEntity.setUserId(accountBankEntity.getUserId());
                transactionEntity.setOrderId(dto.getOrderId());
                transactionEntity.setNote(dto.getNote());
                transactionEntity.setTransStatus(0);
                transactionEntity.setUrlLink(dto.getUrlLink());
                transactionEntity.setTransType("C");
                transactionEntity.setReferenceNumber("");
                transactionEntity.setSign(dto.getSign());
                transactionEntity.setBillId(dto.getBillId());
                //
                if (dto.getTransType() != null && dto.getTransType().trim().equalsIgnoreCase("D")) {
                    transactionEntity.setCustomerBankAccount(dto.getCustomerBankAccount());
                    transactionEntity.setCustomerBankCode(dto.getCustomerBankCode());
                    transactionEntity.setCustomerName(dto.getCustomerName());
                }

                transactionReceiveService.insertTransactionReceive(transactionEntity);
                logger.info("After insertNewTransactionBIDV at: " + DateTimeUtil.getCurrentDateTimeUTC());
            }
        } catch (Exception e) {
            logger.error("Error at insertNewTransactionBIDV: " + e.toString());
        } finally {
            logger.info("QR generate - end insertNewTransactionBIDV at: " + DateTimeUtil.getCurrentDateTimeUTC());
        }
    }

}
