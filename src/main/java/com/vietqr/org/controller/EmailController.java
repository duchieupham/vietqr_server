package com.vietqr.org.controller;

import com.vietqr.org.dto.EmailRequestDTO;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;

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


    @PostMapping("sendemail-ses")
    public ResponseEntity<Object> sendEmailSES(
            @RequestParam String fromMail,
            @RequestParam String toMail,
            @RequestParam String subject,
            @RequestParam String body
    ) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            emailService.sendEmail(fromMail, toMail, subject, body);

            result = new ResponseMessageDTO("SUCCESS", "Sent mail thành công.");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED" + e.getMessage(), "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("/sendEmail-body")
    public ResponseEntity<Object> sendMessage(@RequestBody EmailRequestDTO emailDetails) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom(emailDetails.getFromEmail());
            simpleMailMessage.setTo(emailDetails.getToEmail());
            simpleMailMessage.setSubject(emailDetails.getSubject());
            simpleMailMessage.setText(emailDetails.getBody());
            emailService.sendMessage(simpleMailMessage);

            result = new ResponseMessageDTO("SUCCESS", "Sent mail thành công.");
            httpStatus = HttpStatus.OK;
        }catch (Exception e) {
            result = new ResponseMessageDTO("FAILED" + e.getMessage(), "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }
}
