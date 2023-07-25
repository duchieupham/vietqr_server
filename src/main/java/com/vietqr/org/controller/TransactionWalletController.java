package com.vietqr.org.controller;

import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.CrossOrigin;

import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.vietqr.org.service.AccountLoginService;
import com.vietqr.org.service.BankTypeService;
import com.vietqr.org.service.CaiBankService;
import com.vietqr.org.service.TransactionWalletService;
import com.vietqr.org.util.EnvironmentUtil;
import com.vietqr.org.util.RandomCodeUtil;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.dto.TransWalletInsertDTO;
import com.vietqr.org.dto.VietQRDTO;
import com.vietqr.org.dto.VietQRGenerateDTO;
import com.vietqr.org.entity.BankTypeEntity;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class TransactionWalletController {
    private static final Logger logger = Logger.getLogger(TransactionWalletController.class);

    @Autowired
    TransactionWalletService transactionWalletService;

    @Autowired
    BankTypeService bankTypeService;

    @Autowired
    CaiBankService caiBankService;

    @Autowired
    AccountLoginService accountLoginService;

    @PostMapping("transaction-wallet")
    public ResponseEntity<Object> insertTransactionWallet(@RequestBody TransWalletInsertDTO dto) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null) {
                String userId = accountLoginService.getUserIdByPhoneNo(dto.getPhoneNo());
                if (userId != null) {
                    UUID uuid = UUID.randomUUID();
                    String traceId = "VQR" + RandomCodeUtil.generateRandomUUID();
                    String billNumber = "VRC" + RandomCodeUtil.generateRandomId(10);
                    LocalDateTime currentDateTime = LocalDateTime.now();
                    long time = currentDateTime.toEpochSecond(ZoneOffset.UTC);
                    // create VietQR
                    BankTypeEntity bankTypeEntity = bankTypeService
                            .getBankTypeById(EnvironmentUtil.getBankTypeIdRecharge());
                    String caiValue = caiBankService.getCaiValue(bankTypeEntity.getId());
                    VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
                    vietQRGenerateDTO.setCaiValue(caiValue);
                    vietQRGenerateDTO.setBankAccount(EnvironmentUtil.getBankAccountRecharge());
                    vietQRGenerateDTO.setAmount(dto.getAmount());
                    vietQRGenerateDTO.setContent(traceId + " " + billNumber);
                    // insert transaction_receive
                    // insert transaction_wallet

                } else {
                    logger.error("insertTransactionWallet: USER NOT EXISTED");
                    result = new ResponseMessageDTO("FAILED", "E50");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }

            } else {
                logger.error("insertTransactionWallet: INVALID REQUEST BODY");
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("insertTransactionWallet: Error " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;

        }
        return new ResponseEntity<>(result, httpStatus);
    }
}
