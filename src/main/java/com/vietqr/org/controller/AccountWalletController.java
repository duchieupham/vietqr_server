package com.vietqr.org.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vietqr.org.dto.AccountWalletCustomerDTO;
import com.vietqr.org.entity.AccountWalletEntity;
import com.vietqr.org.service.AccountWalletService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class AccountWalletController {
    private static final Logger logger = Logger.getLogger(AccountWalletController.class);

    @Autowired
    AccountWalletService accountWalletService;

    @GetMapping("account-wallet")
    public ResponseEntity<AccountWalletCustomerDTO> getAccountBankReceiveWps(
            @RequestHeader("Authorization") String token) {
        AccountWalletCustomerDTO result = null;
        HttpStatus httpStatus = null;
        try {
            String userId = getUserIdFromToken(token);
            AccountWalletEntity entity = accountWalletService.getAccountWalletByUserId(userId);
            result = new AccountWalletCustomerDTO(entity.getAmount(), entity.isEnableService());
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("getAccountBankReceiveWps: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    private String getUserIdFromToken(String token) {
        String result = "";
        if (token != null && !token.trim().isEmpty()) {
            String secretKey = "mySecretKey";
            String jwtToken = token.substring(7); // remove "Bearer " from the beginning
            Claims claims = Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(jwtToken).getBody();
            String userId = (String) claims.get("userId");
            result = userId;
        }
        return result;
    }

}
