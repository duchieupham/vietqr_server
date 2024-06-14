package com.vietqr.org.controller;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.vietqr.org.dto.*;
import com.vietqr.org.entity.AccountInformationEntity;
import com.vietqr.org.security.JWTAuthorizationFilter;
import com.vietqr.org.service.AccountLoginService;
import io.jsonwebtoken.Claims;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.vietqr.org.entity.AccountSystemEntity;
import com.vietqr.org.service.AccountSystemService;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.validation.Valid;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class AccountSystemController {
    private static final Logger logger = Logger.getLogger(AccountSystemController.class);

    @Autowired
    AccountSystemService accountSystemService;

    @Autowired
    AccountLoginService accountLoginService;
    @Autowired
    private JWTAuthorizationFilter jwtAuthorizationFilter;

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
                } else {
                    result = "";
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            }
        } catch (Exception e) {
            logger.error("Error at loginAdmin: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }
    @PostMapping("/password-reset")
    public ResponseEntity<ResponseMessageDTO> resetPassword(@RequestParam("phoneNo") String phoneNo, @RequestBody PasswordResetDTO passwordResetDTO, @RequestHeader("Authorization") String token) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (passwordResetDTO != null && isPasswordValid(passwordResetDTO.getNewPassword())) {
                IAccountSystemDTO adminDto = validateAdminToken(token);
                if (adminDto != null) {
                    if (isPhoneNoValid(phoneNo)) {
                        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                        String hashedPassword = passwordEncoder.encode(passwordResetDTO.getNewPassword());
                        boolean isReset = accountSystemService.resetUserPassword(phoneNo, hashedPassword);
                        if (isReset) {
                            result = new ResponseMessageDTO("SUCCESS", "");
                            httpStatus = HttpStatus.OK;
                        } else {
                            logger.error("Failed to reset password");
                            result = new ResponseMessageDTO("FAILED", "E142");
                            httpStatus = HttpStatus.BAD_REQUEST;
                        }
                    } else {
                        logger.error("Invalid phone number");
                        result = new ResponseMessageDTO("FAILED", "E143");
                        httpStatus = HttpStatus.BAD_REQUEST;
                    }
                } else {
                    result = new ResponseMessageDTO("FAILED", "Unauthorized");
                    httpStatus = HttpStatus.UNAUTHORIZED;
                }
            } else {
                logger.error("Invalid password format");
                result = new ResponseMessageDTO("FAILED", "E144");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("Error at resetPassword: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "Error occurred");
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(result, httpStatus);
    }


    private boolean isPasswordValid(String password) {
        return password != null && password.matches("\\d{6}");
    }

    private IAccountSystemDTO validateAdminToken(String token) {
        try {
            Claims claims = jwtAuthorizationFilter.validateToken(token.replace("Bearer ", ""));
            String adminId = claims.get("adminId", String.class);
            Integer role = claims.get("role", Integer.class);
            if (role != null && role == 1) {
                return accountSystemService.findAdminById(adminId);
            } else {
                return null;
            }
        } catch (Exception e) {
            logger.error("Invalid token: " + e.toString());
            return null;
        }
    }
    private boolean isPhoneNoValid(String phoneNo) {
        return accountLoginService.isPhoneNoExists(phoneNo);
    }

    private String getJWTToken(AccountSystemEntity entity) {
        String result = "";
        String secretKey = "mySecretKey";
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


    @PostMapping("admin/create")
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            // Check if the phone number already exists
            boolean phoneExists = isPhoneNoValid(userRequestDTO.getPhoneNo());
            if (phoneExists) {
                result = new ResponseMessageDTO("FAILED", "Phone number already exists");
                httpStatus = HttpStatus.CONFLICT;
            } else {
                // Proceed with user creation
                result = accountSystemService.createUser(userRequestDTO);
                httpStatus = HttpStatus.OK;
            }
        } catch (Exception e) {
            logger.error("Error at createUser: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E04");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PutMapping("admin/{id}/status")
    public ResponseEntity<ResponseMessageDTO> updateUserStatus(@PathVariable String id, @RequestParam boolean status) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            boolean isUpdated = accountSystemService.updateUserStatus(id, status);
            if (isUpdated) {
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            } else {
                logger.error("User not found or status is already " + status);
                result = new ResponseMessageDTO("FAILED", "User not found or status is already " + status);
                httpStatus = HttpStatus.NOT_FOUND;
            }
        } catch (Exception e) {
            logger.error("Error at updateUserStatus: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "Error occurred");
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(result, httpStatus);
    }




}
