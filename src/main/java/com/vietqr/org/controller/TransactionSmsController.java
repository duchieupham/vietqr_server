package com.vietqr.org.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.dto.TransactionSmsInsertDTO;
import com.vietqr.org.dto.TransactionSmsItemDTO;
import com.vietqr.org.entity.TransactionSmsEntity;
import com.vietqr.org.service.TransactionSmsService;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class TransactionSmsController {
    private static final Logger logger = Logger.getLogger(TransactionSmsController.class);

    @Autowired
    TransactionSmsService transactionSmsService;

    // get list
    @GetMapping("transaction-sms/list")
    public ResponseEntity<List<TransactionSmsItemDTO>> getTransactions(
            @RequestParam(value = "type") int type,
            @RequestParam(value = "value") String value,
            @RequestParam(value = "status") int status,
            @RequestParam(value = "offset") int offset) {
        List<TransactionSmsItemDTO> result = new ArrayList<>();
        List<TransactionSmsEntity> list = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            // 0: bankAccount
            // 9: all
            if (type == 0) {
                // check status
                // 9: all
                // 0: pending
                // 1: success
                // 2: cancelled
                if (status == 9) {
                    list = transactionSmsService.getTransactionsByBankId(value, offset);
                } else {
                    list = transactionSmsService.getTransactionsByBankIdAndStatus(value, status, offset);
                }
            } else if (type == 9) {
                // check status
                // 9: all
                // 0: pending
                // 1: success
                // 2: cancelled
                if (status == 9) {
                    list = transactionSmsService.getTransactionsBySmsId(value, offset);
                } else {
                    list = transactionSmsService.getTransactionsByStatus(value, status, offset);
                }
            } else {
                logger.error("getTransactions: INVALID TYPE");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
            if (list != null) {
                if (!list.isEmpty()) {
                    for (TransactionSmsEntity item : list) {
                        TransactionSmsItemDTO dto = new TransactionSmsItemDTO();
                        dto.setId(item.getId());
                        dto.setAmount(item.getAmount());
                        dto.setTransType(item.getTransType());
                        dto.setTime(item.getTime());
                        dto.setTimePaid(item.getTimePaid());
                        dto.setContent(item.getContent());
                        result.add(dto);
                    }
                }
                httpStatus = HttpStatus.OK;
            }
        } catch (Exception e) {
            logger.error("getTransactions: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // insert all
    @GetMapping("transaction-sms")
    public ResponseEntity<ResponseMessageDTO> insertAllTransaction(@RequestBody List<TransactionSmsInsertDTO> list) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (list != null && !list.isEmpty()) {
                if (list.size() <= 50) {
                    List<TransactionSmsEntity> entities = new ArrayList<>();
                    for (TransactionSmsInsertDTO item : list) {
                        TransactionSmsEntity entity = new TransactionSmsEntity();
                        UUID uuid = UUID.randomUUID();
                        entity.setId(uuid.toString());
                        entity.setBankAccount(item.getBankAccount());
                        entity.setBankId(item.getBankId());
                        entity.setContent(item.getContent());
                        entity.setAddress(item.getAddress());
                        entity.setAmount(item.getAmount());
                        entity.setAccountBalance(item.getAccountBalance());
                        entity.setTime(item.getTime());
                        entity.setTimePaid(item.getTime());
                        entity.setTransType(item.getTransType());
                        entity.setReferenceNumber(item.getReferenceNumber());
                        entity.setSmsId(item.getSmsId());
                        entities.add(entity);
                    }
                    if (entities != null && !entities.isEmpty()) {
                        transactionSmsService.insertAll(entities);
                    }
                    result = new ResponseMessageDTO("SUCCESS", "");
                    httpStatus = HttpStatus.OK;
                } else {
                    logger.error("syncBankAccountSms: LIST OVER 50 records");
                    result = new ResponseMessageDTO("FAILED", "E92");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                logger.error("syncBankAccountSms: INVALID REQUEST BODY");
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("syncBankAccountSms: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // get detail
    @GetMapping("transaction-sms/detail")
    public ResponseEntity<TransactionSmsEntity> getTransactionSmsDetail(@RequestParam(value = "id") String id) {
        TransactionSmsEntity result = null;
        HttpStatus httpStatus = null;
        try {
            result = transactionSmsService.getTransactionSmsDetail(id);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("getTransactionSmsDetail: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);

    }
}
