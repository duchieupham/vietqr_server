package com.vietqr.org.controller;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
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
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.dto.TokenProductBankDTO;
import com.vietqr.org.dto.VietQRMMSCreateDTO;
import com.vietqr.org.dto.VietQRMMSDTO;
import com.vietqr.org.dto.VietQRMMSRequestDTO;
import com.vietqr.org.entity.AccountBankReceiveEntity;
import com.vietqr.org.entity.TerminalBankEntity;
import com.vietqr.org.entity.TransactionReceiveEntity;
import com.vietqr.org.service.AccountBankReceiveService;
import com.vietqr.org.service.BankTypeService;
import com.vietqr.org.service.TerminalBankService;
import com.vietqr.org.service.TransactionReceiveService;
import com.vietqr.org.util.EnvironmentUtil;
import com.vietqr.org.util.TransactionRefIdUtil;
import com.vietqr.org.util.bank.mb.MBTokenUtil;

import reactor.core.publisher.Mono;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class VietQRMMSController {
    private static final Logger logger = Logger.getLogger(VietQRMMSController.class);

    @Autowired
    TerminalBankService terminalBankService;

    @Autowired
    AccountBankReceiveService accountBankService;

    @Autowired
    BankTypeService bankTypeService;

    @Autowired
    TransactionReceiveService transactionReceiveService;

    // Generate VietQR MMS (ONLY MB BANK)
    // => default bankTypeId = aa4e489b-254e-4351-9cd4-f62e09c63ebc
    @PostMapping("mms/qr/generate-customer")
    public ResponseEntity<Object> generateVietQRMMS(@RequestBody VietQRMMSCreateDTO dto,
            @RequestHeader("Authorization") String token) {
        Object result = null;
        HttpStatus httpStatus = null;
        UUID transactionUUID = UUID.randomUUID();
        // String traceId = "VQR" + RandomCodeUtil.generateRandomUUID();
        LocalDateTime requestLDT = LocalDateTime.now();
        long requestTime = requestLDT.toEpochSecond(ZoneOffset.UTC);
        logger.info("generateVietQRMMS: start generate at: " + requestTime);
        String bankTypeMB = "aa4e489b-254e-4351-9cd4-f62e09c63ebc";
        // BankTypeEntity bankTypeEntity = bankTypeService.getBankTypeById(bankTypeMB);
        AccountBankReceiveEntity accountBankEntity = null;
        String qrCode = "";
        try {
            // 1. Validate input (amount, content, bankCode) => E34 if Invalid input data
            if (checkRequestBody(dto)) {
                // 2. Find terminal bank by bank_account_raw_number
                accountBankEntity = accountBankService
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
                            VietQRMMSRequestDTO requestDTO = new VietQRMMSRequestDTO();
                            requestDTO.setToken(tokenBankDTO.getAccess_token());
                            requestDTO.setTerminalId(terminalBankEntity.getTerminalId());
                            requestDTO.setAmount(dto.getAmount());
                            requestDTO.setContent(dto.getContent());
                            requestDTO.setOrderId(dto.getOrderId());

                            qrCode = requestVietQRMMS(requestDTO);
                            if (qrCode != null) {
                                String refId = TransactionRefIdUtil.encryptTransactionId(transactionUUID.toString());
                                String qrLink = EnvironmentUtil.getQRLink() + refId;
                                VietQRMMSDTO vietQRMMSDTO = new VietQRMMSDTO(qrCode, refId, qrLink);
                                httpStatus = HttpStatus.OK;
                                result = vietQRMMSDTO;
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
                LocalDateTime currentDateTime = LocalDateTime.now();
                long time = currentDateTime.toEpochSecond(ZoneOffset.UTC);
                insertNewTransaction(qrCode, transactionUUID.toString(), accountBankEntity, dto, time);
            }
        }
    }

    @Async
    private void insertNewTransaction(String qrCode, String transactionUUID,
            AccountBankReceiveEntity accountBankReceiveEntity,
            VietQRMMSCreateDTO dto,
            long time) {
        try {
            TransactionReceiveEntity transactionEntity = new TransactionReceiveEntity();
            transactionEntity.setId(transactionUUID);
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
            transactionEntity.setTerminalCode(dto.getTerminalCode());
            transactionEntity.setQrCode(qrCode);
            transactionEntity.setUserId(accountBankReceiveEntity.getUserId());
            transactionEntity.setNote(dto.getNote());
            transactionEntity.setTransStatus(0);
            transactionReceiveService.insertTransactionReceive(transactionEntity);
            LocalDateTime endTime = LocalDateTime.now();
            long endTimeLong = endTime.toEpochSecond(ZoneOffset.UTC);
            logger.info("insertNewTransaction - end generateVietQRMMS at: " + endTimeLong);
        } catch (Exception e) {
            logger.error("insertNewTransaction - generateVietQRMMS: ERROR: " + e.toString());
        }
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

    private boolean checkRequestBody(VietQRMMSCreateDTO dto) {
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
                    && orderId.length() <= 13
                    && dto.getAmount() != null && !dto.getBankAccount().trim().isEmpty()
                    && !dto.getAmount().trim().equals("0")
                    && dto.getAmount().matches("\\d+")
                    && dto.getBankAccount() != null && !dto.getBankAccount().trim().isEmpty()
                    && dto.getBankCode() != null && dto.getBankCode().equals("MB")) {
                result = true;
            }
        } catch (Exception e) {
            logger.error("checkRequestBody: ERROR: " + e.toString());
        }
        return result;
    }

}
