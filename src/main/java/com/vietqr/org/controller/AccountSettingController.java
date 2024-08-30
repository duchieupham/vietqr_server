package com.vietqr.org.controller;

import java.util.*;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vietqr.org.dto.*;
import com.vietqr.org.json.AccountSettingConfigDTO;
import com.vietqr.org.service.*;
import com.vietqr.org.util.StringUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.vietqr.org.entity.AccountSettingEntity;
import com.vietqr.org.entity.ImageEntity;
import com.vietqr.org.entity.SystemSettingEntity;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class AccountSettingController {
    private static final Logger logger = Logger.getLogger(AccountSettingController.class);

    @Autowired
    AccountSettingService accountSettingService;

    @Autowired
    AccountLoginService accountLoginService;

    @Autowired
    SystemSettingService systemSettingService;

    @Autowired
    ThemeUiService themeUiService;

    @Autowired
    ImageService imageService;

    @Autowired
    MerchantMemberRoleService merchantMemberRoleService;

    @Autowired
    MerchantMemberService merchantMemberService;

    @Autowired
    MerchantBankReceiveService merchantBankReceiveService;

    @GetMapping("accounts/setting/{userId}")
    public ResponseEntity<AccountSettingBackUpDTO> getAccountSetting(@PathVariable("userId") String userId) {
        AccountSettingBackUpDTO result = null;
        HttpStatus httpStatus = null;
        try {
            logger.info("getAccountSetting: " + userId + " at: " + System.currentTimeMillis());
            if (userId != null && !userId.trim().isEmpty()) {
                AccountSettingEntity entity = accountSettingService.getAccountSettingEntity(userId);
                List<MerchantRoleSettingDTO> roles = new ArrayList<>();
                try {
                    List<IMerchantBankMemberDTO> iMerchantBankMemberDTOs = merchantMemberService.getIMerchantBankMemberByUserId(userId);
                    System.out.println(iMerchantBankMemberDTOs.get(0).getMerchantMemberId());
                    List<IMerchantRoleRawDTO> merchantRoleRawDTOS = merchantMemberRoleService
                            .getMerchantIdsByMerchantMemberIds(iMerchantBankMemberDTOs.stream()
                                    .map(IMerchantBankMemberDTO::getMerchantMemberId)
                                    .collect(Collectors.toList()));
                    if (merchantRoleRawDTOS != null) {
                        Map<String, List<IMerchantRoleRawDTO>> merchantRoleMap = merchantRoleRawDTOS.stream()
                                .collect(Collectors.groupingBy(IMerchantRoleRawDTO::getMerchantMemberId));
                        roles = iMerchantBankMemberDTOs.stream()
                                .map(member -> {
                                    MerchantRoleSettingDTO setting = new MerchantRoleSettingDTO();
                                    setting.setMerchantId(member.getMerchantId());
                                    setting.setBankId(member.getBankId());
                                    setting.setRoles(merchantRoleMap.getOrDefault(member.getMerchantMemberId(),new ArrayList<>())
                                            .stream()
                                            .map(role -> {
                                                RoleSettingDTO roleDto = new RoleSettingDTO();
                                                roleDto.setCategory(role.getCategory());
                                                roleDto.setRole(role.getRole());
                                                return roleDto;
                                            })
                                            .collect(Collectors.toList()));
                                    return setting;
                                })
                                .collect(Collectors.toList());
                    }
                } catch (Exception e) {
                    logger.info("getAccountSetting: ERROR: " + e.getMessage() + " " + userId + " at: " + System.currentTimeMillis());
                }
                if (entity != null) {
                    //
                    result = new AccountSettingBackUpDTO();
                    result.setId(entity.getId());
                    result.setUserId(entity.getUserId());
                    result.setGuideMobile(entity.isGuideWeb());
                    result.setGuideMobile(entity.isGuideMobile());
                    result.setVoiceWeb(entity.isVoiceWeb());
                    result.setVoiceMobile(entity.isVoiceMobile());
                    result.setVoiceMobileKiot(entity.isVoiceMobileKiot());
                    result.setStatus(entity.isStatus());
                    result.setEdgeImgId(entity.getEdgeImgId());
                    result.setFooterImgId(entity.getFooterImgId());
                    result.setNotificationMobile(entity.isNotificationMobile());
                    result.setMerchantRoles(roles);
                    //check verifyEmail
                    boolean checkVerify = accountLoginService.getVerifyEmailStatus(userId);
                    result.setVerifyEmail(checkVerify);

                    // cái này làm cho phần thông báo message
                    result.setNotificationMessage(entity.getNotificationMessage());

                    // theme processing
                    SystemSettingEntity systemSettingEntity = systemSettingService.getSystemSetting();
                    String themeImgUrl = "";
                    int themeType = 0;
                    if (systemSettingEntity.isEventTheme() == true) {
                        themeImgUrl = systemSettingEntity.getThemeImgUrl();
                    } else {
                        themeImgUrl = themeUiService.getImgUrlByType(entity.getThemeType());
                        themeType = entity.getThemeType();
                    }
                    result.setThemeType(themeType);
                    result.setThemeImgUrl(themeImgUrl);
                    // logo url
                    result.setLogoUrl(systemSettingEntity.getLogoUrl());
                    result.setKeepScreenOn(entity.isKeepScreenOn());
                    result.setQrShowType(entity.getQrShowType());
                    // user config
                    ObjectMapper mapper = new ObjectMapper();
                    AccountSettingConfigDTO accountSettingConfigDTO = null;
                    try {
                        accountSettingConfigDTO = mapper.readValue(entity.getDataConfig(), AccountSettingConfigDTO.class);
                    } catch (Exception e) {
                        logger.error("getAccountSetting: ERROR: accountSettingConfigDTO: " + e.getMessage());
                    }
                    if (Objects.nonNull(accountSettingConfigDTO)) {
                        result.setUserConfig(accountSettingConfigDTO);
                    } else {
                        result.setUserConfig(new AccountSettingConfigDTO(true));
                    }
                    //
                    httpStatus = HttpStatus.OK;
                } else {
                    logger.error("getAccountSetting: NOT FOUND ACCOUNT SETTING ENTITY");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                logger.error("getAccountSetting: INVALID REQUEST BODY");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("getAccountSetting: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<AccountSettingBackUpDTO>(result, httpStatus);
    }

    @PostMapping("accounts/setting/data-config")
    public ResponseEntity<ResponseMessageDTO> updateDataConfig(@RequestBody UserConfigAccountSettingDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            AccountSettingConfigDTO accountSettingConfigDTO = null;
            if (dto.getUserConfig() != null) {
                // user config
                AccountSettingEntity entity = accountSettingService.getAccountSettingEntity(dto.getUserId());
                try {
                    accountSettingConfigDTO = mapper.readValue(entity.getDataConfig(), AccountSettingConfigDTO.class);
                } catch (Exception e) {
                    logger.error("getAccountSetting: ERROR: accountSettingConfigDTO: " + e.getMessage());
                }
                if (Objects.nonNull(accountSettingConfigDTO)) {
                    accountSettingConfigDTO.setBidvNotification(dto.getUserConfig().isBidvNotification());
                } else {
                    accountSettingConfigDTO = new AccountSettingConfigDTO();
                    accountSettingConfigDTO.setBidvNotification(false);
                }
            }
            String mapping = mapper.writeValueAsString(accountSettingConfigDTO);
            accountSettingService.updateSettingConfig(mapping, dto.getUserId());
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("updateNotificationMobile: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<ResponseMessageDTO>(result, httpStatus);
    }

    @PostMapping("accounts/setting/image")
    public ResponseEntity<ResponseMessageDTO> updateImageKiot(
            @Valid @RequestParam String imgId,
            @Valid @RequestParam MultipartFile image,
            @Valid @RequestParam String userId,
            // type = 0 -> edge image
            // type = 1 -> footer image
            @Valid @RequestParam int type) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (imgId.trim().isEmpty()) {
                UUID uuidImage = UUID.randomUUID();
                String fileName = StringUtils.cleanPath(image.getOriginalFilename());
                ImageEntity imageEntity = new ImageEntity(uuidImage.toString(), fileName, image.getBytes());
                imageService.insertImage(imageEntity);
                if (type == 0) {
                    accountSettingService.updateEdgeImgId(uuidImage.toString(), userId);
                    result = new ResponseMessageDTO("SUCCESS", uuidImage.toString());
                    httpStatus = HttpStatus.OK;
                } else {
                    accountSettingService.updateFooterImgId(uuidImage.toString(), userId);
                    result = new ResponseMessageDTO("SUCCESS", uuidImage.toString());
                    httpStatus = HttpStatus.OK;
                }
            } else {
                String fileName = StringUtils.cleanPath(image.getOriginalFilename());
                imageService.updateImage(image.getBytes(), fileName, imgId);
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            }
        } catch (Exception e) {
            System.out.println("Error at updateImageKiot: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("accounts/setting/notification-mobile")
    public ResponseEntity<ResponseMessageDTO> updateNotificationMobile(@RequestBody AccountSettingNotiDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            accountSettingService.updateNotificationMobile(dto.isNotificationMobile(), dto.getUserId());
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("updateNotificationMobile: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<ResponseMessageDTO>(result, httpStatus);
    }

    @PostMapping("accounts/setting")
    public ResponseEntity<ResponseMessageDTO> updateGuideWebUser(@RequestBody AccountSettingUpdateDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            accountSettingService.updateGuideWebByUserId(dto.getGuideWeb(), dto.getUserId());
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("updateGuideWebUser: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<ResponseMessageDTO>(result, httpStatus);
    }

    @PostMapping("accounts/setting/voice")
    public ResponseEntity<ResponseMessageDTO> updateVoice(@RequestBody AccountSettingVoiceDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            // check type:
            // 0: mobile
            // 1: kiot
            // 2: web
            if (dto != null) {
                if (dto.getType() == 0) {
                    accountSettingService.updateVoiceMobile(dto.getValue(), dto.getUserId());
                    result = new ResponseMessageDTO("SUCCESS", "");
                    httpStatus = HttpStatus.OK;
                } else if (dto.getType() == 1) {
                    accountSettingService.updateVoiceMobileKiot(dto.getValue(), dto.getUserId());
                    result = new ResponseMessageDTO("SUCCESS", "");
                    httpStatus = HttpStatus.OK;
                } else if (dto.getType() == 2) {
                    accountSettingService.updateVoiceWeb(dto.getValue(), dto.getUserId());
                    httpStatus = HttpStatus.OK;
                } else {
                    logger.error("updateVoiceMobile: INVALID REQUEST TYPE");
                    result = new ResponseMessageDTO("FAILED", "E72");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                logger.error("updateVoiceMobile: INVALID REQUEST BODY");
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("updateVoiceMobile: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("accounts/setting/theme")
    public ResponseEntity<ResponseMessageDTO> updateThemeType(
            @RequestBody UserSettingUpdateDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null) {
                accountSettingService.updateThemeType(dto.getValue(), dto.getUserId());
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            } else {
                logger.error("updateThemeType: INVALID REQUEST BODY");
                result = new ResponseMessageDTO("FAILED", "E46");
            }
        } catch (Exception e) {
            logger.error("updateThemeType: ERROR: " + e.toString());
            System.out.println("updateThemeType: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("accounts/setting/screen")
    public ResponseEntity<ResponseMessageDTO> updateKeepScreen(
            @RequestBody UserSettingUpdateCheckDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null) {
                accountSettingService.updateKeepScreenOn(dto.getValue(), dto.getUserId());
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            } else {
                logger.error("updateKeepScreen: INVALID REQUEST BODY");
                result = new ResponseMessageDTO("FAILED", "E46");
            }
        } catch (Exception e) {
            logger.error("updateKeepScreen: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("accounts/setting/qr-show-type")
    public ResponseEntity<ResponseMessageDTO> updateQrShowType(
            @RequestBody UserSettingUpdateDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null) {
                accountSettingService.updateQrShowType(dto.getValue(), dto.getUserId());
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            } else {
                logger.error("updateQrShowType: INVALID REQUEST BODY");
                result = new ResponseMessageDTO("FAILED", "E46");
            }
        } catch (Exception e) {
            logger.error("updateQrShowType: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }
}
