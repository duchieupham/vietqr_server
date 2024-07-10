package com.vietqr.org.controller;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @GetMapping("sendEmail")
    public ResponseEntity<Object> sendEmail() {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            emailService.sendSimpleMessage(
                    "at050801@gmail.com",
                    "Xác thực OTP lấy key active",
                    "Mã OTP của bạn là: 123456. ");

            result = new ResponseMessageDTO("SUCCESS", "Sent mail thành công.");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED" + e.getMessage(), "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }


    @GetMapping("sendemail-ses")
    public ResponseEntity<Object> sendEmailSES() {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            emailService.sendEmail(
                    "anh.ttq@vietqr.vn",
                    "Xác thực OTP lấy key active",
                    "Mã OTP của bạn là: 123456.");

            result = new ResponseMessageDTO("SUCCESS", "Sent mail thành công.");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED" + e.getMessage(), "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

}
