package com.vietqr.org.controller;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vietqr.org.dto.MobileRechargeDTO;
import com.vietqr.org.dto.ResponseMessageDTO;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class VNPTEpayController {
    private static final Logger logger = Logger.getLogger(VNPTEpayController.class);

    // api nap tien dien thoai
    // 1. check so du
    // 2. call topup
    // 3.1. Thanh cong -> tru tien + insert giao dich
    // 3.2. Khong thanh cong -> bao loi
    @PostMapping("epay/mobile-money")
    public ResponseEntity<ResponseMessageDTO> rechargeMobile(@RequestBody MobileRechargeDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {

        } catch (Exception e) {
            System.out.println("Error at registerAccount: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }
}
