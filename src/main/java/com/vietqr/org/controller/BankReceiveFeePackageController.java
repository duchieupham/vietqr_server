package com.vietqr.org.controller;

import com.vietqr.org.dto.BankReceiveFeePackageDTOv2;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.entity.BankReceiveFeePackageEntity;
import com.vietqr.org.service.BankReceiveFeePackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class BankReceiveFeePackageController {

    @Autowired
    BankReceiveFeePackageService bankReceiveFeePackageService;

    @DeleteMapping("/bank-receive-fee-package/delete")
    public ResponseEntity<ResponseMessageDTO> deleteBankReceiveFeePackage(@RequestParam String id) {
        ResponseMessageDTO result;
        HttpStatus httpStatus;
        try {
            bankReceiveFeePackageService.deleteBankReceiveFeePackage(id);
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAIL", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PutMapping("/bank-receive-fee-package/update/{id}")
    public ResponseEntity<ResponseMessageDTO> updateBankReceiveFeePackage(
            @PathVariable String id,
            @RequestBody BankReceiveFeePackageDTOv2 updateDTO) {

        try {
            BankReceiveFeePackageEntity existingPackage = bankReceiveFeePackageService.getFeePackageByBankIds(id);
            if (existingPackage == null) {
                return new ResponseEntity<>(new ResponseMessageDTO("FAIL", "E05"), HttpStatus.NOT_FOUND);
            }

            BankReceiveFeePackageDTOv2 bankReceiveFeePackageDTOV2 = new BankReceiveFeePackageDTOv2(
                    existingPackage.getActiveFee(),
                    existingPackage.getAnnualFee(),
                    existingPackage.getFixFee(),
                    existingPackage.getPercentFee(),
                    existingPackage.getRecordType()
            );

            if (updateDTO.getActiveFee() != null) {
                bankReceiveFeePackageDTOV2.setActiveFee(updateDTO.getActiveFee());
            }
            if (updateDTO.getAnnualFee() != null) {
                bankReceiveFeePackageDTOV2.setAnnualFee(updateDTO.getAnnualFee());
            }
            if (updateDTO.getFixFee() != null) {
                bankReceiveFeePackageDTOV2.setFixFee(updateDTO.getFixFee());
            }
            if (updateDTO.getPercentFee() != null) {
                bankReceiveFeePackageDTOV2.setPercentFee(updateDTO.getPercentFee());
            }
            if (updateDTO.getRecordType() == 0 || updateDTO.getRecordType() == 1) {
                bankReceiveFeePackageDTOV2.setRecordType(updateDTO.getRecordType());
            }

            bankReceiveFeePackageService.saveBankReceiveFeePackage(id, bankReceiveFeePackageDTOV2);

            return new ResponseEntity<>(new ResponseMessageDTO("SUCCESS", ""), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseMessageDTO("FAIL", "E05"), HttpStatus.BAD_REQUEST);
        }
    }
}
