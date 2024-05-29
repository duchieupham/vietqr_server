package com.vietqr.org.controller;

import com.vietqr.org.dto.AccountCustomerMerchantDTO;
import com.vietqr.org.service.AccountCustomerService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
//@CrossOrigin
@RequestMapping("/api")
public class AccountCustomerController {
    private static final Logger logger = Logger.getLogger(AccountCustomerController.class);

    @Autowired
    AccountCustomerService accountCustomerService;

    @GetMapping("admin/account/merchant/{pw}")
    public ResponseEntity<List<AccountCustomerMerchantDTO>> getMerchantByPw(@PathVariable String pw) {
        List<AccountCustomerMerchantDTO> merchant = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            merchant = accountCustomerService.getMerchantNameByPassword(pw);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("getDataTrMonth: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity<>(merchant, httpStatus);
    }
}
