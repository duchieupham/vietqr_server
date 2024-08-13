package com.vietqr.org.controller;

import java.util.UUID;

import javax.validation.Valid;

import com.vietqr.org.service.BankReceiveFeePackageService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vietqr.org.dto.BankReceiveFeePackageInsertDTO;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.entity.AccountBankReceiveEntity;
import com.vietqr.org.entity.BankReceiveFeePackageEntity;
import com.vietqr.org.entity.BankTypeEntity;
import com.vietqr.org.entity.FeePackageEntity;
import com.vietqr.org.service.AccountBankReceiveService;
import com.vietqr.org.service.BankTypeService;
import com.vietqr.org.service.FeePackageService;

@RestController
@CrossOrigin
@RequestMapping("/api/bank-receive-fee-package")

public class BankReceiveFeePackageController {
    private static final Logger logger = Logger.getLogger(BankReceiveFeePackageController.class);

    @Autowired
    FeePackageService feePackageService;

    @Autowired
    BankTypeService bankTypeService;

    @Autowired
    AccountBankReceiveService accountBankReceiveService;

    @Autowired
    BankReceiveFeePackageService bankReceiveFeePackageService;

    @PostMapping("insert-bank-receive-fee-package")
    public ResponseEntity<ResponseMessageDTO> insertBankReceiveFeePackage(
            @Valid @RequestBody BankReceiveFeePackageInsertDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null) {
                FeePackageEntity feePackage = (FeePackageEntity) feePackageService
                        .getFeePackageById(dto.getFeePackageId());
                if (feePackage != null) {
                    AccountBankReceiveEntity accountBankReceive = accountBankReceiveService
                            .getAccountBankById(dto.getBankId());
                    if (accountBankReceive != null) {
                        BankTypeEntity bankType = bankTypeService
                                .getBankTypeById(accountBankReceive.getBankTypeId());
                        if (bankType != null) {
                            UUID uuid = UUID.randomUUID();
                            BankReceiveFeePackageEntity entity = new BankReceiveFeePackageEntity();
                            entity.setId(uuid.toString());
                            entity.setActiveFee(feePackage.getActiveFee());
                            entity.setAnnualFee(feePackage.getAnnualFee());
                            entity.setBankId(dto.getBankId());
                            entity.setData("{"
                                    + "\"bank_account\": \"" + accountBankReceive.getBankAccount() + "\", "
                                    + "\"bank_account_name\": \"" + accountBankReceive.getBankAccountName()
                                    + "\", "
                                    + "\"bank_short_name\": \"" + bankType.getBankShortName() + "\""
                                    + "}");
                            entity.setFeePackageId(dto.getFeePackageId());
                            entity.setFixFee(feePackage.getFixFee());
                            entity.setPercentFee(feePackage.getPercentFee());
                            entity.setRecordType(feePackage.getRecordType());
                            entity.setTitle(feePackage.getTitle());
                            entity.setVat(feePackage.getVat());
                            entity.setMid(dto.getMid());
                            entity.setUserId(dto.getUserId());
                            bankReceiveFeePackageService.insertBankReceiveFeePackage(entity);
                            result = new ResponseMessageDTO("SUCCESS", "");
                            httpStatus = HttpStatus.OK;
                        } else {
                            logger.error(
                                    "insertBankReceiveFeePackage: SERVICE BANK TYPE NOT FOUND");
                            result = new ResponseMessageDTO("FAILED", "E1x");
                            httpStatus = HttpStatus.BAD_REQUEST;
                        }
                    } else {
                        logger.error(
                                "insertBankReceiveFeePackage: SERVICE ACCOUNT BANK RECEIVE NOT FOUND");
                        result = new ResponseMessageDTO("FAILED", "E2x");
                        httpStatus = HttpStatus.BAD_REQUEST;
                    }
                } else {
                    logger.error(
                            "insertBankReceiveFeePackage: SERVICE FEE PACKAGE NOT FOUND");
                    result = new ResponseMessageDTO("FAILED", "E3x");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("insertBankReceiveFeePackage: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }
}
