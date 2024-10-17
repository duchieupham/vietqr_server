package com.vietqr.org.controller;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

import com.vietqr.org.dto.*;
import com.vietqr.org.entity.*;
import com.vietqr.org.service.*;
import com.vietqr.org.util.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
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
    CaiBankService caiBankService;

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
                if (accountBankEntity != null && accountBankEntity.isMmsActive()) {
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
                } else if (accountBankEntity != null && !accountBankEntity.isMmsActive()) {
                    // false
                    // Luồng 1
                    // UUID transcationUUID = UUID.randomUUID();
                    String traceId = "VQR" + RandomCodeUtil.generateRandomUUID();
                    String bankTypeId = "";
                    bankTypeId = bankTypeService.getBankTypeIdByBankCode(dto.getBankCode());
                    VietQRDTO vietQRDTO = new VietQRDTO();
                    try {
                        if (dto.getContent().length() <= 50) {
                            // check if generate qr with transtype = D or C
                            // if D => generate with customer information
                            // if C => do normal
                            // find bankTypeId by bankcode
                            if (bankTypeId != null && !bankTypeId.isEmpty()) {
                                // find bank by bankAccount and banktypeId

                                    accountBankEntity = accountBankService
                                            .getAccountBankByBankAccountAndBankTypeId(dto.getBankAccount(), bankTypeId);
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
                                    String qr = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);
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
                                    String refId = TransactionRefIdUtil.encryptTransactionId(transactionUUID.toString());
                                    String qrLink = EnvironmentUtil.getQRLink() + refId;
                                    vietQRDTO.setTransactionRefId(refId);
                                    vietQRDTO.setQrLink(qrLink);
                                    //
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
                        //System.out.println(e.toString());
                        result = new ResponseMessageDTO("FAILED", "Unexpected Error");
                        httpStatus = HttpStatus.BAD_REQUEST;
                        return new ResponseEntity<>(result, httpStatus);
                    } finally {
                        // insert new transaction with orderId and sign
                            bankTypeId = bankTypeService.getBankTypeIdByBankCode(dto.getBankCode());
                        if (accountBankEntity != null) {
                            //System.out.println("FINALLY accountBankEntity FOUND: " + accountBankEntity.toString());
                            VietQRCreateDTO vietQRCreateDTO = new VietQRCreateDTO();
                            vietQRCreateDTO.setBankId(accountBankEntity.getId());
                            vietQRCreateDTO.setAmount(dto.getAmount() + "");
                            vietQRCreateDTO.setContent(dto.getContent());
                            vietQRCreateDTO.setUserId(accountBankEntity.getUserId());
                            vietQRCreateDTO.setTerminalCode(dto.getTerminalCode());
                            vietQRCreateDTO.setServiceCode("");
                            //
                                vietQRCreateDTO.setTransType("C");
                            if (dto.getUrlLink() != null && !dto.getUrlLink().trim().isEmpty()) {
                                vietQRCreateDTO.setUrlLink(dto.getUrlLink());
                            } else {
                                vietQRCreateDTO.setUrlLink("");
                            }
                            insertNewTransaction(transactionUUID, traceId, vietQRCreateDTO, vietQRDTO, dto.getOrderId(),
                                    dto.getSign(), true);
                        }
                        //
                        LocalDateTime currentDateTime = LocalDateTime.now();
                        long time = currentDateTime.toEpochSecond(ZoneOffset.UTC);
                        String secretKey = "mySecretKey";
                        String jwtToken = token.substring(7); // remove "Bearer " from the beginning
                        Claims claims = Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(jwtToken).getBody();
                        String user = (String) claims.get("user");
                        if (user != null) {
                            String decodedUser = new String(Base64.getDecoder().decode(user));
                            logger.info("qr/generate-customer - user " + decodedUser + " call at " + time);
                            //System.out.println("qr/generate-customer - user " + decodedUser + " call at " + time);
                        } else {
                            logger.info("qr/generate-customer - Sytem User call at " + time);
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
    protected void insertNewTransaction(UUID transcationUUID, String traceId, VietQRCreateDTO dto, VietQRDTO result,
                                        String orderId, String sign, boolean isFromMerchantSync) {
        LocalDateTime startTime = LocalDateTime.now();
        long startTimeLong = startTime.toEpochSecond(ZoneOffset.UTC);
        logger.info("QR generate - start insertNewTransaction at: " + startTimeLong);
        logger.info("QR generate - insertNewTransaction data: " + result.toString());
        try {
            NumberFormat nf = NumberFormat.getInstance(Locale.US);
            // 2. Insert transaction_receive if branch_id and business_id != null
            // 3. Insert transaction_receive_branch if branch_id and business_id != null
            AccountBankReceiveEntity accountBankEntity = accountBankService.getAccountBankById(dto.getBankId());
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
                transactionEntity.setTerminalCode(result.getTerminalCode() != null ? result.getTerminalCode() : "");
                transactionEntity.setQrCode("");
                transactionEntity.setUserId(accountBankEntity.getUserId());
                transactionEntity.setOrderId(orderId);
                transactionEntity.setNote(dto.getNote() != null ? dto.getNote() : "");
                transactionEntity.setStatusResponse(0);
                transactionEntity.setUrlLink(dto.getUrlLink() != null ? dto.getUrlLink() : "");
                if (dto.getTransType() != null) {
                    transactionEntity.setTransType(dto.getTransType());
                } else {
                    transactionEntity.setTransType("C");
                }
                transactionEntity.setReferenceNumber("");
                transactionEntity.setOrderId(orderId);
                transactionEntity.setServiceCode(dto.getServiceCode());
                transactionEntity.setSign(sign);
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

                // insert notification
                UUID notificationUUID = UUID.randomUUID();
                NotificationEntity notiEntity = new NotificationEntity();
                String message = NotificationUtil.getNotiDescNewTransPrefix2()
                        + NotificationUtil.getNotiDescNewTransSuffix1()
                        + nf.format(Double.parseDouble(dto.getAmount()))
                        + NotificationUtil
                        .getNotiDescNewTransSuffix2();
            }

        } catch (Exception e) {
            logger.error("Error at insertNewTransaction: " + e.toString());
        } finally {
            LocalDateTime endTime = LocalDateTime.now();
            long endTimeLong = endTime.toEpochSecond(ZoneOffset.UTC);
            logger.info("QR generate - end insertNewTransaction at: " + endTimeLong);
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
            transactionEntity.setTerminalCode(dto.getTerminalCode() != null ? dto.getTerminalCode() : "");
            transactionEntity.setQrCode(qrCode);
            transactionEntity.setUserId(accountBankReceiveEntity.getUserId());
            transactionEntity.setNote(dto.getNote() != null ? dto.getNote() : "");
            transactionEntity.setStatusResponse(0);
            if (dto.getUrlLink() == null || dto.getUrlLink().isEmpty()) {
                dto.setUrlLink("");
            } else {
                dto.setUrlLink(dto.getUrlLink());
            }
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
