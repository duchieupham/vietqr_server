package com.vietqr.org.controller;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.vietqr.org.dto.*;
import com.vietqr.org.service.*;
import com.vietqr.org.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.vietqr.org.entity.AccountInformationEntity;
import com.vietqr.org.entity.AccountLoginEntity;
import com.vietqr.org.entity.AccountSettingEntity;
import com.vietqr.org.entity.AccountShareEntity;
import com.vietqr.org.entity.AccountWalletEntity;
import com.vietqr.org.entity.CustomerSyncEntity;
import com.vietqr.org.entity.FcmTokenEntity;
import com.vietqr.org.entity.ImageEntity;
import com.vietqr.org.entity.LarkWebhookPartnerEntity;
import com.vietqr.org.entity.NotificationEntity;
import com.vietqr.org.entity.SystemSettingEntity;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import reactor.core.publisher.Mono;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class AccountController {
    private static final Logger logger = Logger.getLogger(AccountController.class);

    @Autowired
    AccountLoginService accountLoginService;

    @Autowired
    AccountInformationService accountInformationService;

    @Autowired
    ImageService imageService;

    @Autowired
    FcmTokenService fcmTokenService;

    @Autowired
    NotificationService notificationService;

    @Autowired
    AccountBankReceiveService accountBankReceiveService;

    @Autowired
    AccountSettingService accountSettingService;

    @Autowired
    private SocketHandler socketHandler;

    @Autowired
    CustomerSyncService customerSyncService;

    @Autowired
    AccountWalletService accountWalletService;

    @Autowired
    AccountShareService accountShareService;

    @Autowired
    MobileCarrierService mobileCarrierService;

    @Autowired
    SystemSettingService systemSettingService;

    @Autowired
    LarkWebhookPartnerService larkWebhookPartnerService;

    @Autowired
    TelegramService telegramService;

    @GetMapping("account/count-registered-today")
    public ResponseEntity<Object> countAccountsRegisteredInDay() {
        Object result = null;
        HttpStatus httpStatus = null;
        AccountCountDTO accountCountDTO = new AccountCountDTO();
        try {
            //set total user today
            long countTotalUsers = accountLoginService.getTotalUsers();

            //set total user today
            StartEndTimeDTO startEndTime = DateTimeUtil.getStartEndCurrentDate();
            long countUseToday = accountLoginService
                    .countAccountsRegisteredInDay(
                            startEndTime.getStartTime(), startEndTime.getEndTime());

            accountCountDTO.setTotalUsers(countTotalUsers);
            accountCountDTO.setTotalUserRegisterToday(countUseToday);

            result = accountCountDTO;
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("AccountLoginController: ERROR: countAccounts: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    //get user details
    @GetMapping("account/users-details")
    public ResponseEntity<Object> getUserDetails(@RequestParam String userId) {
        Object result = null;
        HttpStatus httpStatus = null;
        UserDetailResponseDTO data = new UserDetailResponseDTO();
        try {
            // initialize data
            List<UserInfoDTO> userInfoData = new ArrayList<>();        // fix IUserInfoDTO
            IUserInfoDTO userInfos = null;
            List<BankInfoDTO> bankInfoData = new ArrayList<>();        // fix IBankInfoDTO
            List<IBankInfoDTO> bankInfo = new ArrayList<>();
            List<BankShareDTO> bankShareData = new ArrayList<>();      // fix IBankShareDTO
            List<IBankShareDTO> bankShareInfo = new ArrayList<>();
            List<SocialMediaDTO> socialMediaData = new ArrayList<>();  // fix ISocialMediaDTO
            List<ISocialMediaDTO> socialMediaInfo = new ArrayList<>();
            IBalanceAndScoreDTO balanceAndScoreDTO = null;

            // call service
            // user info
            userInfos = accountLoginService.getUserInfoDetailsByUserId(userId);

            // bank info
            bankInfo = accountBankReceiveService.getBankInfoByUserId(userId);
            bankInfoData = bankInfo.stream().map(item -> {
                BankInfoDTO dto = new BankInfoDTO();
                dto.setBankAccount(item.getBankAccount());
                dto.setBankAccountName(item.getBankAccountName());
                dto.setStatus(item.getStatus());
                dto.setMmsActive(item.getMmsActive());
                dto.setPhoneAuthenticated(item.getPhoneAuthenticated());
                dto.setActiveService(item.getActiveService());
                dto.setBankShortName(item.getBankShortName());
                dto.setNationalId(item.getNationalId());
                dto.setFromDate(item.getFromDate());
                dto.setToDate(item.getToDate());
                // Tính toán số tháng giữa hai LocalDateTime
                LocalDateTime fromDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(item.getFromDate() * 1000L), ZoneId.systemDefault());
                LocalDateTime toDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(item.getToDate() * 1000L), ZoneId.systemDefault());
                int changeMonth = (int) ChronoUnit.MONTHS.between(fromDateTime, toDateTime);
                dto.setActiveService(changeMonth);
                return dto;
            }).collect(Collectors.toList());

            // bank share info
            bankShareInfo = accountBankReceiveService.getBankShareInfoByUserId(userId);
            bankShareData = bankShareInfo.stream().map(items -> {
                BankShareDTO dto2 = new BankShareDTO();
                dto2.setBankAccount(items.getBankAccount());
                dto2.setBankAccountName(items.getBankAccountName());
                dto2.setStatus(items.getStatus());
                dto2.setMmsActive(items.getMmsActive());
                dto2.setPhoneAuthenticated(items.getPhoneAuthenticated());
                dto2.setActiveService(items.getActiveService());
                dto2.setBankShortName(items.getBankShortName());
                dto2.setNationalId(items.getNationalId());
                dto2.setFromDate(items.getFromDate());
                dto2.setToDate(items.getToDate());
                dto2.setActiveService(items.getToDate() - items.getFromDate());
                return dto2;
            }).collect(Collectors.toList());

            // social media info
            socialMediaInfo = telegramService.getSocialInfoByUserId(userId);
            socialMediaData = socialMediaInfo.stream().map(item -> {
                SocialMediaDTO dto3 = new SocialMediaDTO();
                dto3.setPlatform(item.getPlatform());
                dto3.setChatId(item.getChatId());
                dto3.setAccountConnected(item.getAccountConnected());
                return dto3;
            }).collect(Collectors.toList());

            //set balance and score
            balanceAndScoreDTO = accountWalletService.getBalanceAndScore(userId);

            data.setUserInfo(userInfos);
            data.setBankInfo(bankInfoData);
            data.setBankShareInfo(bankShareData);
            data.setSocalMedia(socialMediaData);
            data.setBalance(balanceAndScoreDTO.getBalance());
            data.setScore(balanceAndScoreDTO.getScore());

            result = data;
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("AccountController: ERROR: getUserDetail:  " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // get list user account
    @GetMapping("account/admin-list-account-user")
    public ResponseEntity<Object> getListUserAccount(
            @RequestParam int type,
            @RequestParam String value,
            @RequestParam int page,
            @RequestParam int size) {
        Object result = null;
        HttpStatus httpStatus = null;
        PageResDTO pageResDTO = new PageResDTO();
        try {
            int totalElement = 0;
            int offset = (page - 1) * size;

            List<AdminListUserAccountResponseDTO> data = new ArrayList<>();
            List<IAdminListUserAccountResponseDTO> infos = new ArrayList<>();

            switch (type) {
                case 1:
                    infos = accountInformationService.getAdminListUsersAccount(value, offset, size);
                    totalElement = accountInformationService.countAdminListUsersAccountByName(value);
                    break;
                case 2:
                    infos = accountInformationService.getAdminListUsersAccountByPhone(value, offset, size);
                    totalElement = accountInformationService.countAdminListUsersAccountByPhone(value);
                default:
                    new ArrayList<>();
                    break;
            }

            data = infos.stream().map(item -> {
                AdminListUserAccountResponseDTO dto = new AdminListUserAccountResponseDTO();
                dto.setId(item.getUserId());
                dto.setAddress(StringUtil.getValueNullChecker(item.getAddress()));
                dto.setBirthDate(StringUtil.getValueNullChecker(item.getBirthDate()));
                dto.setEmail(StringUtil.getValueNullChecker(item.getEmail()));
                dto.setFirstName(StringUtil.getValueNullChecker(item.getFirstName()));
                dto.setMiddleName(StringUtil.getValueNullChecker(item.getMiddleName()));
                dto.setLastName(item.getLastName());
                // set full name
                dto.setFullName(item.getLastName() + " " + item.getMiddleName() + " " + item.getFirstName());
                dto.setGender(item.getGender());
                dto.setStatus(item.getStatus());
                dto.setUserIp(StringUtil.getValueNullChecker(item.getUserIp()));
                dto.setPhoneNo(StringUtil.getValueNullChecker(item.getPhoneNo()));
                dto.setGetTimeRegister(item.getTimeRegister());
                dto.setRegisterPlatform(StringUtil.getValueNullChecker(item.getRegisterPlatform()));
                dto.setNationalDate(StringUtil.getValueNullChecker(item.getNationalDate()));
                dto.setNationalId(StringUtil.getValueNullChecker(item.getNationalId()));
                dto.setOldNationalId(StringUtil.getValueNullChecker(item.getOldNationalId()));
                dto.setUserIdDetail(StringUtil.getValueNullChecker(item.getUserIdDetail()));
                dto.setBalance(item.getBalance());
                dto.setScore(item.getScore());
                return dto;
            }).collect(Collectors.toList());

            //set total user today
            long countTotalUsers = accountLoginService.getTotalUsers();

            //set total user today
            StartEndTimeDTO startEndTime = DateTimeUtil.getStartEndCurrentDate();
            long countUseToday = accountLoginService.countAccountsRegisteredInDay(startEndTime.getStartTime(), startEndTime.getEndTime());

            PageDTO pageDTO = new PageDTO();
            pageDTO.setSize(size);
            pageDTO.setPage(page);
            pageDTO.setTotalElement(totalElement);
            pageDTO.setTotalPage(StringUtil.getTotalPage(totalElement, size));

            pageResDTO.setMetadata(pageDTO);
            pageResDTO.setData(data);

            httpStatus = HttpStatus.OK;
            result = pageResDTO;
        } catch (Exception e) {
            logger.error("AccountController: ERROR: getUserListAdmin: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("accounts")
    public ResponseEntity<String> login(@RequestBody AccountLoginDTO dto) {
        String result = "";
        HttpStatus httpStatus = null;
        try {
            logger.info("Login: " + dto.toString());
            String userId = "";
            if (dto.getPhoneNo() != null && !dto.getPhoneNo().isEmpty()) {
                userId = accountLoginService.login(dto.getPhoneNo(), dto.getPassword());
            } else if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
                userId = accountLoginService.loginByEmail(dto.getEmail(), dto.getPassword());
            } else {
                logger.error("LOGIN: Phone number AND email is empty");
            }
            if (userId != null && !userId.isEmpty()) {
                // get user information
                AccountInformationEntity accountInformationEntity = accountInformationService
                        .getAccountInformation(userId);
                logger.info("Login: " + accountInformationEntity.toString());
                // push notification to other devices if user logged in before
                LocalDateTime currentDateTime = LocalDateTime.now();
                //
                String messageNotification = NotificationUtil.getNotiDescLoginWarningPrefix()
                        + dto.getPlatform() + " " + dto.getDevice();
                UUID notificationUuid = UUID.randomUUID();
                NotificationEntity notificationEntity = new NotificationEntity();
                notificationEntity.setId(notificationUuid.toString());
                notificationEntity.setRead(false);
                notificationEntity.setMessage(messageNotification);
                notificationEntity.setTime(currentDateTime.toEpochSecond(ZoneOffset.UTC));
                notificationEntity.setType(NotificationUtil.getNotiTypeLogin());
                notificationEntity.setUserId(userId);
                notificationService.insertNotification(notificationEntity);
                //
                // List<FcmTokenEntity> fcmTokens = new ArrayList<>();
                // fcmTokens = fcmTokenService.getFcmTokensByUserId(userId);
                // if (fcmTokens != null && !fcmTokens.isEmpty()) {
                // logger.info("FCM list size: " + fcmTokens.size());
                // // insert new Notification
                // String messageNotification = NotificationUtil.getNotiDescLoginWarningPrefix()
                // + dto.getPlatform() + " " + dto.getDevice();

                // // push notification to devices
                // for (FcmTokenEntity fcmToken : fcmTokens) {
                // try {
                // if (!fcmToken.getToken().trim().isEmpty()) {
                // FcmRequestDTO fcmDTO = new FcmRequestDTO();
                // fcmDTO.setTitle(NotificationUtil.getNotiTitleLoginWarning());
                // fcmDTO.setMessage(messageNotification);
                // fcmDTO.setToken(fcmToken.getToken());
                // firebaseMessagingService.sendPushNotificationToToken(fcmDTO);
                // logger.info("Send notification to device " + fcmToken.getToken());
                // }
                // } catch (Exception e) {
                // logger.error("Error when Send Notification using FCM " + e.toString());
                // if (e.toString()
                // .contains("The registration token is not a valid FCM registration token")) {
                // fcmTokenService.deleteFcmToken(fcmToken.getToken());
                // }

                // }
                // }
                // }

                // insert new FCM token
                FcmTokenEntity fcmTokenEntity = new FcmTokenEntity();
                UUID uuid = UUID.randomUUID();
                fcmTokenEntity.setId(uuid.toString());
                fcmTokenEntity.setToken(dto.getFcmToken());
                fcmTokenEntity.setUserId(userId);
                fcmTokenEntity.setPlatform(dto.getPlatform());
                fcmTokenEntity.setDevice(dto.getDevice());
                String checkToken = fcmTokenService.checkTokenExistByUserId(dto.getFcmToken(), userId);
                if (StringUtil.isNullOrEmpty(checkToken)) {
                    fcmTokenService.insertFcmToken(fcmTokenEntity);
                }
                String phoneNo = accountLoginService.getPhoneNoById(userId);
                // response login success
                if (dto.getHosting() != null && !dto.getHosting().trim().isEmpty()) {
                    //
                    // check customer_sync existed by userId
                    // do insert or update
                    String check = customerSyncService.checkExistedCustomerSync(userId);
                    String information = "";
                    if (check == null || check.trim().isEmpty()) {
                        UUID cusUuid = UUID.randomUUID();
                        CustomerSyncEntity customerSyncEntity = new CustomerSyncEntity();
                        customerSyncEntity.setId(cusUuid.toString());
                        customerSyncEntity.setUsername("");
                        customerSyncEntity.setPassword("");
                        customerSyncEntity.setIpAddress("");
                        customerSyncEntity.setPort("");
                        customerSyncEntity.setSuffixUrl("");
                        if (dto.getHosting().toUpperCase().contains("HTTP://") || dto.getHosting().toUpperCase()
                                .contains("HTTPS://")) {
                            information = dto.getHosting();
                        } else {
                            information = "https://" + dto.getHosting();
                        }
                        customerSyncEntity.setInformation(information);
                        customerSyncEntity.setUserId(userId);
                        customerSyncEntity.setActive(true);
                        customerSyncService.insertCustomerSync(customerSyncEntity);
                    } else {
                        if (dto.getHosting().toUpperCase().contains("HTTP://") || dto.getHosting().toUpperCase()
                                .contains("HTTPS://")) {
                            information = dto.getHosting();
                        } else {
                            information = "https://" + dto.getHosting();
                        }
                        customerSyncService.updateCustomerSyncInformation(information, userId);
                    }
                    result = getJWTInfinitiveToken(accountInformationEntity, phoneNo, information);
                } else {
                    result = getJWTToken(accountInformationEntity, phoneNo);
                }
                // update login access
                updateAccessLogin(userId);
                httpStatus = HttpStatus.OK;
            } else {
                logger.error("LOGIN: Cannot find user Id" + dto.toString());
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("Error at login: " + e.toString() + dto.toString() + " at: " + System.currentTimeMillis());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    void updateAccessLogin(String userId) {
        try {
            Long currentCount = accountSettingService.getAccessCountByUserId(userId);
            long accessCount = 0;
            if (currentCount != null) {
                accessCount = currentCount + 1;
            }
            LocalDateTime currentDateTime = LocalDateTime.now();
            long time = currentDateTime.toEpochSecond(ZoneOffset.UTC);
            accountSettingService.updateAccessLogin(time, accessCount, userId);
        } catch (Exception e) {
            System.out.println("updateAccessLogin: ERROR: " + e.toString());
            logger.error("updateAccessLogin: ERROR: " + e.toString());
        }
    }

    @PostMapping("accounts/logout")
    public ResponseEntity<ResponseMessageDTO> logout(@Valid @RequestBody LogoutDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            System.out.println("FCM TOKEN: " + dto.getFcmToken());
            fcmTokenService.deleteFcmToken(dto.getFcmToken());
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            System.out.println("Error at logout: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "Unexpected Error");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("accounts/search/{phoneNo}")
    public ResponseEntity<Object> searchAccount(@Valid @PathVariable("phoneNo") String phoneNo) {
        Object result = null;
        HttpStatus httpStatus = null;
        logger.info("accounts/search " + phoneNo);
        try {
            AccountSearchDTO dto = accountInformationService.getAccountSearch(phoneNo);
            if (dto == null) {
                logger.info("searchAccount: CHECK 01 " + phoneNo);
                result = new ResponseMessageDTO("CHECK", "C01");
                httpStatus = HttpStatus.valueOf(201);
            } else {
                logger.info("searchAccount: HAVE RESULT " + phoneNo);
                result = dto;
                httpStatus = HttpStatus.OK;
            }

        } catch (Exception e) {
            System.out.println("Error at searchAccount: " + e.toString());
            logger.error("Error at searchAccount: " + e.getMessage() + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<Object>(result, httpStatus);
    }

    @GetMapping("accounts/list/search/{phoneNo}")
    public ResponseEntity<List<AccountSearchDTO>> searchAccounts(@Valid @PathVariable("phoneNo") String phoneNo) {
        List<AccountSearchDTO> result = null;
        HttpStatus httpStatus = null;
        try {
            result = accountInformationService.getAccountsSearch(phoneNo);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            System.out.println("Error at searchAccounts: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("accounts/register")
    public ResponseEntity<ResponseMessageDTO> registerAccount(@RequestBody AccountLoginDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            AccountCheckDTO accountCheckDTO = accountLoginService.checkExistedPhoneNo(dto.getPhoneNo());
            if (accountCheckDTO == null) {
                UUID uuid = UUID.randomUUID();
                UUID accountInformationUUID = UUID.randomUUID();
                UUID accountSettingUUID = UUID.randomUUID();
                UUID accountWalletUUID = UUID.randomUUID();
                // insert account_login
                AccountLoginEntity accountLoginEntity = new AccountLoginEntity();
                accountLoginEntity.setId(uuid.toString());
                accountLoginEntity.setPhoneNo(dto.getPhoneNo());
                accountLoginEntity.setPassword(dto.getPassword());
                accountLoginEntity.setStatus(true);
                LocalDateTime currentDateTime = LocalDateTime.now();
                long time = currentDateTime.toEpochSecond(ZoneOffset.UTC);
                accountLoginEntity.setTime(time);
                accountLoginEntity.setSyncBitrix(false);
                accountLoginEntity.setIsVerify(false);
                accountLoginService.insertAccountLogin(accountLoginEntity);

                // insert account_information
                AccountInformationEntity accountInformationEntity = new AccountInformationEntity();
                accountInformationEntity.setId(accountInformationUUID.toString());
                accountInformationEntity.setUserId(uuid.toString());
                accountInformationEntity.setAddress("");
                accountInformationEntity.setBirthDate("01/01/1970");
                accountInformationEntity.setEmail("");
                accountInformationEntity.setFirstName("Undefined");
                accountInformationEntity.setMiddleName("");
                accountInformationEntity.setLastName("");
                accountInformationEntity.setGender(0);
                accountInformationEntity.setImgId("");
                accountInformationEntity.setRegisterPlatform(dto.getPlatform());
                accountInformationEntity.setUserIp(dto.getDevice());
                accountInformationEntity.setNationalId("");
                accountInformationEntity.setOldNationalId("");
                accountInformationEntity.setNationalDate("");

                // set carrier type id
                if (dto.getPhoneNo() != null && !dto.getPhoneNo().trim().isEmpty()) {
                    String prefix = dto.getPhoneNo().substring(0, 3);
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
                int check = accountInformationService.insertAccountInformation(accountInformationEntity);

                // insert account setting
                AccountSettingEntity accountSettingEntity = new AccountSettingEntity();
                accountSettingEntity.setId(accountSettingUUID.toString());
                accountSettingEntity.setGuideMobile(false);
                accountSettingEntity.setGuideWeb(false);
                accountSettingEntity.setStatus(true);
                accountSettingEntity.setVoiceMobile(true);
                accountSettingEntity.setVoiceMobileKiot(true);
                accountSettingEntity.setVoiceWeb(true);
                accountSettingEntity.setUserId(uuid.toString());
                accountSettingEntity.setLastLogin(time);
                accountSettingEntity.setAccessCount(1);
                accountSettingEntity.setEdgeImgId("");
                accountSettingEntity.setFooterImgId("");
                // default theme type
                accountSettingEntity.setThemeType(1);
                //
                accountSettingEntity.setKeepScreenOn(false);
                //
                // 0: rectangular
                // 1: square
                accountSettingEntity.setQrShowType(0);
                accountSettingEntity.setNotificationMobile(true);
                //
                accountSettingService.insertAccountSetting(accountSettingEntity);
                ///
                // insert account wallet
                AccountWalletEntity accountWalletEntity = new AccountWalletEntity();
                accountWalletEntity.setId(accountWalletUUID.toString());
                accountWalletEntity.setUserId(uuid.toString());
                accountWalletEntity.setAmount("0");
                accountWalletEntity.setEnableService(true);
                accountWalletEntity.setActive(true);
                accountWalletEntity.setPoint(50);
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
                ///
                // update point if sharing_code != null
                if (dto.getSharingCode() != null && !dto.getSharingCode().isEmpty()) {
                    // find sharingCode is Existed or not
                    String checkExisted = accountWalletService.checkExistedSharingCode(dto.getSharingCode());
                    if (checkExisted != null) {
                        // if existed, do update
                        accountWalletService.updatePointBySharingCode(10, dto.getSharingCode());
                        // insert account_share_entity
                        UUID accountShareId = UUID.randomUUID();
                        accountShareService.insertAccountShare(new AccountShareEntity(accountShareId.toString(),
                                dto.getSharingCode(), uuid.toString()));
                    }
                    // if not existed, do nothing
                    ///
                }
                // insert customer_sync
                if (dto.getHosting() != null && !dto.getHosting().trim().isEmpty()) {
                    UUID cusUuid = UUID.randomUUID();
                    CustomerSyncEntity customerSyncEntity = new CustomerSyncEntity();
                    customerSyncEntity.setId(cusUuid.toString());
                    customerSyncEntity.setUsername("");
                    customerSyncEntity.setPassword("");
                    customerSyncEntity.setIpAddress("");
                    customerSyncEntity.setPort("");
                    customerSyncEntity.setSuffixUrl("");
                    String information = "";
                    if (dto.getHosting().toUpperCase().contains("HTTP://") || dto.getHosting().toUpperCase()
                            .contains("HTTPS://")) {
                        information = dto.getHosting();
                    } else {
                        information = "https://" + dto.getHosting();
                    }
                    customerSyncEntity.setInformation(information);
                    customerSyncEntity.setUserId(uuid.toString());
                    customerSyncEntity.setActive(true);
                    customerSyncService.insertCustomerSync(customerSyncEntity);
                }
                if (check == 1) {
//                    LarkUtil larkUtil = new LarkUtil();
                    GoogleChatUtil googleChatUtil = new GoogleChatUtil();
                    String msgSharingCode = "";
                    if (dto.getSharingCode() != null && !dto.getSharingCode().trim().isEmpty()) {
                        msgSharingCode = "\nĐã nhập mã giới thiệu: " + dto.getSharingCode();
                    }
                    String larkMsg = "🙋‍♂️ Người dùng mới"
                            + "\nSố điện thoại: " + dto.getPhoneNo()
                            + "\nNền tảng: " + dto.getPlatform()
                            + "\nIP: " + dto.getDevice()
                            + msgSharingCode;
                    // SEND TO LARK VIETQR
                    SystemSettingEntity systemSettingEntity = systemSettingService.getSystemSetting();
//                    googleChatUtil.sendMessageToGoogleChat(larkMsg, systemSettingEntity.getWebhookUrl());
                    googleChatUtil.sendMessageToGoogleChat(larkMsg, systemSettingEntity.getWebhookUrl());
                    // SEND TO LARK PARTNER

                    List<LarkWebhookPartnerEntity> partners = new ArrayList<>();
                    partners = larkWebhookPartnerService.getLarkWebhookPartners();
                    if (partners != null && !partners.isEmpty()) {
                        for (LarkWebhookPartnerEntity partner : partners) {
                            if (partner.getWebhook() != null && !partner.getWebhook().trim().isEmpty()
                                    && partner.getActive() != null && partner.getActive() == true) {
                                try {
                                    googleChatUtil.sendMessageToGoogleChat(msgSharingCode, partner.getWebhook());
                                } catch (Exception e) {
                                    logger.error("registerAccount - send lark to customer: " + partner.getWebhook()
                                            + " - " + e.toString());
                                }
                            }
                        }
                    }

                    result = new ResponseMessageDTO("SUCCESS", "");
                    httpStatus = HttpStatus.OK;
                } else {
                    result = new ResponseMessageDTO("FAILED", "E03");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else if (accountCheckDTO != null && accountCheckDTO.getStatus() == true) {
                result = new ResponseMessageDTO("FAILED", "E02");
                httpStatus = HttpStatus.BAD_REQUEST;
            } else if (accountCheckDTO != null && accountCheckDTO.getStatus() == false) {
                // update status account_login and account information = 1
                accountLoginService.updateStatus(1, accountCheckDTO.getId());
                accountLoginService.updatePassword(dto.getPassword(), accountCheckDTO.getId());
                accountInformationService.udpateStatus(1, accountCheckDTO.getId());
                accountBankReceiveService.updateStatusAccountBankByUserId(1, accountCheckDTO.getId());
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            }
        } catch (Exception e) {
            logger.error("Error at registerAccount: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E04");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // push data from mobile to server to send login information
    @PostMapping("accounts/push")
    public ResponseEntity<ResponseMessageDTO> sendDataToLogin(@RequestBody AccountPushLoginDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            Map<String, String> data = new HashMap<>();
            data.put("loginId", dto.getLoginId());
            data.put("userId", dto.getUserId());
            data.put("randomKey", dto.getRandomKey());
            result = new ResponseMessageDTO("SUCCESS", "");
            socketHandler.sendMessageLoginToWeb(dto.getLoginId(), data);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("sendDataToLogin: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // push data from mobile to server to send login information
    @PostMapping("accounts/push/ec")
    public ResponseEntity<ResponseMessageDTO> sendDataToEcLogin(@RequestBody AccountPushLoginDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            Map<String, String> data = new HashMap<>();
            data.put("ecLoginId", dto.getLoginId());
            data.put("randomKey", dto.getRandomKey());
            AccountInformationEntity accountInformationEntity = accountInformationService
                    .getAccountInformation(dto.getUserId());
            String token = getJWTInfinitiveToken(accountInformationEntity, dto.getPhoneNo(), dto.getUrl());
            data.put("token", token);
            result = new ResponseMessageDTO("SUCCESS", "");
            socketHandler.sendMessageEcLoginToWeb(dto.getLoginId(), data);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("sendDataToLogin: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("accounts/cardNumber")
    public ResponseEntity<ResponseMessageDTO> updateCardNumberLogin(
            @RequestBody AccountCardNumberUpdateDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null) {
                // check card number existed
                String checkExisted = "";
                if (dto.getCardNumber() != null && !dto.getCardNumber().isEmpty()) {
                    if (dto.getCardType() == null || dto.getCardType().trim().toUpperCase().equals("CARD")) {
                        checkExisted = accountLoginService.checkExistedCardNumber(dto.getCardNumber());
                    } else if (dto.getCardType() == null
                            || dto.getCardType().trim().toUpperCase().equals("NFC_CARD")) {
                        checkExisted = accountLoginService.checkExistedCardNfcNumber(dto.getCardNumber());
                    }
                }
                if (checkExisted != null && !checkExisted.trim().isEmpty()) {
                    result = new ResponseMessageDTO("CHECK", "C05");
                    httpStatus = HttpStatus.BAD_REQUEST;
                } else {
                    if (dto.getCardNumber().trim().isEmpty()) {
                        if (dto.getCardType() == null || dto.getCardType().trim().toUpperCase().equals("CARD")) {
                            accountLoginService.updateCardNumber(dto.getCardNumber(), dto.getUserId());
                            result = new ResponseMessageDTO("SUCCESS", "");
                            httpStatus = HttpStatus.OK;
                        } else if (dto.getCardType() == null
                                || dto.getCardType().trim().toUpperCase().equals("NFC_CARD")) {
                            accountLoginService.updateCardNfcNumber(dto.getCardNumber(), dto.getUserId());
                            result = new ResponseMessageDTO("SUCCESS", "");
                            httpStatus = HttpStatus.OK;
                        }
                    } else {
                        if (dto.getCardType() == null || dto.getCardType().trim().toUpperCase().equals("CARD")) {
                            accountLoginService.updateCardNumber(dto.getCardNumber(), dto.getUserId());
                            result = new ResponseMessageDTO("SUCCESS", "");
                            httpStatus = HttpStatus.OK;
                        } else if (dto.getCardType() == null
                                || dto.getCardType().trim().toUpperCase().equals("NFC_CARD")) {
                            accountLoginService.updateCardNfcNumber(dto.getCardNumber(), dto.getUserId());
                            result = new ResponseMessageDTO("SUCCESS", "");
                            httpStatus = HttpStatus.OK;
                        }
                    }
                }
            } else {
                logger.error("updateCardNumberLogin: INVALID REQUEST BODY");
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("updateCardNumberLogin: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("accounts/cardNumber/{userId}")
    public ResponseEntity<ResponseMessageDTO> getCardNumberByUserId(@PathVariable(value = "userId") String userId) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            String check = accountLoginService.getCardNumberByUserId(userId);
            if (check != null && !check.trim().isEmpty()) {
                result = new ResponseMessageDTO("SUCCESS", check);
                httpStatus = HttpStatus.OK;
            } else {
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            }
        } catch (Exception e) {
            logger.error("getCardNumberByUserId: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("accounts/cardNumber")
    public ResponseEntity<CardVQRInfoDTO> getCardVQRInfoByUserId(@RequestParam(value = "userId") String userId) {
        CardVQRInfoDTO result = null;
        HttpStatus httpStatus = null;
        try {
            result = accountLoginService.getVcardInforByUserId(userId);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("getCardNumberByUserId: ERROR: " + e.toString());
            System.out.println("getCardNumberByUserId: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // Method: CARD, USER_ID
    @PostMapping("accounts/login")
    public ResponseEntity<String> loginByMethod(@RequestBody AccountLoginMethodDTO dto) {
        String result = "";
        HttpStatus httpStatus = null;
        try {
            String userId = "";
            AccountInformationEntity accountInformationEntity = null;
            // check method
            // Method: CARD, NFC_CARD, USER_ID
            if (dto.getMethod() != null && dto.getMethod().trim().toUpperCase().equals("CARD")) {
                if (dto.getCardNumber() != null && !dto.getCardNumber().trim().isEmpty()) {
                    userId = accountLoginService.loginByCardNumber(dto.getCardNumber());
                } else {
                    logger.error("LOGIN: INVALID cardNumber");
                }
            } else if (dto.getMethod() != null && dto.getMethod().trim().toUpperCase().equals("NFC_CARD")) {
                if (dto.getCardNumber() != null && !dto.getCardNumber().trim().isEmpty()) {
                    userId = accountLoginService.loginByCardNfcNumber(dto.getCardNumber());
                } else {
                    logger.error("LOGIN: INVALID CARD NFC NUMBER");
                }
            } else if (dto.getMethod() != null && dto.getMethod().trim().toUpperCase().equals("USER_ID")) {
                if (dto.getUserId() != null && !dto.getUserId().trim().isEmpty()) {
                    userId = dto.getUserId();
                } else {
                    logger.error("LOGIN: INVALID userId");
                }
            } else {
                logger.error("LOGIN: INVALID METHOD LOGIN");
            }
            // check userId
            if (userId != null && !userId.trim().isEmpty()) {
                accountInformationEntity = accountInformationService.getAccountInformation(userId);
                updateAccessLogin(userId);
            } else {
                logger.error("LOGIN: INVALID userId");
            }
            // check accountInformationEntity
            if (accountInformationEntity != null) {
                // push notification to other devices if user logged in before
                LocalDateTime currentDateTime = LocalDateTime.now();
                List<FcmTokenEntity> fcmTokens = new ArrayList<>();
                fcmTokens = fcmTokenService.getFcmTokensByUserId(userId);
                if (fcmTokens != null && !fcmTokens.isEmpty()) {
                    logger.info("FCM list size: " + fcmTokens.size());
                    // insert new Notification
                    String messageNotification = NotificationUtil.getNotiDescLoginWarningPrefix()
                            + dto.getPlatform() + " " + dto.getDevice();
                    UUID notificationUuid = UUID.randomUUID();
                    NotificationEntity notificationEntity = new NotificationEntity();
                    notificationEntity.setId(notificationUuid.toString());
                    notificationEntity.setRead(false);
                    notificationEntity.setMessage(messageNotification);
                    notificationEntity.setTime(currentDateTime.toEpochSecond(ZoneOffset.UTC));
                    notificationEntity.setType(NotificationUtil.getNotiTypeLogin());
                    notificationEntity.setUserId(userId);
                    notificationService.insertNotification(notificationEntity);
                    // push notification to devices
                    // for (FcmTokenEntity fcmToken : fcmTokens) {
                    // try {
                    // if (!fcmToken.getToken().trim().isEmpty()) {
                    // FcmRequestDTO fcmDTO = new FcmRequestDTO();
                    // fcmDTO.setTitle(NotificationUtil.getNotiTitleLoginWarning());
                    // fcmDTO.setMessage(messageNotification);
                    // fcmDTO.setToken(fcmToken.getToken());
                    // firebaseMessagingService.sendPushNotificationToToken(fcmDTO);
                    // logger.info("Send notification to device " + fcmToken.getToken());
                    // }
                    // } catch (Exception e) {
                    // logger.error("Error when Send Notification using FCM " + e.toString());
                    // if (e.toString()
                    // .contains("The registration token is not a valid FCM registration token")) {
                    // fcmTokenService.deleteFcmToken(fcmToken.getToken());
                    // }

                    // }
                    // }
                }
                // insert new FCM token
                FcmTokenEntity fcmTokenEntity = new FcmTokenEntity();
                UUID uuid = UUID.randomUUID();
                fcmTokenEntity.setId(uuid.toString());
                fcmTokenEntity.setToken(dto.getFcmToken());
                fcmTokenEntity.setUserId(userId);
                fcmTokenEntity.setPlatform(dto.getPlatform());
                fcmTokenEntity.setDevice(dto.getDevice());
                fcmTokenService.insertFcmToken(fcmTokenEntity);
                String phoneNo = accountLoginService.getPhoneNoById(userId);
                // response login success
                result = getJWTToken(accountInformationEntity, phoneNo);
                httpStatus = HttpStatus.OK;
            } else {
                logger.error("LOGIN: INVALID accountInformationEntity");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (

                Exception e) {
            logger.error("LOGIN: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PutMapping("user/password")
    public ResponseEntity<ResponseMessageDTO> updatePassword(@Valid @RequestBody PasswordUpdateDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            // check old password
            // if valid, update password
            String passwordCheck = accountLoginService.checkOldPassword(dto.getUserId(), dto.getOldPassword());
            if (passwordCheck != null && !passwordCheck.isEmpty()) {
                if (passwordCheck.equals(dto.getNewPassword())) {
                    result = new ResponseMessageDTO("FAILED", "E182");
                    httpStatus = HttpStatus.BAD_REQUEST;
                } else {
                    accountLoginService.updatePassword(dto.getNewPassword(), dto.getUserId());
                    result = new ResponseMessageDTO("SUCCESS", "");
                    httpStatus = HttpStatus.OK;
                }
            } else {
                result = new ResponseMessageDTO("FAIELD", "Old Password is not match.");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            System.out.println("Error at updatePassword: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "Cannot update password");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("user/information/{userId}")
    public ResponseEntity<AccountInformationBackUpDTO> getUserInformation(@PathVariable(value = "userId") String userId) {
        AccountInformationBackUpDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (userId != null && !userId.isEmpty()) {
                AccountInformationEntity accountInformationEntity = accountInformationService
                        .getAccountInformation(userId);
                IBalanceAndScoreDTO balanceAndScoreDTO = null;
                // set balance and score
                balanceAndScoreDTO = accountWalletService.getBalanceAndScore(userId);
                // get ngãy đăng ký tài khoản
                long registerDate = accountLoginService.getRegisterDate(userId);
                // Chuyển đổi thời gian kiểu long thành LocalDateTime
                LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(registerDate), ZoneId.systemDefault());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                String formattedDateTime = dateTime.format(formatter);

                if (accountInformationEntity != null) {
                    result = new AccountInformationBackUpDTO();
                    result.setFirstName(accountInformationEntity.getFirstName());
                    result.setMiddleName(accountInformationEntity.getMiddleName());
                    result.setLastName(accountInformationEntity.getLastName());
                    result.setBirthDate(accountInformationEntity.getBirthDate());
                    result.setAddress(accountInformationEntity.getAddress());
                    result.setGender(accountInformationEntity.getGender());
                    result.setEmail(accountInformationEntity.getEmail());
                    result.setUserId(accountInformationEntity.getUserId());
                    result.setNationalId(accountInformationEntity.getNationalId());
                    result.setOldNationalId(accountInformationEntity.getOldNationalId());
                    result.setNationalDate(accountInformationEntity.getNationalDate());
                    result.setImgId(accountInformationEntity.getImgId());
                    result.setCarrierTypeId(accountInformationEntity.getCarrierTypeId());
                    boolean checkVerify = accountLoginService.getVerifyEmailStatus(userId);
                    result.setVerify(checkVerify);
                    result.setBalance(balanceAndScoreDTO.getBalance());
                    result.setScore(balanceAndScoreDTO.getScore());
                    result.setTimeCreated(formattedDateTime);
                    httpStatus = HttpStatus.OK;
                } else {
                    logger.error("getUserInformation: EMPTY RECORD ");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                logger.error("getUserInformation: EMPTY USER ID ");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("getUserInformation: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PutMapping("user/information")
    public ResponseEntity<ResponseMessageDTO> updateInformation(@RequestBody AccountInformationDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null) {
                accountInformationService.updateAccountInformation(dto.getFirstName(), dto.getMiddleName(),
                        dto.getLastName(), dto.getBirthDate(), dto.getAddress(), dto.getGender(), dto.getEmail(),
                        dto.getNationalId(), dto.getOldNationalId(), dto.getNationalDate(),
                        dto.getUserId());
                //
//                LarkUtil larkUtil = new LarkUtil();
                GoogleChatUtil googleChatUtil = new GoogleChatUtil();
                String phoneNo = accountLoginService.getPhoneNoById(dto.getUserId());
                String fullname = dto.getLastName() + " " + dto.getMiddleName() + " " + dto.getFirstName();
                String gender = (dto.getGender() == 0) ? "Nam" : "Nữ";
                String larkMsg = "🧰 Người dùng cập nhật thông tin"
                        + "\nSố điện thoại: " + phoneNo
                        + "\nHọ tên: " + fullname.trim()
                        + "\nĐịa chỉ: " + dto.getAddress()
                        + "\nEmail: " + dto.getEmail()
                        + "\nNgày sinh: " + dto.getBirthDate()
                        + "\nGiới tính: " + gender;
                // SEND TO LARK VIETQR
                SystemSettingEntity systemSettingEntity = systemSettingService.getSystemSetting();
                googleChatUtil.sendMessageToGoogleChat(larkMsg, systemSettingEntity.getWebhookUrl());
                // SEND TO LARK PARTNER
                List<LarkWebhookPartnerEntity> partners = new ArrayList<>();
                partners = larkWebhookPartnerService.getLarkWebhookPartners();
                if (partners != null && !partners.isEmpty()) {
                    for (LarkWebhookPartnerEntity partner : partners) {
                        if (partner.getWebhook() != null && !partner.getWebhook().trim().isEmpty()
                                && partner.getActive() != null && partner.getActive() == true) {
                            googleChatUtil.sendMessageToGoogleChat(larkMsg, partner.getWebhook());
                        }
                    }
                }
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            } else {
                result = new ResponseMessageDTO("FAILED", "E05");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            System.out.println("Error at updateInformation: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "Cannot update information");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("user/image")
    public ResponseEntity<ResponseMessageDTO> updateImage(@Valid @RequestParam String imgId,
                                                          @Valid @RequestParam MultipartFile image, @Valid @RequestParam String userId) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (imgId.isEmpty()) {
                UUID uuidImage = UUID.randomUUID();
                String fileName = StringUtils.cleanPath(image.getOriginalFilename());
                ImageEntity imageEntity = new ImageEntity(uuidImage.toString(), fileName, image.getBytes());
                imageService.insertImage(imageEntity);
                accountInformationService.updateImageId(uuidImage.toString(), userId);
                result = new ResponseMessageDTO("SUCCESS", uuidImage.toString());
                httpStatus = HttpStatus.OK;
            } else {
                String fileName = StringUtils.cleanPath(image.getOriginalFilename());
                imageService.updateImage(image.getBytes(), fileName, imgId);
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            }
        } catch (Exception e) {
            System.out.println("Error at updateImage: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "Cannot update information");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("user/deactive/{userId}")
    public ResponseEntity<ResponseMessageDTO> deactiveUser(@PathVariable(value = "userId") String userId) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            // delete all FCM token
            fcmTokenService.deleteTokensByUserId(userId);
            // delete all notification
            notificationService.deleteNotificationsByUserId(userId);
            // delete all transaction (not yet)
            // update user information
            accountInformationService.updateAccountInformation("Undefined", "", "", "01/01/1970", "", 0, "", "", "", "",
                    userId);
            // update account_login and account_information => deactive (status = 2)
            accountLoginService.updateStatus(0, userId);
            accountInformationService.udpateStatus(0, userId);
            accountBankReceiveService.updateStatusAccountBankByUserId(0, userId);
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
            logger.error("DISABLE USER: " + e.toString());
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("accounts/token-plugin")
    public ResponseEntity<Object> getTokenPlugin(@RequestBody TokenPluginRequestDTO dto) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null && dto.getUserId() != null && !dto.getUserId().trim().isEmpty()) {
                AccountInformationEntity accountInformationEntity = accountInformationService
                        .getAccountInformation(dto.getUserId());
                if (accountInformationEntity != null) {
                    TokenPluginDTO tokenPluginDTO = new TokenPluginDTO();
                    String accessToken = getJWTInfinitiveToken(accountInformationEntity, dto.getPhoneNo(),
                            dto.getHosting());
                    tokenPluginDTO.setAccessToken(accessToken);
                    result = tokenPluginDTO;
                    httpStatus = HttpStatus.OK;
                } else {
                    logger.error("getTokenPlugin: EMPTY request body");
                    result = new ResponseMessageDTO("FAILED", "E38");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                logger.error("getTokenPlugin: EMPTY request body");
                result = new ResponseMessageDTO("FAILED", "E37");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("getTokenPlugin: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // reset password
    // phone no, password encrypt
    @PostMapping("accounts/password/reset")
    public ResponseEntity<ResponseMessageDTO> resetPassword(
            @RequestBody AccountLoginPasswordResetDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null) {
                AccountLoginEntity accountLoginEntity = accountLoginService.getAccountLoginByPhoneNo(dto.getPhoneNo());
                String currentPassword = accountLoginEntity.getPassword();
                if (currentPassword != null && currentPassword.equals(dto.getPassword())) {
                    result = new ResponseMessageDTO("FAILED", "E182");
                    httpStatus = HttpStatus.BAD_REQUEST;
                } else {
                    accountLoginService.resetPassword(dto.getPassword(), dto.getPhoneNo());
                    result = new ResponseMessageDTO("SUCCESS", "");
                    httpStatus = HttpStatus.OK;
                }
            } else {
                logger.error("resetPassword: INVALID REQUEST BODY");
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("resetPassword: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("accounts/sync-bitrix")
    public ResponseEntity<ResponseMessageDTO> syncAccountToBitrix() {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            List<AccountInformationSyncDTO> entities = accountInformationService.getUserInformationSync();
            int counter = 0;
            if (entities != null && !entities.isEmpty()) {
                for (AccountInformationSyncDTO entity : entities) {
                    try {
                        counter++;
                        String originatorId = entity.getId();
                        String name = entity.getLastName() + " " + entity.getMiddleName() + " " + entity.getFirstName();
                        String email = "";
                        if (entity.getEmail() != null) {
                            email = entity.getEmail();
                        }
                        String valueType = "WORK";
                        String phoneNo = StringUtil.formatPhoneNumber(entity.getPhoneNo());
                        String post = "USER";
                        String address = entity.getAddress();
                        String address2 = "";
                        String comment = entity.getBirthDate();
                        String web = "";
                        String vending = "VIETQR";
                        String sourceDescription = "VIETQR-USER";
                        // Thêm API gắn các request param ở đây
                        // Xây dựng URL của API Bitrix với các tham số
                        String apiUrl = "https://crm.bluecom.vn/rest/10/0ji9bblj3wuxq8bi/crm.contact.add.json" +
                                "?FIELDS[ORIGINATOR_ID]=" + originatorId +
                                "&FIELDS[NAME]=" + name.trim() +
                                "&FIELDS[EMAIL][0][VALUE]=" + email +
                                "&FIELDS[EMAIL][0][VALUE_TYPE]=" + valueType +
                                "&FIELDS[PHONE][0][VALUE]=" + phoneNo +
                                "&FIELDS[PHONE][0][VALUE_TYPE]=" + "WORK" +
                                "&FIELDS[POST]=" + post +
                                "&FIELDS[ADDRESS]=" + address +
                                "&FIELDS[ADDRESS_2]=" + address2 +
                                "&FIELDS[COMMENTS]=" + comment +
                                "&FIELDS[ORIGIN_ID]=" + web +
                                "&FIELDS[VENDING]=" + vending +
                                "&FIELDS[SOURCE_DESCRIPTION]=" + sourceDescription;
                        UriComponents uriComponents = UriComponentsBuilder
                                .fromHttpUrl(apiUrl)
                                .buildAndExpand(/* add url parameter here */);
                        WebClient webClient = WebClient.builder()
                                .baseUrl(apiUrl)
                                .build();
                        Mono<ClientResponse> responseMono = webClient.get()
                                .uri(uriComponents.toUri())
                                .exchange();
                        ClientResponse response = responseMono.block();
                        if (response.statusCode().is2xxSuccessful()) {
                            System.out.println(
                                    "SYNC BITRIX SUCCESS ITEM " + counter + ": " + entity.getId() + " - "
                                            + name.trim() + " - "
                                            + entity.getPhoneNo());
                        } else {
                            logger.error(
                                    "SYNC BITRIX FAILED ITEM: " + entity.getId() + " - " + entity.getPhoneNo());
                            System.out.println(
                                    "SYNC BITRIX FAILED ITEM: " + entity.getId() + " - " + entity.getPhoneNo());
                        }
                    } catch (Exception e) {
                        System.out.println("ERROR: " + e.toString());
                    }
                }
            }
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            System.out.println(e.toString());
            logger.error("syncVcardsToBitrix: ERROR: " + e.toString());
            result = new ResponseMessageDTO("syncVcardsToBitrix", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    private String getJWTToken(AccountInformationEntity entity, String phoneNo) {
        String result = "";
        String secretKey = "mySecretKey";
        List<GrantedAuthority> grantedAuthorities = AuthorityUtils
                .commaSeparatedStringToAuthorityList("ROLE_USER");
        result = Jwts
                .builder()
                .claim("userId", entity.getUserId())
                .claim("phoneNo", phoneNo)
                .claim("firstName", entity.getFirstName())
                .claim("middleName", entity.getMiddleName())
                .claim("lastName", entity.getLastName())
                .claim("birthDate", entity.getBirthDate())
                .claim("gender", entity.getGender())
                .claim("address", entity.getAddress())
                .claim("email", entity.getEmail())
                .claim("imgId", entity.getImgId())
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

    private String getJWTInfinitiveToken(AccountInformationEntity entity, String phoneNo, String hosting) {
        String secretKey = "mySecretKey";
        List<GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER");
        String token = Jwts.builder()
                .claim("userId", entity.getUserId())
                .claim("hosting", hosting)
                .claim("phoneNo", phoneNo)
                .claim("firstName", entity.getFirstName())
                .claim("middleName", entity.getMiddleName())
                .claim("lastName", entity.getLastName())
                .claim("authorities",
                        grantedAuthorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .signWith(SignatureAlgorithm.HS512, secretKey.getBytes())
                .compact();

        return token;
    }


}
