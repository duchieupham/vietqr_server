package com.vietqr.org.controller;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.vietqr.org.dto.TokenProductBankDTO;
import com.vietqr.org.dto.TransactionMMSResponseDTO;
import com.vietqr.org.entity.TerminalBankEntity;
import com.vietqr.org.entity.TransactionMMSEntity;
import com.vietqr.org.service.TerminalBankService;
import com.vietqr.org.service.TransactionMMSService;
import com.vietqr.org.util.BankEncryptUtil;
import com.vietqr.org.util.EnvironmentUtil;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class TransactionMMSController {
    private static final Logger logger = Logger.getLogger(TransactionMMSController.class);

    @Autowired
    TransactionMMSService transactionMMSService;

    @Autowired
    TerminalBankService terminalBankService;

    @PostMapping("transaction-mms")
    public ResponseEntity<TransactionMMSResponseDTO> insertTransactionMMS(@RequestBody TransactionMMSEntity entity) {
        TransactionMMSResponseDTO result = null;
        HttpStatus httpStatus = null;
        UUID uuid = UUID.randomUUID();
        LocalDateTime currentDateTime = LocalDateTime.now();
        long time = currentDateTime.toEpochSecond(ZoneOffset.UTC);
        logger.info("receive transaction-mms-sync from MB: " + entity.toString() + " at: " + time);
        try {
            // check result to response TransactionMMSResponseDTO
            if (entity != null) {
                entity.setId(uuid.toString());
            }
            TerminalBankEntity terminalBankEntity = terminalBankService
                    .getTerminalBankByTerminalId(entity.getTerminalLabel());
            result = validateTransactionBank(entity, terminalBankEntity);
            if (result.getResCode().equals("00")) {
                // insert TransactionMMSEntity
                int checkInsert = transactionMMSService.insertTransactionMMS(entity);
                if (checkInsert == 1) {
                    // find bankAccount by terminalLabel (terminal ID)
                    // notify to TID
                    httpStatus = HttpStatus.OK;
                    LocalDateTime insertLocalTime = LocalDateTime.now();
                    long insertTime = insertLocalTime.toEpochSecond(ZoneOffset.UTC);
                    logger.error(
                            "transaction-mms-sync: INSERT SUCCESS at: " + insertTime);

                } else {
                    httpStatus = HttpStatus.BAD_REQUEST;
                    LocalDateTime insertLocalTime = LocalDateTime.now();
                    long insertTime = insertLocalTime.toEpochSecond(ZoneOffset.UTC);
                    logger.error(
                            "transaction-mms-sync: INSERT ERROR at: " + insertTime);
                }
            } else {
                httpStatus = HttpStatus.BAD_REQUEST;
                LocalDateTime resLocalBankTime = LocalDateTime.now();
                long resBankTime = resLocalBankTime.toEpochSecond(ZoneOffset.UTC);
                logger.error(
                        "transaction-mms-sync: Response ERROR: " + result.getResCode() + " - " + result.getResDesc()
                                + " at: " + resBankTime);
            }
            //
            return new ResponseEntity<>(result, httpStatus);
        } catch (Exception e) {
            logger.error("transaction-mms-sync: Error " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
            result = new TransactionMMSResponseDTO("99", "Internal error");
            //
            return new ResponseEntity<>(result, httpStatus);
        } finally {

        }
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
                            // còn giao dịch không hợp lệ chưa bắt
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

    // get token
    private TokenProductBankDTO getBankToken() {
        TokenProductBankDTO result = null;
        try {
            String key = EnvironmentUtil.getUserBankMMSAccess() + ":" + EnvironmentUtil.getPasswordBankMMSAccess();
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
}
