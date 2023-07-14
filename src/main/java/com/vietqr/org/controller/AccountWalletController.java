package com.vietqr.org.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vietqr.org.dto.AccountWalletCustomerDTO;
import com.vietqr.org.dto.AccountWalletDTO;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.entity.AccountWalletEntity;
import com.vietqr.org.service.AccountLoginService;
import com.vietqr.org.service.AccountWalletService;
import com.vietqr.org.util.RandomCodeUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class AccountWalletController {
    private static final Logger logger = Logger.getLogger(AccountWalletController.class);

    @Autowired
    AccountWalletService accountWalletService;

    @Autowired
    AccountLoginService accountLoginService;

    @GetMapping("account-wallet/{userId}")
    public ResponseEntity<AccountWalletDTO> getAccountWalletByUserId(
            @PathVariable("userId") String userId) {
        AccountWalletDTO result = null;
        HttpStatus httpStatus = null;
        try {
            System.out.println("userId: " + userId);

            AccountWalletEntity entity = accountWalletService.getAccountWalletByUserId(userId);
            result = new AccountWalletDTO(entity.getAmount(), entity.getPoint() + "", entity.getSharingCode(),
                    entity.getWalletId(), entity.isEnableService());
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            System.out.println(e.toString());
            logger.error("getAccountWalletByUserId: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

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

    @PostMapping("account-wallet/update-all")
    public ResponseEntity<ResponseMessageDTO> updateAccountWalletTable() {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            // delete all records from account_wallet
            // get all userIds
            List<String> userIds = new ArrayList<>();
            userIds = accountLoginService.getAllUserIds();
            if (userIds != null && !userIds.isEmpty()) {
                accountWalletService.deleteAllAccountWallet();
                System.out.println("delete account wallet success");
                System.out.println("userIds size: " + userIds.size());
                for (int i = 0; i < userIds.size(); i++) {
                    System.out.println("userId: index: " + i + " - id: " + userIds.get(i));
                    //
                    // insert account wallet
                    UUID accountWalletUUID = UUID.randomUUID();
                    AccountWalletEntity accountWalletEntity = new AccountWalletEntity();
                    accountWalletEntity.setId(accountWalletUUID.toString());
                    accountWalletEntity.setUserId(userIds.get(i));
                    accountWalletEntity.setAmount("100000");
                    accountWalletEntity.setEnableService(true);
                    accountWalletEntity.setActive(true);
                    accountWalletEntity.setPoint(0);
                    // set wallet ID
                    String walletId = "";
                    do {
                        walletId = RandomCodeUtil.generateRandomId(12); // Tạo mã ngẫu nhiên
                    } while (accountWalletService.checkExistedWalletId(walletId) != null);
                    accountWalletEntity.setWalletId(walletId);
                    // set sharing code
                    String sharingCode = "";
                    do {
                        sharingCode = RandomCodeUtil.generateRandomId(12); // Tạo mã ngẫu nhiên
                    } while (accountWalletService.checkExistedSharingCode(sharingCode) != null);
                    accountWalletEntity.setSharingCode(sharingCode);
                    accountWalletService.insertAccountWallet(accountWalletEntity);
                }
            }
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("ERROR at updateAccountWalletTable: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }
}
