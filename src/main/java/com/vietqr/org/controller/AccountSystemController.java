package com.vietqr.org.controller;

import com.vietqr.org.dto.*;
import com.vietqr.org.entity.*;
import com.vietqr.org.security.JWTAuthorizationFilter;
import com.vietqr.org.service.*;
import com.vietqr.org.util.DateTimeUtil;
import com.vietqr.org.util.EmailUtil;
import com.vietqr.org.util.RandomCodeUtil;
import com.vietqr.org.util.StringUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @Autowired
    private AccountSettingService accountSettingService;

    @Autowired
    private AccountWalletService accountWalletService;

    @Autowired
    private AccountInformationService accountInformationService;

    @Autowired
    private MobileCarrierService mobileCarrierService;

    @Autowired
    private EmailVerifyService emailVerifyService;

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
    public ResponseEntity<ResponseMessageDTO> resetPassword(@RequestParam("phoneNo") String phoneNo,
                                                            @RequestBody PasswordResetDTO passwordResetDTO,
                                                            @RequestHeader("Authorization") String token) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (passwordResetDTO != null) {
                IAccountSystemDTO adminDto = validateAdminToken(token);
                if (adminDto != null) {
                    boolean isReset = accountSystemService.resetUserPassword(phoneNo, passwordResetDTO.getNewPassword());
                    if (isReset) {
                        result = new ResponseMessageDTO("SUCCESS", "");
                        httpStatus = HttpStatus.OK;
                    } else {
                        logger.error(" Failed to reset password");
                        result = new ResponseMessageDTO("FAILED", "E142");
                        httpStatus = HttpStatus.BAD_REQUEST;
                    }
                } else {
                    result = new ResponseMessageDTO("FAILED", "E144");
                    httpStatus = HttpStatus.UNAUTHORIZED;
                }
            } else {
                result = new ResponseMessageDTO("FAILED", "E144");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("Fail at AccountSystemController : Error at resetPassword: " + e.getMessage() + "at " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "Error occurred");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
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


    @PostMapping("admin/account-create")
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            // Check if the phone number already exists
            boolean phoneExists = isPhoneNoValid(userRequestDTO.getPhoneNo());
            if (phoneExists) {
                result = new ResponseMessageDTO("FAILED", "E144");
                httpStatus = HttpStatus.BAD_REQUEST;
            } else {
                // Proceed with user creation
                String id = UUID.randomUUID().toString();
                String userId = UUID.randomUUID().toString();
                String loginId = UUID.randomUUID().toString();
                String accountSettingUUID = UUID.randomUUID().toString();
                String accountWalletUUID = UUID.randomUUID().toString();

                // Create AccountLoginEntity
                AccountLoginEntity accountLoginEntity = new AccountLoginEntity();
                accountLoginEntity.setId(loginId);
                accountLoginEntity.setPhoneNo(userRequestDTO.getPhoneNo());
                accountLoginEntity.setTime(DateTimeUtil.getCurrentDateTimeUTC());
                accountLoginEntity.setSyncBitrix(false);
                accountLoginEntity.setPassword(userRequestDTO.getPassword());
                accountLoginEntity.setStatus(true);
                accountLoginEntity.setCardNumber("");
                accountLoginEntity.setCardNfcNumber("");
                accountLoginEntity.setEmail(StringUtil.getValueNullChecker(userRequestDTO.getEmail()));
                accountLoginEntity.setIsVerify(false);

                // Create AccountInformationEntity
                AccountInformationEntity accountInformationEntity = new AccountInformationEntity();
                accountInformationEntity.setId(id);
                accountInformationEntity.setUserId(accountLoginEntity.getId());
                accountInformationEntity.setFirstName(StringUtil.getValueNullChecker(userRequestDTO.getFirstName()));
                accountInformationEntity.setMiddleName(StringUtil.getValueNullChecker(userRequestDTO.getMiddleName()));
                accountInformationEntity.setLastName(StringUtil.getValueNullChecker(userRequestDTO.getLastName()));
                accountInformationEntity.setBirthDate("");
                accountInformationEntity.setImgId("");
                accountInformationEntity.setRegisterPlatform("");
                accountInformationEntity.setUserIp("");
                accountInformationEntity.setAddress(StringUtil.getValueNullChecker(userRequestDTO.getAddress()));
                accountInformationEntity.setGender(StringUtil.getValueNullChecker(userRequestDTO.getGender()));
                accountInformationEntity.setEmail(StringUtil.getValueNullChecker(userRequestDTO.getEmail()));
                accountInformationEntity.setNationalId(StringUtil.getValueNullChecker(userRequestDTO.getNationalId()));
                accountInformationEntity.setOldNationalId(StringUtil.getValueNullChecker(userRequestDTO.getOldNationalId()));
                accountInformationEntity.setNationalDate(StringUtil.getValueNullChecker(userRequestDTO.getNationalDate()));

                // Set carrier type id
                if (userRequestDTO.getPhoneNo() != null && !userRequestDTO.getPhoneNo().trim().isEmpty()) {
                    String prefix = userRequestDTO.getPhoneNo().substring(0, 3);
                    String carrierTypeId = mobileCarrierService.getTypeIdByPrefix(prefix);
                    if (carrierTypeId != null) {
                        accountInformationEntity.setCarrierTypeId(carrierTypeId);
                    } else {
                        accountInformationEntity.setCarrierTypeId("");
                    }
                } else {
                    accountInformationEntity.setCarrierTypeId("");
                }
                accountInformationEntity.setStatus(true);

                // Create AccountSettingEntity
                AccountSettingEntity accountSettingEntity = new AccountSettingEntity();
                accountSettingEntity.setId(accountSettingUUID);
                accountSettingEntity.setGuideMobile(false);
                accountSettingEntity.setGuideWeb(false);
                accountSettingEntity.setStatus(true);
                accountSettingEntity.setVoiceMobile(true);
                accountSettingEntity.setVoiceMobileKiot(true);
                accountSettingEntity.setVoiceWeb(true);
                accountSettingEntity.setUserId(accountLoginEntity.getId());
                accountSettingEntity.setLastLogin(DateTimeUtil.getCurrentDateTimeUTC());
                accountSettingEntity.setAccessCount(1);
                accountSettingEntity.setEdgeImgId("");
                accountSettingEntity.setFooterImgId("");
                accountSettingEntity.setThemeType(1);
                accountSettingEntity.setKeepScreenOn(false);
                accountSettingEntity.setQrShowType(0);
                accountSettingEntity.setNotificationMobile(true);

                // Save AccountSettingEntity

                // Create AccountWalletEntity
                AccountWalletEntity accountWalletEntity = new AccountWalletEntity();
                accountWalletEntity.setId(accountWalletUUID);
                accountWalletEntity.setUserId(accountLoginEntity.getId());
                accountWalletEntity.setAmount("0");
                accountWalletEntity.setEnableService(true);
                accountWalletEntity.setActive(true);
                accountWalletEntity.setPoint(50);

                // Set wallet ID
                String walletId = "";
                do {
                    walletId = RandomCodeUtil.generateRandomId(12); // Generate random code
                } while (accountWalletService.checkExistedWalletId(walletId) != null);
                accountWalletEntity.setWalletId(walletId);

                // Set sharing code
                String sharingCode = "";
                do {
                    sharingCode = RandomCodeUtil.generateRandomId(12); // Generate random code
                } while (accountWalletService.checkExistedSharingCode(sharingCode) != null);
                accountWalletEntity.setSharingCode(sharingCode);

                // Save AccountWalletEntity
                accountLoginService.insertAccountLogin(accountLoginEntity);
                accountSettingService.insertAccountSetting(accountSettingEntity);
                accountWalletService.insertAccountWallet(accountWalletEntity);
                accountInformationService.insertAccountInformation(accountInformationEntity);
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            }
        } catch (Exception e) {
            logger.error("Failed at AccountSystemController: Error at createUser: " + e.getMessage() + "at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PutMapping("admin/account-status/{id}")
    public ResponseEntity<ResponseMessageDTO> updateUserStatus(@PathVariable String id, @RequestParam boolean status) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            accountSystemService.updateUserStatus(id, status);
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("Failed at AccountSystemController : Error at updateUserStatus: " + e.getMessage() + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PutMapping("admin/account-update/{userId}")
    public ResponseEntity<ResponseMessageDTO> updateUser(@PathVariable String userId, @Valid @RequestBody UserUpdateRequestDTO userUpdateRequestDTO) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus;
        try {
            accountSystemService.updateUser(userId, userUpdateRequestDTO);

            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("Failed at AccountSystemController:  Error at updateUser: " + e.getMessage() + "at" + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PutMapping("admin/account-email/{userId}")
    public ResponseEntity<ResponseMessageDTO> updateAccountEmail(@PathVariable(value = "userId") String userId, @Valid @RequestBody AccountUpdateEmailDTO accountUpdateEmailDTO) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            boolean isVerified = accountLoginService.getVerifyEmailStatus(userId);
            if (!isVerified) {
                result = new ResponseMessageDTO("FAILED", "E207");
                httpStatus = HttpStatus.BAD_REQUEST;
            } else {
                switch (accountUpdateEmailDTO.getType()) {
                    // Without OTP sending
                    case 0:
                        if (EmailUtil.isVerified(accountUpdateEmailDTO.getEmail())) {
                            accountSystemService.updateAccountEmail(userId, accountUpdateEmailDTO.getEmail(), accountUpdateEmailDTO.getType());
                            result = new ResponseMessageDTO("SUCCESS", "");
                            httpStatus = HttpStatus.OK;
                        } else {
                            result = new ResponseMessageDTO("FAILED", "E206");
                            httpStatus = HttpStatus.BAD_REQUEST;
                        }
                        break;
                    // With OTP sending
                    case 1:
                        String email = "";
                        if (accountUpdateEmailDTO.getEmail().isEmpty()) {
                            email = accountInformationService.getEmailByUserId(userId);
                        } else if (EmailUtil.isVerified(accountUpdateEmailDTO.getEmail())) {
                            email = accountUpdateEmailDTO.getEmail();
                        } else {
                            result = new ResponseMessageDTO("FAILED", "E206");
                            httpStatus = HttpStatus.BAD_REQUEST;
                            break;
                        }

                        // Check OTP
                        switch (handleOTP(userId, email, accountUpdateEmailDTO.getOtp())) {
                            case 0:
                                // Có vấn đề xảy ra khi gửi OTP
                                result = new ResponseMessageDTO("FAILED", "E21");
                                httpStatus = HttpStatus.BAD_REQUEST;
                                break;
                            case 1:
                                // Success
                                accountSystemService.updateAccountEmail(userId, accountUpdateEmailDTO.getEmail(), accountUpdateEmailDTO.getType());
                                emailVerifyService.updateEmailVerifiedByUserId(userId, accountUpdateEmailDTO.getOtp());
                                result = new ResponseMessageDTO("SUCCESS", "");
                                httpStatus = HttpStatus.OK;
                                break;
                            case 2:
                                // OTP không chính xác
                                result = new ResponseMessageDTO("FAILED", "E177");
                                httpStatus = HttpStatus.BAD_REQUEST;
                                break;
                            case 3:
                                // OTP hết hiệu lực
                                result = new ResponseMessageDTO("FAILED", "E175");
                                httpStatus = HttpStatus.BAD_REQUEST;
                                break;
                            default:
                                // OTP đã bị vô hiệu hóa
                                result = new ResponseMessageDTO("FAILED", "E178");
                                httpStatus = HttpStatus.BAD_REQUEST;
                                break;
                        }
                        break;
                    default:
                        result = new ResponseMessageDTO("FAILED", "E46");
                        httpStatus = HttpStatus.BAD_REQUEST;
                        break;
                }
            }
        } catch (Exception e) {
            logger.error("Failed at AccountSystemController:  Error at updateAccountEmail: " + e.getMessage() + "at" + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity<>(result, httpStatus);
    }

    private int handleOTP(String userId, String email, int otp) {
        int result = 0;
        Optional<EmailVerifyEntity> emailVerifyEntity = emailVerifyService.getEmailVerifyByUserEmail(userId, email);
        if (emailVerifyEntity.isPresent()) {
            EmailVerifyEntity entity = emailVerifyEntity.get();
            if (entity.getTimeVerified() > DateTimeUtil.getCurrentDateTimeUTC()) {
                if (otp == entity.getOtp()) {
                    result = 1;
                } else {
                    result = 2;
                }
            } else {
                if (otp == entity.getOtp()) {
                    result = 3;
                } else {
                    if (emailVerifyService.existsOTP(userId, email, otp)) {
                        result = 4;
                    }
                }
            }
        }

        return result;
    }
}
