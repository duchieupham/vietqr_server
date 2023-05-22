package com.vietqr.org.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vietqr.org.dto.AccountBankBranchDTO;
import com.vietqr.org.dto.AccountBankConnectBranchDTO;
import com.vietqr.org.dto.AccountBranchBranchInsertDTO;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.entity.BankReceiveBranchEntity;
import com.vietqr.org.entity.BankReceivePersonalEntity;
import com.vietqr.org.service.AccountBankReceivePersonalService;
import com.vietqr.org.service.AccountBankReceiveService;
import com.vietqr.org.service.BankReceiveBranchService;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class AccountBankReceiveBranchController {
    private static final Logger logger = Logger.getLogger(AccountBankReceiveBranchController.class);

    @Autowired
    BankReceiveBranchService bankReceiveBranchService;

    @Autowired
    AccountBankReceiveService accountBankReceiveService;

    @Autowired
    AccountBankReceivePersonalService accountBankReceivePersonalService;

    @GetMapping("bank-branch/{branchId}")
    public ResponseEntity<List<AccountBankBranchDTO>> getBanksByBranchId(
            @PathVariable(value = "branchId") String branchId) {
        List<AccountBankBranchDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            result = bankReceiveBranchService.getBanksByBranchId(branchId);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("BANK_BRANCH: getBanksByBranchId ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // find available bank to add
    @GetMapping("bank-branch/search/{userId}")
    public ResponseEntity<List<AccountBankConnectBranchDTO>> getAccountBankConnect(
            @PathVariable(value = "userId") String userId) {
        List<AccountBankConnectBranchDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            result = accountBankReceiveService.getAccountBankConnect(userId);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("BANK_BRANCH: getAccountBankConnect: " + e.toString());
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // add bank to branch
    @PostMapping("bank-branch")
    // bank_id; user_id; business_id; branch_id
    public ResponseEntity<ResponseMessageDTO> addBankToBranch(@Valid @RequestBody AccountBranchBranchInsertDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        logger.info("MEMBER: branch-member: addBankToBranch: " + dto.toString());
        try {
            // update account_bank_receive with type = 1
            accountBankReceiveService.updateBankType(dto.getBankId(), 1);
            // insert bank_receive_branch
            UUID uuid = UUID.randomUUID();
            BankReceiveBranchEntity entity = new BankReceiveBranchEntity();
            entity.setId(uuid.toString());
            entity.setBankId(dto.getBankId());
            entity.setBusinessId(dto.getBusinessId());
            entity.setBranchId(dto.getBranchId());
            bankReceiveBranchService.insertBankReceiveBranch(entity);
            // delete bank_receive_personal
            accountBankReceivePersonalService.deleteBankReceivePersonalByBankId(dto.getBankId());
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("MEMBER: branch-member: addBankToBranch ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // remove bank to branch
    @PostMapping("bank-branch/remove")
    // bank_id; user_id; business_id; branch_id
    public ResponseEntity<ResponseMessageDTO> removeBankToBranch(@Valid @RequestBody AccountBranchBranchInsertDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        logger.info("MEMBER: branch-member: removeBankToBranch: " + dto.toString());
        try {
            // update account_bank_receive with type = 1
            accountBankReceiveService.updateBankType(dto.getBankId(), 0);
            // insert bank_receive_personal
            UUID uuid = UUID.randomUUID();
            BankReceivePersonalEntity entity = new BankReceivePersonalEntity();
            entity.setId(uuid.toString());
            entity.setBankId(dto.getBankId());
            entity.setUserId(dto.getUserId());
            accountBankReceivePersonalService.insertAccountBankReceivePersonal(entity);
            // delete bank_receive_branch
            bankReceiveBranchService.deleteBankReceiveBranchByBankIdAndBranchId(dto.getBankId(), dto.getBranchId());
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("MEMBER: branch-member: removeBankToBranch ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }
}
