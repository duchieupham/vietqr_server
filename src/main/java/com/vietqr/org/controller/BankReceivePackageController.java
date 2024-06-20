package com.vietqr.org.controller;

import com.vietqr.org.dto.*;
import com.vietqr.org.entity.AccountBankReceiveEntity;
import com.vietqr.org.entity.BankReceiveFeePackageEntity;
import com.vietqr.org.entity.FeePackageEntity;
import com.vietqr.org.service.AccountBankReceiveService;
import com.vietqr.org.service.BankReceiveFeePackageService;
import com.vietqr.org.service.FeePackageService;
import com.vietqr.org.util.StringUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
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
    @GetMapping("/bank-receive-fee-package-list")
    public ResponseEntity<Object> getAllBankReceiveFeePackages(
            @RequestParam(value = "value", defaultValue = "") String value,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        Object result = null;
        HttpStatus httpStatus = null;
        PageResDTO pageResDTO = new PageResDTO();

        try {
            int totalElement = bankReceiveFeePackageService.countBankReceiveFeePackagesByName(value);
            int offset = (page - 1) * size;
            List<IListBankReceiveFeePackageDTO> data = bankReceiveFeePackageService.getAllBankReceiveFeePackages(value, offset, size);

            PageDTO pageDTO = new PageDTO();
            pageDTO.setSize(size);
            pageDTO.setPage(page);
            pageDTO.setTotalElement(totalElement);
            pageDTO.setTotalPage(StringUtil.getTotalPage(totalElement, size));
            pageResDTO.setMetadata(pageDTO);
            pageResDTO.setData(data);

            httpStatus = HttpStatus.OK;
            result = pageResDTO;
        } catch (Exception e) {
            logger.error("Failed at BankReceiveFeePackageController: Error at getAllBankReceiveFeePackages: "
                    + e.getMessage() + " at " + System.currentTimeMillis());
            httpStatus = HttpStatus.BAD_REQUEST;
            result = new ResponseMessageDTO("FAILED", "E05");
        }

        return new ResponseEntity<>(result, httpStatus);
    }

    @DeleteMapping("/bank-receive-fee-package/delete/{id}")
    public ResponseEntity<ResponseMessageDTO> deleteBankReceiveFeePackage(@PathVariable String id) {
        HttpStatus httpStatus;
        ResponseMessageDTO result;

        try {
            bankReceiveFeePackageService.deleteBankReceiveFeePackageById(id);
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("Failed at BankReceiveFeePackageController: Error at deleteBankReceiveFeePackage: "
                    + e.getMessage() + " at " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity<>(result, httpStatus);
    }
    @GetMapping("/bank-receive-fee-package/{id}")
    public ResponseEntity<Object> getBankReceiveFeePackageDetail(@PathVariable String id) {
        HttpStatus httpStatus;
        Object result;

        try {
            IListBankReceiveFeePackageDTO data = bankReceiveFeePackageService.getBankReceiveFeePackageById(id);
            httpStatus = HttpStatus.OK;
            result = data;
        } catch (Exception e) {
            logger.error("Failed at BankReceiveFeePackageController: Error at getBankReceiveFeePackageDetail: "
                    + e.getMessage() + " at " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity<>(result, httpStatus);
    }
}
