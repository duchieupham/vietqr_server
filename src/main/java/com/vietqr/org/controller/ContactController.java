package com.vietqr.org.controller;

import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.vietqr.org.dto.ContactDetailDTO;
import com.vietqr.org.dto.ContactInsertDTO;
import com.vietqr.org.dto.ContactInsertMultipartDTO;
import com.vietqr.org.dto.ContactInsertWithRelationMultipartDTO;
import com.vietqr.org.dto.ContactListDTO;
import com.vietqr.org.dto.ContactRechargeDTO;
import com.vietqr.org.dto.ContactRelationUpdateDTO;
import com.vietqr.org.dto.ContactScanResultDTO;
import com.vietqr.org.dto.ContactStatusUpdateDTO;
import com.vietqr.org.dto.ContactUpdateDTO;
import com.vietqr.org.dto.ContactUpdateMultipartDTO;
import com.vietqr.org.dto.FcmRequestDTO;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.dto.UserInfoWalletDTO;
import com.vietqr.org.entity.AccountInformationEntity;
import com.vietqr.org.entity.BankTypeEntity;
import com.vietqr.org.entity.ContactEntity;
import com.vietqr.org.entity.FcmTokenEntity;
import com.vietqr.org.entity.ImageEntity;
import com.vietqr.org.entity.NotificationEntity;
import com.vietqr.org.service.AccountInformationService;
import com.vietqr.org.service.AccountWalletService;
import com.vietqr.org.service.BankTypeService;
import com.vietqr.org.service.ContactService;
import com.vietqr.org.service.FcmTokenService;
import com.vietqr.org.service.FirebaseMessagingService;
import com.vietqr.org.service.ImageService;
import com.vietqr.org.service.NotificationService;
import com.vietqr.org.util.NotificationUtil;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class ContactController {
    private static final Logger logger = Logger.getLogger(ContactController.class);

    @Autowired
    ContactService contactService;

    @Autowired
    AccountWalletService accountWalletService;

    @Autowired
    AccountInformationService accountInformationService;

    @Autowired
    FcmTokenService fcmTokenService;

    @Autowired
    NotificationService notificationService;

    @Autowired
    FirebaseMessagingService firebaseMessagingService;

    @Autowired
    BankTypeService bankTypeService;

    @Autowired
    ImageService imageService;

    // add contact with relation
    // 1. check add chua
    // 2. add
    @PostMapping("contacts")
    public ResponseEntity<ResponseMessageDTO> insertContacWithRelationtMultipart(
            @ModelAttribute ContactInsertWithRelationMultipartDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null) {
                String checkExisted = contactService.checkExistedRecord(dto.getUserId(), dto.getValue(), dto.getType());
                if (checkExisted == null) {
                    UUID uuid = UUID.randomUUID();
                    LocalDateTime currentDateTime = LocalDateTime.now();
                    long time = currentDateTime.toEpochSecond(ZoneOffset.UTC);
                    ContactEntity entity = new ContactEntity();
                    entity.setId(uuid.toString());
                    entity.setUserId(dto.getUserId());
                    entity.setNickname(dto.getNickName());
                    entity.setValue(dto.getValue());
                    entity.setAdditionalData(dto.getAdditionalData());
                    entity.setType(dto.getType());
                    // 0: approved - 1: pending
                    entity.setStatus(0);
                    entity.setTime(time);
                    //
                    if (dto.getType() == 2) {
                        entity.setBankTypeId(dto.getBankTypeId());
                        entity.setBankAccount(dto.getBankAccount());
                    } else {
                        entity.setBankTypeId("");
                        entity.setBankAccount("");
                    }
                    // set imgId
                    // check img
                    if (dto.getImage() != null) {
                        UUID uuidImage = UUID.randomUUID();
                        String fileName = StringUtils.cleanPath(dto.getImage().getOriginalFilename());
                        ImageEntity imageEntity = new ImageEntity(uuidImage.toString(), fileName,
                                dto.getImage().getBytes());
                        imageService.insertImage(imageEntity);
                        entity.setImgId(uuidImage.toString());
                    } else {
                        entity.setImgId("");
                    }
                    // set color Type
                    entity.setColorType(dto.getColorType());
                    entity.setRelation(dto.getRelation());
                    int check = contactService.insertContact(entity);
                    if (check == 1) {
                        // push noti with other user if found user by walletId
                        if (dto.getType() == 1 && dto.getValue() != null && !dto.getValue().isEmpty()) {
                            String checkExistedUserId = accountWalletService.getUserIdByWalletId(dto.getValue());
                            if (checkExistedUserId != null) {
                                UUID uuid2 = UUID.randomUUID();
                                ContactEntity entity2 = new ContactEntity();
                                UserInfoWalletDTO userInfoWalletDTO = accountInformationService
                                        .getUserInforWallet(dto.getUserId());
                                if (userInfoWalletDTO != null) {
                                    // insert 2
                                    String nickname = userInfoWalletDTO.getLastName() + " "
                                            + userInfoWalletDTO.getMiddleName() + " "
                                            + userInfoWalletDTO.getFirstName();
                                    entity2.setId(uuid2.toString());
                                    entity2.setUserId(checkExistedUserId);
                                    entity2.setNickname(nickname.trim());
                                    entity2.setValue(userInfoWalletDTO.getWalletId());
                                    entity2.setAdditionalData("");
                                    // default type = 1
                                    entity2.setType(1);
                                    // 0: approved - 1: pending
                                    entity2.setStatus(1);
                                    entity2.setTime(time);
                                    entity2.setBankTypeId("");
                                    entity2.setBankAccount("");
                                    entity2.setColorType(0);
                                    entity2.setImgId("");
                                    entity2.setRelation(0);
                                    contactService.insertContact(entity2);
                                    //
                                    String title = NotificationUtil.getNotiTitleAddVietqrId();
                                    String message = nickname + NotificationUtil.getNotiDescAddVietqrId();
                                    // insert notification
                                    UUID notificationUuid = UUID.randomUUID();
                                    NotificationEntity notificationEntity = new NotificationEntity();
                                    notificationEntity.setId(notificationUuid.toString());
                                    notificationEntity.setRead(false);
                                    notificationEntity.setMessage(message);
                                    notificationEntity.setTime(currentDateTime.toEpochSecond(ZoneOffset.UTC));
                                    notificationEntity.setType(NotificationUtil.getNotiAddVietqrId());
                                    notificationEntity.setUserId(checkExistedUserId);
                                    notificationService.insertNotification(notificationEntity);
                                    // push notification
                                    List<FcmTokenEntity> fcmTokens = new ArrayList<>();
                                    fcmTokens = fcmTokenService.getFcmTokensByUserId(checkExistedUserId);
                                    if (fcmTokens != null && !fcmTokens.isEmpty()) {
                                        for (FcmTokenEntity fcmToken : fcmTokens) {
                                            try {
                                                if (!fcmToken.getToken().trim().isEmpty()) {
                                                    FcmRequestDTO fcmDTO = new FcmRequestDTO();
                                                    fcmDTO.setTitle(title);
                                                    fcmDTO.setMessage(message);
                                                    fcmDTO.setToken(fcmToken.getToken());
                                                    firebaseMessagingService.sendPushNotificationToToken(fcmDTO);
                                                    logger.info("Send notification to device " + fcmToken.getToken());
                                                }
                                            } catch (Exception e) {
                                                logger.error("Error when Send Notification using FCM " + e.toString());
                                                if (e.toString()
                                                        .contains(
                                                                "The registration token is not a valid FCM registration token")) {
                                                    fcmTokenService.deleteFcmToken(fcmToken.getToken());
                                                }
                                            }
                                        }
                                    }
                                }

                            }
                        }
                        result = new ResponseMessageDTO("SUCCESS", "");
                        httpStatus = HttpStatus.OK;
                    } else {
                        result = new ResponseMessageDTO("FAILED", "E48");
                        httpStatus = HttpStatus.BAD_REQUEST;
                    }
                } else {
                    logger.error("insertContact: EXISTED RECORD");
                    result = new ResponseMessageDTO("FAILED", "E47");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                logger.error("insertContact: INVALID REQUEST BODY");
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("insertContact: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // add contact
    // 1. check add chua
    // 2. add
    @PostMapping("contact-qr")
    public ResponseEntity<ResponseMessageDTO> insertContactMultipart(
            @ModelAttribute ContactInsertMultipartDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null) {
                String checkExisted = contactService.checkExistedRecord(dto.getUserId(), dto.getValue(), dto.getType());
                if (checkExisted == null) {
                    UUID uuid = UUID.randomUUID();
                    LocalDateTime currentDateTime = LocalDateTime.now();
                    long time = currentDateTime.toEpochSecond(ZoneOffset.UTC);
                    ContactEntity entity = new ContactEntity();
                    entity.setId(uuid.toString());
                    entity.setUserId(dto.getUserId());
                    entity.setNickname(dto.getNickName());
                    entity.setValue(dto.getValue());
                    entity.setAdditionalData(dto.getAdditionalData());
                    entity.setType(dto.getType());
                    // 0: approved - 1: pending
                    entity.setStatus(0);
                    entity.setTime(time);
                    //
                    if (dto.getType() == 2) {
                        entity.setBankTypeId(dto.getBankTypeId());
                        entity.setBankAccount(dto.getBankAccount());
                    } else {
                        entity.setBankTypeId("");
                        entity.setBankAccount("");
                    }
                    // set imgId
                    // check img
                    if (dto.getImage() != null) {
                        UUID uuidImage = UUID.randomUUID();
                        String fileName = StringUtils.cleanPath(dto.getImage().getOriginalFilename());
                        ImageEntity imageEntity = new ImageEntity(uuidImage.toString(), fileName,
                                dto.getImage().getBytes());
                        imageService.insertImage(imageEntity);
                        entity.setImgId(uuidImage.toString());
                    } else {
                        entity.setImgId("");
                    }
                    // set color Type
                    entity.setColorType(dto.getColorType());
                    entity.setRelation(0);
                    int check = contactService.insertContact(entity);
                    if (check == 1) {
                        // push noti with other user if found user by walletId
                        if (dto.getType() == 1 && dto.getValue() != null && !dto.getValue().isEmpty()) {
                            String checkExistedUserId = accountWalletService.getUserIdByWalletId(dto.getValue());
                            if (checkExistedUserId != null) {
                                UUID uuid2 = UUID.randomUUID();
                                ContactEntity entity2 = new ContactEntity();
                                UserInfoWalletDTO userInfoWalletDTO = accountInformationService
                                        .getUserInforWallet(dto.getUserId());
                                if (userInfoWalletDTO != null) {
                                    // insert 2
                                    String nickname = userInfoWalletDTO.getLastName() + " "
                                            + userInfoWalletDTO.getMiddleName() + " "
                                            + userInfoWalletDTO.getFirstName();
                                    entity2.setId(uuid2.toString());
                                    entity2.setUserId(checkExistedUserId);
                                    entity2.setNickname(nickname.trim());
                                    entity2.setValue(userInfoWalletDTO.getWalletId());
                                    entity2.setAdditionalData("");
                                    // default type = 1
                                    entity2.setType(1);
                                    // 0: approved - 1: pending
                                    entity2.setStatus(1);
                                    entity2.setTime(time);
                                    entity2.setBankTypeId("");
                                    entity2.setBankAccount("");
                                    entity2.setColorType(0);
                                    entity2.setImgId("");
                                    entity2.setRelation(0);
                                    contactService.insertContact(entity2);
                                    //
                                    String title = NotificationUtil.getNotiTitleAddVietqrId();
                                    String message = nickname + NotificationUtil.getNotiDescAddVietqrId();
                                    // insert notification
                                    UUID notificationUuid = UUID.randomUUID();
                                    NotificationEntity notificationEntity = new NotificationEntity();
                                    notificationEntity.setId(notificationUuid.toString());
                                    notificationEntity.setRead(false);
                                    notificationEntity.setMessage(message);
                                    notificationEntity.setTime(currentDateTime.toEpochSecond(ZoneOffset.UTC));
                                    notificationEntity.setType(NotificationUtil.getNotiAddVietqrId());
                                    notificationEntity.setUserId(checkExistedUserId);
                                    notificationService.insertNotification(notificationEntity);
                                    // push notification
                                    List<FcmTokenEntity> fcmTokens = new ArrayList<>();
                                    fcmTokens = fcmTokenService.getFcmTokensByUserId(checkExistedUserId);
                                    if (fcmTokens != null && !fcmTokens.isEmpty()) {
                                        for (FcmTokenEntity fcmToken : fcmTokens) {
                                            try {
                                                if (!fcmToken.getToken().trim().isEmpty()) {
                                                    FcmRequestDTO fcmDTO = new FcmRequestDTO();
                                                    fcmDTO.setTitle(title);
                                                    fcmDTO.setMessage(message);
                                                    fcmDTO.setToken(fcmToken.getToken());
                                                    firebaseMessagingService.sendPushNotificationToToken(fcmDTO);
                                                    logger.info("Send notification to device " + fcmToken.getToken());
                                                }
                                            } catch (Exception e) {
                                                logger.error("Error when Send Notification using FCM " + e.toString());
                                                if (e.toString()
                                                        .contains(
                                                                "The registration token is not a valid FCM registration token")) {
                                                    fcmTokenService.deleteFcmToken(fcmToken.getToken());
                                                }
                                            }
                                        }
                                    }
                                }

                            }
                        }
                        result = new ResponseMessageDTO("SUCCESS", "");
                        httpStatus = HttpStatus.OK;
                    } else {
                        result = new ResponseMessageDTO("FAILED", "E48");
                        httpStatus = HttpStatus.BAD_REQUEST;
                    }
                } else {
                    logger.error("insertContact: EXISTED RECORD");
                    result = new ResponseMessageDTO("FAILED", "E47");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                logger.error("insertContact: INVALID REQUEST BODY");
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("insertContact: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // add contact
    // 1. check add chua
    // 2. add
    @PostMapping("contact")
    public ResponseEntity<ResponseMessageDTO> insertContact(@RequestBody ContactInsertDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null) {
                String checkExisted = contactService.checkExistedRecord(dto.getUserId(), dto.getValue(), dto.getType());
                if (checkExisted == null) {
                    UUID uuid = UUID.randomUUID();
                    LocalDateTime currentDateTime = LocalDateTime.now();
                    long time = currentDateTime.toEpochSecond(ZoneOffset.UTC);
                    ContactEntity entity = new ContactEntity();
                    entity.setId(uuid.toString());
                    entity.setUserId(dto.getUserId());
                    entity.setNickname(dto.getNickName());
                    entity.setValue(dto.getValue());
                    entity.setAdditionalData(dto.getAdditionalData());
                    entity.setType(dto.getType());
                    // 0: approved - 1: pending
                    entity.setStatus(0);
                    entity.setTime(time);
                    entity.setColorType(0);
                    entity.setImgId("");
                    //
                    if (dto.getType() == 2) {
                        entity.setBankTypeId(dto.getBankTypeId());
                        entity.setBankAccount(dto.getBankAccount());
                    } else {
                        entity.setBankTypeId("");
                        entity.setBankAccount("");
                    }
                    entity.setRelation(0);
                    int check = contactService.insertContact(entity);
                    if (check == 1) {
                        // push noti with other user if found user by walletId
                        if (dto.getType() == 1 && dto.getValue() != null && !dto.getValue().isEmpty()) {
                            String checkExistedUserId = accountWalletService.getUserIdByWalletId(dto.getValue());
                            if (checkExistedUserId != null) {
                                UUID uuid2 = UUID.randomUUID();
                                ContactEntity entity2 = new ContactEntity();
                                UserInfoWalletDTO userInfoWalletDTO = accountInformationService
                                        .getUserInforWallet(dto.getUserId());
                                if (userInfoWalletDTO != null) {
                                    // insert 2
                                    String nickname = userInfoWalletDTO.getLastName() + " "
                                            + userInfoWalletDTO.getMiddleName() + " "
                                            + userInfoWalletDTO.getFirstName();
                                    entity2.setId(uuid2.toString());
                                    entity2.setUserId(checkExistedUserId);
                                    entity2.setNickname(nickname.trim());
                                    entity2.setValue(userInfoWalletDTO.getWalletId());
                                    entity2.setAdditionalData("");
                                    // default type = 1
                                    entity2.setType(1);
                                    // 0: approved - 1: pending
                                    entity2.setStatus(1);
                                    entity2.setTime(time);
                                    entity2.setBankTypeId("");
                                    entity2.setBankAccount("");
                                    entity2.setColorType(0);
                                    entity2.setImgId("");
                                    entity2.setRelation(0);
                                    contactService.insertContact(entity2);
                                    //
                                    String title = NotificationUtil.getNotiTitleAddVietqrId();
                                    String message = nickname + NotificationUtil.getNotiDescAddVietqrId();
                                    // insert notification
                                    UUID notificationUuid = UUID.randomUUID();
                                    NotificationEntity notificationEntity = new NotificationEntity();
                                    notificationEntity.setId(notificationUuid.toString());
                                    notificationEntity.setRead(false);
                                    notificationEntity.setMessage(message);
                                    notificationEntity.setTime(currentDateTime.toEpochSecond(ZoneOffset.UTC));
                                    notificationEntity.setType(NotificationUtil.getNotiAddVietqrId());
                                    notificationEntity.setUserId(checkExistedUserId);
                                    notificationService.insertNotification(notificationEntity);
                                    // push notification
                                    List<FcmTokenEntity> fcmTokens = new ArrayList<>();
                                    fcmTokens = fcmTokenService.getFcmTokensByUserId(checkExistedUserId);
                                    if (fcmTokens != null && !fcmTokens.isEmpty()) {
                                        for (FcmTokenEntity fcmToken : fcmTokens) {
                                            try {
                                                if (!fcmToken.getToken().trim().isEmpty()) {
                                                    FcmRequestDTO fcmDTO = new FcmRequestDTO();
                                                    fcmDTO.setTitle(title);
                                                    fcmDTO.setMessage(message);
                                                    fcmDTO.setToken(fcmToken.getToken());
                                                    firebaseMessagingService.sendPushNotificationToToken(fcmDTO);
                                                    logger.info("Send notification to device " + fcmToken.getToken());
                                                }
                                            } catch (Exception e) {
                                                logger.error("Error when Send Notification using FCM " + e.toString());
                                                if (e.toString()
                                                        .contains(
                                                                "The registration token is not a valid FCM registration token")) {
                                                    fcmTokenService.deleteFcmToken(fcmToken.getToken());
                                                }
                                            }
                                        }
                                    }
                                }

                            }
                        }
                        result = new ResponseMessageDTO("SUCCESS", "");
                        httpStatus = HttpStatus.OK;
                    } else {
                        result = new ResponseMessageDTO("FAILED", "E48");
                        httpStatus = HttpStatus.BAD_REQUEST;
                    }
                } else {
                    logger.error("insertContact: EXISTED RECORD");
                    result = new ResponseMessageDTO("FAILED", "E47");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                logger.error("insertContact: INVALID REQUEST BODY");
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("insertContact: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("contact/relation")
    public ResponseEntity<ResponseMessageDTO> updateContactRelation(@RequestBody ContactRelationUpdateDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null) {
                int relation = 0;
                if (dto.getRelation() != null) {
                    relation = dto.getRelation();
                }
                contactService.updateContactRelation(relation, dto.getId());
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            } else {
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("updateContactRelation: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // update status contact
    @PostMapping("contact/status")
    public ResponseEntity<ResponseMessageDTO> updateContactStatus(@RequestBody ContactStatusUpdateDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null) {
                if (dto.getStatus() == 0) {
                    contactService.updateContactStatus(dto.getStatus(), dto.getId());
                    result = new ResponseMessageDTO("SUCCESS", "");
                    httpStatus = HttpStatus.OK;
                } else if (dto.getStatus() == 2) {
                    contactService.deleteContactById(dto.getId());
                    result = new ResponseMessageDTO("SUCCESS", "");
                    httpStatus = HttpStatus.OK;
                } else {
                    logger.error("updateContactStatus: INVALID REQUEST BODY");
                    result = new ResponseMessageDTO("FAILED", "E49");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                logger.error("updateContactStatus: INVALID REQUEST BODY");
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("updateContactStatus: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // get list contact for recharge
    @GetMapping("contact/recharge")
    private ResponseEntity<List<ContactRechargeDTO>> getContactForRecharge(
            @RequestParam(value = "userId") String userId) {
        List<ContactRechargeDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            result = contactService.getContactRecharge(userId);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("Error at getContactForRecharge: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // get list contact approved with filter
    @GetMapping("contact/list")
    public ResponseEntity<List<ContactListDTO>> getContactListApprovedFilters(
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "type") int type,
            @RequestParam(value = "offset") int offset) {
        List<ContactListDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            if (userId != null && !userId.trim().isEmpty()) {
                List<ContactEntity> contactList = new ArrayList<>();
                if (type == 9) {
                    contactList = contactService.getContactApprovedByUserIdWithPagging(userId,
                            offset);
                } else if (type == 8) {
                    contactList = contactService.getContactPublicByUserIdWithPagging(offset);
                } else {
                    contactList = contactService.getContactApprovedByUserIdAndStatusWithPagging(userId, type, offset);
                }
                if (contactList != null && !contactList.isEmpty()) {
                    for (ContactEntity entity : contactList) {
                        ContactListDTO contactListDTO = new ContactListDTO();
                        contactListDTO.setId(entity.getId());
                        contactListDTO.setNickname(entity.getNickname());
                        contactListDTO.setStatus(entity.getStatus());
                        contactListDTO.setType(entity.getType());
                        contactListDTO.setColorType(entity.getColorType());
                        if (entity.getType() == 1) {
                            contactListDTO.setDescription("VietQR ID");
                            String imgId = contactService.getImgIdByWalletId(entity.getValue());
                            if (imgId != null) {
                                contactListDTO.setImgId(imgId);
                            } else {
                                contactListDTO.setImgId("");
                            }
                        } else if (entity.getType() == 2) {
                            // find bankName by bankTypeId
                            String description = "";
                            String imgId = "";
                            if (entity.getBankTypeId() != null && !entity.getBankTypeId().isEmpty()
                                    && entity.getBankAccount() != null && !entity.getBankAccount().isEmpty()) {
                                BankTypeEntity bankTypeEntity = bankTypeService
                                        .getBankTypeById(entity.getBankTypeId());
                                if (bankTypeEntity != null) {
                                    description = bankTypeEntity.getBankShortName() + " - "
                                            + entity.getBankAccount();
                                    imgId = bankTypeEntity.getImgId();
                                }
                            }
                            contactListDTO.setDescription(description.trim());
                            contactListDTO.setImgId(imgId);
                        } else {
                            contactListDTO.setDescription("Khác");
                            contactListDTO.setImgId(entity.getImgId());
                        }
                        result.add(contactListDTO);
                    }
                }
                httpStatus = HttpStatus.OK;
            } else {
                logger.error("getContactListApproved: INVALID USER ID");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("getContactListApproved: ERROR: " + e.toString());
            System.out.println("getContactListApproved: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // get list contact approved
    @GetMapping("contact/list-approved/{userId}")
    public ResponseEntity<List<ContactListDTO>> getContactListApproved(@PathVariable(value = "userId") String userId) {
        List<ContactListDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            if (userId != null && !userId.trim().isEmpty()) {
                List<ContactEntity> contactList = contactService.getContactApprovedByUserId(userId);
                if (contactList != null && !contactList.isEmpty()) {
                    for (ContactEntity entity : contactList) {
                        ContactListDTO contactListDTO = new ContactListDTO();
                        contactListDTO.setId(entity.getId());
                        contactListDTO.setNickname(entity.getNickname());
                        contactListDTO.setStatus(entity.getStatus());
                        contactListDTO.setType(entity.getType());
                        contactListDTO.setColorType(entity.getColorType());
                        if (entity.getType() == 1) {
                            contactListDTO.setDescription("VietQR ID");
                            String imgId = contactService.getImgIdByWalletId(entity.getValue());
                            if (imgId != null) {
                                contactListDTO.setImgId(imgId);
                            } else {
                                contactListDTO.setImgId("");
                            }
                        } else if (entity.getType() == 2) {
                            // find bankName by bankTypeId
                            String description = "";
                            String imgId = "";
                            if (entity.getBankTypeId() != null && !entity.getBankTypeId().isEmpty()
                                    && entity.getBankAccount() != null && !entity.getBankAccount().isEmpty()) {
                                BankTypeEntity bankTypeEntity = bankTypeService.getBankTypeById(entity.getBankTypeId());
                                if (bankTypeEntity != null) {
                                    description = bankTypeEntity.getBankShortName() + " - " + entity.getBankAccount();
                                    imgId = bankTypeEntity.getImgId();
                                }
                            }
                            contactListDTO.setDescription(description.trim());
                            contactListDTO.setImgId(imgId);
                        } else {
                            contactListDTO.setDescription("Khác");
                            contactListDTO.setImgId(entity.getImgId());
                        }
                        result.add(contactListDTO);
                    }
                }
                httpStatus = HttpStatus.OK;
            } else {
                logger.error("getContactListApproved: INVALID USER ID");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("getContactListApproved: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // get list contact pending
    @GetMapping("contact/list-pending/{userId}")
    public ResponseEntity<List<ContactListDTO>> getContactListPending(@PathVariable(value = "userId") String userId) {
        List<ContactListDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            if (userId != null && !userId.trim().isEmpty()) {
                List<ContactEntity> contactList = contactService.getContactPendingByUserId(userId);
                if (contactList != null && !contactList.isEmpty()) {
                    for (ContactEntity entity : contactList) {
                        ContactListDTO contactListDTO = new ContactListDTO();
                        contactListDTO.setId(entity.getId());
                        contactListDTO.setNickname(entity.getNickname());
                        contactListDTO.setStatus(entity.getStatus());
                        contactListDTO.setType(entity.getType());
                        contactListDTO.setColorType(entity.getColorType());
                        if (entity.getType() == 1) {
                            contactListDTO.setDescription("VietQR ID");
                            String imgId = contactService.getImgIdByWalletId(entity.getValue());
                            if (imgId != null) {
                                contactListDTO.setImgId(imgId);
                            } else {
                                contactListDTO.setImgId("");
                            }
                        } else if (entity.getType() == 2) {
                            // find bankName by bankTypeId
                            String description = "";
                            String imgId = "";
                            if (entity.getBankTypeId() != null && !entity.getBankTypeId().isEmpty()
                                    && entity.getBankAccount() != null && !entity.getBankAccount().isEmpty()) {
                                BankTypeEntity bankTypeEntity = bankTypeService.getBankTypeById(entity.getBankTypeId());
                                if (bankTypeEntity != null) {
                                    description = bankTypeEntity.getBankShortName() + " - " + entity.getBankAccount();
                                    imgId = bankTypeEntity.getImgId();
                                }
                            }
                            contactListDTO.setDescription(description.trim());
                            contactListDTO.setImgId(imgId);
                        } else {
                            contactListDTO.setDescription("Khác");
                            contactListDTO.setImgId(entity.getImgId());
                        }
                        result.add(contactListDTO);
                    }
                }
                httpStatus = HttpStatus.OK;
            } else {
                logger.error("getContactListApproved: INVALID USER ID");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("getContactListApproved: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // get detail
    @GetMapping("contact/{id}")
    public ResponseEntity<ContactDetailDTO> getDetailContact(@PathVariable(value = "id") String id) {
        ContactDetailDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (id != null && !id.isEmpty()) {
                ContactEntity entity = contactService.getContactById(id);
                if (entity != null) {
                    result = new ContactDetailDTO();
                    result.setId(entity.getId());
                    result.setNickname(entity.getNickname());
                    result.setValue(entity.getValue());
                    result.setAdditionalData(entity.getAdditionalData());
                    result.setType(entity.getType());
                    result.setStatus(entity.getStatus());
                    String bankShortName = "";
                    String bankName = "";
                    String imgId = "";
                    if (entity.getBankTypeId() != null && !entity.getBankTypeId().isEmpty()) {
                        BankTypeEntity bankTypeEntity = bankTypeService.getBankTypeById(entity.getBankTypeId());
                        if (bankTypeEntity != null) {
                            bankShortName = bankTypeEntity.getBankShortName();
                            bankName = bankTypeEntity.getBankName();
                            imgId = bankTypeEntity.getImgId();
                        }
                    }
                    // VietQR ID
                    if (entity.getType() == 1) {
                        imgId = contactService.getImgIdByWalletId(entity.getValue());
                    } else if (entity.getType() == 3) {
                        imgId = entity.getImgId();
                    }
                    result.setBankShortName(bankShortName);
                    result.setBankName(bankName);
                    if (imgId != null) {
                        result.setImgId(imgId);
                    } else {
                        result.setImgId("");
                    }
                    result.setBankAccount(entity.getBankAccount());
                    result.setColorType(entity.getColorType());
                    httpStatus = HttpStatus.OK;
                } else {
                    logger.error("getDetailContact: RECORD NOT FOUND");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                logger.error("getDetailContact: INVALID REQUEST BODY");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("getDetailContact: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // remove contact
    @DeleteMapping("contact/remove/{id}")
    public ResponseEntity<ResponseMessageDTO> deleteContact(@PathVariable(value = "id") String id) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (id != null && !id.isEmpty()) {
                contactService.deleteContactById(id);
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            } else {
                logger.error("deleteContact: INVALID REQUEST BODY");
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("deleteContact: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // update contact
    // CANNOT UPDATE QR VALUE SO CANNOT UPDATE BANKACCOUNT AND BANKTYPE ID
    @PostMapping("contact/update")
    public ResponseEntity<ResponseMessageDTO> updateContact(@RequestBody ContactUpdateDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null) {
                contactService.updateContact(dto.getNickName(), dto.getType(), dto.getAdditionalData(), dto.getId());
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            } else {
                logger.error("updateContact: INVALID REQUEST BODY");
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("updateContact: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // update contact
    // CANNOT UPDATE QR VALUE SO CANNOT UPDATE BANKACCOUNT AND BANKTYPE ID
    @PostMapping("contact-qr/update")
    public ResponseEntity<ResponseMessageDTO> updateContactMultipart(@ModelAttribute ContactUpdateMultipartDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null) {
                contactService.updateContactMultipart(dto.getNickName(), dto.getAdditionalData(), dto.getColorType(),
                        dto.getId());
                if (dto.getImage() != null) {
                    if (dto.getImgId() != null && !dto.getImgId().trim().isEmpty()) {
                        String fileName = StringUtils.cleanPath(dto.getImage().getOriginalFilename());
                        // System.out.println(fileName);
                        imageService.updateImage(dto.getImage().getBytes(), fileName, dto.getImgId());
                    } else {
                        UUID uuidImage = UUID.randomUUID();
                        String fileName = StringUtils.cleanPath(dto.getImage().getOriginalFilename());
                        ImageEntity imageEntity = new ImageEntity(uuidImage.toString(), fileName,
                                dto.getImage().getBytes());
                        imageService.insertImage(imageEntity);
                        // update contact image
                        contactService.updateImgIdById(uuidImage.toString(), dto.getId());
                    }
                }
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            } else {
                logger.error("updateContact: INVALID REQUEST BODY");
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            logger.error("updateContact: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // get contact info when scan
    @GetMapping("contact/scan-result/{walletId}")
    public ResponseEntity<ContactScanResultDTO> scanContactResult(@PathVariable(value = "walletId") String walletId) {
        ContactScanResultDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (walletId != null && !walletId.isEmpty()) {
                String userId = accountWalletService.getUserIdByWalletId(walletId);
                if (userId != null && !userId.isEmpty()) {
                    AccountInformationEntity accountInformationEntity = accountInformationService
                            .getAccountInformation(userId);
                    if (accountInformationEntity != null) {
                        String nickname = accountInformationEntity.getLastName() + " "
                                + accountInformationEntity.getMiddleName() + " "
                                + accountInformationEntity.getFirstName();
                        String imgId = contactService.getImgIdByWalletId(walletId);
                        if (imgId != null) {
                            result = new ContactScanResultDTO(nickname.trim(), imgId);
                        } else {
                            result = new ContactScanResultDTO(nickname.trim(), "");
                        }
                        httpStatus = HttpStatus.OK;
                    } else {
                        logger.error("scanContactResult: USER INFO NOT FOUND");
                        httpStatus = HttpStatus.BAD_REQUEST;
                    }
                } else {
                    logger.error("scanContactResult: USER ID NOT FOUND");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                logger.error("scanContactResult: INVALID WALLET ID");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("scanContactResult: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }
}
