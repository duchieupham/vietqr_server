package com.vietqr.org.controller;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.validation.Valid;

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

import com.vietqr.org.dto.AccountMemberBranchDTO;
import com.vietqr.org.dto.AccountSearchDTO;
import com.vietqr.org.dto.AccountSearchMemberDTO;
import com.vietqr.org.dto.BranchMemberInsertDTO;
import com.vietqr.org.dto.MemberDeleteInputDTO;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.entity.BranchMemberEntity;
import com.vietqr.org.entity.FcmTokenEntity;
import com.vietqr.org.entity.NotificationEntity;
import com.vietqr.org.service.AccountInformationService;
import com.vietqr.org.service.BranchMemberService;
import com.vietqr.org.service.FcmTokenService;
import com.vietqr.org.service.FirebaseMessagingService;
import com.vietqr.org.service.NotificationService;
import com.vietqr.org.util.NotificationUtil;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class BranchMemberController {
    private static final Logger logger = Logger.getLogger(BranchMemberController.class);

    @Autowired
    BranchMemberService branchMemberService;

    @Autowired
    AccountInformationService accountInformationService;

    @Autowired
    FirebaseMessagingService firebaseMessagingService;

    @Autowired
    FcmTokenService fcmTokenService;

    @Autowired
    NotificationService notificationService;

    // check and search informatio member existed in business member or other
    // branchMember
    @GetMapping("branch-member/search/{phoneNo}/{businessId}")
    public ResponseEntity<Object> checkAndSearchMemberFromBusiness(@Valid @PathVariable("phoneNo") String phoneNo,
            @Valid @PathVariable("businessId") String businessId) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            // 1. search user from system
            AccountSearchDTO dto = accountInformationService.getAccountSearch(phoneNo);
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
                // 2. check user existed from business
                String checkExisted = branchMemberService.checkUserExistedFromBusiness(businessId, dto.getId());
                if (checkExisted != null && !checkExisted.isEmpty()) {
                    // existed
                    accountSearchMemberDTO.setExisted(1);
                } else {
                    // not existed
                    accountSearchMemberDTO.setExisted(0);
                }
                result = accountSearchMemberDTO;
                httpStatus = HttpStatus.OK;
            }
        } catch (Exception e) {
            logger.error("MEMBER: branch-member:checkAndSearchMemberFromBusiness ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<Object>(result, httpStatus);
    }

    // 0: phone
    // 1: name
    @GetMapping("branch-member/search")
    public ResponseEntity<Object> checkAndSearchMemberFromBusinessByFullname(
            @RequestParam(value = "type") int type,
            @RequestParam(value = "value") String value,
            @RequestParam(value = "businessId") String businessId) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            // check type
            if (type == 0) {
                // 0: phone
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
                    // 2. check user existed from business
                    String checkExisted = branchMemberService.checkUserExistedFromBusiness(businessId, dto.getId());
                    if (checkExisted != null && !checkExisted.isEmpty()) {
                        // existed
                        accountSearchMemberDTO.setExisted(1);
                    } else {
                        // not existed
                        accountSearchMemberDTO.setExisted(0);
                    }
                    result = accountSearchMemberDTO;
                    httpStatus = HttpStatus.OK;
                }
            } else if (type == 1) {
                // 1: name
                List<AccountSearchMemberDTO> searchResult = new ArrayList<>();
                List<AccountSearchDTO> dtos = accountInformationService.getAccountSearchByFullname(value);

                if (dtos != null && !dtos.isEmpty()) {
                    for (AccountSearchDTO search : dtos) {
                        String checkExisted = branchMemberService.checkUserExistedFromBusiness(businessId,
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
            } else {
                // error
                result = new ResponseMessageDTO("FAILED", "E88");
                httpStatus = HttpStatus.BAD_REQUEST;
            }

        } catch (Exception e) {
            logger.error("MEMBER: branch-member:checkAndSearchMemberFromBusiness ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
        }
        return new ResponseEntity<Object>(result, httpStatus);
    }

    @PostMapping("branch-member")
    public ResponseEntity<ResponseMessageDTO> insertBranchMember(@Valid @RequestBody BranchMemberInsertDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            UUID uuid = UUID.randomUUID();
            BranchMemberEntity entity = new BranchMemberEntity();
            entity.setId(uuid.toString());
            entity.setBranchId(dto.getBranchId());
            entity.setBusinessId(dto.getBusinessId());
            entity.setUserId(dto.getUserId());
            entity.setRole(dto.getRole());
            int check = branchMemberService.insertBranchMember(entity);
            if (check == 1) {
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
                // push notification
                LocalDateTime currentDateTime = LocalDateTime.now();
                long time = currentDateTime.toEpochSecond(ZoneOffset.UTC);
                UUID notificationUUID = UUID.randomUUID();
                String title = NotificationUtil.getNotiTitleAddMember();
                String message = NotificationUtil.getNotiDescAddMember();
                NotificationEntity notiEntity = new NotificationEntity();
                notiEntity.setId(notificationUUID.toString());
                notiEntity.setRead(false);
                notiEntity.setMessage(message);
                notiEntity.setTime(time);
                notiEntity.setType(NotificationUtil.getNotiTypeAddMember());
                notiEntity.setUserId(dto.getUserId());
                notiEntity.setData(dto.getBusinessId());
                notificationService.insertNotification(notiEntity);
                List<FcmTokenEntity> fcmTokens = new ArrayList<>();
                fcmTokens = fcmTokenService.getFcmTokensByUserId(dto.getUserId());
                Map<String, String> data = new HashMap<>();
                data.put("notificationType", NotificationUtil.getNotiTypeAddMember());
                data.put("notificationId", notificationUUID.toString());
                data.put("businessId", dto.getBusinessId());
                firebaseMessagingService.sendUsersNotificationWithData(data, fcmTokens,
                        title,
                        message);
            } else {
                result = new ResponseMessageDTO("FAILED", "E05");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("MEMBER: branch-member: insert ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @DeleteMapping("branch-member/remove")
    public ResponseEntity<ResponseMessageDTO> removeMemberFromBusiness(@Valid @RequestBody MemberDeleteInputDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            branchMemberService.removeMemberFromBusiness(dto.getUserId(), dto.getBankId());
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
            // push notification
            // LocalDateTime currentDateTime = LocalDateTime.now();
            // long time = currentDateTime.toEpochSecond(ZoneOffset.UTC);
            // UUID notificationUUID = UUID.randomUUID();
            // String title = NotificationUtil.getNotiTitleRemoveMember();
            // String message = NotificationUtil.getNotiDescRemoveMember();
            // NotificationEntity notiEntity = new NotificationEntity();
            // notiEntity.setId(notificationUUID.toString());
            // notiEntity.setRead(false);
            // notiEntity.setMessage(message);
            // notiEntity.setTime(time);
            // notiEntity.setType(NotificationUtil.getNotiTypeRemoveMember());
            // notiEntity.setUserId(dto.getUserId());
            // notiEntity.setData(dto.getBusinessId());
            // notificationService.insertNotification(notiEntity);
            // List<FcmTokenEntity> fcmTokens = new ArrayList<>();
            // fcmTokens = fcmTokenService.getFcmTokensByUserId(dto.getUserId());
            // Map<String, String> data = new HashMap<>();
            // data.put("notificationType", NotificationUtil.getNotiTypeRemoveMember());
            // data.put("notificationId", notificationUUID.toString());
            // firebaseMessagingService.sendUsersNotificationWithData(data, fcmTokens,
            // title,
            // message);
        } catch (Exception e) {
            logger.error("MEMBER: branch-member: removeMemberFromBusiness ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // get list member by branchId
    @GetMapping("branch-member/{branchId}")
    public ResponseEntity<List<AccountMemberBranchDTO>> getMembersFromBranch(
            @Valid @PathVariable("branchId") String branchId) {
        List<AccountMemberBranchDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            result = branchMemberService.getMembersFromBranch(branchId);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("MEMBER: branch-member: getMembersFromBranch ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

}
