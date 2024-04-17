package com.vietqr.org.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.vietqr.org.dto.*;
import com.vietqr.org.entity.MerchantEntity;
import com.vietqr.org.entity.MerchantMemberEntity;
import com.vietqr.org.entity.MerchantMemberRoleEntity;
import com.vietqr.org.service.AccountInformationService;
import com.vietqr.org.service.MerchantMemberRoleService;
import com.vietqr.org.service.MerchantMemberService;
import com.vietqr.org.service.MerchantService;
import com.vietqr.org.util.EnvironmentUtil;
import com.vietqr.org.util.FormatUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class MerchantController {
    private static final Logger logger = Logger.getLogger(MerchantController.class);

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private MerchantMemberService merchantMemberService;

    @Autowired
    private MerchantMemberRoleService merchantMemberRoleService;

    @Autowired
    private AccountInformationService accountInformationService;

    @PostMapping("merchant")
    public ResponseEntity<ResponseMessageDTO> createMerchant(@RequestBody @Valid MerchantRequestDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            MerchantEntity entity = new MerchantEntity();
            String merchantId = UUID.randomUUID().toString();
            entity.setId(merchantId);
            entity.setName(dto.getName());
            entity.setAddress("");
            LocalDateTime now = LocalDateTime.now();
            long time = now.toEpochSecond(ZoneOffset.UTC);
            entity.setTimeCreated(time);
            entity.setTimePublish(time);
            entity.setActive(true);
            entity.setMaster(false);
            entity.setVsoCode("");
            entity.setBusinessSector("");
            entity.setBusinessType(0);
            entity.setTaxId("");
            entity.setUserId(dto.getUserId());
            entity.setType(0);
            entity.setAccountCustomerMerchantId("");
            entity.setRefId("");
            entity.setPublicId(UUID.randomUUID().toString());

            MerchantMemberEntity merchantMemberEntity = new MerchantMemberEntity();
            String merchantMemberId = UUID.randomUUID().toString();
            merchantMemberEntity.setId(merchantMemberId);
            merchantMemberEntity.setMerchantId(merchantId);
            merchantMemberEntity.setActive(true);
            merchantMemberEntity.setUserId(dto.getUserId());
            merchantMemberEntity.setTimeAdded(time);
            merchantMemberEntity.setTerminalId("");

            List<String> roleReceives = new ArrayList<>();
            List<String> roleRefunds = new ArrayList<>();
            roleReceives.add(EnvironmentUtil.getAdminRoleId());
//            roleRefunds.add(EnvironmentUtil.getOnlyReadReceiveTerminalRoleId());
            MerchantMemberRoleEntity merchantMemberRoleEntity = new MerchantMemberRoleEntity();
            merchantMemberRoleEntity.setId(UUID.randomUUID().toString());
            merchantMemberRoleEntity.setMerchantMemberId(merchantMemberId);
            merchantMemberRoleEntity.setUserId(dto.getUserId());
            merchantMemberRoleEntity.setTransReceiveRoleIds(mapper
                    .writeValueAsString(roleReceives));
            merchantMemberRoleEntity.setTransRefundRoleIds(mapper
                    .writeValueAsString(roleRefunds));

            merchantMemberRoleService.insert(merchantMemberRoleEntity);
            merchantMemberService.insert(merchantMemberEntity);
            merchantService.insertMerchant(entity);
            result = new ResponseMessageDTO("SUCCESS", merchantId);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("createMerchant: ERROR: " + e.getMessage() + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("merchant/{userId}")
    public ResponseEntity<List<MerchantResponseDTO>> getMerchantsByUserId(@PathVariable String userId,
                                                                          @RequestParam int offset) {
        List<MerchantResponseDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            result = merchantService.getMerchantsByUserId(userId, offset);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("getMerchantsByUserId: ERROR: " + e.getMessage() + " at: " + System.currentTimeMillis());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("merchant-list/{userId}")
    public ResponseEntity<List<MerchantResponseListDTO>> getMerchantsByUserId(@PathVariable String userId) {
        List<MerchantResponseListDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            result = merchantService.getMerchantsByUserId(userId);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("getMerchantsByUserId: ERROR: " + e.getMessage() + " at: " + System.currentTimeMillis());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @DeleteMapping("merchant/inactive")
    public ResponseEntity<ResponseMessageDTO> inactiveMerchant(@Valid @RequestBody InactiveMerchantDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            int checkResult = merchantService.inactiveMerchantByMerchantId(dto.getMerchantId(), dto.getUserId());
            if (checkResult == 0) {
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            } else {
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            }
        } catch (Exception e) {
            logger.error("inactiveMerchant: ERROR: " + e.getMessage() + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

//    @GetMapping("merchant-member/{merchantId}")
//    public ResponseEntity<List<AccountMemberDTO>> getMemberByMerchantId(@PathVariable String merchantId) {
//        List<AccountMemberDTO> result = new ArrayList<>();
//        HttpStatus httpStatus = null;
//        try {
//            result = merchantMemberService.getMerchantMembersByUserId(merchantId);
//            httpStatus = HttpStatus.OK;
//        } catch (Exception e) {
//            logger.error("getMemberByMerchantId: ERROR: " + e.getMessage() + " at: " + System.currentTimeMillis());
//            httpStatus = HttpStatus.BAD_REQUEST;
//        }
//        return new ResponseEntity<>(result, httpStatus);
//    }

    @DeleteMapping("merchant-member/remove")
    public ResponseEntity<ResponseMessageDTO> removeMemberFromMerchant(
            @Valid @RequestBody MerchantMemberRemoveDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            String merchantMemberId = merchantMemberService
                    .checkUserExistedFromMerchant(dto.getMerchantId(), dto.getUserId());
            if (merchantMemberId == null || merchantMemberId.isEmpty()) {
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            } else {
                merchantMemberRoleService.removeMerchantMemberRole(merchantMemberId, dto.getUserId());
                merchantMemberService.removeMemberFromMerchant(dto.getMerchantId(), dto.getUserId());
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            }
        } catch (Exception e) {
            logger.error("removeMemberFromMerchant: ERROR: " + e.getMessage() + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("merchant-member/search")
    public ResponseEntity<Object> getMemberByMerchantId(
            @RequestParam(value = "type") int type,
            @RequestParam(value = "value") String value,
            @RequestParam(value = "merchantId") String merchantId
    ) {
        Object result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            List<AccountSearchMemberDTO> searchResult = new ArrayList<>();
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
                        // 2. check user existed from merchant
                        String checkExisted = merchantMemberService.checkUserExistedFromMerchant(merchantId, dto.getId());
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
                            String checkExisted = merchantMemberService.checkUserExistedFromMerchant(merchantId,
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
                        if (!searchResult.isEmpty()) {
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
            logger.error("getMemberByMerchantId: ERROR: " + e.getMessage() + " at: " + System.currentTimeMillis());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }
}
