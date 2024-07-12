package com.vietqr.org.controller;

import com.vietqr.org.dto.EmailDetails;
import com.vietqr.org.dto.EmailRequestDTO;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.dto.SendMailRequestDTO;
import com.vietqr.org.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("send-mail")
    public ResponseEntity<Object> sendEmailVerify(@RequestBody SendMailRequestDTO dto) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            emailService.sendMail(dto.getTo(), dto);

            result = new ResponseMessageDTO("SUCCESS", "Sent mail thành công.");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED" + e.getMessage(), "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // Sending email with attachment
    @PostMapping("/sendMailWithAttachment")
    public ResponseEntity<Object> sendMailWithAttachment(@RequestBody EmailDetails details) throws MessagingException {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            String status = emailService.sendMailWithAttachment(details);
            if (status.equals("Error")) {
                result = new ResponseMessageDTO("FAILED", "E05");
                httpStatus = HttpStatus.BAD_REQUEST;
            } else {
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            }
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);

    }

}
