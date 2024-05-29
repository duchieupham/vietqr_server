package com.vietqr.org.controller;

import com.vietqr.org.dto.AccountLoginSyncDTO;
import com.vietqr.org.service.AccountLoginService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@CrossOrigin
@RequestMapping("/api")
public class AccountLoginController {

    private static final Logger logger = Logger.getLogger(AccountLoginController.class);

    @Autowired
    AccountLoginService accountService;

    @GetMapping("/accountLoginSync")
    public ResponseEntity<AccountLoginSyncDTO> countAccount(){
        AccountLoginSyncDTO result = null;
        HttpStatus httpStatus = null;
        try {
            result = accountService.countAccounts();
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            e.printStackTrace();
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);

    }


}
