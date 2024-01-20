package com.vietqr.org.controller;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.validation.Valid;

import com.vietqr.org.dto.*;
import com.vietqr.org.entity.AccountBankReceiveEntity;
import com.vietqr.org.entity.AccountBankReceiveShareEntity;
import com.vietqr.org.service.*;
import com.vietqr.org.util.FormatUtil;
import com.vietqr.org.util.StringUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vietqr.org.entity.FcmTokenEntity;
import com.vietqr.org.entity.NotificationEntity;
import com.vietqr.org.util.NotificationUtil;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class MemberAccountBankReceiveController {
    private static final Logger logger = Logger.getLogger(BranchMemberController.class);

    @Autowired
    AccountInformationService accountInformationService;

    @Autowired
    AccountBankReceiveShareService accountBankReceiveShareService;

    @Autowired
    AccountBankReceiveService accountBankReceiveService;

    @Autowired
    FirebaseMessagingService firebaseMessagingService;

    @Autowired
    FcmTokenService fcmTokenService;

    @Autowired
    NotificationService notificationService;

    // 0: phone
    // 1: name
    @GetMapping("member/search")
    public ResponseEntity<Object> checkAndSearchMemberFromAccountBankReceiveShareByFullName(
            @RequestParam(value = "type") int type,
            @RequestParam(value = "value") String value,
            @RequestParam(value = "bankId") String bankId) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            List<AccountSearchMemberDTO> searchResult = new ArrayList<>();
            // check type
            switch (type) {
                // 0: phone
                case 0:
                    // 1. search user from system
                    AccountSearchDTO dto = accountInformationService.getAccountSearch(value);
                    if (dto == null) {
                        result = new ResponseMessageDTO("CHECK", "C01");
                        httpStatus = HttpStatus.valueOf(201);
                    } else {
                        AccountSearchMemberDTO accountSearchMemberDTO = new AccountSearchMemberDTO();
                        accountSearchMemberDTO.setId(dto.getId());
                        accountSearchMemberDTO.setPhoneNo(dto.getPhoneNo());
                        accountSearchMemberDTO.setFirstName(dto.getFirstName());
                        accountSearchMemberDTO.setMiddleName(dto.getMiddleName());
                        accountSearchMemberDTO.setLastName(dto.getLastName());
                        accountSearchMemberDTO.setImgId(dto.getImgId());
                        // 2. check user existed from account bank receive share
                        String checkExisted = accountBankReceiveShareService.checkUserExistedFromBankReceiveShare(dto.getId(), bankId);
                        if (checkExisted != null && !checkExisted.isEmpty()) {
                            // existed
                            accountSearchMemberDTO.setExisted(1);
                        } else {
                            // not existed
                            accountSearchMemberDTO.setExisted(0);
                        }
                        searchResult.add(accountSearchMemberDTO);
                        result = searchResult;
                        httpStatus = HttpStatus.OK;
                    }
                    break;
                // 1: name
                case 1:
                    List<AccountSearchDTO> dtos = accountInformationService.getAccountSearchByFullname(value);

                    if (!FormatUtil.isListNullOrEmpty(dtos)) {
                        for (AccountSearchDTO search : dtos) {
                            String checkExisted = accountBankReceiveShareService.checkUserExistedFromBankReceiveShare(bankId,
                                    search.getId());
                            AccountSearchMemberDTO accountSearchMemberDTO = new AccountSearchMemberDTO();
                            accountSearchMemberDTO.setId(search.getId());
                            accountSearchMemberDTO.setPhoneNo(search.getPhoneNo());
                            accountSearchMemberDTO.setFirstName(search.getFirstName());
                            accountSearchMemberDTO.setMiddleName(search.getMiddleName());
                            accountSearchMemberDTO.setLastName(search.getLastName());
                            accountSearchMemberDTO.setImgId(search.getImgId());
                            if (checkExisted != null && !checkExisted.trim().isEmpty()) {
                                accountSearchMemberDTO.setExisted(1);
                            } else {
                                accountSearchMemberDTO.setExisted(0);
                            }
                            searchResult.add(accountSearchMemberDTO);

                        }
                        if (searchResult != null && !searchResult.isEmpty()) {
                            result = searchResult;
                            httpStatus = HttpStatus.OK;
                        } else {
                            result = new ResponseMessageDTO("CHECK", "C01");
                            httpStatus = HttpStatus.BAD_REQUEST;
                        }
                    } else {
                        result = new ResponseMessageDTO("CHECK", "C01");
                        httpStatus = HttpStatus.valueOf(201);
                    }
                    break;
                // error
                default:
                    result = new ResponseMessageDTO("FAILED", "E88");
                    httpStatus = HttpStatus.BAD_REQUEST;
                    break;
            }

        } catch (Exception e) {
            logger.error("MEMBER: member:checkAndSearchMemberFromAccountBankReceiveShareByFullName ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
        }
        return new ResponseEntity<Object>(result, httpStatus);
    }

    @PostMapping("member")
    public ResponseEntity<ResponseMessageDTO> insertAccountBankReceiveShareMember(@Valid @RequestBody MemberInsertDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            UUID uuid = UUID.randomUUID();
            AccountBankReceiveShareEntity entity = new AccountBankReceiveShareEntity();
            entity.setId(uuid.toString());
            entity.setBankId(dto.getBankId());
            entity.setUserId(dto.getUserId());
            entity.setOwner(false);
            int check = accountBankReceiveShareService.insertAccountBankReceiveShare(entity);
            if (check == 1) {
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
                // push notification
                // có thể bị ảnh hưởng do đã xóa khái niệm doanh nghiệp
                LocalDateTime currentDateTime = LocalDateTime.now();
                long time = currentDateTime.toEpochSecond(ZoneOffset.UTC);
                UUID notificationUUID = UUID.randomUUID();
                // title, message thay đổi
                AccountBankReceiveEntity accountBankReceiveEntity = accountBankReceiveService.getAccountBankById(dto.getBankId());
                String title = NotificationUtil.getNotiTitleAddMember();
                String message = String.format(NotificationUtil.getNotiDescAddMember(),
                        StringUtil.hiddenString(accountBankReceiveEntity.getBankAccount(), 4));
                NotificationEntity notiEntity = new NotificationEntity();
                notiEntity.setId(notificationUUID.toString());
                notiEntity.setRead(false);
                notiEntity.setMessage(message);
                notiEntity.setTime(time);
                notiEntity.setType(NotificationUtil.getNotiTypeAddMember());
                notiEntity.setUserId(dto.getUserId());
                notiEntity.setData(dto.getBankId());
                notificationService.insertNotification(notiEntity);
                List<FcmTokenEntity> fcmTokens = new ArrayList<>();
                fcmTokens = fcmTokenService.getFcmTokensByUserId(dto.getUserId());
                Map<String, String> data = new HashMap<>();
                // data thay đổi
                data.put("notificationType", NotificationUtil.getNotiTypeAddMember());
                data.put("notificationId", notificationUUID.toString());
//                data.put("bankId", dto.getBankId());
                firebaseMessagingService.sendUsersNotificationWithData(data, fcmTokens,
                        title,
                        message);
            } else {
                result = new ResponseMessageDTO("FAILED", "E05");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("MEMBER: member: insertAccountBankReceiveShareMember ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @DeleteMapping("member/remove")
    public ResponseEntity<ResponseMessageDTO> removeMemberAccountBankReceiveShare(@Valid @RequestBody MemberDeleteInputDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            accountBankReceiveShareService.removeMemberFromBankReceiveShare(dto.getUserId(), dto.getBankId());
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("MEMBER: branch-member: removeMemberAccountBankReceiveShare ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @DeleteMapping("member/remove-all")
    public ResponseEntity<ResponseMessageDTO> removeAllMemberAccountBankReceiveShare(@Valid @RequestBody MemberDeleteAllInputDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            accountBankReceiveShareService.removeAllMemberFromBankReceiveShare(dto.getBankId());
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("MEMBER: branch-member: removeAllMemberAccountBankReceiveShare ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // get list member by bankId
    @GetMapping("member/{bankId}")
    public ResponseEntity<List<AccountMemberDTO>> getMembersFromBankId(
            @Valid @PathVariable("bankId") String bankId) {
        List<AccountMemberDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            result = accountBankReceiveShareService.getMembersFromBankReceiveShare(bankId);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("MEMBER: member: getMembersFromBankId ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

}
