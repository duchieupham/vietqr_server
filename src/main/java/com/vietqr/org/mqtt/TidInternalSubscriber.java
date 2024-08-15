package com.vietqr.org.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vietqr.org.dto.*;
import com.vietqr.org.dto.bidv.VietQRVaRequestDTO;
import com.vietqr.org.entity.AccountBankReceiveEntity;
import com.vietqr.org.entity.BankTypeEntity;
import com.vietqr.org.entity.QrBoxSyncEntity;
import com.vietqr.org.entity.TransactionReceiveEntity;
import com.vietqr.org.entity.bidv.CustomerInvoiceEntity;
import com.vietqr.org.service.*;
import com.vietqr.org.service.bidv.CustomerInvoiceService;
import com.vietqr.org.service.mqtt.AdditionalData;
import com.vietqr.org.service.mqtt.AdditionalDataInTransaction;
import com.vietqr.org.util.*;
import com.vietqr.org.util.annotation.MqttTopicHandler;
import com.vietqr.org.util.bank.bidv.CustomerVaUtil;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.security.SecureRandom;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.vietqr.org.util.RandomCodeUtil.getRandomBillId;

@Component
public class TidInternalSubscriber {

    private static final Logger logger = Logger.getLogger(TidInternalSubscriber.class);

    private static final int CODE_LENGTH = 6;
    private static final String NUMBERS = "0123456789";

    @Autowired
    private QrBoxSyncService qrBoxSyncService;

    @Autowired

    private MqttListenerService mqttListenerService;
    @Autowired
    private AccountBankReceiveService accountBankReceiveService;

    @Autowired
    private BankTypeService bankTypeService;

    @Autowired
    private CaiBankService caiBankService;

    @Autowired
    private TransactionReceiveService transactionReceiveService;

    @Autowired
    private CustomerInvoiceService customerInvoiceService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private TransactionRefundService transactionRefundService;

    @PostConstruct
    public void init() {
        System.out.println("TidInternalSubscriber initialized");
        System.out.println("qrBoxSyncService: " + qrBoxSyncService);
        System.out.println("mqttListenerService: " + mqttListenerService);
    }

