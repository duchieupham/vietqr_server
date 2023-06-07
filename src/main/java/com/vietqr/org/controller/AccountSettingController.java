package com.vietqr.org.controller;

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

import com.vietqr.org.dto.AccountSettingUpdateDTO;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.entity.AccountSettingEntity;
import com.vietqr.org.service.AccountSettingService;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class AccountSettingController {
    private static final Logger logger = Logger.getLogger(AccountSettingController.class);

    @Autowired
    AccountSettingService accountSettingService;

    @GetMapping("accounts/setting/{userId}")
    public ResponseEntity<AccountSettingEntity> getAccountSetting(@PathVariable("userId") String userId) {
        AccountSettingEntity result = null;
        HttpStatus httpStatus = null;
        try {
            if (userId != null && !userId.trim().isEmpty()) {
                result = accountSettingService.getAccountSettingEntity(userId);
                httpStatus = HttpStatus.OK;
            } else {
                logger.error("getAccountSetting: UserID = null or empty");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("getAccountSetting: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<AccountSettingEntity>(result, httpStatus);
    }

    @PostMapping("accounts/setting")
    public ResponseEntity<ResponseMessageDTO> updateGuideWebUser(@Valid @RequestBody AccountSettingUpdateDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            accountSettingService.updateGuideWebByUserId(dto.getGuideWeb(), dto.getUserId());
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("updateGuideWebUser: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
        }
        return new ResponseEntity<ResponseMessageDTO>(result, httpStatus);
    }

}
