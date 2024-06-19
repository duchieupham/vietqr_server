package com.vietqr.org.controller;

import com.vietqr.org.dto.BankReceiveFeePackageUpdateRequestDTO;
import com.vietqr.org.dto.IAccountBankInfoCustomDTO;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.entity.AccountBankReceiveEntity;
import com.vietqr.org.entity.BankReceiveFeePackageEntity;
import com.vietqr.org.entity.FeePackageEntity;
import com.vietqr.org.service.AccountBankReceiveService;
import com.vietqr.org.service.BankReceiveFeePackageService;
import com.vietqr.org.service.FeePackageService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class BankReceivePackageController {

    private static final Logger logger = Logger.getLogger(BankReceivePackageController.class);
    @Autowired
    private BankReceiveFeePackageService bankReceiveFeePackageService;
    @Autowired
    private FeePackageService feePackageService;

    @Autowired
    private AccountBankReceiveService accountBankReceiveService;

    @PostMapping("/bank-receive-fee-package/create")
    public ResponseEntity<ResponseMessageDTO> addFeePackageToBankReceive(
            @RequestParam String bankId,
            @RequestParam String feePackageId,
            @RequestParam String mid
    ){
        HttpStatus httpStatus = null;
        ResponseMessageDTO result = null;
        try{
            FeePackageEntity feePackage = feePackageService.getFeePackageById(feePackageId);
            IAccountBankInfoCustomDTO accountBank = accountBankReceiveService.getAccountBankByBankId(bankId);

            String dataJson = String.format(
                    "{\"mmsActive\": %s, \"bankAccount\": \"%s\", \"userBankName\": \"%s\", \"bankShortName\": \"%s\"}",
                    accountBank.getIsMmsActive(), accountBank.getBankAccount(), accountBank.getBankAccountName(), accountBank.getBankShortName());

            BankReceiveFeePackageEntity bankReceiveFeePackage = new BankReceiveFeePackageEntity();
            String id =  UUID.randomUUID().toString();
            bankReceiveFeePackage.setId(id);
            bankReceiveFeePackage.setBankId(bankId);
            bankReceiveFeePackage.setFeePackageId(feePackageId);
            bankReceiveFeePackage.setTitle(feePackage.getTitle());
            bankReceiveFeePackage.setActiveFee(feePackage.getActiveFee());
            bankReceiveFeePackage.setAnnualFee(feePackage.getAnnualFee());
            bankReceiveFeePackage.setFixFee(feePackage.getFixFee());
            bankReceiveFeePackage.setPercentFee(feePackage.getPercentFee());
            bankReceiveFeePackage.setVat(feePackage.getVat());

            bankReceiveFeePackage.setRecordType(feePackage.getRecordType());
            bankReceiveFeePackage.setUserId(accountBank.getUserId());
            bankReceiveFeePackage.setMid(mid);
            bankReceiveFeePackage.setData(dataJson);

            bankReceiveFeePackageService.saveBankReceiveFeePackage(bankReceiveFeePackage);
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("BankReceivePackageController: ERROR: addFeePackageToBankReceive: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity<>(result, httpStatus);
    }


    @PutMapping("/bank-receive-package-update/{id}")
    public ResponseEntity<ResponseMessageDTO> updateBankReceiveFeePackage(
            @PathVariable String id,
            @Valid @RequestBody BankReceiveFeePackageUpdateRequestDTO updateRequestDTO) {

        ResponseMessageDTO result = null;
        HttpStatus httpStatus;
        try {
            bankReceiveFeePackageService.updateBankReceiveFeePackage(id, updateRequestDTO);

            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("Failed at BankReceiveFeePackageController: Error at updateBankReceiveFeePackage: "
                    + e.getMessage() + " at " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

}