    @MqttTopicHandler(topic = "/vqr/handle-box")
    public void handleIncomingMessage(String topic, MqttMessage message) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            HandleSyncBoxQrDTO dto = mapper.readValue(message.getPayload(), HandleSyncBoxQrDTO.class);
            String checkSum = BoxTerminalRefIdUtil.encryptMacAddr(dto.getMacAddr());
            if (checkSum.equals(dto.getCheckSum())) {
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

                //send to macAddress
                SyncBoxQrDTO syncBoxQrDTO = new SyncBoxQrDTO(certificate, boxId);
                mqttListenerService.publishMessageToCommonTopic("/vqr/handle-box/response/" + dto.getMacAddr(),
                        mapper.writeValueAsString(syncBoxQrDTO));
            }
        } catch (Exception e) {
            logger.error("TidInternalSubscriber: ERROR: " + e.getMessage() +
                    " at: " + System.currentTimeMillis());
        }
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

    private String getRawTerminalCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            int randomIndex = random.nextInt(NUMBERS.length());
            code.append(NUMBERS.charAt(randomIndex));
        }
        return code.toString();
    }


    @MqttTopicHandler(topic = "vietqr/request/#") // Xử lý các yêu cầu khởi tạo QR
    public void handleQRRequest(String topic, MqttMessage message) {
        try {
            String payload = new String(message.getPayload());
            ObjectMapper mapper = new ObjectMapper();
            VietQRCreateCustomerDTO dto = mapper.readValue(payload, VietQRCreateCustomerDTO.class);

            // Xử lý logic QR và phản hồi
            VietQRDTO response = (VietQRDTO) generateQRCustomer(dto);
            String responsePayload = mapper.writeValueAsString(response);

            String tramId = dto.getTerminalCode();
            String responseTopic = "vietqr/response/" + tramId;

            mqttListenerService.publishMessageToCommonTopic(responseTopic, responsePayload);
            logger.info("Response sent to topic: " + responseTopic + " Payload: " + responsePayload);

        } catch (Exception e) {
            logger.error("Error handling QR request: " + e.getMessage());
        }
    }
    @MqttTopicHandler(topic = "vietqr/request/status/#") // Xử lý yêu cầu trạng thái giao dịch
    public void handleTransactionStatus(String topic, MqttMessage message) {
        try {
            // Chuyển payload sang TransactionCheckOrderInputDTO
            String payload = new String(message.getPayload());
            ObjectMapper mapper = new ObjectMapper();
            TransactionCheckOrderInputDTO dto = mapper.readValue(payload, TransactionCheckOrderInputDTO.class);

            // Xử lý yêu cầu và tạo phản hồi
            Object result = handleTransactionStatusRequest(dto);
            String responsePayload = mapper.writeValueAsString(result);

            // Xác định topic phản hồi
            String responseTopic = topic.replace("request", "response");

            // Gửi phản hồi lên đúng topic
            mqttListenerService.publishMessageToCommonTopic(responseTopic, responsePayload);
            logger.info("Response sent to topic: " + responseTopic + " Payload: " + responsePayload);

        } catch (Exception e) {
            logger.error("Error handling transaction status request: " + e.getMessage());
        }
    }
    public Object handleTransactionStatusRequest(TransactionCheckOrderInputDTO dto) {
        Object result = null;
        try {
            if (dto != null) {
                String bankAccountName = accountBankReceiveService.getBankAccountNameByBankAccount(dto.getBankAccount());
                String checkSum = BankEncryptUtil.generateMD5CheckOrderChecksum(dto.getBankAccount(), bankAccountName);

                if (BankEncryptUtil.isMatchChecksum(dto.getCheckSum(), checkSum)) {
                    List<TransReceiveResponseDTO> responseDTOs = new ArrayList<>();
                    if (dto.getValue() != null && !dto.getValue().trim().isEmpty()) {
                        if (dto.getType() != null && dto.getType() == 0) {
                            responseDTOs = transactionReceiveService.getTransByOrderId(dto.getValue(), dto.getBankAccount());
                            result = processTransactions(responseDTOs, dto);
                        } else if (dto.getType() != null && dto.getType() == 1) {
                            responseDTOs = transactionReceiveService.getTransByReferenceNumber(dto.getValue(), dto.getBankAccount());
                            result = processTransactions(responseDTOs, dto);
                        } else {
                            result = new ResponseMessageDTO("FAILED", "E95");
                        }
                    } else {
                        result = new ResponseMessageDTO("FAILED", "E46");
                    }
                } else {
                    result = new ResponseMessageDTO("FAILED", "E39");
                }

            } else {
                result = new ResponseMessageDTO("FAILED", "E46");
            }
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "E05");
        }
        return result;
    }

    private List<TransReceiveResponseCheckOrderDTO> processTransactions(List<TransReceiveResponseDTO> responseDTOs, TransactionCheckOrderInputDTO dto) {
        List<IRefundCheckOrderDTO> iRefundCheckOrderDTOS = transactionRefundService.getTotalRefundedByTransactionId(
                responseDTOs.stream().map(TransReceiveResponseDTO::getTransactionId).collect(Collectors.toList()));
        Map<String, RefundCheckOrderDTO> refundCheckOrderDTOMap;
        if (iRefundCheckOrderDTOS != null && !iRefundCheckOrderDTOS.isEmpty()) {
            refundCheckOrderDTOMap = iRefundCheckOrderDTOS.stream()
                    .collect(Collectors.toMap(IRefundCheckOrderDTO::getTransactionId, item ->
                            new RefundCheckOrderDTO(item.getTransactionId(), item.getRefundCount(), item.getAmountRefunded())));
        } else {
            refundCheckOrderDTOMap = new HashMap<>();
        }

        return responseDTOs.stream().map(item -> {
            TransReceiveResponseCheckOrderDTO checkOrderDTO = new TransReceiveResponseCheckOrderDTO();
            checkOrderDTO.setAmount(item.getAmount());
            checkOrderDTO.setStatus(item.getStatus());
            checkOrderDTO.setNote(StringUtil.getValueNullChecker(item.getNote()));
            checkOrderDTO.setContent(item.getContent());
            checkOrderDTO.setOrderId(item.getOrderId());
            checkOrderDTO.setReferenceNumber(item.getReferenceNumber());
            checkOrderDTO.setTerminalCode(StringUtil.getValueNullChecker(item.getTerminalCode()));
            checkOrderDTO.setTimeCreated(item.getTimeCreated());
            checkOrderDTO.setTimePaid(item.getTimePaid());
            checkOrderDTO.setType(item.getType());
            checkOrderDTO.setTransType(item.getTransType());
            RefundCheckOrderDTO refundCheckOrderDTO = refundCheckOrderDTOMap
                    .getOrDefault(item.getTransactionId(), new RefundCheckOrderDTO(item.getTransactionId()));
            checkOrderDTO.setRefundCount(refundCheckOrderDTO.getRefundCount());
            checkOrderDTO.setAmountRefunded(refundCheckOrderDTO.getAmountRefunded());
            return checkOrderDTO;
        }).collect(Collectors.toList());
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
                                    vietQRDTO.setOrderId(dto.getOrderId());
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
                                    vietQRDTO.setOrderId(dto.getOrderId());
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
                BankTypeEntity bankTypeEntity = null;
                AccountBankReceiveEntity accountBankEntity = null;
                if (dto.getTransType() == null || dto.getTransType().trim().equalsIgnoreCase("C")) {
                    bankTypeEntity = bankTypeService.getBankTypeByBankCode(dto.getBankCode());
                } else {
                    bankTypeEntity = bankTypeService.getBankTypeByBankCode(dto.getCustomerBankCode());
                }
                VietQRDTO vietQRDTO = new VietQRDTO();
                try {
                    if (dto.getContent().length() <= 50) {
                        // check if generate qr with transtype = D or C
                        // if D => generate with customer information
                        // if C => do normal
                        // find bankTypeId by bankcode
                        if (Objects.nonNull(bankTypeEntity)) {
                            // find bank by bankAccount and banktypeId

                            if (dto.getTransType() == null || dto.getTransType().trim().equalsIgnoreCase("C")) {
                                accountBankEntity = accountBankReceiveService
                                        .getAccountBankByBankAccountAndBankTypeId(dto.getBankAccount(),
                                                bankTypeEntity.getId());
                            } else {
                                accountBankEntity = accountBankReceiveService
                                        .getAccountBankByBankAccountAndBankTypeId(dto.getCustomerBankAccount(),
                                                bankTypeEntity.getId());
                            }
                            if (Objects.nonNull(accountBankEntity)) {
                                // get cai value
                                billId = getRandomBillId();
                                String content = billId;
                                // generate qr BIDV
                                VietQRCreateDTO vietQRCreateDTO = new VietQRCreateDTO();
                                vietQRCreateDTO.setBankId(accountBankEntity.getId());
                                vietQRCreateDTO.setAmount(dto.getAmount() + "");
                                vietQRCreateDTO.setContent(billId);
                                vietQRCreateDTO.setUserId(accountBankEntity.getUserId());
                                vietQRCreateDTO.setTerminalCode(dto.getTerminalCode());
                                vietQRCreateDTO.setAdditionalData(dto.getAdditionalData());
                                //
                                if (dto.getTransType() != null && dto.getTransType().trim().equalsIgnoreCase("D")) {
                                    vietQRCreateDTO.setTransType("D");
                                    vietQRCreateDTO.setCustomerBankAccount(dto.getCustomerBankAccount());
                                    vietQRCreateDTO.setCustomerBankCode(dto.getCustomerBankCode());
                                    vietQRCreateDTO.setCustomerName(dto.getCustomerName());
                                } else {
                                    vietQRCreateDTO.setTransType("C");
                                }
                                if (dto.getUrlLink() != null && !dto.getUrlLink().trim().isEmpty()) {
                                    vietQRCreateDTO.setUrlLink(dto.getUrlLink());
                                } else {
                                    vietQRCreateDTO.setUrlLink("");
                                }
                                ResponseMessageDTO responseMessageDTO =
                                        insertNewCustomerInvoiceTransBIDV(vietQRCreateDTO, accountBankEntity, billId);

                                // insert success transaction_receive
                                if ("SUCCESS".equals(responseMessageDTO.getStatus())) {
                                    VietQRVaRequestDTO vietQRVaRequestDTO = new VietQRVaRequestDTO();
                                    vietQRVaRequestDTO.setAmount(dto.getAmount() + "");
                                    vietQRVaRequestDTO.setBillId(billId);
                                    vietQRVaRequestDTO.setUserBankName(accountBankEntity.getBankAccountName());
                                    vietQRVaRequestDTO.setDescription(StringUtil.getValueNullChecker(billId));

                                    ResponseMessageDTO generateVaInvoiceVietQR = CustomerVaUtil.generateVaInvoiceVietQR(vietQRVaRequestDTO, accountBankEntity.getCustomerId());

                                    if ("SUCCESS".equals(generateVaInvoiceVietQR.getStatus())) {
                                        qr = generateVaInvoiceVietQR.getMessage();
                                        // generate VietQRDTO
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
                                        vietQRDTO.setAdditionalData(dto.getAdditionalData());
                                        String refId = TransactionRefIdUtil.encryptTransactionId(transactionUUID.toString());
                                        String qrLink = EnvironmentUtil.getQRLink() + refId;
                                        vietQRDTO.setTransactionRefId(refId);
                                        vietQRDTO.setQrLink(qrLink);
                                        //
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
                                String bankAccount = "";
                                String userBankName = "";
                                String content = "";
                                if (dto.getTransType() == null || dto.getTransType().trim().equalsIgnoreCase("C")) {
                                    bankAccount = dto.getBankAccount();
                                    userBankName = dto.getUserBankName().trim().toUpperCase();
                                } else {
                                    bankAccount = dto.getCustomerBankAccount();
                                    userBankName = dto.getCustomerName().trim().toUpperCase();
                                }
                                // get cai value
                                String caiValue = caiBankService.getCaiValue(bankTypeEntity.getId());
                                // generate VietQRGenerateDTO
                                VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
                                vietQRGenerateDTO.setCaiValue(caiValue);
                                vietQRGenerateDTO.setAmount(dto.getAmount() + "");
                                content = billId;
                                vietQRGenerateDTO.setContent(content);
                                vietQRGenerateDTO.setBankAccount(bankAccount);
                                qr = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);
                                //
                                // generate VietQRDTO
                                vietQRDTO.setBankCode(bankTypeEntity.getBankCode());
                                vietQRDTO.setBankName(bankTypeEntity.getBankName());
                                vietQRDTO.setBankAccount(bankAccount);
                                vietQRDTO.setUserBankName(userBankName);
                                vietQRDTO.setAmount(dto.getAmount() + "");
                                vietQRDTO.setContent(content);
                                vietQRDTO.setQrCode(qr);
                                vietQRDTO.setImgId(bankTypeEntity.getImgId());
                                vietQRDTO.setExisting(0);
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
                    return new ResponseEntity<>(result, httpStatus);
                } catch (Exception e) {
                    logger.error(e.toString());
                    System.out.println(e.toString());
                    result = new ResponseMessageDTO("FAILED", "Unexpected Error");
                    httpStatus = HttpStatus.BAD_REQUEST;
                } finally {
                    if (Objects.nonNull(accountBankEntity) && !StringUtil.isNullOrEmpty(qr)) {
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
                String bankTypeId = "";
                if (dto.getTransType() == null || dto.getTransType().trim().equalsIgnoreCase("C")) {
                    bankTypeId = bankTypeService.getBankTypeIdByBankCode(dto.getBankCode());
                } else {
                    bankTypeId = bankTypeService.getBankTypeIdByBankCode(dto.getCustomerBankCode());
                }
                try {
                    String bankAccount = "";
                    String userBankName = "";
                    String content = "";
                    if (dto.getTransType() == null || dto.getTransType().trim().equalsIgnoreCase("C")) {
                        bankAccount = dto.getBankAccount();
                        userBankName = dto.getUserBankName().trim().toUpperCase();
                    } else {
                        bankAccount = dto.getCustomerBankAccount();
                        userBankName = dto.getCustomerName().trim().toUpperCase();
                    }
                    if (dto.getContent().length() <= 50) {
                        bankTypeEntity = bankTypeService.getBankTypeById(bankTypeId);
                        String caiValue = caiBankService.getCaiValue(bankTypeId);
                        // generate VietQRGenerateDTO
                        VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
                        vietQRGenerateDTO.setCaiValue(caiValue);
                        vietQRGenerateDTO.setAmount(dto.getAmount() + "");
                        if (dto.getReconciliation() == null || dto.getReconciliation()) {
                            content = traceId + " " + dto.getContent();
                        } else {
                            content = dto.getContent();
                        }
                        vietQRGenerateDTO.setContent(content);
                        vietQRGenerateDTO.setBankAccount(bankAccount);
                        qr = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);
                        //
                        vietQRDTO = new VietQRDTO();
                        // generate VietQRDTO
                        vietQRDTO.setBankCode(bankTypeEntity.getBankCode());
                        vietQRDTO.setBankName(bankTypeEntity.getBankName());
                        vietQRDTO.setBankAccount(bankAccount);
                        vietQRDTO.setUserBankName(userBankName);
                        vietQRDTO.setAmount(dto.getAmount() + "");
                        vietQRDTO.setContent(content);
                        vietQRDTO.setQrCode(qr);
                        vietQRDTO.setImgId(bankTypeEntity.getImgId());
                        vietQRDTO.setExisting(0);
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
                transactionEntity.setSign(sign != null ? sign : "");
                if (dto.getTransType() != null && dto.getTransType().trim().toUpperCase().equals("D")) {
                    transactionEntity.setCustomerBankAccount(dto.getCustomerBankAccount());
                    transactionEntity.setCustomerBankCode(dto.getCustomerBankCode());
                    transactionEntity.setCustomerName(dto.getCustomerName());
                }

                List<Object> additionalDataList = new ArrayList<>();
                // Lặp qua từng phần tử trong danh sách additionalData của DTO
                for (AdditionalData additionalData : dto.getAdditionalData()) {
                    additionalDataList.add(new AdditionalDataInTransaction(
                            dto.getAmount(),
                            DateTimeUtil.getCurrentDateTimeUTC(),
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

                // Lưu trữ additionalData vào transactionEntity
                if (dto.getAdditionalData() != null && !dto.getAdditionalData().isEmpty()) {

                    List<Object> additionalDataList = new ArrayList<>();
                    for (AdditionalData additionalData : dto.getAdditionalData()) {
                        additionalDataList.add(new AdditionalDataInTransaction(
                                dto.getAmount(),
                                DateTimeUtil.getCurrentDateTimeUTC(),
                                dto.getServiceCode(),
                                dto.getTerminalCode(),
                                additionalData.getAdditionalData1()
                        ));
                    }
                    ObjectMapper mapper = new ObjectMapper();
                    String additionalDataJson = mapper.writeValueAsString(additionalDataList);
                    transactionEntity.setAdditionalData(additionalDataJson);
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

    private  String formatTimeUtcPlus(long time) {
        long utcPlusSevenTime = time + 25200;
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(utcPlusSevenTime), ZoneId.of("GMT"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH);
        return dateTime.format(formatter);
    }

}
