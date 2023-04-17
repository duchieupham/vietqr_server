package com.vietqr.org.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vietqr.org.dto.TransactionBranchInputDTO;
import com.vietqr.org.dto.TransactionInputDTO;
import com.vietqr.org.dto.TransactionRelatedDTO;
import com.vietqr.org.service.TransactionReceiveBranchService;
import com.vietqr.org.service.TransactionReceiveService;

@RestController
@RequestMapping("/api")
public class TransactionController {
    private static final Logger logger = Logger.getLogger(TransactionController.class);

    @Autowired
    TransactionReceiveBranchService transactionReceiveBranchService;

    @Autowired
    TransactionReceiveService transactionReceiveService;

    @PostMapping("transaction-branch")
    private ResponseEntity<List<TransactionRelatedDTO>> getTransactionsByBranchId(
            @Valid @RequestBody TransactionBranchInputDTO dto) {
        List<TransactionRelatedDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            if (dto.getBranchId().trim().equals("all")) {
                // get transaction by businessId
                result = transactionReceiveBranchService.getTransactionsByBusinessId(dto.getBusinessId(),
                        dto.getOffset());
            } else {
                // get transaction by branchId
                result = transactionReceiveBranchService.getTransactionsByBranchId(dto.getBranchId(), dto.getOffset());
            }
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error(e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("transaction/list")
    private ResponseEntity<List<TransactionRelatedDTO>> getTransactionsByBankId(
            @Valid @RequestBody TransactionInputDTO dto) {
        List<TransactionRelatedDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            result = transactionReceiveService.getTransactions(dto.getOffset(), dto.getBankId());
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error(e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }
}
