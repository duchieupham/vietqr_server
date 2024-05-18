package com.vietqr.org.controller;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.vietqr.org.dto.*;
import com.vietqr.org.entity.*;
import com.vietqr.org.service.*;
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
    CustomerSyncService customerSyncService;

    @Autowired
    TransactionReceiveLogService transactionReceiveLogService;

    @Autowired
    AccountBankReceiveService accountBankService;

    @Autowired
    AccountBankReceiveShareService accountBankReceiveShareService;

    @Autowired
    BankTypeService bankTypeService;

    @Autowired
    AccountCustomerBankService accountCustomerBankService;

    @Autowired
    TerminalService terminalService;

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

    @PostMapping("transaction-mms")
    public ResponseEntity<TransactionMMSResponseDTO> insertTransactionMMS(@RequestBody TransactionMMSEntity entity) {
        TransactionMMSResponseDTO result = null;
        HttpStatus httpStatus = null;
        TerminalBankEntity terminalBankEntity = null;
        TransactionReceiveEntity transactionReceiveEntity = null;
        UUID uuid = UUID.randomUUID();
        int checkInsert = 0;
        LocalDateTime currentDateTime = LocalDateTime.now();
        long time = currentDateTime.toEpochSecond(ZoneOffset.UTC);
        logger.info("receive transaction-mms-sync from MB: " + entity.toString() + " at: " + time);
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
                    LocalDateTime insertLocalTime = LocalDateTime.now();
                    long insertTime = insertLocalTime.toEpochSecond(ZoneOffset.UTC);
                    logger.info(
                            "transaction-mms-sync: INSERT (insertTransactionMMS) SUCCESS at: " + insertTime);
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
                                        entity.getFtCode(), time,
                                        transactionReceiveEntity.getId());
                                LocalDateTime updateLocalTime = LocalDateTime.now();
                                long updateTime = updateLocalTime.toEpochSecond(ZoneOffset.UTC);
                                logger.info(
                                        "transaction-mms-sync: updateTransactionReceiveStatus SUCCESS at: "
                                                + updateTime);
                            } else {
                                //////////////////////////////////////////
                                logger.info("transaction-mms-sync: NOT FOUND transactionReceiveEntity");
                            }
                            ///
                        }

                    }
                } else {
                    httpStatus = HttpStatus.BAD_REQUEST;
                    LocalDateTime insertLocalTime = LocalDateTime.now();
                    long insertTime = insertLocalTime.toEpochSecond(ZoneOffset.UTC);
                    logger.error(
                            "transaction-mms-sync: INSERT ERROR at: " + insertTime);
                    // System.out.println(
                    // "transaction-mms-sync: INSERT ERROR at: " + insertTime);
                }
            } else {
                httpStatus = HttpStatus.BAD_REQUEST;
                LocalDateTime resLocalBankTime = LocalDateTime.now();
                long resBankTime = resLocalBankTime.toEpochSecond(ZoneOffset.UTC);
                logger.error(
                        "transaction-mms-sync: Response ERROR: " + result.getResCode() + " - " + result.getResDesc()
                                + " at: " + resBankTime);
                // System.out.println(
                // "transaction-mms-sync: Response ERROR: " + result.getResCode() + " - " +
                // result.getResDesc()
                // + " at: " + resBankTime);
            }
            LocalDateTime responseLocalTime = LocalDateTime.now();
            long responseTime = responseLocalTime.toEpochSecond(ZoneOffset.UTC);
            logger.info(
                    "transaction-mms-sync: RESPONSE: " + result.getResCode() + " at: " + responseTime);
            // System.out.println(
            // "transaction-mms-sync: RESPONSE at: " + responseTime);
            return new ResponseEntity<>(result, httpStatus);
        } catch (Exception e) {
            logger.error("transaction-mms-sync: Error " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
            result = new TransactionMMSResponseDTO("99", "Internal error");
            LocalDateTime responseLocalTime = LocalDateTime.now();
            long responseTime = responseLocalTime.toEpochSecond(ZoneOffset.UTC);
            logger.info(
                    "transaction-mms-sync: RESPONSE ERRPR at: " + responseTime);
            // System.out.println(
            // "transaction-mms-sync: RESPONSE ERRPR at: " + responseTime);
            return new ResponseEntity<>(result, httpStatus);
        } finally {
            final TransactionMMSResponseDTO tempResult = result;
            final TransactionReceiveEntity tempTransReceive = transactionReceiveEntity;
            final TerminalBankEntity tempTerminalBank = terminalBankEntity;
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
                            getCustomerSyncEntities(tempTransReceive.getId(), tempTerminalBank.getId(),
                                    entity.getFtCode(),
                                    tempTransReceive, time, rawCode, urlLink);
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
                        AccountBankReceiveEntity accountBankEntity = accountBankService
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
                            data.put("branchName", "");
                            data.put("businessName", "");
                            data.put("terminalName", "");
                            data.put("urlLink",
                                    tempTransReceive.getUrlLink() != null ? tempTransReceive.getUrlLink() : "");
                            data.put("type", "" + tempTransReceive.getType());
                            data.put("terminalCode",
                                    tempTransReceive.getTerminalCode() != null ? tempTransReceive.getTerminalCode()
                                            : "");
                            if (tempTransReceive.getTerminalCode() != null &&
                                    !tempTransReceive.getTerminalCode().trim().isEmpty()) {
                                TransactionTerminalTempEntity transactionTerminalTempEntity = new TransactionTerminalTempEntity();
                                transactionTerminalTempEntity.setId(UUID.randomUUID().toString());
                                transactionTerminalTempEntity.setTransactionId(tempTransReceive.getId());
                                transactionTerminalTempEntity.setTerminalCode(tempTransReceive.getTerminalCode());
                                transactionTerminalTempEntity.setTime(time);
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
                            String amountForVoice = StringUtil.removeFormatNumber(tempTransReceive.getAmount() + "");
                            String amountForShow = StringUtil.formatNumberAsString(tempTransReceive.getAmount() + "");
                            data.put("amount", "" + amountForShow);
                            data.put("timePaid", "" + tempTransReceive.getTimePaid());
                            data.put("time", "" + time);
                            data.put("refId", "" + entity.getId());
                            data.put("status", "1");
                            data.put("traceId", "");
                            data.put("transType", "C");
                            data.put("urlLink", tempTransReceive.getUrlLink() != null ? tempTransReceive.getUrlLink() : "");
                            // send msg to QR Link
                            String refId = TransactionRefIdUtil
                                    .encryptTransactionId(tempTransReceive.getId());
                            try {
                                LocalDateTime startRequestDateTime = LocalDateTime.now();
                                long startRequestTime = startRequestDateTime.toEpochSecond(ZoneOffset.UTC);
                                logger.info(
                                        "transaction-mms-sync: sendMessageToTransactionRefId at:" + startRequestTime);
                                // System.out.println(
                                // "transaction-mms-sync: sendMessageToTransactionRefId at:" +
                                // startRequestTime);
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
                                        data1.put("timePaid", "" + time);
                                        data1.put("type", "" + tempTransReceive.getType());
                                        data1.put("time", "" + time);
                                        data1.put("refId", "" + uuid.toString());
                                        data1.put("status", "1");
                                        data1.put("traceId", "");
                                        data1.put("transType", "C");
                                        data1.put("message", String.format(EnvironmentUtil.getVietQrPaymentSuccessQrVoice(), amountForVoice));
                                        String boxRefId = BoxTerminalRefIdUtil.encryptQrBoxId(rawCode);
                                        socketHandler.sendMessageToBoxId(boxRefId, data1);
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
                        // check time QR tinhx
                        LocalDateTime staticQR = LocalDateTime.now();
                        long staticQRTime = staticQR.toEpochSecond(ZoneOffset.UTC);
                        logger.info(
                                "transaction-mms-sync: staticQRTime-start at:" + staticQRTime);
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
                            if (terminalId == null || terminalId.trim().isEmpty()) {
                                try {
                                    terminalSubRawCodeDTO = terminalBankReceiveService
                                            .getTerminalSubFlow2ByTraceTransfer(traceTransfer);
                                    terminalId = terminalSubRawCodeDTO.getTerminalId();
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
                                AccountBankReceiveEntity accountBankReceiveEntity = accountBankService
                                        .getAccountBankById(terminalBankReceiveEntity.getBankId());
                                String transactionId = UUID.randomUUID().toString();
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
                                transactionReceiveEntity1.setTime(time);
                                transactionReceiveEntity1.setTimePaid(time);
                                transactionReceiveEntity1.setBankId(accountBankReceiveEntity.getId());
                                transactionReceiveEntity1.setTransStatus(0);
                                if (terminalBankReceiveEntity.getTerminalCode() != null
                                        && !terminalBankReceiveEntity.getTerminalCode().trim().isEmpty()) {
                                    transactionReceiveEntity1
                                            .setTerminalCode(terminalBankReceiveEntity.getTerminalCode());
                                } else {
                                    transactionReceiveEntity1.setTerminalCode(terminalEntity.getCode());
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
                                transactionTerminalTempEntity.setTime(time);
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
                                        + entity.getDebitAmount()
                                        + NotificationUtil.getNotiDescUpdateTransSuffix2()
                                        + "+" + nf.format(Long.parseLong(entity.getDebitAmount()))
                                        + NotificationUtil.getNotiDescUpdateTransSuffix3()
                                        + entity.getTraceTransfer()
                                        + NotificationUtil.getNotiDescUpdateTransSuffix4()
                                        + entity.getTraceTransfer();
                                notiEntity.setId(notificationUUID.toString());
                                notiEntity.setRead(false);
                                notiEntity.setMessage(message);
                                notiEntity.setTime(time);
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
                                String amountForShow = StringUtil.formatNumberAsString(entity.getDebitAmount() + "");
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
                                data.put("timePaid", "" + time);
                                data.put("type", "" + transactionReceiveEntity1.getType());
                                data.put("time", "" + time);
                                data.put("refId", "" + uuid.toString());
                                data.put("status", "1");
                                data.put("traceId", "");
                                data.put("transType", "C");
                                pushNotification(NotificationUtil.getNotiTitleUpdateTransaction(),
                                        message, notiEntity, data, accountBankReceiveEntity.getUserId());
                                TerminalBankEntity terminalBankEntitySync = terminalBankService
                                        .getTerminalBankByBankAccount(accountBankReceiveEntity.getBankAccount());
                                if (terminalBankEntitySync != null) {
                                    // push data to customerSync
                                    ////////////////////////
                                    String urlLink = tempTransReceive.getUrlLink() != null
                                            ? tempTransReceive.getUrlLink()
                                            : "";
                                    getCustomerSyncEntities(transactionReceiveEntity1.getId(),
                                            terminalBankEntitySync.getId(),
                                            entity.getFtCode(),
                                            transactionReceiveEntity1, time, rawCode, urlLink);
                                } else {
                                    logger.info("transaction-mms-sync: NOT FOUND TerminalBankEntity");
                                }
                                try {
                                    if (isSubTerminal) {
                                        data.put("message", String.format(EnvironmentUtil.getVietQrPaymentSuccessQrVoice(), amountForVoice));
                                        String boxRefId = BoxTerminalRefIdUtil.encryptQrBoxId(subRawCode);
                                        socketHandler.sendMessageToBoxId(boxRefId, data);
                                    }
                                } catch (Exception e) {
                                    logger.error("transaction-mms-sync: ERROR: " + e.toString());
                                }
                            } else {
                                logger.info("transaction-mms-sync: NOT FOUND terminalBankReceiveEntity");
                            }

                            // check time tim thay terminal
                            LocalDateTime findTerminal = LocalDateTime.now();
                            long findTerminalTime = findTerminal.toEpochSecond(ZoneOffset.UTC);
                            logger.info(
                                    "transaction-mms-sync: findTerminal at:" + findTerminalTime);
                            String bankTypeId = "aa4e489b-254e-4351-9cd4-f62e09c63ebc";
                            AccountBankReceiveShareForNotiDTO bankDTO = accountBankService
                                    .findAccountBankByTraceTransfer(traceTransfer,
                                            bankTypeId);
                            if (bankDTO.getBankId() != null) {
                                UUID transcationUUID = UUID.randomUUID();
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
                                // long time = currentDateTime.toEpochSecond(ZoneOffset.UTC);
                                transactionEntity.setTime(time);
                                transactionEntity.setRefId(uuid.toString());
                                transactionEntity.setType(1);
                                transactionEntity.setStatus(1);
                                transactionEntity.setTraceId("");
                                transactionEntity.setTransType("C");
                                transactionEntity.setReferenceNumber(entity.getFtCode());
                                transactionEntity.setOrderId("");
                                transactionEntity.setSign("");
                                transactionEntity.setTimePaid(time);
                                transactionEntity.setTerminalCode(terminalEntity.getCode());
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
                                    transactionTerminalTempEntity.setTime(time);
                                    transactionTerminalTempEntity
                                            .setAmount(Long.parseLong(entity.getDebitAmount() + ""));
                                    transactionTerminalTempService
                                            .insertTransactionTerminal(transactionTerminalTempEntity);
                                }
                                // check time insert transaction QR static success
                                LocalDateTime insertStatic = LocalDateTime.now();
                                long insertStaticTime = insertStatic.toEpochSecond(ZoneOffset.UTC);
                                logger.info(
                                        "transaction-mms-sync: insertStaticQRTimeSuccess at:" + insertStaticTime);
                                // 4. insert and push notification to user.

                                List<String> userIds = terminalService
                                        .getUserIdsByTerminalCode(terminalEntity.getCode());
                                int numThread = userIds.size();
                                int amount = Integer.parseInt(entity.getDebitAmount());
                                NumberFormat nf = NumberFormat.getInstance(Locale.US);
                                ExecutorService executorService = Executors.newFixedThreadPool(numThread);
                                for (String userId : userIds) {
                                    Map<String, String> data = new HashMap<>();
                                    UUID notificationUUID = UUID.randomUUID();
                                    NotificationEntity notiEntity = new NotificationEntity();
                                    String message = NotificationUtil.getNotiDescUpdateTransSuffix1()
                                            + entity.getDebitAmount()
                                            + NotificationUtil.getNotiDescUpdateTransSuffix2()
                                            + "+" + nf.format(amount)
                                            + NotificationUtil.getNotiDescUpdateTransSuffix3()
                                            + entity.getTraceTransfer()
                                            + NotificationUtil.getNotiDescUpdateTransSuffix4()
                                            + entity.getTraceTransfer();
                                    notiEntity.setId(notificationUUID.toString());
                                    notiEntity.setRead(false);
                                    notiEntity.setMessage(message);
                                    notiEntity.setTime(time);
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
                                    data.put("amount", "" + entity.getDebitAmount());
                                    data.put("orderId", "");
                                    data.put("referenceNumber", entity.getFtCode());
                                    data.put("timePaid", "" + time);
                                    data.put("time", "" + time);
                                    data.put("type", "" + transactionEntity.getType());
                                    data.put("refId", "" + uuid.toString());
                                    data.put("status", "1");
                                    data.put("traceId", "");
                                    data.put("transType", "C");
                                    data.put("urlLink", transactionEntity.getUrlLink() != null ? transactionEntity.getUrlLink() : "");
                                    executorService.submit(
                                            () -> pushNotification(NotificationUtil.getNotiTitleUpdateTransaction(),
                                                    message, notiEntity, data, userId));
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
                                // /////// DO INSERT TELEGRAM
                                List<String> chatIds = telegramAccountBankService
                                        .getChatIdsByBankId(bankDTO.getBankId());
                                if (chatIds != null && !chatIds.isEmpty()) {
                                    TelegramUtil telegramUtil = new TelegramUtil();

                                    String telegramMsg = "+" + nf.format(amount) + " VND"
                                            + " | TK: " + bankDTO.getBankShortName() + " - "
                                            + bankAccount
                                            + " | " + convertLongToDate(time)
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

                                    String larkMsg = "+" + nf.format(amount) + " VND"
                                            + " | TK: " + bankDTO.getBankShortName() + " - "
                                            + bankAccount
                                            + " | " + convertLongToDate(time)
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
                                    String googleChatMsg = "+" + amount + " VND"
                                            + " | TK: " + bankDTO.getBankShortName() + " - "
                                            + bankDTO.getBankAccount()
                                            + " | " + convertLongToDate(time)
                                            + " | " + entity.getFtCode()
                                            + " | ND: " + entity.getTraceTransfer();
                                    for (String webhook : ggChatWebhooks) {
                                        googleChatUtil.sendMessageToGoogleChat(googleChatMsg, webhook);
                                    }
                                }
                            }
                        } else if (terminalSubRawCodeDTO != null) {
                            try {
                                subRawCode = terminalSubRawCodeDTO.getRawTerminalCode();
                                AccountBankReceiveEntity accountBankReceiveEntity = accountBankService
                                        .getAccountBankById(terminalSubRawCodeDTO.getBankId());
                                String transactionId = UUID.randomUUID().toString();
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
                                transactionReceiveEntity1.setTime(time);
                                transactionReceiveEntity1.setTimePaid(time);
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
                                transactionTerminalTempEntity.setTime(time);
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
                                        + entity.getDebitAmount()
                                        + NotificationUtil.getNotiDescUpdateTransSuffix2()
                                        + "+" + nf.format(Long.parseLong(entity.getDebitAmount()))
                                        + NotificationUtil.getNotiDescUpdateTransSuffix3()
                                        + entity.getTraceTransfer()
                                        + NotificationUtil.getNotiDescUpdateTransSuffix4()
                                        + entity.getTraceTransfer();
                                notiEntity.setId(notificationUUID.toString());
                                notiEntity.setRead(false);
                                notiEntity.setMessage(message);
                                notiEntity.setTime(time);
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
                                data.put("amount", "" + StringUtil.formatNumberAsString(entity.getDebitAmount()));
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
                                data.put("timePaid", "" + time);
                                data.put("type", "" + transactionReceiveEntity1.getType());
                                data.put("time", "" + time);
                                data.put("refId", "" + uuid.toString());
                                data.put("status", "1");
                                data.put("traceId", "");
                                data.put("transType", "C");
                                data.put("urlLink", "");
                                pushNotification(NotificationUtil.getNotiTitleUpdateTransaction(),
                                        message, notiEntity, data, accountBankReceiveEntity.getUserId());
                                TerminalBankEntity terminalBankEntitySync = terminalBankService
                                        .getTerminalBankByBankAccount(accountBankReceiveEntity.getBankAccount());
                                if (terminalBankEntitySync != null) {
                                    // push data to customerSync
                                    ////////////////////////
                                    getCustomerSyncEntities(transactionReceiveEntity1.getId(),
                                            terminalBankEntitySync.getId(),
                                            entity.getFtCode(),
                                            transactionReceiveEntity1, time, terminalSubRawCodeDTO.getRawTerminalCode(), "");
                                } else {
                                    logger.info("transaction-mms-sync: NOT FOUND TerminalBankEntity");
                                }
                                try {
                                    data.put("message", String.format(EnvironmentUtil.getVietQrPaymentSuccessQrVoice(), amountForVoice));
                                    String boxRefId = BoxTerminalRefIdUtil.encryptQrBoxId(subRawCode);
                                    socketHandler.sendMessageToBoxId(boxRefId, data);
                                } catch (Exception e) {
                                    logger.error("transaction-mms-sync: ERROR: " + e.toString());
                                }
                                String bankTypeId = "aa4e489b-254e-4351-9cd4-f62e09c63ebc";
                                AccountBankReceiveShareForNotiDTO bankDTO = accountBankService
                                        .findAccountBankByTraceTransfer(traceTransfer,
                                                bankTypeId);

                                Long amount = Long.parseLong(entity.getDebitAmount() + "");
                                // /////// DO INSERT TELEGRAM
                                List<String> chatIds = telegramAccountBankService
                                        .getChatIdsByBankId(bankDTO.getBankId());
                                if (chatIds != null && !chatIds.isEmpty()) {
                                    TelegramUtil telegramUtil = new TelegramUtil();

                                    String telegramMsg = "+" + nf.format(amount) + " VND"
                                            + " | TK: " + bankDTO.getBankShortName() + " - "
                                            + bankDTO.getBankAccount()
                                            + " | " + convertLongToDate(time)
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

                                    String larkMsg = "+" + nf.format(amount) + " VND"
                                            + " | TK: " + bankDTO.getBankShortName() + " - "
                                            + bankDTO.getBankAccount()
                                            + " | " + convertLongToDate(time)
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
                                    String googleChatMsg = "+" + amount + " VND"
                                            + " | TK: " + bankTypeEntity.getBankShortName() + " - "
                                            + bankDTO.getBankAccount()
                                            + " | " + convertLongToDate(time)
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
            });
            thread.start();
        }
    }

    private void pushNotification(String title, String message, NotificationEntity notiEntity, Map<String, String> data,
            String userId) {

        if (notiEntity != null) {
            notificationService.insertNotification(notiEntity);
        }
        List<FcmTokenEntity> fcmTokens = new ArrayList<>();
        fcmTokens = fcmTokenService.getFcmTokensByUserId(userId);
        firebaseMessagingService.sendUsersNotificationWithData(data,
                fcmTokens,
                title, message);
        try {
            socketHandler.sendMessageToUser(userId,
                    data);
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
            TransactionReceiveEntity transactionReceiveEntity, long time, String rawTerminalCode, String urlLink) {
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
                transactionBankCustomerDTO.setTerminalCode(rawTerminalCode != null ? rawTerminalCode : "");
                transactionBankCustomerDTO.setUrlLink(urlLink);
                for (TerminalAddressEntity terminalAddressEntity : terminalAddressEntities) {
                    CustomerSyncEntity customerSyncEntity = customerSyncService
                            .getCustomerSyncById(terminalAddressEntity.getCustomerSyncId());
                    if (customerSyncEntity != null) {
                        // System.out.println("customerSyncEntity != null");
                        pushNewTransactionToCustomerSync(transReceiveId, customerSyncEntity, transactionBankCustomerDTO,
                                time * 1000);
                    } else {
                        logger.info("customerSyncEntity = null");
                    }
                }
            } else {
                logger.info("terminalAddressEntites is empty");
            }
        } catch (Exception e) {
            logger.error("getCustomerSyncEntities MMS: ERROR: " + e.toString());
        }
    }

    private ResponseMessageDTO pushNewTransactionToCustomerSync(String transReceiveId, CustomerSyncEntity entity,
            TransactionBankCustomerDTO dto,
            long time) {
        ResponseMessageDTO result = null;
        // final ResponseMessageDTO[] results = new ResponseMessageDTO[1];
        // final List<ResponseMessageDTO> results = new ArrayList<>();
        // final String[] msg = new String[1];
        try {
            logger.info("pushNewTransactionToCustomerSync: orderId: " +
                    dto.getOrderId());
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
            LocalDateTime startRequestDateTime = LocalDateTime.now();
            long startRequestTime = startRequestDateTime.toEpochSecond(ZoneOffset.UTC);
            logger.info("pushNewTransactionToCustomerSync request at:" + startRequestTime);
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
            LocalDateTime responseDateTime = LocalDateTime.now();
            long responseTime = responseDateTime.toEpochSecond(ZoneOffset.UTC);
            logger.info("Response pushNewTransactionToCustomerSync at:" + responseTime);
            // System.out.println("Response pushNewTransactionToCustomerSync at:" +
            // responseTime);
            // System.out.println("response: " + response.toString());
            // System.out.println("response status code: " + response.statusCode());
            if (response.statusCode().is2xxSuccessful()) {
                String json = response.bodyToMono(String.class).block();
                logger.info("Response pushNewTransactionToCustomerSync: " + json);
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
                logger.info("Response pushNewTransactionToCustomerSync: " + json);
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
                logEntity.setTime(time);
                logEntity.setUrlCallback(address);
                transactionReceiveLogService.insert(logEntity);
            }
        }
        return result;
    }

    private TokenDTO getCustomerSyncToken(String transReceiveId, CustomerSyncEntity entity, long time) {
        TokenDTO result = null;
        ResponseMessageDTO msgDTO = null;
        try {
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
                logEntity.setTime(time);
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
                        // System.out.println("data getPayDate: " + entity.getPayDate());
                        // System.out.println("data getDebitAmount: " + entity.getDebitAmount());
                        // System.out.println("data checksum: " + dataCheckSum);
                        if (BankEncryptUtil.isMatchChecksum(dataCheckSum, entity.getCheckSum())) {
                            result = new TransactionMMSResponseDTO("00", "Success");
                        } else {
                            result = new TransactionMMSResponseDTO("12", "False checksum");
                        }
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
                System.out.println("refundFromMB: RESPONSE: " + json);
                logger.info("refundFromMB: RESPONSE: " + json);
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
        System.out.println("RESULT REFUND: " + result);
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
}
