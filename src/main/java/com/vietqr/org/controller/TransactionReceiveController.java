package com.vietqr.org.controller;

import com.vietqr.org.dto.CountTransactionsDTO;
import com.vietqr.org.dto.TransactionReceiveSyncDTO;
import com.vietqr.org.service.TransactionReceiveService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/bank/api")
public class TransactionReceiveController {
    private static final Logger logger = Logger.getLogger(TransactionReceiveController.class);

    @Autowired
    TransactionReceiveService transactionReceiveService;

    @GetMapping("transactions")
    public ResponseEntity<CountTransactionsDTO> countTransAccount() {
        CountTransactionsDTO result = null;
        HttpStatus httpStatus = null;
        try {
            result = transactionReceiveService.countTransAccount();
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            e.printStackTrace();
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("transactions/{time}")
    public ResponseEntity<TransactionReceiveSyncDTO> getTransactionReceiveSyncByTime(@PathVariable long time) {
        TransactionReceiveSyncDTO data = null;
        HttpStatus status = null;
        try {
            data = transactionReceiveService.getTransactionReceiveSyncByTime(time);
            status = HttpStatus.OK;
        } catch (Exception e) {
            e.printStackTrace();
            status = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(data, status);
    }
}
