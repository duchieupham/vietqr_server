package com.vietqr.org.controller;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vietqr.org.dto.FCMTokenUpdateDTO;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.service.FcmTokenService;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class FCMTokenController {
    private static final Logger logger = Logger.getLogger(FCMTokenController.class);

    @Autowired
    FcmTokenService fcmTokenService;

    @PostMapping("fcm-token/update")
    public ResponseEntity<ResponseMessageDTO> updateToken(@Valid @RequestBody FCMTokenUpdateDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            fcmTokenService.updateToken(dto.getNewToken(), dto.getUserId(), dto.getOldToken());
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error(e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<ResponseMessageDTO>(result, httpStatus);
    }
}
