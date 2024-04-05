package com.vietqr.org.controller;

import com.vietqr.org.dto.*;
import com.vietqr.org.entity.AccountBankReceiveEntity;
import com.vietqr.org.service.AccountBankReceiveService;
import com.vietqr.org.service.AccountLoginService;
import com.vietqr.org.service.BankReceiveOTPService;
import com.vietqr.org.service.KeyBankAccountReceiveService;
import com.vietqr.org.util.DateTimeUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class KeyAccountBankReceiveController {

    private static final Logger logger = Logger.getLogger(KeyAccountBankReceiveController.class);

    @Autowired
    private KeyBankAccountReceiveService keyBankAccountReceiveService;

    @Autowired
    private BankReceiveOTPService bankReceiveOTPService;

    @Autowired
    private AccountLoginService accountLoginService;

    @Autowired
    private AccountBankReceiveService accountBankReceiveService;

    @GetMapping("request-active-key")
    public ResponseEntity<Object> requestActiveBankReceive(
            @Valid @RequestBody RequestActiveBankReceiveDTO dto) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            logger.info("requestActiveBankReceive: request: " + dto.toString()
                    + " at: " + System.currentTimeMillis());
            //
            //1. check password chính xác hay chưa
            String checkPassword = accountLoginService.checkPassword(dto.getUserId(), dto.getPassword());
            if (checkPassword == null || checkPassword.isEmpty()) {
                result = new ResponseMessageDTO("FAILED", "E55");
                httpStatus = HttpStatus.BAD_REQUEST;
            } else {
                //2. check bankId có tồn tại hay không và đã authenticated chưa (đã active chưa)
                BankReceiveCheckDTO bankReceiveCheckDTO = accountBankReceiveService
                        .checkBankReceiveActive(dto.getBankId());
                if (bankReceiveCheckDTO == null) {
                    result = new ResponseMessageDTO("FAILED", "E25");
                    httpStatus = HttpStatus.BAD_REQUEST;
                } else if (!bankReceiveCheckDTO.getAuthenticated()) {
                    result = new ResponseMessageDTO("FAILED", "E101");
                    httpStatus = HttpStatus.BAD_REQUEST;
                } else if (!Objects.equals(bankReceiveCheckDTO.getUserId(), dto.getUserId())) {
                    result = new ResponseMessageDTO("FAILED", "E126");
                    httpStatus = HttpStatus.BAD_REQUEST;
                } else {
                    //3. check key có valid không (đã tồn tại trong db chưa)
                    KeyActiveBankReceiveDTO bankReceiveDTO = keyBankAccountReceiveService.checkKeyExist(dto.getKey());
                    if (bankReceiveDTO == null) {
                        result = new ResponseMessageDTO("FAILED", "E127");
                        httpStatus = HttpStatus.BAD_REQUEST;
                    } else if (bankReceiveDTO.getStatus() == 0) {
                        result = new ResponseMessageDTO("FAILED", "E127");
                        httpStatus = HttpStatus.BAD_REQUEST;
                    } else {
                        String otp = randomOtp();
                        KeyBankReceiveActiveDTO entity = accountBankReceiveService
                                .getAccountBankKeyById(dto.getBankId());
                        long validFeeFrom = entity.getValidFeeFrom();
                        long validFeeTo = entity.getValidFeeTo();
                        if (entity.getIsValidService()) {
                            validFeeTo = validFeeTo +
                                    DateTimeUtil.plusMonthAsLongInt(validFeeTo, bankReceiveDTO.getDuration());
                        } else {
                            LocalDateTime time = LocalDateTime.now();
                            long currentTime = time.toEpochSecond(ZoneOffset.UTC);
                            validFeeFrom = currentTime;
                            validFeeTo = validFeeFrom +
                                    DateTimeUtil.plusMonthAsLongInt(currentTime, bankReceiveDTO.getDuration());
                        }


                        // return response
                        RequestActiveBankResponseDTO responseDTO = new RequestActiveBankResponseDTO();
                        responseDTO.setKey(bankReceiveDTO.getKey());
                        responseDTO.setOtp(otp);
                        responseDTO.setDuration(bankReceiveDTO.getDuration());
                        responseDTO.setValidFrom(validFeeFrom);
                        responseDTO.setValidTo(validFeeTo);

                        result = responseDTO;
                        httpStatus = HttpStatus.OK;
                    }
                }
            }
            //3. check key có valid không (đã tồn tại trong db chưa)
            //
        } catch (Exception e) {
            logger.error("requestActiveBankReceive: ERROR: " + e.getMessage() + " at " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("key/confirm-active")
    public ResponseEntity<ResponseMessageDTO> confirmActiveBankReceive(
            @Valid @RequestBody ConfirmActiveBankReceiveDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            logger.info("requestActiveBankReceive: request: " + dto.toString() + " at: " + System.currentTimeMillis());
            //
            BankReceiveOtpDTO bankReceiveOtpDTO = bankReceiveOTPService
                    .checkBankReceiveOtp(dto.getUserId(), dto.getBankId(), dto.getOtp());


            //
            result = new ResponseMessageDTO("SUCCESS", "");
            logger.info("requestActiveBankReceive: response: STATUS: " + result.getStatus()
                 + ", MESSAGE: " + result.getMessage() + " at: " + System.currentTimeMillis());
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("requestActiveBankReceive: ERROR: " + e.getMessage() + " at " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    private String randomOtp() {
        return String.valueOf((int) (Math.random() * 9000) + 1000);
    }
}
