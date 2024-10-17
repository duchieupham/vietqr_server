package com.vietqr.org.controller;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vietqr.org.dto.AccountCheckDTO;
import com.vietqr.org.dto.AccountSmsLoginDTO;
import com.vietqr.org.dto.AccountSmsRegisterDTO;
import com.vietqr.org.dto.AccountSmsSearchDTO;
import com.vietqr.org.dto.LogoutDTO;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.entity.AccountSmsEntity;
import com.vietqr.org.entity.FcmTokenSmsEntity;
import com.vietqr.org.service.AccountSmsService;
import com.vietqr.org.service.FcmTokenSmsService;
import com.vietqr.org.service.MobileCarrierService;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class AccountSmsController {
    private static final Logger logger = Logger.getLogger(AccountSmsController.class);

    @Autowired
    AccountSmsService accountSmsService;

    @Autowired
    MobileCarrierService mobileCarrierService;

    @Autowired
    FcmTokenSmsService fcmTokenSmsService;

    @PostMapping("accounts-sms")
    public ResponseEntity<String> loginSms(@RequestBody AccountSmsLoginDTO dto) {
        String result = "";
        HttpStatus httpStatus = null;
        try {
            String id = "";
            if (dto.getPhoneNo() != null && !dto.getPhoneNo().isEmpty()) {
                id = accountSmsService.loginSms(dto.getPhoneNo(), dto.getPassword());
            }
            if (id != null && !id.isEmpty()) {
                AccountSmsEntity entity = accountSmsService.getAccountSmsById(id);
                if (entity != null) {
                    updateAccessSmsLogin(id);
                    UUID uuid = UUID.randomUUID();
                    FcmTokenSmsEntity fcmTokenSmsEntity = new FcmTokenSmsEntity();
                    fcmTokenSmsEntity.setId(uuid.toString());
                    fcmTokenSmsEntity.setSmsId(id);
                    fcmTokenSmsEntity.setDevice(dto.getDevice());
                    fcmTokenSmsEntity.setToken(dto.getFcmToken());
                    fcmTokenSmsService.insert(fcmTokenSmsEntity);
                    result = getJWTToken(entity);
                    httpStatus = HttpStatus.OK;
                } else {
                    logger.error("LOGIN SMS: Cannot find user Id");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }

            } else {
                logger.error("LOGIN SMS: Cannot find user Id");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("LOGIN SMS: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    void updateAccessSmsLogin(String id) {
        try {
            Long currentCount = accountSmsService.getAccessCountById(id);
            long accessCount = 0;
            if (currentCount != null) {
                accessCount = currentCount + 1;
            }
            LocalDateTime currentDateTime = LocalDateTime.now();
            long time = currentDateTime.toEpochSecond(ZoneOffset.UTC);
            accountSmsService.updateAccessLoginSms(time, accessCount, id);
        } catch (Exception e) {
            logger.error("updateAccessSmsLogin: ERROR: " + e.toString());
        }
    }

    @PostMapping("accounts-sms/register")
    public ResponseEntity<ResponseMessageDTO> registerAccountSms(@RequestBody AccountSmsRegisterDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            AccountCheckDTO accountCheckDTO = accountSmsService.checkExistedPhoneNo(dto.getPhoneNo());
            if (accountCheckDTO == null) {
                // insert new account
                UUID uuid = UUID.randomUUID();
                AccountSmsEntity entity = new AccountSmsEntity();
                entity.setId(uuid.toString());
                entity.setPhoneNo(dto.getPhoneNo());
                entity.setPassword(dto.getPassword());
                entity.setStatus(true);
                entity.setEmail(dto.getEmail());
                entity.setCardNumber("");
                String fullname = dto.getFullName().trim();
                if (fullname.trim().isEmpty()) {
                    fullname = "Undefined";
                }
                entity.setFullName(fullname);
                entity.setImgId("");
                if (dto.getPhoneNo() != null && !dto.getPhoneNo().trim().isEmpty()) {
                    String prefix = dto.getPhoneNo().substring(0, 3);
                    String carrierTypeId = mobileCarrierService.getTypeIdByPrefix(prefix);
                    if (carrierTypeId != null) {
                        entity.setCarrierTypeId(carrierTypeId);
                    } else {
                        entity.setCarrierTypeId("");
                    }
                } else {
                    entity.setCarrierTypeId("");
                }
                entity.setUserIp(dto.getUserIp());
                entity.setAccessCount(0);
                entity.setLastLogin(0);
                LocalDateTime currentDateTime = LocalDateTime.now();
                long time = currentDateTime.toEpochSecond(ZoneOffset.UTC);
                entity.setTime(time);
                entity.setVoiceSms(true);
                accountSmsService.insert(entity);
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            } else if (accountCheckDTO != null && accountCheckDTO.getStatus() == true) {
                result = new ResponseMessageDTO("FAILED", "E02");
                httpStatus = HttpStatus.BAD_REQUEST;
            } else if (accountCheckDTO != null && accountCheckDTO.getStatus() == false) {
                accountSmsService.updateStatus(1, accountCheckDTO.getId());
                accountSmsService.updatePassword(dto.getPassword(), accountCheckDTO.getId());
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            }
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("accounts-sms/search/{phoneNo}")
    public ResponseEntity<Object> searchAccount(@Valid @PathVariable("phoneNo") String phoneNo) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            AccountSmsSearchDTO dto = accountSmsService.getAccountSmsSearch(phoneNo);
            if (dto == null) {
                result = new ResponseMessageDTO("CHECK", "C01");
                httpStatus = HttpStatus.valueOf(201);
            } else {
                result = dto;
                httpStatus = HttpStatus.OK;
            }

        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<Object>(result, httpStatus);
    }

    @PostMapping("accounts-sms/logout")
    public ResponseEntity<ResponseMessageDTO> logout(@Valid @RequestBody LogoutDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            fcmTokenSmsService.deleteFcmTokenSms(dto.getFcmToken());
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    private String getJWTToken(AccountSmsEntity entity) {
        String result = "";
        String secretKey = "mySecretKey";
        List<GrantedAuthority> grantedAuthorities = AuthorityUtils
                .commaSeparatedStringToAuthorityList("ROLE_USER");
        result = Jwts
                .builder()
                .claim("smsId", entity.getId())
                .claim("phoneNo", entity.getPhoneNo())
                .claim("fullName", entity.getFullName())
                .claim("imgId", entity.getImgId())
                .claim("email", entity.getEmail())
                .claim("carrierTypeId", entity.getCarrierTypeId())
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
