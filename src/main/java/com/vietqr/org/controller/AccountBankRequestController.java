package com.vietqr.org.controller;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vietqr.org.dto.AccountBankRequestDTO;
import com.vietqr.org.dto.AccountBankRequestItemDTO;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.entity.AccountBankRequestEntity;
import com.vietqr.org.service.AccountBankRequestService;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class AccountBankRequestController {

    private static final Logger logger = Logger.getLogger(AccountBankRequestController.class);

    @Autowired
    AccountBankRequestService accountBankRequestService;

    @GetMapping("account-bank-request")
    public ResponseEntity<List<AccountBankRequestItemDTO>> getAccountBankRequests(
            @RequestParam(value = "offset") int offset) {
        List<AccountBankRequestItemDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            result = accountBankRequestService.getAccountBankRequests(offset);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("getAccountBankRequests: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("account-bank-request")
    public ResponseEntity<ResponseMessageDTO> insertAccountBankRequest(
            @RequestBody AccountBankRequestDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null) {
                UUID uuid = UUID.randomUUID();
                AccountBankRequestEntity entity = new AccountBankRequestEntity();
                entity.setId(uuid.toString());
                entity.setBankAccount(dto.getBankAccount());
                entity.setUserBankName(dto.getUserBankName());
                entity.setBankCode(dto.getBankCode());
                entity.setNationalId(dto.getNationalId());
                entity.setPhoneAuthenticated(dto.getPhoneAuthenticated());
                entity.setRequestType(dto.getRequestType());
                entity.setAddress(dto.getAddress());
                //
                LocalDateTime currentDateTime = LocalDateTime.now();
                long time = currentDateTime.toEpochSecond(ZoneOffset.UTC);
                entity.setTimeCreated(time);
                entity.setSync(false);
                entity.setUserId(dto.getUserId());
                accountBankRequestService.insert(entity);
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            } else {
                logger.error("insertAccountBankRequest: INVALID REQUEST BODY");
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("insertAccountBankRequest: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PutMapping("account-bank-request")
    public ResponseEntity<ResponseMessageDTO> updateAccountBankRequest(
            @RequestParam(value = "id") String id,
            @RequestBody AccountBankRequestDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null) {
                accountBankRequestService.updateAccountBankRequest(dto.getBankAccount(), dto.getUserBankName(),
                        dto.getBankCode(), dto.getNationalId(), dto.getPhoneAuthenticated(), dto.getRequestType(),
                        dto.getAddress(), id);
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            } else {
                logger.error("updateAccountBankRequest: INVALID REQUEST BODY");
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("updateAccountBankRequest: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @DeleteMapping("account-bank-request")
    public ResponseEntity<ResponseMessageDTO> deleteAccountBankRequest(
            @RequestParam(value = "id") String id) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (id != null) {
                accountBankRequestService.deleteAccountBankRequest(id);
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            } else {
                logger.error("deleteAccountBankRequest: INVALID REQUEST BODY");
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("deleteAccountBankRequest: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

}
