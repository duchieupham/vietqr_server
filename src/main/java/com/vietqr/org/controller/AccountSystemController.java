package com.vietqr.org.controller;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vietqr.org.dto.AccountSystemDTO;
import com.vietqr.org.entity.AccountSystemEntity;
import com.vietqr.org.service.AccountSystemService;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class AccountSystemController {
    private static final Logger logger = Logger.getLogger(AccountSystemController.class);

    @Autowired
    AccountSystemService accountSystemService;

    @PostMapping("accounts-admin")
    public ResponseEntity<String> loginAdmin(@RequestBody AccountSystemDTO dto) {
        String result = "";
        HttpStatus httpStatus = null;
        try {
            if (dto != null) {
                AccountSystemEntity entity = accountSystemService.loginAdmin(dto.getUsername(), dto.getPassword());
                if (entity != null) {
                    String token = getJWTToken(entity);
                    result = token;
                    httpStatus = HttpStatus.OK;
                }
            }
        } catch (Exception e) {
            logger.error("Error at loginAdmin: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    private String getJWTToken(AccountSystemEntity entity) {
        String result = "";
        String secretKey = "VietQRAdminSecretKey";
        List<GrantedAuthority> grantedAuthorities = AuthorityUtils
                .commaSeparatedStringToAuthorityList("ROLE_USER");
        result = Jwts
                .builder()
                .claim("adminId", entity.getId())
                .claim("name", entity.getName())
                .claim("role", entity.getRole())
                .claim("authorities",
                        grantedAuthorities.stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 900000000))
                .signWith(SignatureAlgorithm.HS512,
                        secretKey.getBytes())
                .compact();
        return result;
    }
}
