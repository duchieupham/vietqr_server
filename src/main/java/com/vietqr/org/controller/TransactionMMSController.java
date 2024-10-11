package com.vietqr.org.controller;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.gson.Gson;
import com.vietqr.org.dto.*;
import com.vietqr.org.dto.mapper.ErrorCodeMapper;
import com.vietqr.org.dto.mapping.RefundMappingRedisDTO;
import com.vietqr.org.entity.*;
import com.vietqr.org.service.*;
import com.vietqr.org.service.mqtt.MqttMessagingService;
import com.vietqr.org.service.redis.IdempotencyService;
import com.vietqr.org.service.social.*;
import com.vietqr.org.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.netty.http.client.HttpClient;

import javax.validation.Valid;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class TransactionMMSController {
    private static final Logger logger = Logger.getLogger(TransactionMMSController.class);

    @Autowired
    TransactionMMSService transactionMMSService;

    @Autowired
    TerminalBankService terminalBankService;

    @Autowired
    TransactionTerminalTempService transactionTerminalTempService;

    @Autowired
    TransactionReceiveService transactionReceiveService;

    @Autowired
    GoogleChatAccountBankService googleChatAccountBankService;

    @Autowired
    TerminalAddressService terminalAddressService;

    @Autowired
    MerchantSyncService merchantSyncService;

    @Autowired
    CustomerSyncService customerSyncService;

    @Autowired
    TransactionReceiveLogService transactionReceiveLogService;

    @Autowired
    AccountBankReceiveService accountBankReceiveService;

    @Autowired
    TerminalItemService terminalItemService;

    @Autowired
    AccountBankReceiveShareService accountBankReceiveShareService;

    @Autowired
    BankTypeService bankTypeService;

    @Autowired
    AccountCustomerBankService accountCustomerBankService;

    @Autowired
    CustomerErrorLogService customerErrorLogService;

    @Autowired
    TerminalService terminalService;

    @Autowired
    SystemSettingService systemSettingService;

    @Autowired
    SocketHandler socketHandler;

    @Autowired
    NotificationService notificationService;

    @Autowired
    FcmTokenService fcmTokenService;

    @Autowired
    FirebaseMessagingService firebaseMessagingService;

    @Autowired
    TelegramAccountBankService telegramAccountBankService;

    @Autowired
    TerminalBankReceiveService terminalBankReceiveService;

    @Autowired
    LarkAccountBankService larkAccountBankService;

    @Autowired
    BankReceiveConnectionService bankReceiveConnectionService;

    @Autowired
    MerchantConnectionService merchantConnectionService;

    @Autowired
    TransactionRefundService transactionRefundService;

    @Autowired
    TransactionRefundLogService transactionRefundLogService;

    @Autowired
    TelegramService telegramService;

    @Autowired
    LarkService larkService;

    @Autowired
    IdempotencyService idempotencyService;

    @Autowired
    MqttMessagingService mqttMessagingService;

    @Autowired
    SlackAccountBankService slackAccountBankService;

    @Autowired
    SlackService slackService;

    @Autowired
    DiscordAccountBankService discordAccountBankService;

    @Autowired
    DiscordService discordService;

    @Autowired
    GoogleSheetAccountBankService googleSheetAccountBankService;

    @Autowired
    GoogleSheetService googleSheetService;

    @Autowired
    TransactionBankService transactionBankService;

    @Autowired
    TransReceiveTempService transReceiveTempService;

    @PostMapping("transaction-mms")
    public ResponseEntity<TransactionMMSResponseDTO> insertTransactionMMS(@RequestBody TransactionMMSEntity entity) {
        TransactionMMSResponseDTO result = null;
        HttpStatus httpStatus = null;
        TerminalBankEntity terminalBankEntity = null;
        TransactionReceiveEntity transactionReceiveEntity = null;
        UUID uuid = UUID.randomUUID();
        int checkInsert = 0;
        logger.info("receive transaction-mms-sync from MB: " + entity.toString() + " at: " + DateTimeUtil.getCurrentDateTimeUTC());
        long timePaid = DateTimeUtil.getDateTimeAsLongMMS(entity.getPayDate());
        long currentTime = DateTimeUtil.getCurrentDateTimeUTC();
        try {
            // check result to response TransactionMMSResponseDTO
            if (entity != null) {
                entity.setId(uuid.toString());
            }
            terminalBankEntity = terminalBankService
                    .getTerminalBankByTerminalId(entity.getTerminalLabel());
            // check input cua MB bank
            result = validateTransactionBank(entity, terminalBankEntity);
            if (result.getResCode().equals("00")) {
                // insert TransactionMMSEntity
                checkInsert = transactionMMSService.insertTransactionMMS(entity);
                if (checkInsert == 1) {
                    // find bankAccount by terminalLabel (terminal ID)
                    // notify to TID
                    httpStatus = HttpStatus.OK;
                    logger.info(
                            "transaction-mms-sync: INSERT (insertTransactionMMS) SUCCESS at: " + DateTimeUtil.getCurrentDateTimeUTC());
                    ///
                    // find transaction_receive to update
                    // amount
                    // order_id -> reference_label_code
                    // status = 0
                    // (traceId => bill_number)
                    if (result != null && result.getResCode().equals("00")) {
                        // referenceLabelCode is empty => static QR
                        // referenceLabelCode is not empty => transaction QR
                        if (entity.getReferenceLabelCode() != null
                                && !entity.getReferenceLabelCode().trim().isEmpty()) {
                            // TRANSACTION QR
                            transactionReceiveEntity = transactionReceiveService
                                    .getTransactionByOrderId(entity.getReferenceLabelCode(), entity.getDebitAmount());
                            if (transactionReceiveEntity != null) {
                                // transactionReceiveEntity != null:
                                // update status transaction receive
                                transactionReceiveService.updateTransactionReceiveStatus(1, uuid.toString(),
                                        entity.getFtCode(), timePaid,
                                        transactionReceiveEntity.getId());
                                logger.info(
                                        "transaction-mms-sync: updateTransactionReceiveStatus SUCCESS at: "
                                                + DateTimeUtil.getCurrentDateTimeUTC());
                            } else {
                                //////////////////////////////////////////
                                logger.info("transaction-mms-sync: NOT FOUND transactionReceiveEntity");
                            }
                            ///
                        }

                    }
                } else {
                    httpStatus = HttpStatus.BAD_REQUEST;
                    logger.error(
                            "transaction-mms-sync: INSERT ERROR at: " + DateTimeUtil.getCurrentDateTimeUTC());
                }
            } else {
                httpStatus = HttpStatus.BAD_REQUEST;
                logger.error(
                        "transaction-mms-sync: Response ERROR: " + result.getResCode() + " - " + result.getResDesc()
                                + " at: " + DateTimeUtil.getCurrentDateTimeUTC());
            }
            logger.info(
                    "transaction-mms-sync: RESPONSE: " + result.getResCode() + " at: " + DateTimeUtil.getCurrentDateTimeUTC());
            return new ResponseEntity<>(result, httpStatus);
        } catch (Exception e) {
            logger.error("transaction-mms-sync: Error " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
            result = new TransactionMMSResponseDTO("99", "Internal error");
            logger.info(
                    "transaction-mms-sync: RESPONSE ERRPR at: " + DateTimeUtil.getCurrentDateTimeUTC());
            // System.out.println(
            // "transaction-mms-sync: RESPONSE ERRPR at: " + responseTime);
            return new ResponseEntity<>(result, httpStatus);
        } finally {
            final TransactionMMSResponseDTO tempResult = result;
            final TransactionReceiveEntity tempTransReceive = transactionReceiveEntity;
            final TerminalBankEntity tempTerminalBank = terminalBankEntity;
            TransactionReceiveEntity finalTransactionReceiveEntity = transactionReceiveEntity;
            TerminalBankEntity finalTerminalBankEntity = terminalBankEntity;
            TransactionReceiveEntity finalTransactionReceiveEntity1 = transactionReceiveEntity;
            Thread thread = new Thread(() -> {
                if (tempResult != null && tempResult.getResCode().equals("00")) {
                    // tempTransReceive is null => static QR
                    // tempTransReceive is not empty => transaction QR
                    ///
                    //
                    // TRANSACTION QR
                    if (tempTransReceive != null) {
                        String rawCode = "";
                        boolean isSubTerminal = false;
                        // push data to customerSync
                        if (tempTerminalBank != null) {
                            System.out.println("terminal bank != null");
                            String terminalId = "";
                            String traceTransfer = entity.getTraceTransfer();
                            if (!StringUtil.isNullOrEmpty(traceTransfer)) {
                                terminalId = terminalService
                                        .getTerminalByTraceTransfer(traceTransfer);
                                if (terminalId == null || terminalId.trim().isEmpty()) {
                                    terminalId = terminalBankReceiveService
                                            .getTerminalByTraceTransfer(traceTransfer);
                                }
                            }
                            if (terminalId != null && !terminalId.trim().isEmpty()) {
                                // if VietQR terminal existed, insert new transaction
                                TerminalEntity terminalEntity = terminalService
                                        .findTerminalById(terminalId);
                                TerminalBankReceiveEntity terminalBankReceiveEntity = terminalBankReceiveService
                                        .getTerminalBankReceiveByTraceTransfer(traceTransfer);
                                if (terminalEntity != null) {
                                    rawCode = terminalEntity.getRawTerminalCode();
                                } else if (terminalBankReceiveEntity != null) {
                                    rawCode = terminalBankReceiveEntity.getRawTerminalCode();
                                    isSubTerminal = true;
                                }
                            }
                            String urlLink = tempTransReceive.getUrlLink() != null
                                    ? tempTransReceive.getUrlLink()
                                    : "";
                            try {
                                Thread thread2 = new Thread(() -> {
                                    // DO INSERT MQTT BY QVAN
                                    try {
                                        if(finalTransactionReceiveEntity.getAdditionalData() != null) {
                                            ObjectMapper objectMapper = new ObjectMapper();
                                            JsonNode additionalDataArray = objectMapper.readTree(finalTransactionReceiveEntity.getAdditionalData());
                                            String terminalCode = additionalDataArray.get(0).get("terminalCode").asText();

                                            // Tạo mqttTopic với giá trị terminalCode
                                            String mqttTopic = "vietqr/bdsd/" + terminalCode;

                                            // Tạo dữ liệu JSON thông báo
                                            Map<String, Object> notificationData = new HashMap<>();
                                            notificationData.put("referenceNumber", entity.getFtCode());
                                            notificationData.put("bankAccount", finalTransactionReceiveEntity.getBankAccount());
                                            notificationData.put("amount", finalTransactionReceiveEntity.getAmount());
                                            notificationData.put("transType", finalTransactionReceiveEntity.getTransType());
                                            notificationData.put("content", finalTransactionReceiveEntity.getContent());
                                            notificationData.put("status", 1);
                                            String formattedTime = formatTimeUtcPlus(finalTransactionReceiveEntity.getTimePaid());
                                            notificationData.put("timePaid", formattedTime);
                                            notificationData.put("orderId", finalTransactionReceiveEntity.getOrderId());

                                            // Chuyển đổi dữ liệu thành chuỗi JSON
                                            Gson gson = new Gson();
                                            String payload = gson.toJson(notificationData);

                                            // Xuất bản thông điệp MQTT
                                            MQTTUtil.sendMessage(mqttTopic, payload);
                                            logger.info("Balance change notification sent to topic: " + mqttTopic + " Payload: "
                                                    + payload + " at: " + System.currentTimeMillis());
                                        }
                                    }
                                    catch (Exception e) {
                                        // Xử lý các ngoại lệ khác nếu có
                                        logger.error("Error while sending balance change notification: " + e.toString());
                                    }
                                });
                                thread2.start();
                            } catch (Exception e) {
                                logger.error("getCustomerSyncEntitiesV2: ERROR: " + e.getMessage() +
                                        " at: " + System.currentTimeMillis());
                            }
                            try {
                                final String finalRawCode = rawCode;
                                Thread thread2 = new Thread(() -> {
                                    getCustomerSyncEntities(tempTransReceive.getId(), tempTerminalBank.getId(),
                                            entity.getFtCode(),
                                            tempTransReceive, timePaid, finalRawCode, urlLink, "", tempTransReceive.getSubCode());
                                });
                                thread2.start();
                            } catch (Exception e) {
                                logger.error("getCustomerSyncEntitiesV2: ERROR: " + e.getMessage() +
                                        " at: " + System.currentTimeMillis());
                            }
                            try {
                                final String finalRawCode = rawCode;
                                Thread thread2 = new Thread(() -> {
                                    getCustomerSyncEntitiesV2(tempTransReceive.getId(), tempTerminalBank.getId(),
                                            entity.getFtCode(),
                                            tempTransReceive, timePaid, finalRawCode, urlLink, "");
                                });
                                thread2.start();
                            } catch (Exception e) {
                                logger.error("getCustomerSyncEntitiesV2: ERROR: " + e.getMessage() +
                                        " at: " + System.currentTimeMillis());
                            }
                        } else {
                            // System.out.println("terminal bank = null");
                            logger.info(
                                    "transaction-mms-sync: terminalBankEntity = NULL; CANNOT push data to customerSync");
                        }
                        try {
                            if (tempTerminalBank != null) {
                                TerminalSubRawCodeDTO rawCodeDTO = terminalBankReceiveService
                                        .getTerminalBankReceiveForRawByTerminalCode(tempTransReceive.getTerminalCode());
                                if (rawCodeDTO != null && rawCodeDTO.getTypeOfQr() == 2) {
                                    rawCode = rawCodeDTO.getRawTerminalCode();
                                    isSubTerminal = true;
                                }
                            }
                        } catch (Exception e) {
                            logger.info(
                                    "transaction-mms-sync: ERROR get rawCode ");
                        }
                        // push notification to qr link
                        // qr link nao push ve qr link đó
                        // default bankTypeId
                        String bankTypeId = "aa4e489b-254e-4351-9cd4-f62e09c63ebc";
                        BankTypeEntity bankTypeEntity = bankTypeService
                                .getBankTypeById(bankTypeId);
                        AccountBankReceiveEntity accountBankEntity = accountBankReceiveService
                                .getAccountBankByBankAccountAndBankTypeId(tempTransReceive.getBankAccount(),
                                        bankTypeId);
                        if (accountBankEntity != null) {
                            Map<String, String> data = new HashMap<>();
                            data.put("notificationType", NotificationUtil.getNotiTypeUpdateTransaction());
                            data.put("notificationId", "");
                            data.put("bankAccount", accountBankEntity.getBankAccount());
                            data.put("bankName", bankTypeEntity.getBankName());
                            data.put("bankCode", bankTypeEntity.getBankCode());
                            data.put("bankId", accountBankEntity.getId());
                            data.put("terminalName", "");
                            data.put("urlLink",
                                    tempTransReceive.getUrlLink() != null ? tempTransReceive.getUrlLink() : "");
                            data.put("type", "" + tempTransReceive.getType());
                            data.put("transType", "" + "C");// add transType
                            data.put("terminalCode",
                                    tempTransReceive.getTerminalCode() != null ? tempTransReceive.getTerminalCode()
                                            : "");
                            if (tempTransReceive.getTerminalCode() != null &&
                                    !tempTransReceive.getTerminalCode().trim().isEmpty()) {
                                TransactionTerminalTempEntity transactionTerminalTempEntity = new TransactionTerminalTempEntity();
                                transactionTerminalTempEntity.setId(UUID.randomUUID().toString());
                                transactionTerminalTempEntity.setTransactionId(tempTransReceive.getId());
                                transactionTerminalTempEntity.setTerminalCode(tempTransReceive.getTerminalCode());
                                transactionTerminalTempEntity.setTime(timePaid);
                                transactionTerminalTempEntity
                                        .setAmount(Long.parseLong(tempTransReceive.getAmount() + ""));
                                transactionTerminalTempService.insertTransactionTerminal(transactionTerminalTempEntity);
                            }
                            data.put("rawTerminalCode", "");
                            data.put("orderId", tempTransReceive.getOrderId() != null
                                    ? tempTransReceive.getOrderId()
                                    : "");
                            data.put("referenceNumber", tempTransReceive.getReferenceNumber() != null
                                    ? tempTransReceive.getReferenceNumber()
                                    : "");
                            data.put("content", tempTransReceive.getContent());
                            String amountForShow = StringUtil.formatNumberAsString(entity.getDebitAmount() + "");
                            try {
                                amountForShow = processHiddenAmount(tempTransReceive.getAmount(), accountBankEntity.getId(),
                                        accountBankEntity.isValidService(), tempTransReceive.getId());
                            } catch (Exception e) {
                                logger.error("processHiddenAmount: ERROR: MMS:" + e.getMessage() + " at: " + System.currentTimeMillis());
                            }
                            String amountForVoice = StringUtil.removeFormatNumber(tempTransReceive.getAmount() + "");
                            data.put("amount", "" + amountForShow);
                            data.put("timePaid", "" + tempTransReceive.getTimePaid());
                            data.put("time", "" + timePaid);
                            data.put("refId", "" + entity.getId());
                            data.put("status", "1");
                            data.put("traceId", "");
                            data.put("transType", "C");
                            data.put("urlLink", tempTransReceive.getUrlLink() != null ?
                                    tempTransReceive.getUrlLink() : "");

                            UUID notificationUUID = UUID.randomUUID();
                            NotificationEntity notiEntity = new NotificationEntity();
                            String message = NotificationUtil.getNotiDescUpdateTransSuffix1()
                                    + accountBankEntity.getBankAccount()
                                    + NotificationUtil.getNotiDescUpdateTransSuffix2()
                                    + "+" + amountForShow
                                    + NotificationUtil.getNotiDescUpdateTransSuffix3()
                                    + StringUtil.getValueNullChecker(tempTransReceive.getTerminalCode())
                                    + NotificationUtil.getNotiDescUpdateTransSuffix4()
                                    + tempTransReceive.getContent();
                            notiEntity.setId(notificationUUID.toString());
                            notiEntity.setRead(false);
                            notiEntity.setMessage(message);
                            notiEntity.setTime(currentTime);
                            notiEntity.setType(NotificationUtil.getNotiTypeUpdateTransaction());
                            notiEntity.setUserId(tempTransReceive.getUserId());
                            notiEntity.setData(tempTransReceive.getId());



                            pushNotification(NotificationUtil.getNotiTitleUpdateTransaction(),
                                    message, notiEntity, data, tempTransReceive.getUserId(), StringUtil.getValueNullChecker(accountBankEntity.getPushNotification(), 1));

                            // send msg to QR Link
                            String refId = TransactionRefIdUtil
                                    .encryptTransactionId(tempTransReceive.getId());
                            try {
                                LocalDateTime startRequestDateTime = LocalDateTime.now();
                                long startRequestTime = startRequestDateTime.toEpochSecond(ZoneOffset.UTC);
                                logger.info(
                                        "transaction-mms-sync: sendMessageToTransactionRefId at:" + startRequestTime);
                                if (isSubTerminal) {
                                    try {
                                        Map<String, String> data1 = new HashMap<>();
                                        data1.put("notificationType", NotificationUtil.getNotiTypeUpdateTransaction());
                                        data1.put("notificationId", UUID.randomUUID().toString());
                                        data1.put("transactionReceiveId", tempTransReceive.getId());
                                        data1.put("bankAccount", tempTransReceive.getBankAccount());
                                        data1.put("bankName", bankTypeEntity.getBankName());
                                        data1.put("bankCode", bankTypeEntity.getBankCode());
                                        data1.put("bankId", tempTransReceive.getBankId());
                                        data1.put("content", tempTransReceive.getContent());
                                        data1.put("amount", "" + amountForShow);
                                        data1.put("terminalName", "");
                                        data1.put("terminalCode", tempTransReceive.getTerminalCode());
                                        data1.put("rawTerminalCode", "");
                                        data1.put("orderId",
                                                entity.getReferenceLabelCode() != null ? entity.getReferenceLabelCode() : "");
                                        data1.put("referenceNumber", entity.getFtCode() != null ? entity.getFtCode() : "");
                                        data1.put("timePaid", "" + timePaid);
                                        data1.put("type", "" + tempTransReceive.getType());
                                        data1.put("time", "" + currentTime);
                                        data1.put("refId", "" + uuid.toString());
                                        data1.put("status", "1");
                                        data1.put("traceId", "");
                                        data1.put("transType", "C");
                                        data1.put("message", String.format(EnvironmentUtil.getVietQrPaymentSuccessQrVoice(), amountForVoice));
                                        pushNotificationBoxIdRef(amountForVoice, amountForShow, rawCode);
                                    } catch (Exception ignored) {
                                    }
                                }
                                socketHandler.sendMessageToTransactionRefId(refId, data);
                            } catch (Exception e) {
                                logger.error("transaction-mms-sync: ERROR: " + e.toString());
                            }

                        } else {
                            logger.info("transaction-mms-sync: NOT FOUND accountBankEntity");
                        }
                    } else {

                        if (!StringUtil.isNullOrEmpty(entity.getTraceTransfer())) {
                            TerminalItemEntity terminalItemEntity = terminalItemService
                                    .getTerminalItemByTraceTransferAndAmount(entity.getTraceTransfer(),
                                            entity.getDebitAmount(), entity.getReferenceLabelCode());
                            if (Objects.nonNull(terminalItemEntity)) {
                                // qr bán động
                                String transactionId = UUID.randomUUID().toString();
                                AccountBankReceiveEntity accountBankReceiveEntity = accountBankReceiveService
                                        .getAccountBankById(terminalItemEntity.getBankId());
                                String amountForShow = StringUtil.formatNumberAsString(entity.getDebitAmount() + "");
                                try {
                                    amountForShow = processHiddenAmount(Long.parseLong(entity.getDebitAmount()), accountBankReceiveEntity.getId(),
                                            accountBankReceiveEntity.isValidService(), transactionId);
                                } catch (Exception e) {
                                    logger.error("processHiddenAmount: ERROR: MMS:" + e.getMessage() + " at: " + System.currentTimeMillis());
                                }
                                TransactionReceiveEntity transactionReceive = new TransactionReceiveEntity();
                                transactionReceive.setId(transactionId);
                                transactionReceive.setStatus(1);
                                transactionReceive.setType(3);
                                transactionReceive.setAmount(Long.parseLong(entity.getDebitAmount()));
                                transactionReceive.setRefId(uuid.toString());
                                transactionReceive.setTraceId("");
                                transactionReceive.setTransType("C");
                                transactionReceive.setReferenceNumber(entity.getFtCode());
                                transactionReceive.setOrderId("");
                                transactionReceive.setSign("");
                                transactionReceive.setTime(currentTime);
                                transactionReceive.setTimePaid(timePaid);
                                transactionReceive.setBankId(accountBankReceiveEntity.getId());
                                transactionReceive.setTransStatus(0);
                                transactionReceive.setTerminalCode(terminalItemEntity.getTerminalCode());
                                transactionReceive.setContent(entity.getTraceTransfer());
                                transactionReceive.setBankAccount(accountBankReceiveEntity.getBankAccount());
                                transactionReceive.setQrCode("");
                                transactionReceive.setUserId(accountBankReceiveEntity.getUserId());
                                transactionReceive.setNote("");
                                transactionReceive.setUrlLink("");
                                transactionReceive.setServiceCode(terminalItemEntity.getRawServiceCode());
                                transactionReceiveService.insertTransactionReceive(transactionReceive);

                                BankTypeEntity bankTypeEntity = bankTypeService
                                        .getBankTypeById(accountBankReceiveEntity.getBankTypeId());
                                NumberFormat nf = NumberFormat.getInstance(Locale.US);
                                Map<String, String> data = new HashMap<>();
                                UUID notificationUUID = UUID.randomUUID();
                                NotificationEntity notiEntity = new NotificationEntity();
                                String message = NotificationUtil.getNotiDescUpdateTransSuffix1()
                                        + accountBankReceiveEntity.getBankAccount()
                                        + NotificationUtil.getNotiDescUpdateTransSuffix2()
                                        + "+" + amountForShow
                                        + NotificationUtil.getNotiDescUpdateTransSuffix3()
                                        + entity.getTraceTransfer()
                                        + NotificationUtil.getNotiDescUpdateTransSuffix4()
                                        + entity.getTraceTransfer();
                                notiEntity.setId(notificationUUID.toString());
                                notiEntity.setRead(false);
                                notiEntity.setMessage(message);
                                notiEntity.setTime(currentTime);
                                notiEntity.setType(NotificationUtil.getNotiTypeUpdateTransaction());
                                notiEntity.setUserId(accountBankReceiveEntity.getUserId());
                                notiEntity.setData(transactionId);
                                data.put("notificationType", NotificationUtil.getNotiTypeUpdateTransaction());
                                data.put("notificationId", notificationUUID.toString());
                                data.put("transactionReceiveId", transactionId);
                                data.put("bankAccount", accountBankReceiveEntity.getBankAccount());
                                data.put("bankName", bankTypeEntity.getBankName());
                                data.put("bankCode", bankTypeEntity.getBankCode());
                                data.put("bankId", accountBankReceiveEntity.getId());
                                data.put("content", "" + terminalItemEntity.getContent());
                                data.put("amount", "" + amountForShow);
                                data.put("terminalName", "");
                                data.put("terminalCode", "");
                                data.put("rawTerminalCode", "");
                                data.put("orderId",
                                        entity.getReferenceLabelCode() != null ? entity.getReferenceLabelCode() : "");
                                data.put("referenceNumber", entity.getFtCode() != null ? entity.getFtCode() : "");
                                data.put("timePaid", "" + timePaid);
                                data.put("type", "" + transactionReceive.getType());
                                data.put("time", "" + currentTime);
                                data.put("refId", "" + uuid.toString());
                                data.put("status", "1");
                                data.put("traceId", "");
                                data.put("transType", "C");
                                TerminalBankEntity terminalBankEntitySync = terminalBankService
                                        .getTerminalBankByBankAccount(accountBankReceiveEntity.getBankAccount());
                                if (terminalBankEntitySync != null) {
                                    try {
                                        Thread thread4 = new Thread(() -> {
                                            try {
                                                // push data to customerSync
                                                ////////////////////////
                                                getCustomerSyncEntities(transactionReceive.getId(),
                                                        terminalBankEntitySync.getId(),
                                                        entity.getFtCode(),
                                                        transactionReceive, timePaid, "", "",
                                                        terminalItemEntity.getRawServiceCode(), "");
                                            } catch (Exception e) {
                                                // Xử lý các ngoại lệ khác nếu có
                                                logger.error("Error while getCustomerSyncEntities: " + e.toString());
                                            }
                                        });
                                        thread4.start();
                                    } catch (Exception e) {
                                        logger.error("getCustomerSyncEntities: ERROR: " + e.getMessage() +
                                                " at: " + System.currentTimeMillis());
                                    }
                                } else {
                                    logger.info("transaction-mms-sync: NOT FOUND TerminalBankEntity");
                                }
                                pushNotification(NotificationUtil.getNotiTitleUpdateTransaction(),
                                        message, notiEntity, data, accountBankReceiveEntity.getUserId(),
                                        StringUtil.getValueNullChecker(accountBankReceiveEntity.getPushNotification(), 1));

                                // Push notifications to Telegram
                                List<String> webhooks = larkAccountBankService.getWebhooksByBankId(terminalItemEntity.getBankId());
                                if (webhooks != null && !webhooks.isEmpty()) {
                                    LarkUtil larkUtil = new LarkUtil();
                                    for (String webhook : webhooks) {
                                        try {
                                            LarkEntity larkEntity = larkService.getLarkByWebhook(webhook);
                                            if (larkEntity != null) {
                                                List<String> notificationTypes = new ObjectMapper().readValue(larkEntity.getNotificationTypes(), new TypeReference<List<String>>() {
                                                });
                                                List<String> notificationContents = new ObjectMapper().readValue(larkEntity.getNotificationContents(), new TypeReference<List<String>>() {
                                                });
                                                boolean sendNotification = shouldSendNotification(notificationTypes, entity, finalTransactionReceiveEntity);
                                                if (sendNotification) {
                                                    String larkMsg = createMessage(notificationContents, "C", amountForShow, bankTypeEntity, terminalItemEntity.getBankAccount(), timePaid, entity.getFtCode(), entity.getTraceTransfer());
                                                    String formattedTime = formatTimeForGoogleChat(timePaid);
                                                    larkMsg = larkMsg.replace(convertLongToDate(timePaid), formattedTime);
                                                    larkUtil.sendMessageToLark(larkMsg, webhook);
                                                }
                                            }
                                        } catch (JsonProcessingException e) {
                                            logger.error("Error processing JSON for Lark notification: " + e.getMessage());
                                        } catch (Exception e) {
                                            logger.error("Error sending Lark notification: " + e.getMessage());
                                        }
                                    }
                                }

                                /////// DO INSERT GOOGLE CHAT
                                List<String> ggChatWebhooks = googleChatAccountBankService.getWebhooksByBankId(terminalItemEntity.getBankId());
                                if (ggChatWebhooks != null && !ggChatWebhooks.isEmpty()) {
                                    GoogleChatUtil googleChatUtil = new GoogleChatUtil();
                                    String googleChatMsg = "+" + amountForShow + " VND"
                                            + " | TK: " + bankTypeEntity.getBankShortName() + " - "
                                            + terminalItemEntity.getBankAccount()
                                            + " | " + convertLongToDate(timePaid)
                                            + " | " + entity.getFtCode()
                                            + " | ND: " + terminalItemEntity.getContent();
                                    for (String webhook : ggChatWebhooks) {
                                        googleChatUtil.sendMessageToGoogleChat(googleChatMsg, webhook);
                                    }
                                }
                                // DO INSERT GOOGLE SHEET BY QVAN
                                List<String> ggSheetWebhooks = googleSheetAccountBankService.getWebhooksByBankId(terminalItemEntity.getBankId());
                                if (ggSheetWebhooks != null && !ggSheetWebhooks.isEmpty()) {
                                    GoogleSheetUtil googleSheetUtil = GoogleSheetUtil.getInstance();
                                    for (String webhook : ggSheetWebhooks) {
                                        try {
                                            GoogleSheetEntity googleSheetEntity = googleSheetService.getGoogleSheetByWebhook(webhook);
                                            if (googleSheetEntity != null) {
                                                List<String> notificationTypes = new ObjectMapper().readValue(googleSheetEntity.getNotificationTypes(), new TypeReference<List<String>>() {});
                                                List<String> notificationContents = new ObjectMapper().readValue(googleSheetEntity.getNotificationContents(), new TypeReference<List<String>>() {});
                                                boolean sendNotification = shouldSendNotification(notificationTypes, entity, finalTransactionReceiveEntity);
                                                if (sendNotification) {
                                                    if (!googleSheetUtil.headerInsertedProperties.containsKey(webhook) || !Boolean.parseBoolean(googleSheetUtil.headerInsertedProperties.getProperty(webhook))) {
                                                        googleSheetUtil.insertHeader(webhook);
                                                    }

                                                    Map<String, String> datas = new HashMap<>();
                                                    datas.put("bankAccount", terminalItemEntity.getBankAccount());
                                                    datas.put("bankName", bankTypeEntity.getBankName());
                                                    datas.put("bankShortName", bankTypeEntity.getBankShortName());
                                                    datas.put("content", terminalItemEntity.getContent());
                                                    datas.put("amount", String.valueOf(entity.getDebitAmount()));
                                                    datas.put("timePaid", String.valueOf(finalTransactionReceiveEntity.getTimePaid()));
                                                    datas.put("time", String.valueOf(currentTime));
                                                    datas.put("type", String.valueOf(finalTransactionReceiveEntity.getType()));
                                                    datas.put("terminalName", finalTerminalBankEntity != null ? finalTerminalBankEntity.getTerminalName() : "");
                                                    datas.put("orderId", finalTransactionReceiveEntity.getOrderId() != null ? finalTransactionReceiveEntity.getOrderId() : "");
                                                    datas.put("referenceNumber", finalTransactionReceiveEntity.getReferenceNumber() != null ? finalTransactionReceiveEntity.getReferenceNumber() : "");
                                                    datas.put("transType", finalTransactionReceiveEntity.getTransType()); // Assuming this is what you meant by transaction type
                                                    datas.put("status", "1");
                                                    googleSheetUtil.insertTransactionToGoogleSheet(datas, notificationContents, webhook);
                                                }
                                            }
                                        } catch (Exception e) {
                                            logger.error("Error sending Google Sheets notification: " + e.getMessage());
                                        }
                                    }
                                }



                            } else {
                                // qr tĩnh
                                logger.info(
                                        "transaction-mms-sync: staticQRTime-start at:" + DateTimeUtil.getCurrentDateTimeUTC());
                                // STATTIC QR
                                String terminalId = "";
                                String subRawCode = "";
                                boolean isSubTerminal = false;
                                boolean insertTransaction = false;
                                TerminalSubRawCodeDTO terminalSubRawCodeDTO = null;
                                // get trace Transfer to find VietQR terminal
                                String traceTransfer = entity.getTraceTransfer();
                                if (!StringUtil.isNullOrEmpty(traceTransfer)) {
                                    terminalId = terminalService
                                            .getTerminalByTraceTransfer(traceTransfer);
                                    if (StringUtil.isNullOrEmpty(terminalId)) {
                                        try {
                                            terminalSubRawCodeDTO = terminalBankReceiveService
                                                    .getTerminalSubFlow2ByTraceTransfer(traceTransfer);
                                            if (Objects.nonNull(terminalSubRawCodeDTO)) {
                                                terminalId = terminalSubRawCodeDTO.getTerminalId();
                                            }
                                        } catch (Exception e) {
                                            terminalId = terminalBankReceiveService
                                                    .getTerminalByTraceTransfer(traceTransfer);
                                        }
                                    }
                                }
                                // if exist terminalId, find bankAccount by terminalId
                                if (terminalId != null && !terminalId.trim().isEmpty()) {
                                    // if VietQR terminal existed, insert new transaction
                                    TerminalEntity terminalEntity = terminalService
                                            .findTerminalById(terminalId);

                                    TerminalBankReceiveEntity terminalBankReceiveEntity = terminalBankReceiveService
                                            .getTerminalBankReceiveByTraceTransfer(traceTransfer);
                                    if (terminalBankReceiveEntity != null) {
                                        String transactionId = UUID.randomUUID().toString();
                                        AccountBankReceiveEntity accountBankReceiveEntity = accountBankReceiveService
                                                .getAccountBankById(terminalBankReceiveEntity.getBankId());
                                        String amountForShow = StringUtil.formatNumberAsString(entity.getDebitAmount() + "");
                                        try {
                                            amountForShow = processHiddenAmount(Long.parseLong(entity.getDebitAmount()), accountBankReceiveEntity.getId(),
                                                    accountBankReceiveEntity.isValidService(), transactionId);
                                        } catch (Exception e) {
                                            logger.error("processHiddenAmount: ERROR: MMS:" + e.getMessage() + " at: " + System.currentTimeMillis());
                                        }
                                        TransactionReceiveEntity transactionReceiveEntity1 = new TransactionReceiveEntity();
                                        transactionReceiveEntity1.setId(transactionId);
                                        transactionReceiveEntity1.setStatus(1);
                                        transactionReceiveEntity1.setType(1);
                                        transactionReceiveEntity1.setAmount(Long.parseLong(entity.getDebitAmount()));
                                        transactionReceiveEntity1.setRefId(uuid.toString());
                                        transactionReceiveEntity1.setTraceId("");
                                        transactionReceiveEntity1.setTransType("C");
                                        transactionReceiveEntity1.setReferenceNumber(entity.getFtCode());
                                        transactionReceiveEntity1.setOrderId("");
                                        transactionReceiveEntity1.setSign("");
                                        transactionReceiveEntity1.setTime(currentTime);
                                        transactionReceiveEntity1.setTimePaid(timePaid);
                                        transactionReceiveEntity1.setBankId(accountBankReceiveEntity.getId());
                                        transactionReceiveEntity1.setTransStatus(0);
                                        if (terminalBankReceiveEntity.getTerminalCode() != null
                                                && !terminalBankReceiveEntity.getTerminalCode().trim().isEmpty()) {
                                            transactionReceiveEntity1
                                                    .setTerminalCode(terminalBankReceiveEntity.getTerminalCode());
                                        } else {
                                            transactionReceiveEntity1.setTerminalCode(terminalEntity.getCode()
                                                    != null ? terminalEntity.getCode() : "");
                                        }
                                        transactionReceiveEntity1.setContent(entity.getTraceTransfer());
                                        transactionReceiveEntity1.setBankAccount(accountBankReceiveEntity.getBankAccount());
                                        transactionReceiveEntity1.setQrCode("");
                                        transactionReceiveEntity1.setUserId(accountBankReceiveEntity.getUserId());
                                        transactionReceiveEntity1.setNote("");
                                        transactionReceiveEntity1.setUrlLink("");
                                        transactionReceiveService.insertTransactionReceive(transactionReceiveEntity1);
                                        String code = "";
                                        String rawCode = "";
                                        if (terminalBankReceiveEntity.getTerminalCode() != null
                                                && !terminalBankReceiveEntity.getTerminalCode().trim().isEmpty()) {
                                            code = terminalBankReceiveEntity.getTerminalCode();
                                            rawCode = terminalBankReceiveEntity.getRawTerminalCode();
                                            subRawCode = terminalBankReceiveEntity.getRawTerminalCode();
                                            isSubTerminal = true;
                                        } else {
                                            code = terminalEntity.getCode();
                                            rawCode = terminalEntity.getRawTerminalCode();
                                        }
                                        final String tempTerminalCode = code;
                                        TransactionTerminalTempEntity transactionTerminalTempEntity = new TransactionTerminalTempEntity();
                                        transactionTerminalTempEntity.setId(UUID.randomUUID().toString());
                                        transactionTerminalTempEntity.setTransactionId(transactionId);
                                        transactionTerminalTempEntity.setTerminalCode(tempTerminalCode);
                                        transactionTerminalTempEntity.setTime(timePaid);
                                        transactionTerminalTempEntity.setAmount(Long.parseLong(entity.getDebitAmount() + ""));
                                        transactionTerminalTempService.insertTransactionTerminal(transactionTerminalTempEntity);

                                        insertTransaction = true;
                                        BankTypeEntity bankTypeEntity = bankTypeService
                                                .getBankTypeById(accountBankReceiveEntity.getBankTypeId());
                                        NumberFormat nf = NumberFormat.getInstance(Locale.US);
                                        Map<String, String> data = new HashMap<>();
                                        UUID notificationUUID = UUID.randomUUID();
                                        NotificationEntity notiEntity = new NotificationEntity();
                                        String message = NotificationUtil.getNotiDescUpdateTransSuffix1()
                                                + accountBankReceiveEntity.getBankAccount()
                                                + NotificationUtil.getNotiDescUpdateTransSuffix2()
                                                + "+" + amountForShow
                                                + NotificationUtil.getNotiDescUpdateTransSuffix3()
                                                + entity.getTraceTransfer()
                                                + NotificationUtil.getNotiDescUpdateTransSuffix4()
                                                + entity.getTraceTransfer();
                                        notiEntity.setId(notificationUUID.toString());
                                        notiEntity.setRead(false);
                                        notiEntity.setMessage(message);
                                        notiEntity.setTime(currentTime);
                                        notiEntity.setType(NotificationUtil.getNotiTypeUpdateTransaction());
                                        notiEntity.setUserId(accountBankReceiveEntity.getUserId());
                                        notiEntity.setData(transactionId);
                                        data.put("notificationType", NotificationUtil.getNotiTypeUpdateTransaction());
                                        data.put("notificationId", notificationUUID.toString());
                                        data.put("transactionReceiveId", transactionId);
                                        data.put("bankAccount", accountBankReceiveEntity.getBankAccount());
                                        data.put("bankName", bankTypeEntity.getBankName());
                                        data.put("bankCode", bankTypeEntity.getBankCode());
                                        data.put("bankId", accountBankReceiveEntity.getId());
                                        data.put("content", "" + traceTransfer);
                                        String amountForVoice = StringUtil.removeFormatNumber(entity.getDebitAmount() + "");
                                        data.put("amount", "" + amountForShow);
                                        if (terminalEntity != null) {
                                            data.put("terminalName",
                                                    terminalEntity.getName() != null ? terminalEntity.getName() : "");
                                            data.put("terminalCode",
                                                    terminalEntity.getCode() != null ? terminalEntity.getCode() : "");
                                            data.put("rawTerminalCode",
                                                    terminalEntity.getRawTerminalCode() != null
                                                            ? terminalEntity.getRawTerminalCode()
                                                            : "");
                                        }
                                        data.put("orderId",
                                                entity.getReferenceLabelCode() != null ? entity.getReferenceLabelCode() : "");
                                        data.put("referenceNumber", entity.getFtCode() != null ? entity.getFtCode() : "");
                                        data.put("timePaid", "" + timePaid);
                                        data.put("type", "" + transactionReceiveEntity1.getType());
                                        data.put("time", "" + currentTime);
                                        data.put("refId", "" + uuid.toString());
                                        data.put("status", "1");
                                        data.put("traceId", "");
                                        data.put("transType", "C");
                                        pushNotification(NotificationUtil.getNotiTitleUpdateTransaction(),
                                                message, notiEntity, data, accountBankReceiveEntity.getUserId(), StringUtil.getValueNullChecker(accountBankReceiveEntity.getPushNotification(), 1));
                                        TerminalBankEntity terminalBankEntitySync = terminalBankService
                                                .getTerminalBankByBankAccount(accountBankReceiveEntity.getBankAccount());
                                        if (terminalBankEntitySync != null) {
                                            // push data to customerSync
                                            ////////////////////////
                                            getCustomerSyncEntities(transactionReceiveEntity1.getId(),
                                                    terminalBankEntitySync.getId(),
                                                    entity.getFtCode(),
                                                    transactionReceiveEntity1, timePaid, rawCode, "", "", "");
                                            try {
                                                final String finalRawCode = rawCode;
                                                Thread thread2 = new Thread(() -> {
                                                    getCustomerSyncEntitiesV2(transactionReceiveEntity1.getId(), terminalBankEntitySync.getId(),
                                                            entity.getFtCode(),
                                                            transactionReceiveEntity1, timePaid, finalRawCode, "", "");
                                                });
                                                thread2.start();
                                            } catch (Exception e) {
                                                logger.error("getCustomerSyncEntitiesV2: ERROR: " + e.getMessage() +
                                                        " at: " + System.currentTimeMillis());
                                            }
                                        } else {
                                            logger.info("transaction-mms-sync: NOT FOUND TerminalBankEntity");
                                        }
                                        try {
                                            if (isSubTerminal) {
                                                pushNotificationBoxIdRef(amountForVoice, amountForShow, subRawCode);
                                            }
                                        } catch (Exception e) {
                                            logger.error("transaction-mms-sync: ERROR: " + e.toString());
                                        }

                                        // DO INSERT GOOGLE SHEET BY QVAN
                                        List<String> ggSheetWebhooks = googleSheetAccountBankService.getWebhooksByBankId(accountBankReceiveEntity.getId());
                                        if (ggSheetWebhooks != null && !ggSheetWebhooks.isEmpty()) {
                                            GoogleSheetUtil googleSheetUtil = GoogleSheetUtil.getInstance();
                                            for (String webhook : ggSheetWebhooks) {
                                                try {
                                                    GoogleSheetEntity googleSheetEntity = googleSheetService.getGoogleSheetByWebhook(webhook);
                                                    if (googleSheetEntity != null) {
                                                        List<String> notificationTypes = new ObjectMapper().readValue(googleSheetEntity.getNotificationTypes(), new TypeReference<List<String>>() {});
                                                        List<String> notificationContents = new ObjectMapper().readValue(googleSheetEntity.getNotificationContents(), new TypeReference<List<String>>() {});
                                                        boolean sendNotification = shouldSendNotification(notificationTypes, entity, transactionReceiveEntity1);
                                                        if (sendNotification) {
                                                            if (!googleSheetUtil.headerInsertedProperties.containsKey(webhook) || !Boolean.parseBoolean(googleSheetUtil.headerInsertedProperties.getProperty(webhook))) {
                                                                googleSheetUtil.insertHeader(webhook);
                                                            }

                                                            Map<String, String> datas = new HashMap<>();
                                                            datas.put("bankAccount", accountBankReceiveEntity.getBankAccount());
                                                            datas.put("bankName", bankTypeEntity.getBankName());
                                                            datas.put("bankShortName", bankTypeEntity.getBankShortName());
                                                            datas.put("content", entity.getTraceTransfer());
                                                            datas.put("amount", String.valueOf(entity.getDebitAmount()));
                                                            datas.put("timePaid", String.valueOf(transactionReceiveEntity1.getTimePaid()));
                                                            datas.put("time", String.valueOf(currentTime));
                                                            datas.put("type", String.valueOf(transactionReceiveEntity1.getType()));
                                                            datas.put("terminalName", terminalEntity != null ? terminalEntity.getCode() : "");
                                                            datas.put("orderId", transactionReceiveEntity1.getOrderId() != null ? transactionReceiveEntity1.getOrderId() : "");
                                                            datas.put("referenceNumber", transactionReceiveEntity1.getReferenceNumber() != null ? transactionReceiveEntity1.getReferenceNumber() : "");
                                                            datas.put("transType", transactionReceiveEntity1.getTransType()); // Assuming this is what you meant by transaction type
                                                            datas.put("status", "1");
                                                            googleSheetUtil.insertTransactionToGoogleSheet(datas, notificationContents, webhook);
                                                        }
                                                    }
                                                } catch (Exception e) {
                                                    logger.error("Error sending Google Sheets notification: " + e.getMessage());
                                                }
                                            }
                                        }
                                    } else {
                                        logger.info("transaction-mms-sync: NOT FOUND terminalBankReceiveEntity");
                                    }

                                    // check time tim thay terminal
                                    logger.info(
                                            "transaction-mms-sync: findTerminal at:" + DateTimeUtil.getCurrentDateTimeUTC());
                                    String bankTypeId = "aa4e489b-254e-4351-9cd4-f62e09c63ebc";
                                    AccountBankReceiveShareForNotiDTO bankDTO = accountBankReceiveService
                                            .findAccountBankByTraceTransfer(traceTransfer,
                                                    bankTypeId);
                                    if (bankDTO != null && bankDTO.getBankId() != null) {
                                        UUID transcationUUID = UUID.randomUUID();
                                        String amountForShow = StringUtil.formatNumberAsString(entity.getDebitAmount() + "");
                                        try {
                                            amountForShow = processHiddenAmount(Long.parseLong(entity.getDebitAmount()), bankDTO.getBankId(),
                                                    bankDTO.getIsValidService(), transcationUUID.toString());
                                        } catch (Exception e) {
                                            logger.error("processHiddenAmount: ERROR: MMS:" + e.getMessage() + " at: " + System.currentTimeMillis());
                                        }
                                        TransactionReceiveEntity transactionEntity = new TransactionReceiveEntity();
                                        transactionEntity.setId(transcationUUID.toString());
                                        transactionEntity.setBankAccount(bankDTO.getBankAccount());
                                        String bankAccount = bankDTO.getBankAccount();
                                        transactionEntity.setBankId(bankDTO.getBankId());
                                        // get content from terminalCode from terminal (now ignore to test static
                                        // VietQR)
                                        transactionEntity.setContent(traceTransfer);
                                        //
                                        transactionEntity.setAmount(Long.parseLong(entity.getDebitAmount()));
                                        // final LocalDateTime currentDateTime = LocalDateTime.now();
                                        // long timePaid = currentDateTime.toEpochSecond(ZoneOffset.UTC);
                                        transactionEntity.setTime(currentTime);
                                        transactionEntity.setRefId(uuid.toString());
                                        transactionEntity.setType(1);
                                        transactionEntity.setStatus(1);
                                        transactionEntity.setTraceId("");
                                        transactionEntity.setTransType("C");
                                        transactionEntity.setReferenceNumber(entity.getFtCode());
                                        transactionEntity.setOrderId("");
                                        transactionEntity.setSign("");
                                        transactionEntity.setTimePaid(timePaid);
                                        transactionEntity.setTerminalCode(terminalEntity != null ? terminalEntity.getCode() : "");
                                        transactionEntity.setQrCode("");
                                        transactionEntity.setUserId(bankDTO.getUserId());
                                        transactionEntity.setNote("");
                                        transactionEntity.setTransStatus(0);
                                        transactionEntity.setUrlLink("");
                                        if (!insertTransaction) {
                                            transactionReceiveService.insertTransactionReceive(transactionEntity);
                                            final String tempTerminalCode = terminalEntity.getCode();
                                            TransactionTerminalTempEntity transactionTerminalTempEntity = new TransactionTerminalTempEntity();
                                            transactionTerminalTempEntity.setId(UUID.randomUUID().toString());
                                            transactionTerminalTempEntity.setTransactionId(transcationUUID.toString());
                                            transactionTerminalTempEntity.setTerminalCode(tempTerminalCode);
                                            transactionTerminalTempEntity.setTime(currentTime);
                                            transactionTerminalTempEntity
                                                    .setAmount(Long.parseLong(entity.getDebitAmount() + ""));
                                            transactionTerminalTempService
                                                    .insertTransactionTerminal(transactionTerminalTempEntity);
                                        }
                                        logger.info(
                                                "transaction-mms-sync: insertStaticQRTimeSuccess at:" + DateTimeUtil.getCurrentDateTimeUTC());
                                        // 4. insert and push notification to user.

                                        List<String> userIds = terminalService
                                                .getUserIdsByTerminalCode(terminalEntity.getCode());
                                        int numThread = userIds.size();
                                        int amount = Integer.parseInt(entity.getDebitAmount());
                                        NumberFormat nf = NumberFormat.getInstance(Locale.US);
                                        ExecutorService executorService = Executors.newFixedThreadPool(numThread);
                                        try {
                                            for (String userId : userIds) {
                                                Map<String, String> data = new HashMap<>();
                                                UUID notificationUUID = UUID.randomUUID();
                                                NotificationEntity notiEntity = new NotificationEntity();
                                                String message = NotificationUtil.getNotiDescUpdateTransSuffix1()
                                                        + bankDTO.getBankAccount()
                                                        + NotificationUtil.getNotiDescUpdateTransSuffix2()
                                                        + "+" + amountForShow
                                                        + NotificationUtil.getNotiDescUpdateTransSuffix3()
                                                        + entity.getTraceTransfer()
                                                        + NotificationUtil.getNotiDescUpdateTransSuffix4()
                                                        + entity.getTraceTransfer();
                                                notiEntity.setId(notificationUUID.toString());
                                                notiEntity.setRead(false);
                                                notiEntity.setMessage(message);
                                                notiEntity.setTime(currentTime);
                                                notiEntity.setType(NotificationUtil.getNotiTypeUpdateTransaction());
                                                notiEntity.setUserId(userId);
                                                notiEntity.setData(transcationUUID.toString());
                                                data.put("notificationType", NotificationUtil.getNotiTypeUpdateTransaction());
                                                data.put("notificationId", notificationUUID.toString());
                                                data.put("transactionReceiveId", transcationUUID.toString());
                                                data.put("bankAccount", bankAccount);
                                                data.put("bankName", bankDTO.getBankName());
                                                data.put("bankCode", bankDTO.getBankCode());
                                                data.put("bankId", bankDTO.getBankId());
                                                data.put("content", "" + traceTransfer);
                                                data.put("terminalName",
                                                        terminalEntity.getName() != null ? terminalEntity.getName() : "");
                                                data.put("terminalCode",
                                                        terminalEntity.getCode() != null ? terminalEntity.getCode() : "");
                                                data.put("rawTerminalCode",
                                                        terminalEntity.getRawTerminalCode() != null
                                                                ? terminalEntity.getRawTerminalCode()
                                                                : "");
                                                data.put("amount", "" + amountForShow);
                                                data.put("orderId", "");
                                                data.put("referenceNumber", entity.getFtCode());
                                                data.put("timePaid", "" + timePaid);
                                                data.put("time", "" + currentTime);
                                                data.put("type", "" + transactionEntity.getType());
                                                data.put("refId", "" + uuid.toString());
                                                data.put("status", "1");
                                                data.put("traceId", "");
                                                data.put("transType", "C");
                                                data.put("urlLink", transactionEntity.getUrlLink() != null ? transactionEntity.getUrlLink() : "");
                                                executorService.submit(
                                                        () -> pushNotification(NotificationUtil.getNotiTitleUpdateTransaction(),
                                                                message, notiEntity, data, userId, StringUtil.getValueNullChecker(bankDTO.getPushNotification(), 1)));
                                                try {
                                                    // send msg to QR Link
                                                    String refId = TransactionRefIdUtil
                                                            .encryptTransactionId(transactionEntity.getId());
                                                    socketHandler.sendMessageToTransactionRefId(refId, data);
                                                } catch (IOException e) {
                                                    logger.error(
                                                            "WS: socketHandler.sendMessageToUser - updateTransaction ERROR: "
                                                                    + e.toString());
                                                }
                                            }
                                        } finally {
                                            executorService.shutdown(); // Yêu cầu các luồng dừng khi hoàn tất công việc
                                            try {
                                                if (!executorService.awaitTermination(120, TimeUnit.SECONDS)) {
                                                    executorService.shutdownNow(); // Nếu vẫn chưa dừng sau 60 giây, cưỡng chế dừng
                                                }
                                            } catch (InterruptedException e) {
                                                executorService.shutdownNow(); // Nếu bị ngắt khi chờ, cưỡng chế dừng
                                            }
                                        }

                                        // /////// DO INSERT TELEGRAM
                                        List<String> chatIds = telegramAccountBankService
                                                .getChatIdsByBankId(bankDTO.getBankId());
                                        if (chatIds != null && !chatIds.isEmpty()) {
                                            TelegramUtil telegramUtil = new TelegramUtil();

                                            String telegramMsg = "+" + amountForShow + " VND"
                                                    + " | TK: " + bankDTO.getBankShortName() + " - "
                                                    + bankAccount
                                                    + " | " + convertLongToDate(timePaid)
                                                    + " | " + entity.getFtCode()
                                                    + " | ND: " + entity.getTraceTransfer();
                                            for (String chatId : chatIds) {
                                                telegramUtil.sendMsg(chatId, telegramMsg);
                                            }
                                        }

                                        /////// DO INSERT LARK
                                        List<String> webhooks = larkAccountBankService
                                                .getWebhooksByBankId(bankDTO.getBankId());
                                        if (webhooks != null && !webhooks.isEmpty()) {
                                            LarkUtil larkUtil = new LarkUtil();

                                            String larkMsg = "+" + amountForShow + " VND"
                                                    + " | TK: " + bankDTO.getBankShortName() + " - "
                                                    + bankAccount
                                                    + " | " + convertLongToDate(timePaid)
                                                    + " | " + entity.getFtCode()
                                                    + " | ND: " + entity.getTraceTransfer();
                                            for (String webhook : webhooks) {
                                                larkUtil.sendMessageToLark(larkMsg, webhook);
                                            }
                                        }

                                        /////// DO INSERT GOOGLE CHAT
                                        List<String> ggChatWebhooks = googleChatAccountBankService.getWebhooksByBankId(bankDTO.getBankId());
                                        if (ggChatWebhooks != null && !ggChatWebhooks.isEmpty()) {
                                            GoogleChatUtil googleChatUtil = new GoogleChatUtil();
                                            String googleChatMsg = "+" + amountForShow + " VND"
                                                    + " | TK: " + bankDTO.getBankShortName() + " - "
                                                    + bankDTO.getBankAccount()
                                                    + " | " + convertLongToDate(timePaid)
                                                    + " | " + entity.getFtCode()
                                                    + " | ND: " + entity.getTraceTransfer();
                                            for (String webhook : ggChatWebhooks) {
                                                googleChatUtil.sendMessageToGoogleChat(googleChatMsg, webhook);
                                            }
                                        }
                                    }

                                } else if (terminalSubRawCodeDTO != null) {
                                    try {
                                        String transactionId = UUID.randomUUID().toString();
                                        subRawCode = terminalSubRawCodeDTO.getRawTerminalCode();
                                        AccountBankReceiveEntity accountBankReceiveEntity = accountBankReceiveService
                                                .getAccountBankById(terminalSubRawCodeDTO.getBankId());
                                        String amountForShow = StringUtil.formatNumberAsString(entity.getDebitAmount() + "");
                                        try {
                                            amountForShow = processHiddenAmount(Long.parseLong(entity.getDebitAmount()), accountBankReceiveEntity.getId(),
                                                    accountBankReceiveEntity.isValidService(), transactionId);
                                        } catch (Exception e) {
                                            logger.error("processHiddenAmount: ERROR: MMS:" + e.getMessage() + " at: " + System.currentTimeMillis());
                                        }
                                        TransactionReceiveEntity transactionReceiveEntity1 = new TransactionReceiveEntity();
                                        transactionReceiveEntity1.setId(transactionId);
                                        transactionReceiveEntity1.setStatus(1);
                                        transactionReceiveEntity1.setType(1);
                                        transactionReceiveEntity1.setAmount(Long.parseLong(entity.getDebitAmount()));
                                        transactionReceiveEntity1.setRefId(uuid.toString());
                                        transactionReceiveEntity1.setTraceId("");
                                        transactionReceiveEntity1.setTransType("C");
                                        transactionReceiveEntity1.setReferenceNumber(entity.getFtCode());
                                        transactionReceiveEntity1.setOrderId("");
                                        transactionReceiveEntity1.setSign("");
                                        transactionReceiveEntity1.setTime(currentTime);
                                        transactionReceiveEntity1.setTimePaid(timePaid);
                                        transactionReceiveEntity1.setBankId(accountBankReceiveEntity.getId());
                                        transactionReceiveEntity1.setTransStatus(0);
                                        transactionReceiveEntity1.setTerminalCode(terminalSubRawCodeDTO.getTerminalCode());
                                        transactionReceiveEntity1.setContent(entity.getTraceTransfer());
                                        transactionReceiveEntity1.setBankAccount(accountBankReceiveEntity.getBankAccount());
                                        transactionReceiveEntity1.setQrCode("");
                                        transactionReceiveEntity1.setUserId(accountBankReceiveEntity.getUserId());
                                        transactionReceiveEntity1.setNote("");
                                        transactionReceiveEntity1.setUrlLink("");
                                        transactionReceiveService.insertTransactionReceive(transactionReceiveEntity1);
                                        TransactionTerminalTempEntity transactionTerminalTempEntity = new TransactionTerminalTempEntity();
                                        transactionTerminalTempEntity.setId(UUID.randomUUID().toString());
                                        transactionTerminalTempEntity.setTransactionId(transactionId);
                                        transactionTerminalTempEntity.setTerminalCode(terminalSubRawCodeDTO.getTerminalCode());
                                        transactionTerminalTempEntity.setTime(timePaid);
                                        transactionTerminalTempEntity.setAmount(Long.parseLong(entity.getDebitAmount() + ""));
                                        transactionTerminalTempService.insertTransactionTerminal(transactionTerminalTempEntity);

                                        insertTransaction = true;
                                        BankTypeEntity bankTypeEntity = bankTypeService
                                                .getBankTypeById(accountBankReceiveEntity.getBankTypeId());
                                        NumberFormat nf = NumberFormat.getInstance(Locale.US);
                                        Map<String, String> data = new HashMap<>();
                                        UUID notificationUUID = UUID.randomUUID();
                                        NotificationEntity notiEntity = new NotificationEntity();
                                        String message = NotificationUtil.getNotiDescUpdateTransSuffix1()
                                                + accountBankReceiveEntity.getBankAccount()
                                                + NotificationUtil.getNotiDescUpdateTransSuffix2()
                                                + "+" + amountForShow
                                                + NotificationUtil.getNotiDescUpdateTransSuffix3()
                                                + entity.getTraceTransfer()
                                                + NotificationUtil.getNotiDescUpdateTransSuffix4()
                                                + entity.getTraceTransfer();
                                        notiEntity.setId(notificationUUID.toString());
                                        notiEntity.setRead(false);
                                        notiEntity.setMessage(message);
                                        notiEntity.setTime(currentTime);
                                        notiEntity.setType(NotificationUtil.getNotiTypeUpdateTransaction());
                                        notiEntity.setUserId(accountBankReceiveEntity.getUserId());
                                        notiEntity.setData(transactionId);
                                        data.put("notificationType", NotificationUtil.getNotiTypeUpdateTransaction());
                                        data.put("notificationId", notificationUUID.toString());
                                        data.put("transactionReceiveId", transactionId);
                                        data.put("bankAccount", accountBankReceiveEntity.getBankAccount());
                                        data.put("bankName", bankTypeEntity.getBankName());
                                        data.put("bankCode", bankTypeEntity.getBankCode());
                                        data.put("bankId", accountBankReceiveEntity.getId());
                                        data.put("content", "" + traceTransfer);
                                        String amountForVoice = StringUtil.removeFormatNumber(entity.getDebitAmount());
                                        data.put("amount", "" + amountForShow);
                                        data.put("terminalName", "");
                                        data.put("terminalCode",
                                                terminalSubRawCodeDTO.getTerminalCode() != null ?
                                                        terminalSubRawCodeDTO.getTerminalCode() : "");
                                        data.put("rawTerminalCode",
                                                terminalSubRawCodeDTO.getRawTerminalCode() != null
                                                        ? terminalSubRawCodeDTO.getRawTerminalCode()
                                                        : "");

                                        data.put("orderId",
                                                entity.getReferenceLabelCode() != null ? entity.getReferenceLabelCode() : "");
                                        data.put("referenceNumber", entity.getFtCode() != null ? entity.getFtCode() : "");
                                        data.put("timePaid", "" + timePaid);
                                        data.put("type", "" + transactionReceiveEntity1.getType());
                                        data.put("time", "" + currentTime);
                                        data.put("refId", "" + uuid.toString());
                                        data.put("status", "1");
                                        data.put("traceId", "");
                                        data.put("transType", "C");
                                        data.put("urlLink", "");
                                        pushNotification(NotificationUtil.getNotiTitleUpdateTransaction(),
                                                message, notiEntity, data, accountBankReceiveEntity.getUserId(), StringUtil.getValueNullChecker(accountBankReceiveEntity.getPushNotification(), 1));
                                        TerminalBankEntity terminalBankEntitySync = terminalBankService
                                                .getTerminalBankByBankAccount(accountBankReceiveEntity.getBankAccount());
                                        if (terminalBankEntitySync != null) {
                                            // push data to customerSync
                                            ////////////////////////
                                            getCustomerSyncEntities(transactionReceiveEntity1.getId(),
                                                    terminalBankEntitySync.getId(),
                                                    entity.getFtCode(),
                                                    transactionReceiveEntity1, timePaid, terminalSubRawCodeDTO.getRawTerminalCode(),
                                                    "", "", "");
                                            try {
                                                final String finalRawCode = terminalSubRawCodeDTO.getRawTerminalCode();
                                                Thread thread2 = new Thread(() -> {
                                                    getCustomerSyncEntitiesV2(transactionReceiveEntity1.getId(),
                                                            terminalBankEntitySync.getId(),
                                                            entity.getFtCode(),
                                                            transactionReceiveEntity1, timePaid, finalRawCode, "", "");
                                                });
                                                thread2.start();
                                            } catch (Exception e) {
                                                logger.error("getCustomerSyncEntitiesV2: ERROR: " + e.getMessage() +
                                                        " at: " + System.currentTimeMillis());
                                            }
                                        } else {
                                            logger.info("transaction-mms-sync: NOT FOUND TerminalBankEntity");
                                        }
                                        try {
                                            pushNotificationBoxIdRef(amountForVoice,
                                                    StringUtil.formatNumberAsString(entity.getDebitAmount()),
                                                    subRawCode);
                                        } catch (Exception e) {
                                            logger.error("transaction-mms-sync: ERROR: " + e.toString());
                                        }
                                        String bankTypeId = "aa4e489b-254e-4351-9cd4-f62e09c63ebc";
                                        AccountBankReceiveShareForNotiDTO bankDTO = accountBankReceiveService
                                                .findAccountBankByTraceTransfer(traceTransfer,
                                                        bankTypeId);

                                        Long amount = Long.parseLong(entity.getDebitAmount() + "");
                                        // /////// DO INSERT TELEGRAM
                                        List<String> chatIds = telegramAccountBankService
                                                .getChatIdsByBankId(bankDTO.getBankId());
                                        if (chatIds != null && !chatIds.isEmpty()) {
                                            TelegramUtil telegramUtil = new TelegramUtil();

                                            String telegramMsg = "+" + amountForShow + " VND"
                                                    + " | TK: " + bankDTO.getBankShortName() + " - "
                                                    + bankDTO.getBankAccount()
                                                    + " | " + convertLongToDate(timePaid)
                                                    + " | " + entity.getFtCode()
                                                    + " | ND: " + entity.getTraceTransfer();
                                            for (String chatId : chatIds) {
                                                telegramUtil.sendMsg(chatId, telegramMsg);
                                            }
                                        }

                                        /////// DO INSERT LARK
                                        List<String> webhooks = larkAccountBankService
                                                .getWebhooksByBankId(bankDTO.getBankId());
                                        if (webhooks != null && !webhooks.isEmpty()) {
                                            LarkUtil larkUtil = new LarkUtil();

                                            String larkMsg = "+" + amountForShow + " VND"
                                                    + " | TK: " + bankDTO.getBankShortName() + " - "
                                                    + bankDTO.getBankAccount()
                                                    + " | " + convertLongToDate(timePaid)
                                                    + " | " + entity.getFtCode()
                                                    + " | ND: " + entity.getTraceTransfer();
                                            for (String webhook : webhooks) {
                                                larkUtil.sendMessageToLark(larkMsg, webhook);
                                            }
                                        }

                                        /////// DO INSERT GOOGLE CHAT
                                        List<String> ggChatWebhooks = googleChatAccountBankService.getWebhooksByBankId(bankDTO.getBankId());
                                        if (ggChatWebhooks != null && !ggChatWebhooks.isEmpty()) {
                                            GoogleChatUtil googleChatUtil = new GoogleChatUtil();
                                            String googleChatMsg = "+" + amountForShow + " VND"
                                                    + " | TK: " + bankTypeEntity.getBankShortName() + " - "
                                                    + bankDTO.getBankAccount()
                                                    + " | " + convertLongToDate(timePaid)
                                                    + " | " + entity.getFtCode()
                                                    + " | ND: " + entity.getTraceTransfer();
                                            for (String webhook : ggChatWebhooks) {
                                                googleChatUtil.sendMessageToGoogleChat(googleChatMsg, webhook);
                                            }
                                        }



                                    } catch (Exception e) {
                                        logger.error("transaction-mms: push to QR-Box: " + e.getMessage() + "at: " + System.currentTimeMillis());
                                    }
                                }
                            }
                        }
                    }
                }
            });
            thread.start();
        }
    }


    private boolean shouldSendNotification(List<String> notificationTypes, TransactionMMSEntity entity, TransactionReceiveEntity transactionReceiveEntity) {
        // Kiểm tra cấu hình có giao dịch đến hay không
        if (notificationTypes.contains("CREDIT")) {
            // Nếu có, push thông báo
            return true;
        } else {
            // Nếu không, kiểm tra xem có phải giao dịch RECON hay không
            if (notificationTypes.contains("RECON")) {
                if (isReconTransaction(transactionReceiveEntity)) {
                    // Nếu là giao dịch RECON, push thông báo
                    return true;
                } else {
                    // Nếu không phải giao dịch RECON, không push
                    return false;
                }
            } else {
                // Nếu không có RECON, không push
                return false;
            }
        }
    }

    private boolean isReconTransaction(TransactionReceiveEntity transactionReceiveEntity) {
        return (transactionReceiveEntity.getType() == 0 || transactionReceiveEntity.getType() == 1);
    }

    private String createMessage(List<String> notificationContents, String transType, String amount, BankTypeEntity bankTypeEntity, String bankAccount, long time, String referenceNumber, String content) {
        StringBuilder msgBuilder = new StringBuilder();

        if (notificationContents.contains("AMOUNT")) {
            String prefix = transType.toUpperCase().equals("D") ? "-" : "+";
            msgBuilder.append(prefix).append(amount).append(" VND ");
        }

        msgBuilder.append("| TK: ").append(bankTypeEntity.getBankShortName()).append(" - ").append(bankAccount)
                .append(" | ").append(convertLongToDate(time));

        if (notificationContents.contains("REFERENCE_NUMBER")) {
            msgBuilder.append(" | ").append(referenceNumber);
        }
        if (notificationContents.contains("CONTENT")) {
            msgBuilder.append(" | ND: ").append(content);
        }

        // Loại bỏ ký tự "|" thừa ở đầu nếu không có "AMOUNT"
        String message = msgBuilder.toString();
        if (!notificationContents.contains("AMOUNT")) {
            message = message.replaceFirst("^\\| ", "");
        }

        return message;
    }

    private String formatTimeForGoogleChat(long time) {
        long utcPlusSevenTime = time + 25200;
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(utcPlusSevenTime), ZoneId.of("GMT"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH);
        return dateTime.format(formatter);
    }

    private void pushNotification(String title, String message, NotificationEntity notiEntity, Map<String, String> data,
                                  String userId, int pushWss) {

        if (notiEntity != null) {
            notificationService.insertNotification(notiEntity);
        }
        List<FcmTokenEntity> fcmTokens = new ArrayList<>();
        fcmTokens = fcmTokenService.getFcmTokensByUserId(userId);
        firebaseMessagingService.sendUsersNotificationWithData(data,
                fcmTokens,
                title, message);
        try {
            if(pushWss == 1){
                socketHandler.sendMessageToUser(userId, data);
            }
        } catch (IOException e) {
            logger.error(
                    "transaction-sync: WS: socketHandler.sendMessageToUser - RECHARGE ERROR: "
                            + e.toString());
        }
    }

    public String convertLongToDate(long timestamp) {
        String result = "";
        try {
            // Tạo một đối tượng Instant từ timestamp
            Instant instant = Instant.ofEpochSecond(timestamp);

            // Tạo một đối tượng LocalDateTime từ Instant và ZoneOffset.UTC
            LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);

            // Chuyển đổi múi giờ từ UTC sang UTC+7
            ZoneOffset offset = ZoneOffset.ofHours(7);
            dateTime = dateTime.plusHours(offset.getTotalSeconds() / 3600);

            // Định dạng ngày tháng năm
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

            // Chuyển đổi thành chuỗi ngày tháng năm
            result = dateTime.format(formatter);

        } catch (Exception e) {
            logger.error("convertLongToDate: ERROR: " + e.toString());
        }
        return result;
    }

    private void getCustomerSyncEntities(String transReceiveId, String terminalBankId, String ftCode,
                                         TransactionReceiveEntity transactionReceiveEntity, long time,
                                         String rawTerminalCode, String urlLink, String serviceCode, String subCode) {
        try {
            // find customerSyncEntities by terminal_bank_id
            List<TerminalAddressEntity> terminalAddressEntities = new ArrayList<>();
            // System.out.println("terminal Bank ID: " + terminalBankId);
            terminalAddressEntities = terminalAddressService
                    .getTerminalAddressByTerminalBankId(terminalBankId);
            if (terminalAddressEntities != null && !terminalAddressEntities.isEmpty()) {
                // System.out.println("terminalAddressEntites != empty");
                TransactionBankCustomerDTO transactionBankCustomerDTO = new TransactionBankCustomerDTO();
                transactionBankCustomerDTO.setTransactionid(transactionReceiveEntity.getId());
                transactionBankCustomerDTO.setTransactiontime(time * 1000);
                transactionBankCustomerDTO.setReferencenumber(ftCode);
                transactionBankCustomerDTO.setAmount(transactionReceiveEntity.getAmount());
                transactionBankCustomerDTO.setContent(transactionReceiveEntity.getContent());
                transactionBankCustomerDTO.setBankaccount(transactionReceiveEntity.getBankAccount());
                transactionBankCustomerDTO.setTransType("C");
                transactionBankCustomerDTO.setReciprocalAccount("");
                transactionBankCustomerDTO.setReciprocalBankCode("");
                transactionBankCustomerDTO.setVa("");
                transactionBankCustomerDTO.setValueDate(0);
                transactionBankCustomerDTO.setSign(transactionReceiveEntity.getSign());
                transactionBankCustomerDTO.setOrderId(transactionReceiveEntity.getOrderId());
                if (!StringUtil.isNullOrEmpty(rawTerminalCode)) {
                    transactionBankCustomerDTO.setTerminalCode(rawTerminalCode);
                } else if (!StringUtil.isNullOrEmpty(transactionReceiveEntity.getTerminalCode())) {
                    transactionBankCustomerDTO.setTerminalCode(transactionReceiveEntity.getTerminalCode());
                } else {
                    transactionBankCustomerDTO.setTerminalCode("");
                }
                transactionBankCustomerDTO.setUrlLink(urlLink);
                transactionBankCustomerDTO.setServiceCode(serviceCode);
                transactionBankCustomerDTO.setSubTerminalCode(StringUtil.getValueNullChecker(subCode));
                int numThread = terminalAddressEntities.size();
                ExecutorService executorService = Executors.newFixedThreadPool(numThread);
                try {
                    for (TerminalAddressEntity terminalAddressEntity : terminalAddressEntities) {
                        CustomerSyncEntity customerSyncEntity = customerSyncService
                                .getCustomerSyncById(terminalAddressEntity.getCustomerSyncId());
                        if (customerSyncEntity != null) {
                            // System.out.println("customerSyncEntity != null");
                            String retryErrors = customerErrorLogService.getRetryErrorsByCustomerId(customerSyncEntity.getId());
                            List<String> errors = new ArrayList<>();
                            errors = mapperErrors(retryErrors);
                            List<String> finalErrors = errors;
                            executorService.submit(() -> pushNewTransactionToCustomerSync(transReceiveId, customerSyncEntity, transactionBankCustomerDTO,
                                    time * 1000, 1, finalErrors));
                        } else {
                            logger.info("customerSyncEntity = null");
                        }
                    }
                } finally {
                    executorService.shutdown(); // Yêu cầu các luồng dừng khi hoàn tất công việc
                    try {
                        if (!executorService.awaitTermination(700, TimeUnit.SECONDS)) {
                            executorService.shutdownNow(); // Nếu vẫn chưa dừng sau 60 giây, cưỡng chế dừng
                        }
                    } catch (InterruptedException e) {
                        executorService.shutdownNow(); // Nếu bị ngắt khi chờ, cưỡng chế dừng
                    }
                }
            } else {
                logger.info("terminalAddressEntites is empty");
            }
        } catch (Exception e) {
            logger.error("getCustomerSyncEntities MMS: ERROR: " + e.toString());
        }
    }

    private List<String> mapperErrors(String errors) {
        List<String> result = new ArrayList<>();
        try {
            if (StringUtil.isNullOrEmpty(errors)) {
                ObjectMapper mapper = new ObjectMapper();
                List<ErrorCodeMapper> list = mapper.readValue(errors, new TypeReference<List<ErrorCodeMapper>>() {
                });
                for (ErrorCodeMapper dto : list) {
                    result.add(dto.getErrorCode());
                }
            }

        } catch (Exception e) {
            logger.error("mapperErrors: Error: " + e.toString());
        }
        return result;
    }

    private void getCustomerSyncEntitiesV2(String transReceiveId, String terminalBankId, String ftCode,
                                           TransactionReceiveEntity transactionReceiveEntity, long time,
                                           String rawTerminalCode, String urlLink, String subCode) {
        try {
            // find customerSyncEntities by terminal_bank_id
            List<TerminalAddressEntity> terminalAddressEntities = new ArrayList<>();
            // System.out.println("terminal Bank ID: " + terminalBankId);
            terminalAddressEntities = terminalAddressService
                    .getTerminalAddressByTerminalBankId(terminalBankId);
            if (!terminalAddressEntities.isEmpty()) {
                // System.out.println("terminalAddressEntites != empty");
                TransactionBankCustomerDTO transactionBankCustomerDTO = new TransactionBankCustomerDTO();
                transactionBankCustomerDTO.setTransactionid(transactionReceiveEntity.getId());
                transactionBankCustomerDTO.setTransactiontime(time * 1000);
                transactionBankCustomerDTO.setReferencenumber(ftCode);
                transactionBankCustomerDTO.setAmount(transactionReceiveEntity.getAmount());
                transactionBankCustomerDTO.setContent(transactionReceiveEntity.getContent());
                transactionBankCustomerDTO.setBankaccount(transactionReceiveEntity.getBankAccount());
                transactionBankCustomerDTO.setTransType("C");
                transactionBankCustomerDTO.setReciprocalAccount("");
                transactionBankCustomerDTO.setReciprocalBankCode("");
                transactionBankCustomerDTO.setVa("");
                transactionBankCustomerDTO.setValueDate(0);
                transactionBankCustomerDTO.setSign(transactionReceiveEntity.getSign());
                transactionBankCustomerDTO.setOrderId(transactionReceiveEntity.getOrderId());
                if (!StringUtil.isNullOrEmpty(rawTerminalCode)) {
                    transactionBankCustomerDTO.setTerminalCode(rawTerminalCode);
                } else if (!StringUtil.isNullOrEmpty(transactionReceiveEntity.getTerminalCode())) {
                    transactionBankCustomerDTO.setTerminalCode(transactionReceiveEntity.getTerminalCode());
                } else {
                    transactionBankCustomerDTO.setTerminalCode("");
                }
                transactionBankCustomerDTO.setUrlLink(urlLink);
                transactionBankCustomerDTO.setSubTerminalCode(StringUtil.getValueNullChecker(subCode));
                String bankId = terminalAddressEntities.get(0).getBankId();
                List<BankReceiveConnectionEntity> bankReceiveConnectionEntities = new ArrayList<>();
                bankReceiveConnectionEntities = bankReceiveConnectionService
                        .getBankReceiveConnectionByBankId(bankId);

                try {
                    if (bankReceiveConnectionEntities != null && !bankReceiveConnectionEntities.isEmpty()) {
                        List<String> merchantIds = bankReceiveConnectionEntities.stream()
                                .map(BankReceiveConnectionEntity::getMid) // Replace with the actual method to get the merchant ID
                                .distinct() // Ensures the list is unique
                                .collect(Collectors.toList());
                        List<MerchantSyncEntity> merchantSyncEntities = merchantSyncService.getMerchantSyncByIds(merchantIds);
                        if (merchantSyncEntities != null && !merchantSyncEntities.isEmpty()) {
                            for (MerchantSyncEntity entity: merchantSyncEntities) {
                                pushTransactionSyncForClientId(entity, transactionBankCustomerDTO);
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.error("getCustomerSyncEntitiesV2 WSS: ERROR: " + e.toString());
                }

                if (bankReceiveConnectionEntities != null && !bankReceiveConnectionEntities.isEmpty()) {
                    int numThread = bankReceiveConnectionEntities.size();
                    ExecutorService executorService = Executors.newFixedThreadPool(numThread);
                    try {
                        for (BankReceiveConnectionEntity bankReceiveConnectionEntity : bankReceiveConnectionEntities) {
                            MerchantConnectionEntity merchantConnectionEntity = merchantConnectionService
                                    .getMerchanConnectionById(bankReceiveConnectionEntity.getMidConnectId());
                            String retryErrors = customerErrorLogService.getRetryErrorsByCustomerId(merchantConnectionEntity.getMid());
                            List<String> errorCodes = new ArrayList<>();
                            errorCodes = mapperErrors(retryErrors);
                            if (merchantConnectionEntity != null) {
                                List<String> finalErrorCodes = errorCodes;
                                executorService.submit(() -> pushNewTransactionToCustomerSyncV2(transReceiveId, merchantConnectionEntity,
                                        transactionBankCustomerDTO, 1, finalErrorCodes));
                            }
                        }
                    } finally {
                        executorService.shutdown(); // Yêu cầu các luồng dừng khi hoàn tất công việc
                        try {
                            if (!executorService.awaitTermination(700, TimeUnit.SECONDS)) {
                                executorService.shutdownNow(); // Nếu vẫn chưa dừng sau 60 giây, cưỡng chế dừng
                            }
                        } catch (InterruptedException e) {
                            executorService.shutdownNow(); // Nếu bị ngắt khi chờ, cưỡng chế dừng
                        }
                    }
                }
            } else {
                logger.info("terminalAddressEntites is empty");
            }
        } catch (Exception e) {
            logger.error("getCustomerSyncEntities MMS: ERROR: " + e.toString());
        }
    }

    private void pushTransactionSyncForClientId(MerchantSyncEntity merchantSyncEntity, TransactionBankCustomerDTO dto) {
        try {
            logger.info("transaction-sync: WS: pushTransactionSyncForClientId - orderId: " + dto.getOrderId() + " clientId: " + merchantSyncEntity.getClientId());
            Thread thread = new Thread(() -> {
                try {
                    Map<String, String> data = new HashMap<>();
                    data.put("transactionid", dto.getTransactionid());
                    data.put("notificationType", NotificationUtil.getNotiTypeUpdateTransaction());
                    data.put("transactiontime", dto.getTransactiontime() + "");
                    data.put("referencenumber", dto.getReferencenumber());
                    data.put("amount", dto.getAmount() + "");
                    data.put("content", dto.getContent());
                    data.put("bankaccount", dto.getBankaccount());
                    data.put("transType", dto.getTransType());
                    data.put("orderId", dto.getOrderId());
                    data.put("terminalCode", dto.getTerminalCode());
                    data.put("serviceCode", dto.getServiceCode());
                    data.put("subTerminalCode", dto.getSubTerminalCode());
                    socketHandler.sendMessageToClientId(merchantSyncEntity.getClientId(),
                            data);
                } catch (IOException e) {
                    logger.error(
                            "transaction-sync: WS: socketHandler.pushTransactionSyncForClientId - RECHARGE ERROR: "
                                    + e.toString());
                }
            });
            thread.start();
        } catch (Exception e) {
            logger.error("CustomerSync: Error: " + e.toString());
        }
    }

    private void pushNewTransactionToCustomerSyncV2(String transReceiveId, MerchantConnectionEntity entity,
                                                    TransactionBankCustomerDTO dto, int retryCount, List<String> errorCodes) {
        ResponseMessageDTO result = null;
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
        long time = DateTimeUtil.getCurrentDateTimeUTC();
        TransactionLogResponseDTO transactionLogResponseDTO = new TransactionLogResponseDTO();
        try {
            transactionLogResponseDTO.setTimeRequest(DateTimeUtil.getCurrentDateTimeUTC());
            logger.info("pushNewTransactionToCustomerSync: orderId: " +
                    dto.getOrderId());
            logger.info("pushNewTransactionToCustomerSync: sign: " + dto.getSign());
            System.out.println("pushNewTransactionToCustomerSync: orderId: " +
                    dto.getOrderId());
            System.out.println("pushNewTransactionToCustomerSync: sign: " + dto.getSign());
            TokenDTO tokenDTO = null;
            if (entity.getUsername() != null && !entity.getUsername().trim().isEmpty() &&
                    entity.getPassword() != null
                    && !entity.getPassword().trim().isEmpty()) {
                tokenDTO = getCustomerSyncTokenV2(transReceiveId, entity);
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
            data.put("serviceCode", dto.getServiceCode());
            data.put("subTerminalCode", dto.getSubTerminalCode());
            WebClient.Builder webClientBuilder = WebClient.builder()
                    .baseUrl(entity.getUrlCallback());

            // Create SSL context to ignore SSL handshake exception
            SslContext sslContext = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build();
            HttpClient httpClient = HttpClient.create().secure(sslContextSpec -> sslContextSpec.sslContext(sslContext));

            WebClient webClient = webClientBuilder.clientConnector(new ReactorClientHttpConnector(httpClient))
                    .build();

            logger.info("uriComponents: " + entity.getUrlCallback() + " " + webClient.get().uri(builder -> builder.path("/").build()).toString());
            System.out
                    .println("uriComponents: " + entity.getUrlCallback() + " " + webClient.get().uri(builder -> builder.path("/").build()).toString());
            // Mono<TransactionResponseDTO> responseMono = null;
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
            System.out.println("response status code: " + response.statusCode());
            try {
                transactionLogResponseDTO.setTimeResponse(DateTimeUtil.getCurrentDateTimeUTC());
                transactionLogResponseDTO.setStatusCode(response.statusCode().value());
            } catch (Exception e) {}
            if (response.statusCode().is2xxSuccessful()) {
                String json = response.bodyToMono(String.class).block();
                System.out.println("Response pushNewTransactionToCustomerSync: " + json);
                logger.info("Response pushNewTransactionToCustomerSync: " + json + " status: " + response.statusCode());
                String errorCode = validateFormatCallbackResponse(json);
                if (!StringUtil.isNullOrEmpty(errorCode)) {
                    // retry callback
                    if (Objects.nonNull(errorCodes) && errorCodes.contains(errorCode)) {
                        if (retryCount < 10) {
                            pushNewTransactionToCustomerSyncV2(transReceiveId, entity,
                                    dto, ++retryCount, errorCodes);
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
                // nếu trả sai format retry callback
                String errorCode = validateFormatCallbackResponse(json);
                if (!StringUtil.isNullOrEmpty(errorCode)) {
                    // retry callback
                    if (Objects.nonNull(errorCodes) && errorCodes.contains(errorCode)) {
                        if (retryCount < 10) {
                            pushNewTransactionToCustomerSyncV2(transReceiveId, entity,
                                    dto, ++retryCount, errorCodes);
                        }
                    }
                }
                System.out.println("Response pushNewTransactionToCustomerSync: " + json);
                logger.info("Response pushNewTransactionToCustomerSync: " + json + " status: " + response.statusCode());
                result = new ResponseMessageDTO("FAILED", "E05 - " + json);
            }
        } catch (Exception e) {
            LocalDateTime currentDateTime = LocalDateTime.now();
            long responseTime = currentDateTime.toEpochSecond(ZoneOffset.UTC);
            result = new ResponseMessageDTO("FAILED", "E05 - " + e.toString());
            logger.error(
                    "Error Unexpected at pushNewTransactionToCustomerSync: " +
                            entity.getUrlCallback() + " - "
                            + e.toString()
                            + " at: " + responseTime);

//            // retry callback
            if (retryCount < 10) {
                pushNewTransactionToCustomerSyncV2(transReceiveId, entity,
                        dto, ++retryCount, errorCodes);
            }
        } finally {
            if (result != null) {
                UUID logUUID = UUID.randomUUID();
                String address = entity.getUrlCallback();
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
                result = rootNode.get("errorReason").asText();;
            }
        } catch (Exception e) {
            logger.error("validateFormatCallbackResponse: ERROR: " +
                    e.getMessage() + " at: " + System.currentTimeMillis());
        }
        return result;
    }

    private TokenDTO getCustomerSyncTokenV2(String transReceiveId, MerchantConnectionEntity entity) {
        TokenDTO result = null;
        ResponseMessageDTO msgDTO = null;
        TransactionLogResponseDTO transactionLogResponseDTO = new TransactionLogResponseDTO();
        try {
            transactionLogResponseDTO.setTimeRequest(DateTimeUtil.getCurrentDateTimeUTC());
            String key = entity.getUsername() + ":" + entity.getPassword();
            String encodedKey = Base64.getEncoder().encodeToString(key.getBytes());
            logger.info("key: " + encodedKey + " - username: " + entity.getUsername() + " - password: "
                    + entity.getPassword());
            UriComponents uriComponents = null;
            WebClient webClient = null;
            Map<String, Object> data = new HashMap<>();
            uriComponents = UriComponentsBuilder
                    .fromHttpUrl(entity.getUrlGetToken())
                    .buildAndExpand();
            webClient = WebClient.builder()
                    .baseUrl(entity.getUrlGetToken())
                    .build();
            System.out.println("uriComponents: " + uriComponents.getPath());
            Mono<TokenDTO> responseMono = webClient.method(HttpMethod.POST)
                    .uri(uriComponents.toUri())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Basic " + encodedKey)
                    .body(BodyInserters.fromValue(data))
                    .exchange()
                    .flatMap(clientResponse -> {
                        System.out.println("status code: " + clientResponse.statusCode());
                        try {
                            transactionLogResponseDTO.setTimeResponse(DateTimeUtil.getCurrentDateTimeUTC());
                            transactionLogResponseDTO.setStatusCode(clientResponse.statusCode().value());
                        } catch (Exception e) {}
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
                logger.info("Token got: " + result.getAccess_token() + " - from: " + entity.getUrlGetToken());
            } else {
                msgDTO = new ResponseMessageDTO("FAILED", "E05");
                logger.info("Token could not be retrieved from: " + entity.getUrlGetToken());
            }
        } catch (Exception e) {
            msgDTO = new ResponseMessageDTO("FAILED", "E05 - " + e.toString());
            logger.error("Error at getCustomerSyncToken: " + entity.getUrlGetToken() + " - " + e.toString());
        } finally {
            if (msgDTO != null) {
                UUID logUUID = UUID.randomUUID();
                String address = entity.getUrlGetToken();
                TransactionReceiveLogEntity logEntity = new TransactionReceiveLogEntity();
                logEntity.setId(logUUID.toString());
                logEntity.setTransactionId(transReceiveId);
                logEntity.setStatus(msgDTO.getStatus());
                logEntity.setMessage(msgDTO.getMessage());
                logEntity.setStatusCode(transactionLogResponseDTO.getStatusCode());
                logEntity.setType(0);
                logEntity.setTimeResponse(transactionLogResponseDTO.getTimeResponse());
                logEntity.setTime(transactionLogResponseDTO.getTimeRequest());
                logEntity.setUrlCallback(address);
                transactionReceiveLogService.insert(logEntity);
            }
        }
        return result;
    }

    private ResponseMessageDTO pushNewTransactionToCustomerSync(String transReceiveId, CustomerSyncEntity entity,
                                                                TransactionBankCustomerDTO dto,
                                                                long time, int retryCount, List<String> errorCodes) {
        ResponseMessageDTO result = null;
        TransactionLogResponseDTO transactionLogResponseDTO = new TransactionLogResponseDTO();
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
                    dto.getOrderId() + " at: " + System.currentTimeMillis());
            // System.out.println("pushNewTransactionToCustomerSync: orderId: " +
            // dto.getOrderId());
            logger.info("pushNewTransactionToCustomerSync: sign: " + dto.getSign());
            // System.out.println("pushNewTransactionToCustomerSync: orderId: " +
            // dto.getOrderId());
            // System.out.println("pushNewTransactionToCustomerSync: sign: " +
            // dto.getSign());
            TokenDTO tokenDTO = null;
            if (entity.getUsername() != null && !entity.getUsername().trim().isEmpty() &&
                    entity.getPassword() != null
                    && !entity.getPassword().trim().isEmpty()) {
                tokenDTO = getCustomerSyncToken(transReceiveId, entity);
            } else if (entity.getToken() != null && !entity.getToken().trim().isEmpty()) {
                logger.info("Get token from record: " + entity.getId() + " at: " + System.currentTimeMillis());
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
            data.put("serviceCode", dto.getServiceCode());
            data.put("subTerminalCode", dto.getSubTerminalCode());
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
            // System.out.println("pushNewTransactionToCustomerSync request at:" +
            // startRequestTime);
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
            try {
                transactionLogResponseDTO.setTimeResponse(DateTimeUtil.getCurrentDateTimeUTC());
                transactionLogResponseDTO.setStatusCode(response.statusCode().value());
            } catch (Exception e) {}
            logger.info("Response pushNewTransactionToCustomerSync response orderId: " + dto.getOrderId()
                    + " at: " + System.currentTimeMillis());
            // System.out.println("Response pushNewTransactionToCustomerSync at:" +
            // responseTime);
            // System.out.println("response: " + response.toString());
            // System.out.println("response status code: " + response.statusCode());
            if (response.statusCode().is2xxSuccessful()) {
                String json = response.bodyToMono(String.class).block();
                logger.info("Response pushNewTransactionToCustomerSync: " + json + " status Code: " + response.statusCode());
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
            } else if (response.statusCode().value() != 400 && (response.statusCode().is4xxClientError() || response.statusCode().is5xxServerError())) {
                String json = response.bodyToMono(String.class).block();
                logger.info("Response pushNewTransactionToCustomerSync: " + json + " status Code: " + response.statusCode());
                // retry callback
                if (retryCount < 10) {
                    pushNewTransactionToCustomerSync(transReceiveId, entity,
                            dto, time, ++retryCount, errorCodes);
                }
                result = new ResponseMessageDTO("FAILED", "E05 - " + json);
            } else {
                String json = response.bodyToMono(String.class).block();
                logger.info("Response pushNewTransactionToCustomerSync: " + json + " status Code: " + response.statusCode());
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
            if (retryCount < 10) {
                pushNewTransactionToCustomerSync(transReceiveId, entity,
                        dto, time, ++retryCount, errorCodes);
            }
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

    private TokenDTO getCustomerSyncToken(String transReceiveId, CustomerSyncEntity entity) {
        TokenDTO result = null;
        ResponseMessageDTO msgDTO = null;
        TransactionLogResponseDTO transactionLogResponseDTO = new TransactionLogResponseDTO();
        try {
            transactionLogResponseDTO.setTimeRequest(DateTimeUtil.getCurrentDateTimeUTC());
            String key = entity.getUsername() + ":" + entity.getPassword();
            String encodedKey = Base64.getEncoder().encodeToString(key.getBytes());
            logger.info("key: " + encodedKey + " - username: " + entity.getUsername() + " - password: "
                    + entity.getPassword());
            // System.out.println("key: " + encodedKey + " - username: " +
            // entity.getUsername() + " - password: "
            // + entity.getPassword());
            String suffixUrl = entity.getSuffixUrl() != null && !entity.getSuffixUrl().isEmpty() ? entity.getSuffixUrl()
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
            // System.out.println("uriComponents: " + uriComponents.toString());
            Mono<TokenDTO> responseMono = webClient.method(HttpMethod.POST)
                    .uri(uriComponents.toUri())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Basic " + encodedKey)
                    .body(BodyInserters.fromValue(data))
                    .exchange()
                    .flatMap(clientResponse -> {
                        System.out.println("status code: " + clientResponse.statusCode());
                        try {
                            transactionLogResponseDTO.setTimeResponse(DateTimeUtil.getCurrentDateTimeUTC());
                            transactionLogResponseDTO.setStatusCode(clientResponse.statusCode().value());
                        } catch (Exception e) {}
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

            Optional<TokenDTO> resultOptional = responseMono.subscribeOn(Schedulers.boundedElastic()).blockOptional();
            if (resultOptional.isPresent()) {
                result = resultOptional.get();
                msgDTO = new ResponseMessageDTO("SUCCESS", "");
                if (entity.getIpAddress() != null && !entity.getIpAddress().isEmpty()) {
                    logger.info("Token got: " + result.getAccess_token() + " - from: " + entity.getIpAddress());
                } else {
                    logger.info("Token got: " + result.getAccess_token() + " - from: " + entity.getInformation());
                }

                // System.out.println("Token got: " + result.getAccess_token() + " - from: " +
                // entity.getIpAddress());
            } else {
                msgDTO = new ResponseMessageDTO("FAILED", "E05");
                if (entity.getIpAddress() != null && !entity.getIpAddress().isEmpty()) {
                    logger.info("Token could not be retrieved from: " + entity.getIpAddress());
                } else {
                    logger.info("Token could not be retrieved from: " + entity.getInformation());
                }
            }
            ///
        } catch (Exception e) {
            msgDTO = new ResponseMessageDTO("FAILED", "E05 - " + e.toString());
            if (entity.getIpAddress() != null && !entity.getIpAddress().isEmpty()) {
                logger.error("Error at getCustomerSyncToken: " + entity.getIpAddress() + " - " + e.toString());
                // System.out.println("Error at getCustomerSyncToken: " + entity.getIpAddress()
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
        LocalDateTime responseDateTime = LocalDateTime.now();
        long responseTime = responseDateTime.toEpochSecond(ZoneOffset.UTC);
        logger.info("getCustomerSyncToken response at:" + responseTime);
        // System.out.println("getCustomerSyncToken response at:" + responseTime);
        return result;
    }

    // get result - TransactionMMSResponseDTO
    private TransactionMMSResponseDTO validateTransactionBank(TransactionMMSEntity entity,
                                                              TerminalBankEntity terminalBankEntity) {
        TransactionMMSResponseDTO result = null;
        try {
            if (entity != null) {
                // check duplicated FtCode
                String checkExisted = transactionMMSService.checkExistedFtCode(entity.getFtCode());
                if (checkExisted == null) {
                    // check valid terminalId
                    if (terminalBankEntity != null) {
                        // check checkSum is match with data (MD5, checksum = traceTransfer +
                        // billNumber + payDate + debitAmount + acccessKey)
                        String dataCheckSum = BankEncryptUtil.generateMD5Checksum(entity.getTraceTransfer(),
                                entity.getBillNumber(), entity.getPayDate(), entity.getDebitAmount());
                        // System.out.println("data getTraceTransfer: " + entity.getTraceTransfer());
                        // System.out.println("data getBillNumber: " + entity.getBillNumber());
//                        System.out.println("data getPayDate: " + entity.getPayDate());
                        // System.out.println("data getDebitAmount: " + entity.getDebitAmount());
                        // System.out.println("data checksum: " + dataCheckSum);
                        if (BankEncryptUtil.isMatchChecksum(dataCheckSum, entity.getCheckSum())) {
//                        if (true) {
                            result = new TransactionMMSResponseDTO("00", "Success");
                        } else {
                            logger.info("FAILED CHECKSUM: CHECKORDER: " + entity.getReferenceLabelCode());
                            result = new TransactionMMSResponseDTO("00", "Success");
                        }
//                        else {
//                            logger.info("FAILED CHECKSUM: CHECKORDER: " + entity.getReferenceLabelCode());
//                            ResponseObjectDTO responseObjectDTO = checkOrderFromMB(entity.getFtCode(), entity.getReferenceLabelCode());
//                            if (responseObjectDTO != null && "SUCCESS".equals(responseObjectDTO.getStatus())) {
//                                logger.info("SUCCESS CHECKSUM: CHECKORDER: INFO: " + entity.getReferenceLabelCode());
//                                result = new TransactionMMSResponseDTO("00", "Success");
//                            } else {
//                                // checksum is not match
//                                // System.out.println("checksum is not match"
//                                logger.error("FAILED CHECKSUM: CHECKORDER: ERROR: " + entity.getReferenceLabelCode());
//                                result = new TransactionMMSResponseDTO("12", "False checksum");
//                            }
//                        }
                    } else {
                        result = new TransactionMMSResponseDTO("07", "Merchant is not exist");
                    }
                } else {
                    result = new TransactionMMSResponseDTO("20", "Duplicated FtCode");
                }
            } else {
                result = new TransactionMMSResponseDTO("02", "Invalid order");
            }
        } catch (Exception e) {
            logger.error("transaction-mms-sync - validateTransactionBank: Error " + e.toString());
            result = new TransactionMMSResponseDTO("99", "Internal error");
        }
        return result;
    }

    // check order API
    @PostMapping("transaction-mms/check-order")
    public ResponseEntity<Object> checkTranscationMMS(@RequestBody TransMMSCheckInDTO dto) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            String accessKey = "SABAccessKey";
            String checkSum = BankEncryptUtil.generateMD5CheckOrderChecksum(dto.getBankAccount(),
                    accessKey);
            if (BankEncryptUtil.isMatchChecksum(dto.getCheckSum(), checkSum)) {
                TransMMSCheckOutDTO tranMMSCheckOutDTO = null;
                if (dto != null && dto.getOrderId() != null && dto.getReferenceNumber() != null) {
                    // 3 trường hợp
                    // 1. Đủ ftCode và orderId
                    // 2. Có ftCode không có orderId
                    // 3. Có orderId mà không ftCode
                    if (!dto.getReferenceNumber().isEmpty() && !dto.getOrderId().isEmpty()) {

                        TransactionReceiveEntity transactionReceiveEntity = transactionReceiveService
                                .getTransactionReceiveByRefNumberAndOrderId(dto.getReferenceNumber(), dto.getOrderId());
                        if (transactionReceiveEntity != null) {
                            // trùng ft code và order id trong gd
                            tranMMSCheckOutDTO = new TransMMSCheckOutDTO();
                            tranMMSCheckOutDTO.setReferenceNumber(transactionReceiveEntity.getReferenceNumber());
                            tranMMSCheckOutDTO.setOrderId(transactionReceiveEntity.getOrderId());
                            tranMMSCheckOutDTO.setAmount(transactionReceiveEntity.getAmount() + "");
                            tranMMSCheckOutDTO.setTime(transactionReceiveEntity.getTime());
                            tranMMSCheckOutDTO.setTransType(transactionReceiveEntity.getTransType());
                            tranMMSCheckOutDTO.setStatus(transactionReceiveEntity.getStatus());
                            result = tranMMSCheckOutDTO;
                            httpStatus = HttpStatus.OK;
                        } else {
                            // không trùng ft code và order id trong gd
                            // Mặc định lấy theo ftCode
                            transactionReceiveEntity = transactionReceiveService
                                    .getTransactionReceiveByRefNumber(dto.getReferenceNumber());
                            if (transactionReceiveEntity != null) {
                                // trùng ft code và order id trong gd
                                tranMMSCheckOutDTO = new TransMMSCheckOutDTO();
                                tranMMSCheckOutDTO.setReferenceNumber(transactionReceiveEntity.getReferenceNumber());
                                tranMMSCheckOutDTO.setOrderId(transactionReceiveEntity.getOrderId());
                                tranMMSCheckOutDTO.setAmount(transactionReceiveEntity.getAmount() + "");
                                tranMMSCheckOutDTO.setTime(transactionReceiveEntity.getTime());
                                tranMMSCheckOutDTO.setTransType(transactionReceiveEntity.getTransType());
                                tranMMSCheckOutDTO.setStatus(transactionReceiveEntity.getStatus());
                                result = tranMMSCheckOutDTO;
                                httpStatus = HttpStatus.OK;
                            } else {
                                transactionReceiveEntity = transactionReceiveService
                                        .getTransactionReceiveByOrderId(dto.getOrderId());
                                if (transactionReceiveEntity != null) {
                                    tranMMSCheckOutDTO = new TransMMSCheckOutDTO();
                                    tranMMSCheckOutDTO
                                            .setReferenceNumber(transactionReceiveEntity.getReferenceNumber());
                                    tranMMSCheckOutDTO.setOrderId(transactionReceiveEntity.getOrderId());
                                    tranMMSCheckOutDTO.setAmount(transactionReceiveEntity.getAmount() + "");
                                    tranMMSCheckOutDTO.setTime(transactionReceiveEntity.getTime());
                                    tranMMSCheckOutDTO.setTransType(transactionReceiveEntity.getTransType());
                                    tranMMSCheckOutDTO.setStatus(transactionReceiveEntity.getStatus());
                                    result = tranMMSCheckOutDTO;
                                    httpStatus = HttpStatus.OK;
                                }
                            }
                        }
                        // đi xuống 2 phần dưới
                    } else if (!dto.getReferenceNumber().isEmpty() && dto.getOrderId().isEmpty()) {
                        // thấy
                        TransactionReceiveEntity transactionReceiveEntity = transactionReceiveService
                                .getTransactionReceiveByRefNumber(dto.getReferenceNumber());
                        if (transactionReceiveEntity != null) {
                            // trùng ft code và order id trong gd
                            tranMMSCheckOutDTO = new TransMMSCheckOutDTO();
                            tranMMSCheckOutDTO.setReferenceNumber(transactionReceiveEntity.getReferenceNumber());
                            tranMMSCheckOutDTO.setOrderId(transactionReceiveEntity.getOrderId());
                            tranMMSCheckOutDTO.setAmount(transactionReceiveEntity.getAmount() + "");
                            tranMMSCheckOutDTO.setTime(transactionReceiveEntity.getTime());
                            tranMMSCheckOutDTO.setTransType(transactionReceiveEntity.getTransType());
                            tranMMSCheckOutDTO.setStatus(transactionReceiveEntity.getStatus());
                            result = tranMMSCheckOutDTO;
                            httpStatus = HttpStatus.OK;
                        }
                        // không thấy

                    } else if (!dto.getOrderId().isEmpty() && dto.getReferenceNumber().isEmpty()) {
                        // thấy
                        TransactionReceiveEntity transactionReceiveEntity = transactionReceiveService
                                .getTransactionReceiveByOrderId(dto.getOrderId());
                        if (transactionReceiveEntity != null) {
                            tranMMSCheckOutDTO = new TransMMSCheckOutDTO();
                            tranMMSCheckOutDTO.setReferenceNumber(transactionReceiveEntity.getReferenceNumber());
                            tranMMSCheckOutDTO.setOrderId(transactionReceiveEntity.getOrderId());
                            tranMMSCheckOutDTO.setAmount(transactionReceiveEntity.getAmount() + "");
                            tranMMSCheckOutDTO.setTime(transactionReceiveEntity.getTime());
                            tranMMSCheckOutDTO.setTransType(transactionReceiveEntity.getTransType());
                            tranMMSCheckOutDTO.setStatus(transactionReceiveEntity.getStatus());
                            result = tranMMSCheckOutDTO;
                            httpStatus = HttpStatus.OK;
                        }
                        // không thấy
                    }
                    if (tranMMSCheckOutDTO == null) {
                        logger.error("checkTranscationMMS: CANNOT FOUND RECORD");
                        httpStatus = HttpStatus.BAD_REQUEST;
                        result = new ResponseMessageDTO("FAILED", "E40");
                    }
                } else {
                    logger.error("checkTranscationMMS: INVALID REQUEST BODY");
                    httpStatus = HttpStatus.BAD_REQUEST;
                    result = new ResponseMessageDTO("FAILED", "E39");
                }
            } else {
                logger.error("checkTranscationMMS: INVALID REQUEST BODY");
                httpStatus = HttpStatus.BAD_REQUEST;
                result = new ResponseMessageDTO("FAILED", "E39");
            }
        } catch (Exception e) {
            logger.error("checkTranscationMMS: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
            result = new ResponseMessageDTO("FAILED", "E05");
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // refund API for SAB
    @PostMapping("transaction-mms/refund")
    public ResponseEntity<ResponseMessageDTO> refund(@RequestBody RefundRequestDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            logger.info("refund: Bank Account: " + dto.getBankAccount());
            logger.info("refund: FT Code: " + dto.getReferenceNumber());
            logger.info("refund: Amount: " + dto.getAmount());
            String accessKey = "SABAccessKey";
            String checkSum = BankEncryptUtil.generateMD5RefundCustomerChecksum(dto.getBankAccount(),
                    dto.getReferenceNumber(), accessKey);
            if (BankEncryptUtil.isMatchChecksum(dto.getCheckSum(), checkSum)) {
                // find terminal ID by bankAccount
                TerminalBankEntity terminalBankEntity = terminalBankService
                        .getTerminalBankByBankAccount(dto.getBankAccount());
                if (terminalBankEntity != null) {
                    String refundResult = refundFromMB(terminalBankEntity.getTerminalId(), dto.getReferenceNumber(),
                            dto.getAmount(), dto.getContent());
                    if (refundResult != null) {
                        if (refundResult.trim().equals("4863")) {
                            logger.error("refund: ERROR: " + dto.getBankAccount() + " FT CODE IS NOT EXISTED");
                            httpStatus = HttpStatus.BAD_REQUEST;
                            result = new ResponseMessageDTO("FAILED", "E44");
                        } else if (refundResult.trim().equals("4857")) {
                            logger.error("refund: ERROR: " + dto.getBankAccount() + " INVALID AMOUNT");
                            httpStatus = HttpStatus.BAD_REQUEST;
                            result = new ResponseMessageDTO("FAILED", "E45");
                        } else if (refundResult.trim().contains("FT")) {
                            httpStatus = HttpStatus.OK;
                            result = new ResponseMessageDTO("SUCCESS", refundResult);
                        } else {
                            logger.error("refund: ERROR: UNEXPECTED ERROR");
                            httpStatus = HttpStatus.BAD_REQUEST;
                            result = new ResponseMessageDTO("FAILED", "E05");
                        }
                    } else {
                        logger.error("refund: ERROR: " + dto.getBankAccount() + " REFUND FAILED");
                        httpStatus = HttpStatus.BAD_REQUEST;
                        result = new ResponseMessageDTO("FAILED", "E43");
                    }
                } else {
                    logger.error("refund: ERROR: " + dto.getBankAccount() + " INVALID TERMINAL");
                    httpStatus = HttpStatus.BAD_REQUEST;
                    result = new ResponseMessageDTO("FAILED", "E42");
                }
            } else {
                logger.error("refund: ERROR: " + dto.getReferenceNumber() + " INVALID CHECKSUM");
                httpStatus = HttpStatus.BAD_REQUEST;
                result = new ResponseMessageDTO("FAILED", "E41");
            }
        } catch (Exception e) {
            logger.error("refund: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
            result = new ResponseMessageDTO("FAILED", "E05");
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // FOR merchant
    @PostMapping("transactions/refund")
    public ResponseEntity<ResponseMessageDTO> refundForMerchant(
            @RequestHeader("Authorization") String token,
            @RequestBody RefundRequestDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null) {
                logger.info("refundForMerchant: Bank Account: " + dto.getBankAccount());
                logger.info("refundForMerchant: FT Code: " + dto.getReferenceNumber());
                logger.info("refundForMerchant: Amount: " + dto.getAmount());
                // String accessKey = "SABAccessKey";
                String username = getUsernameFromToken(token);
                if (username != null && !username.trim().isEmpty()) {
                    List<String> checkExistedCustomerSync = accountCustomerBankService
                            .checkExistedCustomerSyncByUsername(username);
                    if (checkExistedCustomerSync != null && !checkExistedCustomerSync.isEmpty()) {
                        // check bankAccount belong to merchant
                        String checkValidBankAccount = accountCustomerBankService.checkExistedBankAccountIntoMerchant(
                                dto.getBankAccount(), checkExistedCustomerSync.get(0));
                        if (checkValidBankAccount != null && !checkValidBankAccount.trim().isEmpty()) {
                            // process refund
                            String checkSum = BankEncryptUtil.generateMD5RefundCustomerChecksum(dto.getBankAccount(),
                                    dto.getReferenceNumber(), username);
                            if (BankEncryptUtil.isMatchChecksum(dto.getCheckSum(), checkSum)) {
                                // find terminal ID by bankAccount
                                TerminalBankEntity terminalBankEntity = terminalBankService
                                        .getTerminalBankByBankAccount(dto.getBankAccount());
                                if (terminalBankEntity != null) {
                                    String refundResult = refundFromMB(terminalBankEntity.getTerminalId(),
                                            dto.getReferenceNumber(),
                                            dto.getAmount(), dto.getContent());
                                    if (refundResult != null) {
                                        if (refundResult.trim().equals("4863")) {
                                            logger.error(
                                                    "refundForMerchant: ERROR: " + dto.getBankAccount()
                                                            + " FT CODE IS NOT EXISTED");
                                            httpStatus = HttpStatus.BAD_REQUEST;
                                            result = new ResponseMessageDTO("FAILED", "E44");
                                        } else if (refundResult.trim().equals("4857")) {
                                            logger.error(
                                                    "refundForMerchant: ERROR: " + dto.getBankAccount()
                                                            + " INVALID AMOUNT");
                                            httpStatus = HttpStatus.BAD_REQUEST;
                                            result = new ResponseMessageDTO("FAILED", "E45");
                                        } else if (refundResult.trim().contains("FT")) {
                                            httpStatus = HttpStatus.OK;
                                            result = new ResponseMessageDTO("SUCCESS", refundResult);
                                        } else {
                                            logger.error("refundForMerchant: ERROR: UNEXPECTED ERROR");
                                            httpStatus = HttpStatus.BAD_REQUEST;
                                            result = new ResponseMessageDTO("FAILED", "E05");
                                        }
                                    } else {
                                        logger.error(
                                                "refundForMerchant: ERROR: " + dto.getBankAccount() + " REFUND FAILED");
                                        httpStatus = HttpStatus.BAD_REQUEST;
                                        result = new ResponseMessageDTO("FAILED", "E43");
                                    }
                                } else {
                                    logger.error(
                                            "refundForMerchant: ERROR: " + dto.getBankAccount() + " INVALID TERMINAL");
                                    httpStatus = HttpStatus.BAD_REQUEST;
                                    result = new ResponseMessageDTO("FAILED", "E42");
                                }
                            } else {
                                logger.error(
                                        "refundForMerchant: ERROR: " + dto.getReferenceNumber() + " INVALID CHECKSUM");
                                httpStatus = HttpStatus.BAD_REQUEST;
                                result = new ResponseMessageDTO("FAILED", "E41");
                            }
                        } else {
                            // bank account is not matched
                            System.out.println("refundForMerchant: BANK ACCOUNT IS NOT MATCH WITH MERCHANT INFO");
                            logger.error("refundForMerchant: BANK ACCOUNT IS NOT MATCH WITH MERCHANT INFO");
                            result = new ResponseMessageDTO("FAILED", "E77");
                            httpStatus = HttpStatus.BAD_REQUEST;
                        }
                    } else {
                        // merchant is not existed
                        System.out.println("refundForMerchant: MERCHANT IS NOT EXISTED");
                        logger.error("refundForMerchant: MERCHANT IS NOT EXISTED");
                        result = new ResponseMessageDTO("FAILED", "E104");
                        httpStatus = HttpStatus.BAD_REQUEST;
                    }
                } else {
                    System.out.println("refundForMerchant: INVALID TOKEN");
                    logger.error("refundForMerchant: INVALID TOKEN");
                    result = new ResponseMessageDTO("FAILED", "E74");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                System.out.println("refundForMerchant: INVALID REQUEST BODY");
                logger.error("refundForMerchant: INVALID REQUEST BODY");
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("refundForMerchant: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
            result = new ResponseMessageDTO("FAILED", "E05");
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // Refund Service
    @PostMapping("transaction/refund")
    public ResponseEntity<ResponseMessageDTO> refundService(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody RefundRequestDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        TransactionRefundLogEntity refundLogEntity = null;
        String idempotencyKey = "";
        long time = 0;
        try {
            if (dto != null) {
                logger.info("refundForMerchant: Bank Account: " + dto.getBankAccount());
                logger.info("refundForMerchant: FT Code: " + dto.getReferenceNumber());
                logger.info("refundForMerchant: Amount: " + dto.getAmount());
                idempotencyKey = BankEncryptUtil
                        .generateIdempotencyKey(dto.getReferenceNumber(), dto.getBankAccount());
                Optional<String> existingResponse = idempotencyService.getResponseForKey(idempotencyKey);
                if (existingResponse.isPresent()) {
                    result = new ResponseMessageDTO("FAILED", "E158");
                    httpStatus = HttpStatus.BAD_REQUEST;
                } else {
                    // String accessKey = "SABAccessKey";
                    String username = getUsernameFromToken(token);
                    if (username != null && !username.trim().isEmpty()) {
                        List<String> checkExistedCustomerSync = accountCustomerBankService
                                .checkExistedCustomerSyncByUsername(username);
                        if (checkExistedCustomerSync != null && !checkExistedCustomerSync.isEmpty()) {
                            // check bankAccount belong to merchant
                            String checkValidBankAccount = accountCustomerBankService.checkExistedBankAccountIntoMerchant(
                                    dto.getBankAccount(), checkExistedCustomerSync.get(0));
                            if (checkValidBankAccount != null && !checkValidBankAccount.trim().isEmpty()) {
                                // check secretKey
//                            String secretKey = "secretKey";
                                // get secretKey by bankAccount
                                String secretKey = accountCustomerBankService.checkSecretKey(dto.getBankAccount(), checkExistedCustomerSync.get(0));
                                // process refund
                                String checkSum = BankEncryptUtil.generateRefundMD5Checksum(secretKey, dto.getReferenceNumber()
                                        , dto.getAmount(), dto.getBankAccount());
//                            String checkSum = "c68ee42e728b9dbb13dcb2a3d509b877";
                                if (BankEncryptUtil.isMatchChecksum(dto.getCheckSum(), checkSum)) {
                                    // find terminal ID by bankAccount
                                    boolean checkIdempotency =
                                            idempotencyService.saveResponseForKey(idempotencyKey, dto.getReferenceNumber(), 30);
                                    if (checkIdempotency) {
                                        TransactionReceiveEntity transactionReceiveEntity = transactionReceiveService
                                                .getTransactionReceiveByRefNumber(dto.getReferenceNumber(), "C");
                                        if (Objects.nonNull(transactionReceiveEntity)
                                        && (!StringUtil.isNullOrEmpty(dto.getTerminalCode())
                                            || !StringUtil.isNullOrEmpty(dto.getSubTerminalCode()))
                                        && (!StringUtil.getValueNullChecker(transactionReceiveEntity.getTerminalCode())
                                                .equals(dto.getTerminalCode())
                                        || !StringUtil.getValueNullChecker(transactionReceiveEntity.getSubCode())
                                                .equals(dto.getSubTerminalCode()))) {

                                            logger.error(
                                                    "refundForMerchant: ERROR: " + dto.getReferenceNumber() + " INVALID TERMINAL COD OR SUB CODE");
                                            httpStatus = HttpStatus.BAD_REQUEST;
                                            result = new ResponseMessageDTO("FAILED", "E164");
                                        } else {
                                            TerminalBankEntity terminalBankEntity = terminalBankService
                                                    .getTerminalBankByBankAccount(dto.getBankAccount());
                                            if (terminalBankEntity != null) {
                                                // check multiTimes transaction_refund;
                                                TransactionCheckMultiTimesDTO checkMultiTimesDTO =
                                                        transactionRefundService.getTransactionRefundCheck(dto.getBankAccount(),
                                                                dto.getReferenceNumber());
                                                time = DateTimeUtil.getCurrentDateTimeUTC();

                                                refundLogEntity = new TransactionRefundLogEntity();
                                                refundLogEntity.setId(UUID.randomUUID().toString());
                                                refundLogEntity.setBankAccount(dto.getBankAccount());
                                                refundLogEntity.setReferenceNumber(dto.getReferenceNumber());
                                                refundLogEntity.setContent(dto.getContent());
                                                refundLogEntity.setAmount(Long.parseLong(dto.getAmount()));
                                                refundLogEntity.setTimeCreated(time);
                                                refundLogEntity.setTimeResponse(DateTimeUtil.getCurrentDateTimeUTC());
                                                refundLogEntity.setRefNumber(dto.getReferenceNumber());
                                                refundLogEntity.setCheckSum(dto.getCheckSum());
                                                // nếu đã refund trước đó
                                                if (Objects.nonNull(checkMultiTimesDTO)) {
                                                    if (checkMultiTimesDTO.getMultiTimes()) {
                                                        // lenh refund truoc do cho hoan tien nhieu lan => cho phep hoan tien tiep
                                                        String refundResult = refundFromMB(terminalBankEntity.getTerminalId(),
                                                                dto.getReferenceNumber(),
                                                                dto.getAmount(), dto.getContent());
                                                        refundLogEntity.setReferenceNumber("");
                                                        refundLogEntity.setStatus(0);

                                                        if (refundResult != null) {
                                                            refundLogEntity.setMessage(refundResult);
                                                            switch (refundResult) {
                                                                case "4863":
                                                                    logger.error(
                                                                            "refundForMerchant: ERROR: " + dto.getReferenceNumber()
                                                                                    + " FT CODE IS NOT EXISTED");

                                                                    httpStatus = HttpStatus.BAD_REQUEST;
                                                                    result = new ResponseMessageDTO("FAILED", "E44");
                                                                    break;
                                                                case "4857":
                                                                    logger.error(
                                                                            "refundForMerchant: ERROR: " + dto.getReferenceNumber()
                                                                                    + " INVALID AMOUNT");

                                                                    httpStatus = HttpStatus.BAD_REQUEST;
                                                                    result = new ResponseMessageDTO("FAILED", "E45");
                                                                    break;
                                                                case "002":
                                                                    logger.error(
                                                                            "refundForMerchant: ERROR: " + dto.getReferenceNumber()
                                                                                    + " CONNECTION TIMEOUT");

                                                                    httpStatus = HttpStatus.BAD_REQUEST;
                                                                    result = new ResponseMessageDTO("FAILED", "E215");
                                                                    break;
                                                                case "4877":
                                                                    logger.error(
                                                                            "refundForMerchant: ERROR: " + dto.getReferenceNumber()
                                                                                    + " RECORD DOESN'T EXIST TBL MMS_PAYMENT_BANK");

                                                                    httpStatus = HttpStatus.BAD_REQUEST;
                                                                    result = new ResponseMessageDTO("FAILED", "E218");
                                                                    break;
                                                                case "201":
                                                                    logger.error(
                                                                            "refundForMerchant: ERROR: " + dto.getReferenceNumber()
                                                                                    + " ACCOUNT NUMBER OR CARD IS INVALID");

                                                                    httpStatus = HttpStatus.BAD_REQUEST;
                                                                    result = new ResponseMessageDTO("FAILED", "E216");
                                                                    break;
                                                                case "412":
                                                                    logger.error(
                                                                            "refundForMerchant: ERROR: " + dto.getReferenceNumber()
                                                                                    + " BENEFICIARY BANK HASN’T JOINED THE SERVICE");

                                                                    httpStatus = HttpStatus.BAD_REQUEST;
                                                                    result = new ResponseMessageDTO("FAILED", "E217");
                                                                    break;
                                                                default:
                                                                    if (refundResult.trim().contains("FT")) {
                                                                        refundLogEntity.setStatus(1);
                                                                        refundLogEntity.setReferenceNumber(refundResult);
                                                                        refundLogEntity.setMessage(refundResult);
                                                                        insertTransactionRefundRedis(refundResult, dto, terminalBankEntity);
                                                                        httpStatus = HttpStatus.OK;
                                                                        result = new ResponseMessageDTO("SUCCESS", refundResult);
                                                                    } else {
                                                                        logger.error("refundForMerchant: ERROR: UNEXPECTED ERROR " + dto.getReferenceNumber());
                                                                        httpStatus = HttpStatus.BAD_REQUEST;
                                                                        result = new ResponseMessageDTO("FAILED", "E43");
                                                                    }
                                                                    break;
                                                            }
                                                        } else {
                                                            logger.error(
                                                                    "refundForMerchant: ERROR: " + dto.getBankAccount() + " REFUND FAILED");
                                                            refundLogEntity.setReferenceNumber("");
                                                            refundLogEntity.setStatus(0);
                                                            refundLogEntity.setMessage("");

                                                            httpStatus = HttpStatus.BAD_REQUEST;
                                                            result = new ResponseMessageDTO("FAILED", "E43");
                                                        }
                                                    } else {
                                                        // lenh refund truoc do chi cho hoan tien 1 lan => tra loi
                                                        logger.error("refundService: ERROR: Only refund one time");
                                                        // logger
                                                        refundLogEntity.setReferenceNumber("");
                                                        refundLogEntity.setStatus(0);
                                                        refundLogEntity.setMessage("");
                                                        httpStatus = HttpStatus.BAD_REQUEST;
                                                        result = new ResponseMessageDTO("FAILED", "E157");
                                                    }
                                                } else {
                                                    // lenh refund truoc do cho hoan tien nhieu lan => cho phep hoan tien tiep
                                                    String refundResult = refundFromMB(terminalBankEntity.getTerminalId(),
                                                            dto.getReferenceNumber(),
                                                            dto.getAmount(), dto.getContent());
                                                    refundLogEntity.setReferenceNumber("");
                                                    refundLogEntity.setStatus(0);

                                                    if (refundResult != null) {
                                                        refundLogEntity.setMessage(refundResult);
                                                        switch (refundResult) {
                                                            case "4863":
                                                                logger.error(
                                                                        "refundForMerchant: ERROR: " + dto.getReferenceNumber()
                                                                                + " FT CODE IS NOT EXISTED");

                                                                httpStatus = HttpStatus.BAD_REQUEST;
                                                                result = new ResponseMessageDTO("FAILED", "E44");
                                                                break;
                                                            case "4857":
                                                                logger.error(
                                                                        "refundForMerchant: ERROR: " + dto.getReferenceNumber()
                                                                                + " INVALID AMOUNT");

                                                                httpStatus = HttpStatus.BAD_REQUEST;
                                                                result = new ResponseMessageDTO("FAILED", "E45");
                                                                break;
                                                            case "002":
                                                                logger.error(
                                                                        "refundForMerchant: ERROR: " + dto.getReferenceNumber()
                                                                                + " CONNECTION TIMEOUT");

                                                                httpStatus = HttpStatus.BAD_REQUEST;
                                                                result = new ResponseMessageDTO("FAILED", "E215");
                                                                break;
                                                            case "4877":
                                                                logger.error(
                                                                        "refundForMerchant: ERROR: " + dto.getReferenceNumber()
                                                                                + " RECORD DOESN'T EXIST TBL MMS_PAYMENT_BANK");

                                                                httpStatus = HttpStatus.BAD_REQUEST;
                                                                result = new ResponseMessageDTO("FAILED", "E218");
                                                                break;
                                                            case "201":
                                                                logger.error(
                                                                        "refundForMerchant: ERROR: " + dto.getReferenceNumber()
                                                                                + " ACCOUNT NUMBER OR CARD IS INVALID");

                                                                httpStatus = HttpStatus.BAD_REQUEST;
                                                                result = new ResponseMessageDTO("FAILED", "E216");
                                                                break;
                                                            case "412":
                                                                logger.error(
                                                                        "refundForMerchant: ERROR: " + dto.getReferenceNumber()
                                                                                + " BENEFICIARY BANK HASN’T JOINED THE SERVICE");

                                                                httpStatus = HttpStatus.BAD_REQUEST;
                                                                result = new ResponseMessageDTO("FAILED", "E217");
                                                                break;
                                                            default:
                                                                if (refundResult.trim().contains("FT")) {
                                                                    refundLogEntity.setStatus(1);
                                                                    refundLogEntity.setReferenceNumber(refundResult);
                                                                    refundLogEntity.setMessage(refundResult);
                                                                    insertTransactionRefundRedis(refundResult, dto, terminalBankEntity);
                                                                    httpStatus = HttpStatus.OK;
                                                                    result = new ResponseMessageDTO("SUCCESS", refundResult);
                                                                } else {
                                                                    logger.error("refundForMerchant: ERROR: UNEXPECTED ERROR " + dto.getReferenceNumber());
                                                                    httpStatus = HttpStatus.BAD_REQUEST;
                                                                    result = new ResponseMessageDTO("FAILED", "E43");
                                                                }
                                                                break;
                                                        }
                                                    } else {
                                                        logger.error(
                                                                "refundForMerchant: ERROR: " + dto.getBankAccount() + " REFUND FAILED");
                                                        refundLogEntity.setReferenceNumber("");
                                                        refundLogEntity.setStatus(0);
                                                        refundLogEntity.setMessage("");

                                                        httpStatus = HttpStatus.BAD_REQUEST;
                                                        result = new ResponseMessageDTO("FAILED", "E43");
                                                    }
                                                }
                                            } else {
                                                logger.error(
                                                        "refundForMerchant: ERROR: " + dto.getBankAccount() + " INVALID TERMINAL");
                                                httpStatus = HttpStatus.BAD_REQUEST;
                                                result = new ResponseMessageDTO("FAILED", "E42");
                                            }
                                        }
                                    } else {
                                        logger.error(
                                                "refundForMerchant: ERROR: " + dto.getReferenceNumber() + " PROCESSING ANOTHER REFUND");
                                        httpStatus = HttpStatus.BAD_REQUEST;
                                        result = new ResponseMessageDTO("FAILED", "E158");
                                    }
                                } else {
                                    logger.error(
                                            "refundForMerchant: ERROR: " + dto.getReferenceNumber() + " INVALID CHECKSUM");
                                    httpStatus = HttpStatus.BAD_REQUEST;
                                    result = new ResponseMessageDTO("FAILED", "E39");
                                }
                            } else {
                                // bank account is not matched
                                System.out.println("refundForMerchant: BANK ACCOUNT IS NOT MATCH WITH MERCHANT INFO");
                                logger.error("refundForMerchant: BANK ACCOUNT IS NOT MATCH WITH MERCHANT INFO");
                                result = new ResponseMessageDTO("FAILED", "E77");
                                httpStatus = HttpStatus.BAD_REQUEST;
                            }
                        } else {
                            // merchant is not existed
                            System.out.println("refundForMerchant: MERCHANT IS NOT EXISTED");
                            logger.error("refundForMerchant: MERCHANT IS NOT EXISTED");
                            result = new ResponseMessageDTO("FAILED", "E104");
                            httpStatus = HttpStatus.BAD_REQUEST;
                        }
                    } else {
                        System.out.println("refundForMerchant: INVALID TOKEN");
                        logger.error("refundForMerchant: INVALID TOKEN");
                        result = new ResponseMessageDTO("FAILED", "E74");
                        httpStatus = HttpStatus.BAD_REQUEST;
                    }
                }
            } else {
                System.out.println("refundForMerchant: INVALID REQUEST BODY");
                logger.error("refundForMerchant: INVALID REQUEST BODY");
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("refundForMerchant: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
            result = new ResponseMessageDTO("FAILED", "E05");
        } finally {
            final ResponseMessageDTO finalResult = result;
            TransactionRefundLogEntity finalRefundLogEntity = refundLogEntity;
            long finalTime = time;
            String finalIdempotencyKey = idempotencyKey;
            Thread thread = new Thread(() -> {
                if (finalRefundLogEntity != null) {
                    transactionRefundLogService.insert(finalRefundLogEntity);
                }
                if (finalResult.getStatus().equals("SUCCESS")) {
                    TransactionReceiveEntity transactionReceiveEntity = transactionReceiveService
                            .getTransactionReceiveByRefNumber(dto.getReferenceNumber(), "C");
                    TransactionRefundEntity entity = new TransactionRefundEntity();
                    entity.setId(UUID.randomUUID().toString());
                    entity.setBankAccount(dto.getBankAccount());
                    if (transactionReceiveEntity != null) {
                        entity.setBankId(StringUtil.getValueNullChecker(transactionReceiveEntity.getBankId()));
                        entity.setTransactionId(transactionReceiveEntity.getId());
                        entity.setUserId(transactionReceiveEntity.getUserId());
                    } else {
                        entity.setBankId("");
                        entity.setTransactionId("");
                        entity.setUserId("");
                    }
                    entity.setContent(dto.getContent());
                    entity.setAmount(Long.parseLong(dto.getAmount()));
                    entity.setTime(finalTime);
                    entity.setTimePaid(DateTimeUtil.getCurrentDateTimeUTC());
                    entity.setRefNumber(dto.getReferenceNumber());
                    entity.setTransType("D");
                    entity.setNote("");
                    entity.setStatus(1);
                    entity.setReferenceNumber(finalResult.getMessage());
                    entity.setMultiTimes(StringUtil.getValueNullChecker(dto.getMultiTimes()));
                    transactionRefundService.insert(entity);
                }
                if (!StringUtil.isNullOrEmpty(finalIdempotencyKey) && !"E158".equals(finalResult.getMessage())) {
                    idempotencyService.deleteResponseForKey(finalIdempotencyKey);
                }
            });
            thread.start();
        }
        return new ResponseEntity<>(result, httpStatus);
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

    private boolean checkDuplicateReferenceNumber(String refNumber, String transType) {
        boolean result = false;
        try {
            // if transType = C => check all transaction_bank and transaction_mms
            // if transType = D => check only transaction bank
            String check = transactionBankService.checkExistedReferenceNumber(refNumber, transType);
            if (check == null || check.isEmpty()) {
                result = true;
            }
        } catch (Exception e) {
            logger.error(e.toString());
        }
        return result;
    }

    private ResponseObjectDTO checkOrderFromMB(String ftCode, String orderId) {
        ResponseObjectDTO result = null;
        try {
            TokenProductBankDTO token = getBankToken();
            if (token != null) {
                UUID clientMessageId = UUID.randomUUID();
                Map<String, Object> data = new HashMap<>();
                String checkSum = BankEncryptUtil.generateCheckOrderMD5Checksum(ftCode, "", orderId);
                data.put("traceTransfer", ftCode);
                data.put("referenceLabel", orderId);
                data.put("billNumber", "");
                data.put("checkSum", checkSum);
                UriComponents uriComponents = UriComponentsBuilder
                        .fromHttpUrl(EnvironmentUtil.getBankUrl()
                                + "ms/offus/public/payment-service/payment/v1.0/checkOrder")
                        .buildAndExpand(/* add url parameter here */);
                WebClient webClient = WebClient.builder()
                        .baseUrl(
                                EnvironmentUtil.getBankUrl()
                                        + "ms/offus/public/payment-service/payment/v1.0/checkOrder")
                        .build();
                Mono<ClientResponse> responseMono = webClient.post()
                        .uri(uriComponents.toUri())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("clientMessageId", clientMessageId.toString())
                        .header("userName", EnvironmentUtil.getUsernameAPI())
                        .header("secretKey", EnvironmentUtil.getSecretKeyAPI())
                        .header("Authorization", "Bearer " + getBankToken().getAccess_token())
                        .body(BodyInserters.fromValue(data))
                        .exchange();
                ClientResponse response = responseMono.block();

                String json = response.bodyToMono(String.class).block();
                logger.info("checkOrderFromMB: RESPONSE: " + json + " orderId: " + orderId);
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(json);
                if (rootNode.get("errorCode") != null) {
                    // 000
                    logger.info("checkOrderFromMB: RESPONSE: " + rootNode.asText() + " orderId: " + orderId);
                    if ((rootNode.get("errorCode").asText()).trim().equals("000")) {
                        if (rootNode.get("data").get("traceTransfer") != null) {
                            result = new ResponseObjectDTO("SUCCESS", rootNode.get("data").get("traceTransfer"));
                            logger.info("checkOrderFromMB: RESPONSE FT: " + result);
                        } else {
                            result = new ResponseObjectDTO("FAILED", "E05");
                            logger.error("checkOrderFromMB: RESPONSE: FT NULL");
                        }
                    }
                    // "4863" FT code not existed
                    else if ((rootNode.get("errorCode").asText()).trim().equals("4863")) {
                        result = new ResponseObjectDTO("FAILED", "4863");
                    }
                    // "4857" Invalid amount
                    else if ((rootNode.get("errorCode").asText()).trim().equals("4857")) {
                        result = new ResponseObjectDTO("FAILED", "4857");
                    }
                } else {
                    result = new ResponseObjectDTO("FAILED", "E05");
                    logger.error("checkOrderFromMB: RESPONSE: ERROR CODE NULL");
                }
            } else {
                result = new ResponseObjectDTO("FAILED", "E05");
                logger.error("ERROR at checkOrderFromMB: " + orderId + " - " + " TOKEN BANK IS INVALID");
            }
        } catch (Exception e) {
            result = new ResponseObjectDTO("FAILED", "E05");
            logger.error("ERROR at checkOrderFromMB: " + orderId + " - " + e.toString());
        }
        return result;
    }

    // get token bank product
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

    private String getUsernameFromToken(String token) {
        String result = "";
        if (token != null && !token.trim().isEmpty()) {
            String secretKey = "mySecretKey";
            String jwtToken = token.substring(7); // remove "Bearer " from the beginning
            Claims claims = Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(jwtToken).getBody();
            String userId = (String) claims.get("user");
            if (userId != null) {
                result = new String(Base64.getDecoder().decode(userId));
            }
        }
        return result;
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
                System.out.println("refundFromMB: RESPONSE: " + json + " FT Code: " + ftCode);
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
                    else if ((rootNode.get("errorCode").asText()).trim() != null) {
                        result = rootNode.get("errorCode").asText();
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
        System.out.println("RESULT REFUND: " + result);
        return result;
    }

    private void pushNotificationBoxIdRef(String amountForVoice, String amount, String boxIdRef) {
        Map<String, String> data = new HashMap<>();
        if (!StringUtil.isNullOrEmpty(boxIdRef)) {
            try {
                BoxEnvironmentResDTO messageBox = systemSettingService.getSystemSettingBoxEnv();
                String messageForBox = StringUtil.getMessageBox(messageBox.getBoxEnv());
                data.put("notificationType", NotificationUtil.getNotiTypeUpdateTransaction());
                data.put("amount", amount);
                data.put("message", String.format(messageForBox, amountForVoice));
                String idRefBox = BoxTerminalRefIdUtil.encryptQrBoxId(boxIdRef);
                socketHandler.sendMessageToBoxId(idRefBox, data);
                try {
                    MessageBoxDTO messageBoxDTO = new MessageBoxDTO();
                    messageBoxDTO.setNotificationType(NotificationUtil.getNotiTypeUpdateTransaction());
                    messageBoxDTO.setAmount(amount);
                    messageBoxDTO.setMessage(String.format(messageForBox, amountForVoice));
                    ObjectMapper mapper = new ObjectMapper();
                    mqttMessagingService.sendMessageToBoxId(idRefBox, mapper.writeValueAsString(messageBoxDTO));
                } catch (Exception e) {
                    logger.error("MQTT: socketHandler.sendMessageToQRBox - "
                            + boxIdRef + " at: " + System.currentTimeMillis());
                }
                logger.info("WS: socketHandler.sendMessageToQRBox - "
                        + boxIdRef + " at: " + System.currentTimeMillis());
            } catch (IOException e) {
                logger.error(
                        "WS: socketHandler.sendMessageToBox - updateTransaction ERROR: " + e.toString());
            }
        }
    }

    private String processHiddenAmount(long amount, String bankId, boolean isValidService, String transactionId) {
        String result = "";
        try {
            long currentStartDate = DateTimeUtil.getStartDateUTCPlus7();
            result = amount + "";
            if (isValidService) {
                result = formatAmountNumber(amount + "");

                // Save transactionReceiveId if user expired active
                Thread thread = new Thread(() -> {
                    SystemSettingEntity systemSetting = systemSettingService.getSystemSetting();
                    if (systemSetting.getServiceActive() <= currentStartDate) {
                        TransReceiveTempEntity entity = transReceiveTempService
                                .getLastTimeByBankId(bankId);
                        String transIds = "";
                        if (entity == null) {
                            entity = new TransReceiveTempEntity();
                            entity.setId(UUID.randomUUID().toString());
                            entity.setBankId(bankId);
                            entity.setLastTimes(currentStartDate);
                            entity.setTransIds(transactionId);
                            entity.setNums(1);
                            transReceiveTempService.insert(entity);
                        } else {
                            processSaveTransReceiveTemp(bankId, entity.getNums(), entity.getLastTimes(), transactionId,
                                    currentStartDate, entity, 1);
                        }
                    }
                });
                thread.start();
            } else {
                SystemSettingEntity systemSetting = systemSettingService.getSystemSetting();

                if (systemSetting.getServiceActive() <= currentStartDate) {
                    TransReceiveTempEntity entity = transReceiveTempService
                            .getLastTimeByBankId(bankId);
                    if (entity == null) {
                        result = formatAmountNumber(amount + "");
                        entity = new TransReceiveTempEntity();
                        entity.setNums(1);
                        entity.setId(UUID.randomUUID().toString());
                        entity.setBankId(bankId);
                        entity.setTransIds(transactionId);
                        entity.setLastTimes(currentStartDate);
                        transReceiveTempService.insert(entity);
                    } else {
                        boolean checkFiveTrans = processSaveTransReceiveTemp(bankId, entity.getNums(),
                                entity.getLastTimes(), transactionId,
                                currentStartDate, entity, 1);
                        if (checkFiveTrans) {
                            result = formatAmountNumber(amount + "");
                        } else {
                            result = "*****";
                        }
                    }
                } else {
                    result = formatAmountNumber(amount + "");
                }
            }
        } catch (Exception e) {
            result = formatAmountNumber(amount + "");
            logger.error("TransactionBankController: ERROR: processHiddenAmount: "
                    + e.getMessage() + " at: " + System.currentTimeMillis());
        }
        return result;
    }

    private String formatAmountNumber(String amount) {
        String result = amount;
        try {
            if (StringUtil.containsOnlyDigits(amount)) {
                NumberFormat nf = NumberFormat.getInstance(Locale.US);
                Long numberAmount = Long.parseLong(amount);
                result = nf.format(numberAmount);
            }
        } catch (Exception ignored) {}
        return result;
    }

    private boolean processSaveTransReceiveTemp(String bankId, int preNum,
                                                long lastTime, String transactionId,
                                                long currentStartDate, TransReceiveTempEntity entity,
                                                int numBreak) {
        boolean result = false;
        if (numBreak <= 5) {
            ++numBreak;
            try {
                if (preNum == 5 && lastTime == currentStartDate) {
                    result = false;
                } else {
                    int aftNum = preNum;
                    String transIds = transactionId + "," + entity.getTransIds();
                    int nums = entity.getNums();
                    if (entity.getLastTimes() < currentStartDate) {
                        aftNum = 1;
                        if (StringUtil.isNullOrEmpty(entity.getTransIds())) {
                            transIds = transactionId;
                        } else {
                            transIds = transactionId + "," + entity.getTransIds();
                        }
                    } else if (entity.getNums() < 5) {
                        aftNum = preNum + 1;
                        transIds = transactionId + "," + entity.getTransIds();
                    }
                    int checkUpdateSuccess = transReceiveTempService
                            .updateTransReceiveTemp(transIds, aftNum, currentStartDate,
                                    lastTime, preNum, entity.getTransIds(), entity.getId());
                    if (checkUpdateSuccess == 0) {
                        TransReceiveTempEntity updateReceiveEntity = transReceiveTempService
                                .getLastTimeByBankId(bankId);
                        result = processSaveTransReceiveTemp(updateReceiveEntity.getBankId(), updateReceiveEntity.getNums(),
                                updateReceiveEntity.getLastTimes(), transactionId, currentStartDate, updateReceiveEntity, numBreak);
                    } else {
                        result = true;
                    }
                }
            } catch (Exception e) {
                logger.error("processSaveTransReceiveTemp: ERROR: " + e.getMessage() + " at: " + System.currentTimeMillis());
            }
        } else {
            result = false;
        }
        return result;
    }

    private  String formatTimeUtcPlus(long time) {
        long utcPlusSevenTime = time + 25200;
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(utcPlusSevenTime), ZoneId.of("GMT"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH);
        return dateTime.format(formatter);
    }
}
