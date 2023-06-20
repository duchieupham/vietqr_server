package com.vietqr.org.controller;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

import com.vietqr.org.dto.TokenDTO;
import com.vietqr.org.dto.TransactionBankCustomerDTO;
import com.vietqr.org.dto.TransactionMMSResponseDTO;
import com.vietqr.org.dto.TransactionResponseDTO;
import com.vietqr.org.entity.CustomerSyncEntity;
import com.vietqr.org.entity.TerminalAddressEntity;
import com.vietqr.org.entity.TerminalBankEntity;
import com.vietqr.org.entity.TransactionMMSEntity;
import com.vietqr.org.entity.TransactionReceiveEntity;
import com.vietqr.org.service.CustomerSyncService;
import com.vietqr.org.service.TerminalAddressService;
import com.vietqr.org.service.TerminalBankService;
import com.vietqr.org.service.TransactionMMSService;
import com.vietqr.org.service.TransactionReceiveService;
import com.vietqr.org.util.BankEncryptUtil;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

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
    TransactionReceiveService transactionReceiveService;

    @Autowired
    TerminalAddressService terminalAddressService;

    @Autowired
    CustomerSyncService customerSyncService;

    @PostMapping("transaction-mms")
    public ResponseEntity<TransactionMMSResponseDTO> insertTransactionMMS(@RequestBody TransactionMMSEntity entity) {
        TransactionMMSResponseDTO result = null;
        HttpStatus httpStatus = null;
        TerminalBankEntity terminalBankEntity = null;
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
                    logger.error(
                            "transaction-mms-sync: INSERT SUCCESS at: " + insertTime);
                    ///
                    // find transaction_receive to update
                    // amount
                    // order_id -> reference_label_code
                    // status = 0
                    // (traceId => bill_number)
                    if (result != null && result.getResCode().equals("00")) {
                        TransactionReceiveEntity transactionReceiveEntity = transactionReceiveService
                                .getTransactionByOrderId(entity.getReferenceLabelCode(), entity.getDebitAmount());
                        if (transactionReceiveEntity != null) {
                            // transactionReceiveEntity != null:
                            // push data to customerSync
                            if (terminalBankEntity != null) {
                                getCustomerSyncEntities(terminalBankEntity.getId(), entity.getFtCode(),
                                        transactionReceiveEntity, time);
                            } else {
                                logger.info(
                                        "transaction-mms-sync: terminalBankEntity = NULL; CANNOT push data to customerSync");
                            }
                            // update
                            transactionReceiveService.updateTransactionReceiveStatus(1, uuid.toString(),
                                    entity.getFtCode(),
                                    transactionReceiveEntity.getId());
                        } else {
                            logger.info("transaction-mms-sync: NOT FOUND transactionReceiveEntity");
                            // transactionReceiveEntity = null =>
                            // push data to customerSync
                            // insert
                        }
                        ///
                    }
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

        } catch (Exception e) {
            logger.error("transaction-mms-sync: Error " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
            result = new TransactionMMSResponseDTO("99", "Internal error");
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    private void getCustomerSyncEntities(String terminalBankId, String ftCode,
            TransactionReceiveEntity transactionReceiveEntity, long time) {
        try {
            // find customerSyncEntities by terminal_bank_id
            List<TerminalAddressEntity> terminalAddressEntities = new ArrayList<>();
            terminalAddressEntities = terminalAddressService.getTerminalAddressByTerminalBankId(terminalBankId);
            if (!terminalAddressEntities.isEmpty()) {
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
                for (TerminalAddressEntity terminalAddressEntity : terminalAddressEntities) {
                    CustomerSyncEntity customerSyncEntity = customerSyncService
                            .getCustomerSyncById(terminalAddressEntity.getCustomerSyncId());
                    pushNewTransactionToCustomerSync(customerSyncEntity, transactionBankCustomerDTO, time * 1000);
                }
            }
        } catch (Exception e) {
            logger.error("getCustomerSyncEntities MMS: ERROR: " + e.toString());
        }
    }

    private void pushNewTransactionToCustomerSync(CustomerSyncEntity entity,
            TransactionBankCustomerDTO dto, long time) {
        try {
            logger.info("pushNewTransactionToCustomerSync MMS: orderId: " + dto.getOrderId());
            logger.info("pushNewTransactionToCustomerSync MMS: sign: " + dto.getSign());
            TokenDTO tokenDTO = null;
            if (entity.getUsername() != null && !entity.getUsername().trim().isEmpty() && entity.getPassword() != null
                    && !entity.getPassword().trim().isEmpty()) {
                tokenDTO = getCustomerSyncToken(entity);
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
            String suffixUrl = "";
            if (entity.getSuffixUrl() != null && !entity.getSuffixUrl().isEmpty()) {
                suffixUrl = entity.getSuffixUrl();
            }
            UriComponents uriComponents = null;
            WebClient webClient = null;
            if (entity.getIpAddress() != null && !entity.getIpAddress().isEmpty()) {
                uriComponents = UriComponentsBuilder
                        .fromHttpUrl("http://" + entity.getIpAddress() + ":" + entity.getPort() + "/" + suffixUrl
                                + "/bank/api/transaction-sync")
                        .buildAndExpand(/* add url parameter here */);
                webClient = WebClient.builder()
                        .baseUrl("http://" + entity.getIpAddress() + ":" + entity.getPort() + "/" + suffixUrl
                                + "/bank/api/transaction-sync")
                        .build();
            } else {
                uriComponents = UriComponentsBuilder
                        .fromHttpUrl(entity.getInformation() + "/" + suffixUrl
                                + "/bank/api/transaction-sync")
                        .buildAndExpand(/* add url parameter here */);
                webClient = WebClient.builder()
                        .baseUrl(entity.getInformation() + "/" + suffixUrl
                                + "/bank/api/transaction-sync")
                        .build();
            }
            //
            logger.info("uriComponents: " + uriComponents.toString());
            Mono<TransactionResponseDTO> responseMono = null;
            if (tokenDTO != null) {
                responseMono = webClient.post()
                        .uri(uriComponents.toUri())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + tokenDTO.getAccess_token())
                        .body(BodyInserters.fromValue(data))
                        .retrieve()
                        .bodyToMono(TransactionResponseDTO.class);
            } else {
                responseMono = webClient.post()
                        .uri(uriComponents.toUri())
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(data))
                        .retrieve()
                        .bodyToMono(TransactionResponseDTO.class);
            }
            responseMono.subscribe(transactionResponseDTO -> {
                LocalDateTime currentDateTime = LocalDateTime.now();
                long responseTime = currentDateTime.toEpochSecond(ZoneOffset.UTC);
                if (transactionResponseDTO != null && transactionResponseDTO.getObject() != null) {
                    if (entity.getIpAddress() != null && !entity.getIpAddress().isEmpty()) {
                        logger.info("pushNewTransactionToCustomerSync MMS SUCCESS: " + entity.getIpAddress() + " - "
                                + transactionResponseDTO.getObject().getReftransactionid() + " at: "
                                + responseTime);
                    } else {
                        logger.info("pushNewTransactionToCustomerSync MMS SUCCESS: " + entity.getInformation() + " - "
                                + transactionResponseDTO.getObject().getReftransactionid() + " at: "
                                + responseTime);
                    }
                    // System.out.println("pushNewTransactionToCustomerSync SUCCESS: " +
                    // entity.getIpAddress() + " - "
                    // + transactionResponseDTO.getObject().getReftransactionid());
                } else {
                    if (entity.getIpAddress() != null && !entity.getIpAddress().isEmpty()) {
                        logger.error("Error at pushNewTransactionToCustomerSync MMS: " + entity.getIpAddress() + " - "
                                + (transactionResponseDTO != null ? transactionResponseDTO.getErrorReason() : "")
                                + " at: " + responseTime);
                    } else {
                        logger.error("Error at pushNewTransactionToCustomerSync MMS: " + entity.getInformation() + " - "
                                + (transactionResponseDTO != null ? transactionResponseDTO.getErrorReason() : "")
                                + " at: " + responseTime);
                    }
                }
            }, error -> {
                LocalDateTime currentDateTime = LocalDateTime.now();
                long responseTime = currentDateTime.toEpochSecond(ZoneOffset.UTC);
                if (entity.getIpAddress() != null && !entity.getIpAddress().isEmpty()) {
                    logger.error("Error at pushNewTransactionToCustomerSync MMS: " + entity.getIpAddress() + " - "
                            + error.toString() + " at: " + responseTime);
                } else {
                    logger.error("Error at pushNewTransactionToCustomerSync MMS: " + entity.getInformation() + " - "
                            + error.toString() + " at: " + responseTime);
                }

            });
        } catch (Exception e) {
            LocalDateTime currentDateTime = LocalDateTime.now();
            long responseTime = currentDateTime.toEpochSecond(ZoneOffset.UTC);
            if (entity.getIpAddress() != null && !entity.getIpAddress().isEmpty()) {
                logger.error(
                        "Error at pushNewTransactionToCustomerSync MMS: " + entity.getIpAddress() + " - " + e.toString()
                                + " at: " + responseTime);
            } else {
                logger.error(
                        "Error at pushNewTransactionToCustomerSync MMS: " + entity.getInformation() + " - "
                                + e.toString()
                                + " at: " + responseTime);
            }
        }
    }

    private TokenDTO getCustomerSyncToken(CustomerSyncEntity entity) {
        TokenDTO result = null;
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
                if (entity.getIpAddress() != null && !entity.getIpAddress().isEmpty()) {
                    logger.info("Token got: " + result.getAccess_token() + " - from: " + entity.getIpAddress());
                } else {
                    logger.info("Token got: " + result.getAccess_token() + " - from: " + entity.getInformation());
                }

                // System.out.println("Token got: " + result.getAccess_token() + " - from: " +
                // entity.getIpAddress());
            } else {
                if (entity.getIpAddress() != null && !entity.getIpAddress().isEmpty()) {
                    logger.info("Token could not be retrieved from: " + entity.getIpAddress());
                } else {
                    logger.info("Token could not be retrieved from: " + entity.getInformation());
                }
            }
            ///
        } catch (Exception e) {
            if (entity.getIpAddress() != null && !entity.getIpAddress().isEmpty()) {
                logger.error("Error at getCustomerSyncToken: " + entity.getIpAddress() + " - " + e.toString());
                // System.out.println("Error at getCustomerSyncToken: " + entity.getIpAddress()
                // + " - " + e.toString());
            } else {
                logger.error("Error at getCustomerSyncToken: " + entity.getInformation() + " - " + e.toString());
            }
        }
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

}
