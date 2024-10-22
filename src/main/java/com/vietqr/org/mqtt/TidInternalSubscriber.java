package com.vietqr.org.mqtt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vietqr.org.controller.TransactionBankController;
import com.vietqr.org.dto.*;
import com.vietqr.org.dto.bidv.VietQRVaRequestDTO;
import com.vietqr.org.dto.mapping.RefundMappingRedisDTO;
import com.vietqr.org.entity.*;
import com.vietqr.org.entity.bidv.CustomerInvoiceEntity;
import com.vietqr.org.service.*;
import com.vietqr.org.service.bidv.CustomerInvoiceService;
import com.vietqr.org.service.mqtt.AdditionalData;
import com.vietqr.org.service.mqtt.AdditionalDataInTransaction;
import com.vietqr.org.service.redis.IdempotencyService;
import com.vietqr.org.util.*;
import com.vietqr.org.util.annotation.MqttTopicHandler;
import com.vietqr.org.util.bank.bidv.CustomerVaUtil;
import com.vietqr.org.util.bank.mb.MBTokenUtil;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.netty.http.client.HttpClient;

import javax.annotation.PostConstruct;
import java.security.SecureRandom;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
    private TransactionRefundService transactionRefundService;

    @Autowired
    TerminalBankService terminalBankService;

    @Autowired
    IdempotencyService idempotencyService;

    @Autowired
    AccountCustomerBankService accountCustomerBankService;

    @Autowired
    TransactionBankService transactionBankService;

    @Autowired
    TransactionBankController transactionBankController;

    @Autowired
    TerminalService terminalService;

    @Autowired
    TerminalBankReceiveService terminalBankReceiveService;

    @Autowired
    CustomerErrorLogService customerErrorLogService;

    @Autowired
    TransactionReceiveLogService transactionReceiveLogService;

    @PostConstruct
    public void init() {
    }

    @MqttTopicHandler(topic = "/vqr/handle-box")
    public void handleIncomingMessage(String topic, MqttMessage message) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            HandleSyncBoxQrDTO dto = mapper.readValue(message.getPayload(), HandleSyncBoxQrDTO.class);
            String checkSum = BoxTerminalRefIdUtil.encryptMacAddr(dto.getMacAddr());
            if (checkSum.equals(dto.getCheckSum())) {
                String macAddr = dto.getMacAddr().replaceAll("\\:", "");
//                dto.setMacAddr(macAddr);
                macAddr = macAddr.replaceAll("\\.", "");
                String qrBoxCode = getRandomNumberUniqueQRBox();
                String certificate = EnvironmentUtil.getVietQrBoxInteralPrefix() + BoxTerminalRefIdUtil.encryptQrBoxId(qrBoxCode + macAddr);
                String boxId = BoxTerminalRefIdUtil.encryptQrBoxId(qrBoxCode);
                QrBoxSyncEntity entity = qrBoxSyncService.getByMacAddress(macAddr);
                if (entity != null) {
                    if (StringUtil.isNullOrEmpty(entity.getBoxId())) {
                        boxId = BoxTerminalRefIdUtil.encryptQrBoxId(entity.getQrBoxCode());
                    } else {
                        boxId = entity.getBoxId();
                    }
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
                SyncBoxQrDTO syncBoxQrDTO = new SyncBoxQrDTO(entity.getCertificate(), boxId);
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
    public void handleQR(String topic, MqttMessage message) {
        try {
            String payload = new String(message.getPayload());
            ObjectMapper mapper = new ObjectMapper();
            VietQRCreateCustomerDTO dto = mapper.readValue(payload, VietQRCreateCustomerDTO.class);

            // Xử lý logic QR và phản hồi
            VietQRDTO response = (VietQRDTO) generateQRCustomerV2(dto);
            String responsePayload = mapper.writeValueAsString(response);

            String tramId = dto.getTerminalCode();
            String responseTopic = "vietqr/response/" + tramId;

            mqttListenerService.publishMessageToCommonTopic(responseTopic, responsePayload);
            logger.info("Response sent to topic: " + responseTopic + " Payload: " + responsePayload);

        } catch (Exception e) {
            logger.error("Error handling QR request: " + e.getMessage());
        }
    }

    @MqttTopicHandler(topic = "vietqr/request-status/#") // Xử lý yêu cầu trạng thái giao dịch
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
            String responseTopic = topic.replace("request-status", "response-status");

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

    public Object generateQRCustomerV2(VietQRCreateCustomerDTO dto) {
        Object result = null;
        int qrType = dto.getQrType() != null ? dto.getQrType() : 0;
        Object response = null;

        if (qrType == 0) {
            response = generateDynamicQrCustomerV2(dto);
            result = response;
        } else {
            // Invalid QR type
            result = new ResponseMessageDTO("FAILED", "E46");
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
                transactionEntity.setStatusResponse(0);
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

    private void insertNewTransactionBIDV(UUID transcationUUID, VietQRBIDVCreateDTO dto,
                                          boolean isFromMerchantSync,String traceId,
                                          AccountBankGenerateBIDVDTO accountBank) {
        logger.info("QR generate - start insertNewTransactionBIDV at: " + DateTimeUtil.getCurrentDateTimeUTC());
        try {
            if (Objects.nonNull(accountBank)) {
                TransactionReceiveEntity transactionEntity = new TransactionReceiveEntity();
                transactionEntity.setId(transcationUUID.toString());
                transactionEntity.setBankAccount(accountBank.getBankAccount());
                transactionEntity.setBankId(accountBank.getId());
                transactionEntity.setContent(dto.getContent());
                transactionEntity.setAmount(Long.parseLong(dto.getAmount()));
                transactionEntity.setTime(DateTimeUtil.getCurrentDateTimeUTC());
                transactionEntity.setRefId("");
                transactionEntity.setType(0);
                transactionEntity.setStatus(0);
                transactionEntity.setTraceId(traceId);
                transactionEntity.setTimePaid(0);
                transactionEntity.setTerminalCode(dto.getTerminalCode());
                transactionEntity.setSubCode(StringUtil.getValueNullChecker(dto.getSubTerminalCode()));
                transactionEntity.setQrCode(dto.getQr());
                transactionEntity.setUserId(accountBank.getUserId());
                transactionEntity.setOrderId(dto.getOrderId());
                transactionEntity.setNote(dto.getNote());
                transactionEntity.setStatusResponse(0);
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

    private Object generateDynamicQrCustomerV2(VietQRCreateCustomerDTO dto) {
        Object result = null;
        HttpStatus httpStatus = null;
        UUID transactionUUID = UUID.randomUUID();
        String serviceCode = !StringUtil.isNullOrEmpty(dto.getServiceCode()) ? dto.getServiceCode() : "";
        VietQRDTO vietQRDTO = null;

        switch (dto.getBankCode().toUpperCase()) {
            case "MB":
                String qrMMS = "";
                String checkExistedMMSBank = accountBankReceiveService.checkMMSBankAccount(dto.getBankAccount());
                boolean checkMMS = false;
                String transType = dto.getTransType() != null ? dto.getTransType().trim() : "C";
                if (checkExistedMMSBank != null && !checkExistedMMSBank.trim().isEmpty() && transType.equals("C")) {
                    checkMMS = true;
                }

                // Flow 1
                if (!checkMMS) {
                    String traceId = "VQR" + RandomCodeUtil.generateRandomUUID();
                    String bankTypeId = dto.getTransType() == null || dto.getTransType().trim().toUpperCase().equals("C")
                            ? bankTypeService.getBankTypeIdByBankCode(dto.getBankCode())
                            : bankTypeService.getBankTypeIdByBankCode(dto.getCustomerBankCode());
                    VietQRDTO vietQRDTOs = new VietQRDTO();
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
                                    vietQRDTOs.setBankCode(bankTypeEntity.getBankCode());
                                    vietQRDTOs.setBankName(bankTypeEntity.getBankName());
                                    vietQRDTOs.setBankAccount(accountBankEntity.getBankAccount());
                                    vietQRDTOs.setUserBankName(accountBankEntity.getBankAccountName().toUpperCase());
                                    vietQRDTOs.setAmount(dto.getAmount() + "");
                                    vietQRDTOs.setContent(content);
                                    vietQRDTOs.setQrCode(qr);
                                    vietQRDTOs.setImgId(bankTypeEntity.getImgId());
                                    vietQRDTOs.setExisting(1);
                                    vietQRDTOs.setTransactionId("");
                                    vietQRDTOs.setTerminalCode(dto.getTerminalCode());
                                    vietQRDTOs.setServiceCode(dto.getServiceCode());
                                    vietQRDTOs.setOrderId(dto.getOrderId());
                                    vietQRDTOs.setAdditionalData(dto.getAdditionalData());
                                    String refId = TransactionRefIdUtil.encryptTransactionId(transactionUUID.toString());
                                    String qrLink = EnvironmentUtil.getQRLink() + refId;
                                    vietQRDTOs.setTransactionRefId(refId);
                                    vietQRDTOs.setQrLink(qrLink);
                                    result = vietQRDTOs;
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
                                    vietQRDTOs.setBankCode(bankTypeEntity.getBankCode());
                                    vietQRDTOs.setBankName(bankTypeEntity.getBankName());
                                    vietQRDTOs.setBankAccount(bankAccount);
                                    vietQRDTOs.setUserBankName(userBankName);
                                    vietQRDTOs.setAmount(dto.getAmount() + "");
                                    vietQRDTOs.setContent(content);
                                    vietQRDTOs.setQrCode(qr);
                                    vietQRDTOs.setImgId(bankTypeEntity.getImgId());
                                    vietQRDTOs.setExisting(0);
                                    vietQRDTOs.setServiceCode(dto.getServiceCode());
                                    vietQRDTOs.setOrderId(dto.getOrderId());
                                    vietQRDTOs.setAdditionalData(dto.getAdditionalData());
                                    result = vietQRDTOs;

                                }
                            } else {
                                result = new ResponseMessageDTO("FAILED", "E24");
                            }
                        } else {
                            result = new ResponseMessageDTO("FAILED", "E26");
                        }
                        return result;
                    } catch (Exception e) {
                        logger.error(e.toString());
                        result = new ResponseMessageDTO("FAILED", "Unexpected Error");
                        return result;
                    }finally {
                        // Insert transaction logic for Flow 1
                        if (result instanceof VietQRDTO) {
                            AccountBankReceiveEntity accountBankQR = accountBankReceiveService.getAccountBankByBankAccountAndBankTypeId(dto.getBankAccount(), bankTypeId);
                            if (accountBankQR != null) {
                                VietQRCreateDTO vietQRCreateDTO = new VietQRCreateDTO();
                                vietQRCreateDTO.setBankId(accountBankQR.getId());
                                vietQRCreateDTO.setAmount(dto.getAmount() + "");
                                vietQRCreateDTO.setContent(dto.getContent());
                                vietQRCreateDTO.setUserId(accountBankQR.getUserId());
                                vietQRCreateDTO.setTerminalCode(dto.getTerminalCode());
                                vietQRCreateDTO.setServiceCode(serviceCode);
                                vietQRCreateDTO.setTransType("C");
                                vietQRCreateDTO.setUrlLink(StringUtil.getValueNullChecker(dto.getUrlLink()));
                                vietQRCreateDTO.setAdditionalData(dto.getAdditionalData());
                                Thread thread1 = new Thread(() ->
                                        insertNewTransaction(transactionUUID, traceId, vietQRCreateDTO, vietQRDTOs, dto.getOrderId(), dto.getSign(), true)
                                );
                                thread1.start();
                            }
                        }
                    }
                } else {
                    try {
                        // 1. Validate input (amount, content, bankCode)
                        if (checkRequestBodyFlow2(dto)) {
                            AccountBankReceiveEntity accountBankEntity = accountBankReceiveService
                                    .getAccountBankByBankAccountAndBankTypeId(dto.getBankAccount(), "aa4e489b-254e-4351-9cd4-f62e09c63ebc");
                            if (accountBankEntity != null) {
                                String terminalId = terminalBankService.getTerminalBankQRByBankAccount(dto.getBankAccount());
                                if (terminalId == null) {
                                    // Terminal not found error
                                    result = new ResponseMessageDTO("FAILED", "E35");
                                } else {
                                    TokenProductBankDTO tokenBankDTO = MBTokenUtil.getMBBankToken();
                                    //TokenProductBankDTO tokenBankDTO = new TokenProductBankDTO();
                                    if (tokenBankDTO != null) {
                                        String content = StringUtil.isNullOrEmpty(dto.getContent()) ?
                                                "VQR" + RandomCodeUtil.generateRandomUUID() : dto.getContent();
                                        if (accountBankEntity.getBankAccount().equals("4144898989")) {
                                            content = !StringUtil.isNullOrEmpty(dto.getContent()) ?
                                                    (dto.getContent() + " " + "Ghe Massage AeonBT") : "Ghe Massage AeonBT";
                                        }
                                        VietQRMMSRequestDTO requestDTO = new VietQRMMSRequestDTO();
                                        requestDTO.setToken(tokenBankDTO.getAccess_token());
                                        requestDTO.setTerminalId(terminalId);
                                        requestDTO.setAmount(dto.getAmount() + "");
                                        requestDTO.setContent(content);
                                        requestDTO.setOrderId(dto.getOrderId());
                                        ResponseMessageDTO responseMessageDTO = requestVietQRMMS(requestDTO);
                                        if (Objects.nonNull(responseMessageDTO) && "SUCCESS".equals(responseMessageDTO.getStatus())) {
                                            String qrCode = responseMessageDTO.getMessage();
                                            vietQRDTO = new VietQRDTO();
                                            IBankTypeQR bankTypeEntity = bankTypeService.getBankTypeQRById("aa4e489b-254e-4351-9cd4-f62e09c63ebc");
                                            vietQRDTO.setBankCode(bankTypeEntity.getBankCode());
                                            vietQRDTO.setBankName(bankTypeEntity.getBankName());
                                            vietQRDTO.setBankAccount(accountBankEntity.getBankAccount());
                                            vietQRDTO.setUserBankName(accountBankEntity.getBankAccountName().toUpperCase());
                                            vietQRDTO.setAmount(dto.getAmount() + "");
                                            vietQRDTO.setContent(content);
                                            vietQRDTO.setQrCode(qrCode);
                                            vietQRDTO.setImgId(bankTypeEntity.getImgId());
                                            vietQRDTO.setExisting(1);
                                            vietQRDTO.setTransactionId("");
                                            vietQRDTO.setTerminalCode(dto.getTerminalCode());
                                            vietQRDTO.setTransactionRefId(TransactionRefIdUtil.encryptTransactionId(transactionUUID.toString()));
                                            vietQRDTO.setQrLink(EnvironmentUtil.getQRLink() + vietQRDTO.getTransactionRefId());
                                            vietQRDTO.setOrderId(dto.getOrderId());
                                            vietQRDTO.setAdditionalData(dto.getAdditionalData());
                                            vietQRDTO.setServiceCode(StringUtil.getValueNullChecker(dto.getServiceCode()));
                                            vietQRDTO.setSubTerminalCode(StringUtil.getValueNullChecker(dto.getSubTerminalCode()));
                                            result = vietQRDTO;
                                        } else {
                                            result = new ResponseMessageDTO("FAILED", responseMessageDTO != null ? responseMessageDTO.getMessage() : "E05");
                                        }



//                                            String qrCodes = "00020101021238570010A000000727012700069704220113VQRQ00027klkm0208QRIBFTTA53037045405120005802VN62270107NPS6869081241791304 SaB63046F1F";
//                                            vietQRDTO = new VietQRDTO();
//                                            IBankTypeQR bankTypeEntity = bankTypeService.getBankTypeQRById("aa4e489b-254e-4351-9cd4-f62e09c63ebc");
//                                            vietQRDTO.setBankCode(bankTypeEntity.getBankCode());
//                                            vietQRDTO.setBankName(bankTypeEntity.getBankName());
//                                            vietQRDTO.setBankAccount(accountBankEntity.getBankAccount());
//                                            vietQRDTO.setUserBankName(accountBankEntity.getBankAccountName().toUpperCase());
//                                            vietQRDTO.setAmount(dto.getAmount() + "");
//                                            vietQRDTO.setContent(content);
//                                            //vietQRDTO.setQrCode(qrCode);
//                                            vietQRDTO.setQrCode(qrCodes);
//                                            vietQRDTO.setImgId(bankTypeEntity.getImgId());
//                                            vietQRDTO.setExisting(1);
//                                            vietQRDTO.setTransactionId("");
//                                            vietQRDTO.setTerminalCode(dto.getTerminalCode());
//                                            vietQRDTO.setTransactionRefId(TransactionRefIdUtil.encryptTransactionId(transactionUUID.toString()));
//                                            vietQRDTO.setQrLink(EnvironmentUtil.getQRLink() + vietQRDTO.getTransactionRefId());
//                                            vietQRDTO.setOrderId(dto.getOrderId());
//                                            vietQRDTO.setAdditionalData(dto.getAdditionalData());
//                                            vietQRDTO.setServiceCode(StringUtil.getValueNullChecker(dto.getServiceCode()));
//                                            vietQRDTO.setSubTerminalCode(StringUtil.getValueNullChecker(dto.getSubTerminalCode()));
//                                            result = vietQRDTO;

                                    } else {
                                        result = new ResponseMessageDTO("FAILED", "E05");
                                    }
                                }
                            } else {
                                result = new ResponseMessageDTO("FAILED", "E36");
                            }
                        } else {
                            result = new ResponseMessageDTO("FAILED", "E34");
                        }
                    } catch (Exception e) {
                        // General error handling for unexpected errors
                        logger.error("generateDynamicQrCustomer Flow 2: ERROR: " + e.toString());
                        result = new ResponseMessageDTO("FAILED", "E05");

                    } finally {
                        // Insert transaction logic for Flow 2
                        if (result instanceof VietQRDTO && vietQRDTO.getQrCode() != null) {
                            VietQRMMSCreateDTO vietQRMMSCreateDTO = new VietQRMMSCreateDTO();
                            vietQRMMSCreateDTO.setBankAccount(dto.getBankAccount());
                            vietQRMMSCreateDTO.setBankCode(dto.getBankCode());
                            vietQRMMSCreateDTO.setAmount(dto.getAmount() + "");
                            vietQRMMSCreateDTO.setContent(dto.getContent());
                            vietQRMMSCreateDTO.setOrderId(dto.getOrderId());
                            vietQRMMSCreateDTO.setSign(dto.getSign());
                            vietQRMMSCreateDTO.setTerminalCode(dto.getTerminalCode());
                            vietQRMMSCreateDTO.setNote(dto.getNote());
                            vietQRMMSCreateDTO.setServiceCode(serviceCode);
                            vietQRMMSCreateDTO.setSubTerminalCode(dto.getSubTerminalCode());
                            vietQRMMSCreateDTO.setUrlLink(StringUtil.getValueNullChecker(dto.getUrlLink()));
                            vietQRMMSCreateDTO.setAdditionalData(dto.getAdditionalData());
                            // Ensure accountBankEntity is properly fetched
                            AccountBankReceiveEntity finalAccountBankEntity = accountBankReceiveService
                                    .getAccountBankByBankAccountAndBankTypeId(dto.getBankAccount(), "aa4e489b-254e-4351-9cd4-f62e09c63ebc");
                            VietQRDTO finalVietQRDTO1 = vietQRDTO;

                            Thread thread3 = new Thread(() ->
                                    insertNewTransactionFlow2(
                                            finalVietQRDTO1.getQrCode(),
                                            transactionUUID.toString(),
                                            finalAccountBankEntity,
                                            vietQRMMSCreateDTO,
                                            LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))
                            );
                            thread3.start();
                        }
                    }
                }
                break;
            case "BIDV":
                String traceBIDVId = "VQR" + RandomCodeUtil.generateRandomUUID();
                String qr = "";
                String billId = "";
                BankCaiTypeDTO bankCaiTypeDTOBIDV = null;
                AccountBankGenerateBIDVDTO accountBankBIDV = null;
                if (dto.getTransType() == null || dto.getTransType().trim().equalsIgnoreCase("C")) {
                    bankCaiTypeDTOBIDV = bankTypeService.getBankCaiByBankCode(dto.getBankCode());
                } else {
                    bankCaiTypeDTOBIDV = bankTypeService.getBankCaiByBankCode(dto.getCustomerBankCode());
                }
                vietQRDTO = new VietQRDTO();
                try {
                    if (dto.getContent().length() <= 50) {
                        // check if generate qr with transtype = D or C
                        // if D => generate with customer information
                        // if C => do normal
                        // find bankTypeId by bankcode
                        if (Objects.nonNull(bankCaiTypeDTOBIDV)) {
                            // find bank by bankAccount and banktypeId

                            if (dto.getTransType() == null || dto.getTransType().trim().equalsIgnoreCase("C")) {
                                accountBankBIDV = accountBankReceiveService
                                        .getAccountBankBIDVByBankAccountAndBankTypeId(dto.getBankAccount(),
                                                bankCaiTypeDTOBIDV.getId());
                            } else {
                                accountBankBIDV = accountBankReceiveService
                                        .getAccountBankBIDVByBankAccountAndBankTypeId(dto.getCustomerBankAccount(),
                                                bankCaiTypeDTOBIDV.getId());
                            }
                            if (Objects.nonNull(accountBankBIDV)) {
                                // get cai value
                                billId = getRandomBillId();
                                String content = billId;
                                // generate qr BIDV
                                VietQRCreateDTO vietQRCreateDTO = new VietQRCreateDTO();
                                vietQRCreateDTO.setBankId(accountBankBIDV.getId());
                                vietQRCreateDTO.setAmount(dto.getAmount() + "");
                                vietQRCreateDTO.setContent(billId);
                                vietQRCreateDTO.setUserId(accountBankBIDV.getUserId());
                                vietQRCreateDTO.setTerminalCode(dto.getTerminalCode());
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
                                        insertNewCustomerInvoiceTransBIDV(vietQRCreateDTO, accountBankBIDV, billId);
                                // insert success transaction_receive
                                if ("SUCCESS".equals(responseMessageDTO.getStatus())) {
                                    VietQRVaRequestDTO vietQRVaRequestDTO = new VietQRVaRequestDTO();
                                    if ("0".equals(dto.getAmount())) {
                                        vietQRVaRequestDTO.setAmount("");
                                    } else {
                                        vietQRVaRequestDTO.setAmount(dto.getAmount() + "");
                                    }
                                    vietQRVaRequestDTO.setAmount(dto.getAmount() + "");
                                    vietQRVaRequestDTO.setBillId(billId);
                                    vietQRVaRequestDTO.setUserBankName(accountBankBIDV.getBankAccountName());
                                    vietQRVaRequestDTO.setDescription(StringUtil.getValueNullChecker(billId));

                                    ResponseMessageDTO generateVaInvoiceVietQR = new ResponseMessageDTO("SUCCESS", "");
                                    if (!EnvironmentUtil.isProduction()) {
                                        String bankAccountRequest= "";
                                        if (dto.getTransType() == null || dto.getTransType().trim().equalsIgnoreCase("C")) {
                                            bankAccountRequest = dto.getBankAccount();
                                        } else {
                                            bankAccountRequest = dto.getCustomerBankAccount();
                                        }
                                        VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
                                        vietQRGenerateDTO.setCaiValue(bankCaiTypeDTOBIDV.getCaiValue());
                                        vietQRGenerateDTO.setAmount(dto.getAmount() + "");
                                        content = traceBIDVId + " " + billId;
                                        vietQRGenerateDTO.setContent(content);
                                        vietQRGenerateDTO.setBankAccount(bankAccountRequest);
                                        qr = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);
                                        generateVaInvoiceVietQR = new ResponseMessageDTO("SUCCESS", qr);
                                    } else {
                                        if ("0".equals(dto.getAmount())) {
                                            vietQRVaRequestDTO.setAmount("");
                                        } else {
                                            vietQRVaRequestDTO.setAmount(dto.getAmount() + "");
                                        }
                                        generateVaInvoiceVietQR = CustomerVaUtil.generateVaInvoiceVietQR(vietQRVaRequestDTO, accountBankBIDV.getCustomerId());
                                    }
                                    if ("SUCCESS".equals(generateVaInvoiceVietQR.getStatus())) {
                                        qr = generateVaInvoiceVietQR.getMessage();

                                        // generate VietQRDTO
                                        vietQRDTO.setBankCode(bankCaiTypeDTOBIDV.getBankCode());
                                        vietQRDTO.setBankName(bankCaiTypeDTOBIDV.getBankName());
                                        vietQRDTO.setBankAccount(accountBankBIDV.getBankAccount());
                                        vietQRDTO.setUserBankName(accountBankBIDV.getBankAccountName().toUpperCase());
                                        vietQRDTO.setAmount(dto.getAmount() + "");
                                        vietQRDTO.setContent(content);
                                        vietQRDTO.setQrCode(qr);
                                        vietQRDTO.setImgId(bankCaiTypeDTOBIDV.getImgId());
                                        vietQRDTO.setExisting(1);
                                        vietQRDTO.setTransactionId("");
                                        vietQRDTO.setTerminalCode(dto.getTerminalCode());
                                        vietQRDTO.setTransactionRefId(TransactionRefIdUtil.encryptTransactionId(transactionUUID.toString()));
                                        vietQRDTO.setQrLink(EnvironmentUtil.getQRLink() + vietQRDTO.getTransactionRefId());
                                        vietQRDTO.setOrderId(dto.getOrderId());
                                        vietQRDTO.setServiceCode(StringUtil.getValueNullChecker(dto.getServiceCode()));
                                        vietQRDTO.setSubTerminalCode(StringUtil.getValueNullChecker(dto.getSubTerminalCode()));
                                        vietQRDTO.setAdditionalData(dto.getAdditionalData());
                                        //
                                        result = vietQRDTO;
                                    } else {
                                        result = new ResponseMessageDTO("FAILED", "E05");
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
                                // generate VietQRGenerateDTO
                                VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
                                vietQRGenerateDTO.setCaiValue(bankCaiTypeDTOBIDV.getCaiValue());
                                vietQRGenerateDTO.setAmount(dto.getAmount() + "");
                                content = billId;
                                vietQRGenerateDTO.setContent(content);
                                vietQRGenerateDTO.setBankAccount(bankAccount);
                                qr = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);
                                //
                                // generate VietQRDTO
                                vietQRDTO.setBankCode(bankCaiTypeDTOBIDV.getBankCode());
                                vietQRDTO.setBankName(bankCaiTypeDTOBIDV.getBankName());
                                vietQRDTO.setBankAccount(bankAccount);
                                vietQRDTO.setUserBankName(userBankName);
                                vietQRDTO.setAmount(dto.getAmount() + "");
                                vietQRDTO.setContent(content);
                                vietQRDTO.setTerminalCode(dto.getTerminalCode());
                                vietQRDTO.setQrCode(qr);
                                vietQRDTO.setImgId(bankCaiTypeDTOBIDV.getImgId());
                                vietQRDTO.setExisting(0);
                                vietQRDTO.setTransactionId("");
                                vietQRDTO.setOrderId(dto.getOrderId());
                                vietQRDTO.setServiceCode(StringUtil.getValueNullChecker(dto.getServiceCode()));
                                vietQRDTO.setSubTerminalCode(StringUtil.getValueNullChecker(dto.getSubTerminalCode()));
                                vietQRDTO.setTransactionRefId(TransactionRefIdUtil.encryptTransactionId(transactionUUID.toString()));
                                vietQRDTO.setQrLink(EnvironmentUtil.getQRLink() + vietQRDTO.getTransactionRefId());
                                vietQRDTO.setAdditionalData(dto.getAdditionalData());
                                result = vietQRDTO;
                            }
                        } else {
                            result = new ResponseMessageDTO("FAILED", "E24");
                        }
                    } else {
                        result = new ResponseMessageDTO("FAILED", "E26");
                    }
                } catch (Exception e) {
                    logger.error(e.toString());
                    //System.out.println(e.toString());
                    result = new ResponseMessageDTO("FAILED", "Unexpected Error");
                } finally {
                    if (Objects.nonNull(accountBankBIDV) && !StringUtil.isNullOrEmpty(qr)) {
                        VietQRBIDVCreateDTO dto1 = new VietQRBIDVCreateDTO();
                        dto1.setContent(dto.getContent());
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
                        dto1.setSubTerminalCode(StringUtil.getValueNullChecker(dto.getSubTerminalCode()));
                        dto1.setQr(qr);
                        dto1.setAdditionalData(dto.getAdditionalData());
                        AccountBankGenerateBIDVDTO finalAccountBankBIDV = accountBankBIDV;
                        Thread thread = new Thread(() -> {
                            insertNewTransactionBIDV(transactionUUID, dto1, false, traceBIDVId,
                                    finalAccountBankBIDV);
                        });
                        thread.start();
                    }
                }
                break;
            default:
                // Default case
                try {
                    String traceId = "VQR" + RandomCodeUtil.generateRandomUUID();
                    BankCaiTypeDTO bankCaiTypeDTO = dto.getTransType() == null || dto.getTransType().trim().equalsIgnoreCase("C")
                            ? bankTypeService.getBankCaiByBankCode(dto.getBankCode())
                            : bankTypeService.getBankCaiByBankCode(dto.getCustomerBankCode());
                    if (dto.getContent().length() <= 50 && bankCaiTypeDTO != null) {
                        String bankAccount = dto.getTransType() == null || dto.getTransType().trim().equalsIgnoreCase("C")
                                ? dto.getBankAccount()
                                : dto.getCustomerBankAccount();
                        String userBankName = dto.getTransType() == null || dto.getTransType().trim().equalsIgnoreCase("C")
                                ? dto.getUserBankName().trim().toUpperCase()
                                : dto.getCustomerName().trim().toUpperCase();
                        VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
                        vietQRGenerateDTO.setCaiValue(bankCaiTypeDTO.getCaiValue());
                        vietQRGenerateDTO.setAmount(dto.getAmount() + "");
                        String content = dto.getReconciliation() == null || dto.getReconciliation()
                                ? traceId + " " + dto.getContent()
                                : dto.getContent();
                        vietQRGenerateDTO.setContent(content);
                        vietQRGenerateDTO.setBankAccount(bankAccount);
                        qr = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);
                        vietQRDTO = new VietQRDTO();
                        vietQRDTO.setBankCode(bankCaiTypeDTO.getBankCode());
                        vietQRDTO.setBankName(bankCaiTypeDTO.getBankName());
                        vietQRDTO.setBankAccount(bankAccount);
                        vietQRDTO.setUserBankName(userBankName);
                        vietQRDTO.setAmount(dto.getAmount() + "");
                        vietQRDTO.setContent(content);
                        vietQRDTO.setQrCode(qr);
                        vietQRDTO.setImgId(bankCaiTypeDTO.getImgId());
                        vietQRDTO.setExisting(0);
                        vietQRDTO.setOrderId(dto.getOrderId());
                        vietQRDTO.setAdditionalData(new ArrayList<>());
                        vietQRDTO.setServiceCode(StringUtil.getValueNullChecker(dto.getServiceCode()));
                        vietQRDTO.setSubTerminalCode(StringUtil.getValueNullChecker(dto.getSubTerminalCode()));
                        vietQRDTO.setAdditionalData(dto.getAdditionalData());
                        result = vietQRDTO;
                    } else {
                        result = new ResponseMessageDTO("FAILED", "E26");
                    }
                } catch (Exception e) {
                    logger.error(e.toString());
                    result = new ResponseMessageDTO("FAILED", "Unexpected Error");
                }
                break;
        }
        return result;
    }

    private boolean checkRequestBodyFlow2(VietQRCreateCustomerDTO dto) {
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
            if (dto != null
                    && content.length() <= 19
                    && orderId.length() <= 22
                    && dto.getAmount() != null && !dto.getBankAccount().trim().isEmpty()
                    && dto.getBankAccount() != null && !dto.getBankAccount().trim().isEmpty()
                    && dto.getBankCode() != null && dto.getBankCode().equals("MB")
                    && StringUtil.isLatinAndNumeric(content)) {
                result = true;
            }
        } catch (Exception e) {
            logger.error("checkRequestBody: ERROR: " + e.toString());
        }
        return result;
    }

    private ResponseMessageDTO requestVietQRMMS(VietQRMMSRequestDTO dto) {
        ResponseMessageDTO result = null;
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
            String content = "";
            if (dto.getContent() != null && !dto.getContent().trim().isEmpty()) {
                content = dto.getContent();
            } else {
                String traceId = "VQR" + RandomCodeUtil.generateRandomUUID();
                content = traceId;
            }
            data.put("transactionPurpose", content);
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
                        String qrCode = rootNode.get("data").get("qrcode").asText();
                        logger.info("requestVietQRMMS: RESPONSE qrcode: " + qrCode);
                        result = new ResponseMessageDTO("SUCCESS", qrCode);
                    } else {
                        logger.info("requestVietQRMMS: RESPONSE qrcode is null");
                    }
                } else {
                    logger.info("requestVietQRMMS: RESPONSE data is null");
                }
            } else {
                String json = response.bodyToMono(String.class).block();
                logger.error("requestVietQRMMS: RESPONSE: ERROR " + response.statusCode().value() + " - " + json);
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(json);
                if (rootNode.get("errorCode") != null) {
                    String getMessageBankCode = getMessageBankCode(rootNode.get("errorCode").asText());
                    result = new ResponseMessageDTO("FAILED", getMessageBankCode);
                } else {
                    logger.info("requestVietQRMMS: RESPONSE data is null");
                }
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

    String getMessageBankCode(String errBankCode) {
        switch (errBankCode) {
            case "404":
                return "E165";
            case "203":
                return "E165";
            case "205":
                return "E166";
            default:
                return "E05";
        }
    }

    @MqttTopicHandler(topic = "vietqr/refund-request/#")
    public void handleRefundRequest(String topic, MqttMessage message) {
        ObjectMapper mapper = new ObjectMapper();
        try {

            RefundRequestDTO dto = mapper.readValue(message.getPayload(), RefundRequestDTO.class);
            ResponseMessageDTO result = validateRefundRequest(dto);

            if ("SUCCESS".equals(result.getStatus())) {
                result = processRefund(dto);
            }
            String responseTopic = "vietqr/refund-response/" + dto.getBankAccount();
            publishResponse(responseTopic, result);

        } catch (Exception e) {
            logger.error("Error processing refund request: " + e.getMessage());
            publishResponse("vietqr/refund-response/", new ResponseMessageDTO("FAILED", "Error: " + e.getMessage()));
        }
    }

    // Hàm kiểm tra tính hợp lệ của yêu cầu refund
    private ResponseMessageDTO validateRefundRequest(RefundRequestDTO dto) {
        if (dto == null) {
            logger.error("Invalid request body");
            return new ResponseMessageDTO("FAILED", "E46");
        }

        String idempotencyKey = BankEncryptUtil.generateIdempotencyKey(dto.getReferenceNumber(), dto.getBankAccount());
        Optional<String> existingResponse = idempotencyService.getResponseForKey(idempotencyKey);

        if (existingResponse.isPresent()) {
            logger.error("Duplicate request detected with key: " + idempotencyKey);
            return new ResponseMessageDTO("FAILED", "E158");
        }

        return new ResponseMessageDTO("SUCCESS", "");
    }

    // Hàm xử lý refund
    private ResponseMessageDTO processRefund(RefundRequestDTO dto) {
        String idempotencyKey = BankEncryptUtil.generateIdempotencyKey(dto.getReferenceNumber(), dto.getBankAccount());
        long time = DateTimeUtil.getCurrentDateTimeUTC();
        String username = accountCustomerBankService.findUsernameByBankAccount(dto.getBankAccount());

        List<String> checkExistedCustomerSync = accountCustomerBankService.checkExistedCustomerSyncByUsername(username);
        if (checkExistedCustomerSync == null || checkExistedCustomerSync.isEmpty()) {
            logger.error("Merchant not found");
            return new ResponseMessageDTO("FAILED", "E104");
        }

        String checkValidBankAccount = accountCustomerBankService.checkExistedBankAccountIntoMerchant(dto.getBankAccount(), checkExistedCustomerSync.get(0));
        if (checkValidBankAccount == null || checkValidBankAccount.trim().isEmpty()) {
            logger.error("Bank account does not match with merchant info");
            return new ResponseMessageDTO("FAILED", "E77");
        }

        String secretKey = accountCustomerBankService.checkSecretKey(dto.getBankAccount(), checkExistedCustomerSync.get(0));
        String checkSum = BankEncryptUtil.generateRefundMD5Checksum(secretKey, dto.getReferenceNumber(), dto.getAmount(), dto.getBankAccount());
       // String checkSum = "c68ee42e728b9dbb13dcb2a3d509b877";
        if (!BankEncryptUtil.isMatchChecksum(dto.getCheckSum(), checkSum)) {
            logger.error("Invalid checksum");
            return new ResponseMessageDTO("FAILED", "E39");
        }

        boolean checkIdempotency = idempotencyService.saveResponseForKey(idempotencyKey, dto.getReferenceNumber(), 30);
        if (!checkIdempotency) {
            logger.error("Processing another refund");
            return new ResponseMessageDTO("FAILED", "E158");
        }

        TerminalBankEntity terminalBankEntity = terminalBankService.getTerminalBankByBankAccount(dto.getBankAccount());
        if (terminalBankEntity == null) {
            logger.error("Invalid terminal bank account");
            return new ResponseMessageDTO("FAILED", "E42");
        }

        // Tạo log refund sử dụng hàm createRefundLogEntity
        TransactionRefundLogEntity refundLogEntity = createRefundLogEntity(dto, time);

        String refundResult = refundFromMB(terminalBankEntity.getTerminalId(), dto.getReferenceNumber(), dto.getAmount(), dto.getContent());

        if (refundResult != null && refundResult.trim().contains("FT")) {
                refundLogEntity.setStatus(1);
                refundLogEntity.setReferenceNumber(refundResult);
                refundLogEntity.setMessage(refundResult);
                insertTransactionRefundRedis(refundResult, dto, terminalBankEntity);
                return new ResponseMessageDTO("SUCCESS", dto.getReferenceNumber());
        } else {
                return handleRefundError(refundResult, dto);
        }
    }

    // Hàm tạo đối tượng TransactionRefundLogEntity
    private TransactionRefundLogEntity createRefundLogEntity(RefundRequestDTO dto, long time) {
        TransactionRefundLogEntity refundLogEntity = new TransactionRefundLogEntity();
        refundLogEntity.setId(UUID.randomUUID().toString());
        refundLogEntity.setBankAccount(dto.getBankAccount());
        refundLogEntity.setReferenceNumber(dto.getReferenceNumber());
        refundLogEntity.setContent(dto.getContent());
        refundLogEntity.setAmount(Long.parseLong(dto.getAmount()));
        refundLogEntity.setTimeCreated(time);
        refundLogEntity.setTimeResponse(DateTimeUtil.getCurrentDateTimeUTC());
        refundLogEntity.setRefNumber(dto.getReferenceNumber());
        refundLogEntity.setCheckSum(dto.getCheckSum());
        return refundLogEntity;
    }

    // Hàm xử lý lỗi refund dựa trên mã lỗi
    private ResponseMessageDTO handleRefundError(String refundResult, RefundRequestDTO dto) {
        switch (refundResult) {
            case "4863":
                logger.error("FT CODE IS NOT EXISTED: " + dto.getReferenceNumber());
                return new ResponseMessageDTO("FAILED", "E44");
            case "4857":
                logger.error("INVALID AMOUNT: " + dto.getReferenceNumber());
                return new ResponseMessageDTO("FAILED", "E45");
            case "002":
                logger.error("CONNECTION TIMEOUT: " + dto.getReferenceNumber());
                return new ResponseMessageDTO("FAILED", "E215");
            case "4877":
                logger.error("RECORD DOESN'T EXIST TBL MMS_PAYMENT_BANK: " + dto.getReferenceNumber());
                return new ResponseMessageDTO("FAILED", "E218");
            case "201":
                logger.error("ACCOUNT NUMBER OR CARD IS INVALID: " + dto.getReferenceNumber());
                return new ResponseMessageDTO("FAILED", "E216");
            case "412":
                logger.error("BENEFICIARY BANK HASN’T JOINED THE SERVICE: " + dto.getReferenceNumber());
                return new ResponseMessageDTO("FAILED", "E217");
            default:
                logger.error("Unexpected error during refund: " + dto.getReferenceNumber());
                return new ResponseMessageDTO("FAILED", "E43");
        }
    }

    // Hàm gửi phản hồi kết quả qua MQTT
    private void publishResponse(String topic, ResponseMessageDTO response) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String responsePayload = mapper.writeValueAsString(response);
            mqttListenerService.publishMessageToCommonTopic(topic, responsePayload);
            logger.info("Response sent to topic: " + topic + " Payload: " + responsePayload);
        } catch (JsonProcessingException e) {
            logger.error("Error publishing response: " + e.getMessage());
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }

    private String refundFromMB(String terminalId, String ftCode, String amount, String content) {
        String result = null;
        try {
            TokenProductBankDTO token = getBankToken();
            if (token != null) {
                // {
                // "terminalID": "BLC2",
                // "traceTransfer":"FT23149029920410",
                // "amount":"30000",
                // "content":"Hoan tien test"
                // }
                UUID clientMessageId = UUID.randomUUID();
                String transactionId = RandomCodeUtil.generateRandomId(12);
                Map<String, Object> data = new HashMap<>();
                data.put("terminalID", terminalId);
                data.put("traceTransfer", ftCode);
                data.put("amount", amount);
                data.put("content", content);
                UriComponents uriComponents = UriComponentsBuilder
                        .fromHttpUrl(EnvironmentUtil.getBankUrl()
                                + "ms/offus/public/payment-service/payment/v1.0/refundVietQR")
                        .buildAndExpand(/* add url parameter here */);
                WebClient webClient = WebClient.builder()
                        .baseUrl(
                                EnvironmentUtil.getBankUrl()
                                        + "ms/offus/public/payment-service/payment/v1.0/refundVietQR")
                        .build();
                Mono<ClientResponse> responseMono = webClient.post()
                        .uri(uriComponents.toUri())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("clientMessageId", clientMessageId.toString())
                        .header("userName", EnvironmentUtil.getUsernameAPI())
                        .header("secretKey", EnvironmentUtil.getSecretKeyAPI())
                        .header("transactionID", transactionId.toString())
                        .header("Authorization", "Bearer " + getBankToken().getAccess_token())
                        .body(BodyInserters.fromValue(data))
                        .exchange();
                ClientResponse response = responseMono.block();

                String json = response.bodyToMono(String.class).block();
                //System.out.println("refundFromMB: RESPONSE: " + json + " FT Code: " + ftCode);
                logger.info("refundFromMB: RESPONSE: " + json + " FT Code: " + ftCode);
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(json);
                if (rootNode.get("errorCode") != null) {
                    // 000
                    if ((rootNode.get("errorCode").asText()).trim().equals("000")) {
                        if (rootNode.get("data").get("ft") != null) {
                            result = rootNode.get("data").get("ft").asText();
                            logger.info("refundFromMB: RESPONSE FT: " + result);
                        } else {
                            logger.error("refundFromMB: RESPONSE: FT NULL");
                        }
                    }
                    // "4863" FT code not existed
                    else if ((rootNode.get("errorCode").asText()).trim().equals("4863")) {
                        result = "4863";
                    }
                    // "4857" Invalid amount
                    else if ((rootNode.get("errorCode").asText()).trim().equals("4857")) {
                        result = "4857";
                    }
                } else {
                    logger.error("refundFromMB: RESPONSE: ERROR CODE NULL");
                }

            } else {
                logger.error("ERROR at refundFromMB: " + ftCode + " - " + " TOKEN BANK IS INVALID");
            }
        } catch (Exception e) {
            logger.error("ERROR at refundFromMB: " + ftCode + " - " + e.toString());
        }
        return result;
    }

    private TokenProductBankDTO getBankToken() {
        TokenProductBankDTO result = null;
        try {
            String key = EnvironmentUtil.getUserBankAccess() + ":" + EnvironmentUtil.getPasswordBankAccess();
            String encodedKey = Base64.getEncoder().encodeToString(key.getBytes());
            UriComponents uriComponents = UriComponentsBuilder
                    .fromHttpUrl(EnvironmentUtil.getBankUrl() + "oauth2/v1/token")
                    .buildAndExpand(/* add url parameter here */);
            WebClient webClient = WebClient.builder()
                    .baseUrl(EnvironmentUtil.getBankUrl()
                            + "oauth2/v1/token")
                    .build();
            // Call POST API
            TokenProductBankDTO response = webClient.method(HttpMethod.POST)
                    .uri(uriComponents.toUri())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .header("Authorization", "Basic " + encodedKey)
                    .body(BodyInserters.fromFormData("grant_type", "client_credentials"))
                    .exchange()
                    .flatMap(clientResponse -> {
                        if (clientResponse.statusCode().is2xxSuccessful()) {
                            return clientResponse.bodyToMono(TokenProductBankDTO.class);
                        } else {
                            clientResponse.body((clientHttpResponse, context) -> {
                                logger.info(clientHttpResponse.getBody().collectList().block().toString());
                                return clientHttpResponse.getBody();
                            });
                            return null;
                        }
                    })
                    .block();
            result = response;
        } catch (Exception e) {
            logger.error(e.toString());
        }
        return result;
    }

    private void insertTransactionRefundRedis(String ftCode, RefundRequestDTO dto, TerminalBankEntity terminalBankEntity) {
        try {
            TransactionReceiveEntity transactionReceiveEntity = transactionReceiveService.getTransactionReceiveByRefNumber(dto.getReferenceNumber(), "C");
            if (StringUtil.isNullOrEmpty(dto.getTerminalCode())) {
                if (Objects.nonNull(transactionReceiveEntity)) {
                    dto.setTerminalCode(transactionReceiveEntity.getTerminalCode());
                }
            }
            if (StringUtil.isNullOrEmpty(dto.getSubTerminalCode())) {
                if (Objects.nonNull(transactionReceiveEntity)) {
                    dto.setSubTerminalCode(transactionReceiveEntity.getSubCode());
                }
            }
            String orderId = "";
            if (Objects.nonNull(transactionReceiveEntity)) {
                orderId = transactionReceiveEntity.getOrderId();
                transactionReceiveService.updateTransactionRefundStatus(ftCode,
                        transactionReceiveEntity.getSubCode(),
                        transactionReceiveEntity.getTerminalCode(), orderId, 0);
            }
            RefundMappingRedisDTO refundMappingRedisDTO = new RefundMappingRedisDTO(
                    StringUtil.getValueNullChecker(dto.getTerminalCode()),
                    StringUtil.getValueNullChecker(dto.getSubTerminalCode()),
                    StringUtil.getValueNullChecker(dto.getReferenceNumber()),
                    StringUtil.getValueNullChecker(orderId)
            );
            ObjectMapper mapper = new ObjectMapper();
            idempotencyService.saveResponseForUUIDRefundKey(ftCode, mapper.writeValueAsString(refundMappingRedisDTO), 3600);

        } catch (Exception e) {
            logger.error("insertTransactionRefundRedis: ERROR: " + e.toString() + " at: " + System.currentTimeMillis());
        }
    }

    @Async
    protected void insertNewTransactionFlow2(String qrCode, String transcationUUID,
                                             AccountBankReceiveEntity accountBankReceiveEntity,
                                             VietQRMMSCreateDTO dto,
                                             long time) {
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
            transactionEntity.setSubCode(StringUtil.getValueNullChecker(dto.getSubTerminalCode()));
            transactionEntity.setQrCode(qrCode);
            transactionEntity.setUserId(accountBankReceiveEntity.getUserId());
            transactionEntity.setNote(dto.getNote() != null ? dto.getNote() : "");
            transactionEntity.setStatusResponse(0);
            transactionEntity.setUrlLink(dto.getUrlLink() != null ? dto.getUrlLink() : "");
            transactionEntity.setServiceCode(dto.getServiceCode());
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
            LocalDateTime endTime = LocalDateTime.now();
            long endTimeLong = endTime.toEpochSecond(ZoneOffset.UTC);
            logger.info("insertNewTransaction - end generateVietQRMMS at: " + endTimeLong);
        } catch (Exception e) {
            logger.error("insertNewTransaction - generateVietQRMMS: ERROR: " + e.toString());
        }
    }

    private ResponseMessageDTO insertNewCustomerInvoiceTransBIDV(VietQRCreateDTO dto,
                                                                 AccountBankGenerateBIDVDTO bidvdto, String billId) {
        ResponseMessageDTO responseMessageDTO = new ResponseMessageDTO();
        logger.info("QR generate - start insertNewCustomerInvoiceTransBIDV at: " + DateTimeUtil.getCurrentDateTimeUTC());
        try {
            long amount = 0;
            if (Objects.nonNull(bidvdto) && !StringUtil.isNullOrEmpty(billId)) {
                if (!StringUtil.isNullOrEmpty(bidvdto.getCustomerId())) {
                    CustomerInvoiceEntity entity = new CustomerInvoiceEntity();
                    entity.setId(UUID.randomUUID().toString());
                    entity.setCustomerId(bidvdto.getCustomerId());
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

    @MqttTopicHandler(topic = "vietqr/test/transaction-callback")
    public void testCallbackForCustomer(String topic, MqttMessage message) throws MqttException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            // Chuyển payload thành đối tượng DTO
            TransactionTestCallbackDTO callbackDTO = mapper.readValue(message.getPayload(), TransactionTestCallbackDTO.class);

            if (callbackDTO != null) {
                String bankAccount = callbackDTO.getBankAccount();
                String content = callbackDTO.getContent();
                String amountStr = callbackDTO.getAmount();
                String transType = callbackDTO.getTransType();

                long amount = Long.parseLong(amountStr);
                String username = accountCustomerBankService.findUsernameByBankAccount(bankAccount);

                if (username != null && !username.trim().isEmpty()) {
                    List<String> checkExistedCustomerSync = accountCustomerBankService.checkExistedCustomerSyncByUsername(username);
                    if (checkExistedCustomerSync != null && !checkExistedCustomerSync.isEmpty()) {
                        for (String customerSyncId : checkExistedCustomerSync) {
                            List<String> checkExistedAccCusBank = accountCustomerBankService
                                    .checkExistedAccountCustomerBankByBankAccount(callbackDTO.getBankAccount(), customerSyncId);

                            if (checkExistedAccCusBank != null && !checkExistedAccCusBank.isEmpty()) {
                                TransactionBankDTO dto = new TransactionBankDTO();
                                UUID transactionId = UUID.randomUUID();
                                UUID referenceNumber = UUID.randomUUID();
                                LocalDateTime transactionLD = LocalDateTime.now();
                                long transactionTime = transactionLD.toEpochSecond(ZoneOffset.UTC);

                                dto.setTransactionid(transactionId.toString());
                                dto.setTransactiontime(transactionTime * 1000);
                                dto.setReferencenumber(username + "-" + referenceNumber.toString());
                                dto.setAmount(Integer.parseInt(callbackDTO.getAmount()));
                                dto.setContent(callbackDTO.getContent());
                                dto.setBankaccount(callbackDTO.getBankAccount());
                                dto.setTransType(callbackDTO.getTransType());

                                ResponseMessageDTO result = transactionBankController.insertTransBank(dto);
                                publishResponseMessage(topic, result, mapper);
                            } else {
                                publishResponseMessage(topic, new ResponseMessageDTO("FAILED", "BANK ACCOUNT IS NOT MATCH WITH MERCHANT INFO"), mapper);
                            }
                        }
                    } else {
                        publishResponseMessage(topic, new ResponseMessageDTO("FAILED", "MERCHANT IS NOT EXISTED"), mapper);
                    }
                } else {
                    publishResponseMessage(topic, new ResponseMessageDTO("FAILED", "INVALID USERNAME"), mapper);
                }
            } else {
                publishResponseMessage(topic, new ResponseMessageDTO("FAILED", "INVALID REQUEST BODY"), mapper);
            }
        } catch (Exception e) {
            publishResponseMessage(topic, new ResponseMessageDTO("FAILED", "Error: " + e.getMessage()), mapper);
        }
    }

    // Hàm để publish phản hồi lên đúng topic
    private void publishResponseMessage(String topic, ResponseMessageDTO responseMessage, ObjectMapper mapper) throws JsonProcessingException, MqttException {
        String responsePayload = mapper.writeValueAsString(responseMessage);
        String responseTopic = topic.replace("test/transaction-callback", "response/transaction-callback");
        mqttListenerService.publishMessageToCommonTopic(responseTopic, responsePayload);
        logger.info("Response sent to topic: " + responseTopic + " Payload: " + responsePayload);
    }

    private ResponseMessageDTO pushNewTransactionToCustomerSync(String transReceiveId, CustomerSyncEntity entity,
                                                                TransactionBankCustomerDTO dto,
                                                                long time, int retryCount, List<String> errorCodes) {
        ResponseMessageDTO result = null;
        TransactionLogResponseDTO transactionLogResponseDTO = new TransactionLogResponseDTO();
        // final ResponseMessageDTO[] results = new ResponseMessageDTO[1];
        // final List<ResponseMessageDTO> results = new ArrayList<>();
        // final String[] msg = new String[1];
        if (retryCount > 1 && retryCount <= 5) {
            try {
                Thread.sleep(1000 * (retryCount - 1) + retryCount); // Sleep for 12000 milliseconds (12 seconds)
            } catch (InterruptedException e) {
                // Handle the exception if the thread is interrupted during sleep
                e.printStackTrace();
            }
        } else if (retryCount > 5 && retryCount <= 10) {
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                // Handle the exception if the thread is interrupted during sleep
                e.printStackTrace();
            }
        }
        try {
            transactionLogResponseDTO.setTimeRequest(DateTimeUtil.getCurrentDateTimeUTC());
            logger.info("pushNewTransactionToCustomerSync: orderId: " +
                    dto.getOrderId());
            logger.info("pushNewTransactionToCustomerSync: sign: " + dto.getSign());
            TokenDTO tokenDTO = null;
            if (entity.getUsername() != null && !entity.getUsername().trim().isEmpty() &&
                    entity.getPassword() != null
                    && !entity.getPassword().trim().isEmpty()) {
                tokenDTO = getCustomerSyncToken(transReceiveId, entity, time);
            } else if (entity.getToken() != null && !entity.getToken().trim().isEmpty()) {
                logger.info("Get token from record: " + entity.getId());
                tokenDTO = new TokenDTO(entity.getToken(), "Bearer", 0);
            }
            Map<String, Object> data = new HashMap<>();
            data.put("transactionid", dto.getTransactionid());
            data.put("transactiontime", dto.getTransactiontime());
            data.put("referencenumber", dto.getReferencenumber());
            data.put("amount", dto.getAmount());
            data.put("content", dto.getContent());
            data.put("bankaccount", dto.getBankaccount());
            data.put("transType", dto.getTransType());
            data.put("orderId", dto.getOrderId());
            data.put("sign", dto.getSign());
            data.put("terminalCode", dto.getTerminalCode());
            data.put("urlLink", dto.getUrlLink());
            data.put("subTerminalCode", dto.getSubTerminalCode());
            data.put("serviceCode", "");
            String suffixUrl = "";
            if (entity.getSuffixUrl() != null && !entity.getSuffixUrl().isEmpty()) {
                suffixUrl = entity.getSuffixUrl();
            }
            WebClient.Builder webClientBuilder = WebClient.builder()
                    .baseUrl(entity.getInformation() + "/" + suffixUrl +
                            "/bank/api/transaction-sync");

            if (entity.getIpAddress() != null && !entity.getIpAddress().isEmpty()) {
                webClientBuilder.baseUrl("http://" + entity.getIpAddress() + ":" +
                        entity.getPort() + "/" + suffixUrl
                        + "/bank/api/transaction-sync");
            }

            // Create SSL context to ignore SSL handshake exception
            SslContext sslContext = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build();
            HttpClient httpClient = HttpClient.create().secure(sslContextSpec -> sslContextSpec.sslContext(sslContext));

            WebClient webClient = webClientBuilder.clientConnector(new ReactorClientHttpConnector(httpClient))
                    .build();

            logger.info("uriComponents: " + webClient.get().uri(builder -> builder.path("/").build()).toString());
            System.out
                    .println("uriComponents: " + webClient.get().uri(builder -> builder.path("/").build()).toString());
            // Mono<TransactionResponseDTO> responseMono = null;
            logger.info("pushNewTransactionToCustomerSync request orderId: " + dto.getOrderId()
                    + " at: " + System.currentTimeMillis());
            Mono<ClientResponse> responseMono = null;
            if (tokenDTO != null) {
                responseMono = webClient.post()
                        // .uri("/bank/api/transaction-sync")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + tokenDTO.getAccess_token())
                        .body(BodyInserters.fromValue(data))
                        .exchange();
                // .retrieve()
                // .bodyToMono(TransactionResponseDTO.class);
            } else {
                responseMono = webClient.post()
                        // .uri("/bank/api/transaction-sync")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(data))
                        .exchange();
                // .retrieve()
                // .bodyToMono(TransactionResponseDTO.class);
            }

            ClientResponse response = responseMono.block();
            logger.info("Response pushNewTransactionToCustomerSync response orderId: " + dto.getOrderId()
                    + " at: " + System.currentTimeMillis());
            //System.out.println("response status code: " + response.statusCode());
            try {
                transactionLogResponseDTO.setStatusCode(response.statusCode().value());
                transactionLogResponseDTO.setTimeResponse(DateTimeUtil.getCurrentDateTimeUTC());
            } catch (Exception e) {
            }
            if (response.statusCode().is2xxSuccessful()) {
                String json = response.bodyToMono(String.class).block();
                //System.out.println("Response pushNewTransactionToCustomerSync: " + json);
                logger.info("Response pushNewTransactionToCustomerSync: " + json + " status: " + response.statusCode());
                String errorCode = validateFormatCallbackResponse(json);
                if (!StringUtil.isNullOrEmpty(errorCode)) {
                    // retry callback
                    if (Objects.nonNull(errorCodes) && errorCodes.contains(errorCode)) {
                        if (retryCount < 10) {
                            pushNewTransactionToCustomerSync(transReceiveId, entity,
                                    dto, time, ++retryCount, errorCodes);
                        }
                    }
                }
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(json);
                if (rootNode.get("object") != null) {
                    String reftransactionid = rootNode.get("object").get("reftransactionid").asText();
                    if (reftransactionid != null) {
                        result = new ResponseMessageDTO("SUCCESS", "");
                    } else {
                        result = new ResponseMessageDTO("FAILED", "E05 - " + json);
                    }
                } else {
                    result = new ResponseMessageDTO("FAILED", "E05 - " + json);
                }
            } else {
                String json = response.bodyToMono(String.class).block();
                //System.out.println("Response pushNewTransactionToCustomerSync: " + json);
                logger.info("Response pushNewTransactionToCustomerSync: " + json + " status: " + response.statusCode());
                String errorCode = validateFormatCallbackResponse(json);
                if (!StringUtil.isNullOrEmpty(errorCode)) {
                    // retry callback
                    if (Objects.nonNull(errorCodes) && errorCodes.contains(errorCode)) {
                        if (retryCount < 10) {
                            pushNewTransactionToCustomerSync(transReceiveId, entity,
                                    dto, time, ++retryCount, errorCodes);
                        }
                    }
                }
                result = new ResponseMessageDTO("FAILED", "E05 - " + json);
            }

        } catch (Exception e) {
            LocalDateTime currentDateTime = LocalDateTime.now();
            long responseTime = currentDateTime.toEpochSecond(ZoneOffset.UTC);
            result = new ResponseMessageDTO("FAILED", "E05 - " + e.toString());
            if (entity.getIpAddress() != null && !entity.getIpAddress().isEmpty()) {
                logger.error(
                        "Error Unexpected at pushNewTransactionToCustomerSync: " +
                                entity.getIpAddress() + " - "
                                + e.toString()
                                + " at: " + responseTime);
            } else {
                logger.error(
                        "Error Unexpected at pushNewTransactionToCustomerSync: " +
                                entity.getInformation() + " - "
                                + e.toString()
                                + " at: " + responseTime);
            }
            if (retryCount < 10) {
                pushNewTransactionToCustomerSync(transReceiveId, entity,
                        dto, time, ++retryCount, errorCodes);
            }
        } finally {
            if (result != null) {
                UUID logUUID = UUID.randomUUID();
                String suffixUrl = "";
                if (entity.getSuffixUrl() != null && !entity.getSuffixUrl().isEmpty()) {
                    suffixUrl = "/" + entity.getSuffixUrl();
                }
                String address = "";
                if (entity.getIpAddress() != null && !entity.getIpAddress().isEmpty()) {
                    address = "http://" + entity.getIpAddress() + ":" + entity.getPort() + suffixUrl
                            + "/bank/api/transaction-sync";
                } else {
                    address = entity.getInformation() + suffixUrl + "/bank/api/transaction-sync";
                }
                TransactionReceiveLogEntity logEntity = new TransactionReceiveLogEntity();
                logEntity.setId(logUUID.toString());
                logEntity.setTransactionId(transReceiveId);
                logEntity.setStatus(result.getStatus());
                logEntity.setMessage(result.getMessage());
                logEntity.setStatusCode(StringUtil.getValueNullChecker(transactionLogResponseDTO.getStatusCode()));
                logEntity.setType(1);
                logEntity.setTimeResponse(transactionLogResponseDTO.getTimeResponse());
                logEntity.setTime(transactionLogResponseDTO.getTimeRequest());
                logEntity.setUrlCallback(address);
                transactionReceiveLogService.insert(logEntity);
            }
        }
        return result;
    }

    private String validateFormatCallbackResponse(String json) {
        String result = "";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(json);
            if (rootNode.has("error") &&
                    rootNode.has("errorReason") &&
                    rootNode.has("toastMessage") &&
                    rootNode.has("object")) {
                result = rootNode.get("errorReason").asText();
            }
        } catch (Exception e) {
            logger.error("validateFormatCallbackResponse: ERROR: " +
                    e.getMessage() + " at: " + System.currentTimeMillis());
        }
        return result;
    }

    private TokenDTO getCustomerSyncToken(String transReceiveId, CustomerSyncEntity entity, long time) {
        TokenDTO result = null;
        ResponseMessageDTO msgDTO = null;
        TransactionLogResponseDTO transactionLogResponseDTO = new TransactionLogResponseDTO();
        try {
            transactionLogResponseDTO.setTimeRequest(DateTimeUtil.getCurrentDateTimeUTC());
            String key = entity.getUsername() + ":" + entity.getPassword();
            String encodedKey = Base64.getEncoder().encodeToString(key.getBytes());
            logger.info("key: " + encodedKey + " - username: " + entity.getUsername() + " - password: "
                    + entity.getPassword());

            String suffixUrl = entity.getSuffixUrl() != null && !entity.getSuffixUrl().isEmpty()
                    ? entity.getSuffixUrl()
                    : "";
            UriComponents uriComponents = null;
            WebClient webClient = null;
            Map<String, Object> data = new HashMap<>();
            if (entity.getIpAddress() != null && !entity.getIpAddress().isEmpty()) {
                uriComponents = UriComponentsBuilder
                        .fromHttpUrl(
                                "http://" + entity.getIpAddress() + ":" + entity.getPort() + "/" + suffixUrl
                                        + "/api/token_generate")
                        .buildAndExpand();
                webClient = WebClient.builder()
                        .baseUrl("http://" + entity.getIpAddress() + ":" + entity.getPort() + "/" + suffixUrl
                                + "/api/token_generate")
                        .build();
            } else {
                uriComponents = UriComponentsBuilder
                        .fromHttpUrl(
                                entity.getInformation() + "/" + suffixUrl
                                        + "/api/token_generate")
                        .buildAndExpand();
                webClient = WebClient.builder()
                        .baseUrl(entity.getInformation() + "/" + suffixUrl
                                + "/api/token_generate")
                        .build();
            }
            //System.out.println("uriComponents: " + uriComponents.getPath());
            Mono<TokenDTO> responseMono = webClient.method(HttpMethod.POST)
                    .uri(uriComponents.toUri())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Basic " + encodedKey)
                    .body(BodyInserters.fromValue(data))
                    .exchange()
                    .flatMap(clientResponse -> {
                        //System.out.println("status code: " + clientResponse.statusCode());
                        try {
                            transactionLogResponseDTO.setTimeResponse(DateTimeUtil.getCurrentDateTimeUTC());
                            transactionLogResponseDTO.setStatusCode(clientResponse.statusCode().value());
                        } catch (Exception e) {
                        }
                        if (clientResponse.statusCode().is2xxSuccessful()) {
                            return clientResponse.bodyToMono(TokenDTO.class);
                        } else {
                            return clientResponse.bodyToMono(String.class)
                                    .flatMap(error -> {
                                        logger.info("Error response: " + error);
                                        return Mono.empty();
                                    });
                        }
                    });
            Optional<TokenDTO> resultOptional = responseMono.subscribeOn(Schedulers.boundedElastic())
                    .blockOptional();
            if (resultOptional.isPresent()) {
                result = resultOptional.get();
                msgDTO = new ResponseMessageDTO("SUCCESS", "");
                if (entity.getIpAddress() != null && !entity.getIpAddress().isEmpty()) {
                    logger.info("Token got: " + result.getAccess_token() + " - from: " + entity.getIpAddress());
                } else {
                    logger.info("Token got: " + result.getAccess_token() + " - from: " + entity.getInformation());
                }
            } else {
                msgDTO = new ResponseMessageDTO("FAILED", "E05");
                if (entity.getIpAddress() != null && !entity.getIpAddress().isEmpty()) {
                    logger.info("Token could not be retrieved from: " + entity.getIpAddress());
                } else {
                    logger.info("Token could not be retrieved from: " + entity.getInformation());
                }
            }
        } catch (Exception e) {
            msgDTO = new ResponseMessageDTO("FAILED", "E05 - " + e.toString());
            if (entity.getIpAddress() != null && !entity.getIpAddress().isEmpty()) {
                logger.error("Error at getCustomerSyncToken: " + entity.getIpAddress() + " - " + e.toString());
                // //System.out.println("Error at getCustomerSyncToken: " + entity.getIpAddress()
                // + " - " + e.toString());
            } else {
                logger.error("Error at getCustomerSyncToken: " + entity.getInformation() + " - " + e.toString());
            }
        } finally {
            if (msgDTO != null) {
                UUID logUUID = UUID.randomUUID();
                String suffixUrl = "";
                if (entity.getSuffixUrl() != null && !entity.getSuffixUrl().isEmpty()) {
                    suffixUrl = "/" + entity.getSuffixUrl();
                }
                String address = "";
                if (entity.getIpAddress() != null && !entity.getIpAddress().isEmpty()) {
                    address = "http://" + entity.getIpAddress() + ":" + entity.getPort() + suffixUrl
                            + "/api/token_generate";
                } else {
                    address = entity.getInformation() + suffixUrl + "/api/token_generate";
                }
                TransactionReceiveLogEntity logEntity = new TransactionReceiveLogEntity();
                logEntity.setId(logUUID.toString());
                logEntity.setTransactionId(transReceiveId);
                logEntity.setStatus(msgDTO.getStatus());
                logEntity.setMessage(msgDTO.getMessage());
                logEntity.setStatusCode(StringUtil.getValueNullChecker(transactionLogResponseDTO.getStatusCode()));
                logEntity.setType(0);
                logEntity.setTimeResponse(transactionLogResponseDTO.getTimeResponse());
                logEntity.setTime(transactionLogResponseDTO.getTimeRequest());
                logEntity.setUrlCallback(address);
                transactionReceiveLogService.insert(logEntity);
            }
        }
        return result;
    }
}
