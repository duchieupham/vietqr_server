package com.vietqr.org.controller;


import com.vietqr.org.dto.*;
import com.vietqr.org.entity.MerchantEntity;
import com.vietqr.org.entity.MerchantMemberEntity;
import com.vietqr.org.service.AccountInformationService;
import com.vietqr.org.service.MerchantMemberService;
import com.vietqr.org.service.MerchantService;
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
    private AccountInformationService accountInformationService;

    @PostMapping("merchant")
    public ResponseEntity<ResponseMessageDTO> createMerchant(@RequestBody @Valid MerchantRequestDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
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
            entity.setVso("");
            entity.setUserId(dto.getUserId());
            entity.setType(0);
            entity.setAccountCustomerMerchantId("");
            entity.setRefId("");
            entity.setPublicId(UUID.randomUUID().toString());
            merchantService.insertMerchant(entity);
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("merchant/{userId}")
    public ResponseEntity<List<MerchantResponseDTO>> getMerchantsByUserId(@PathVariable String userId) {
        List<MerchantResponseDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            result = merchantService.getMerchantsByUserId(userId);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
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
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("merchant-member/{merchantId}")
    public ResponseEntity<List<AccountMemberDTO>> getMemberByMerchantId(@PathVariable String merchantId) {
        List<AccountMemberDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            result = merchantMemberService.getMerchantMembersByUserId(merchantId);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("merchant-member")
    public ResponseEntity<ResponseMessageDTO> addMemberToMerchant(
            @Valid @RequestBody MerchantMemberInsertDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            MerchantMemberEntity entity = new MerchantMemberEntity();
            entity.setId(UUID.randomUUID().toString());
            entity.setActive(true);
            entity.setMerchantId(dto.getMerchantId());
            entity.setUserId(dto.getUserId());
            entity.setRole(dto.getRole());
            LocalDateTime currentDateTime = LocalDateTime.now();
            long time = currentDateTime.toEpochSecond(ZoneOffset.UTC);
            entity.setTimeAdded(time);
            merchantMemberService.insertMemberToMerchant(entity);
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @DeleteMapping("merchant-member/remove")
    public ResponseEntity<ResponseMessageDTO> removeMemberFromMerchant(
            @Valid @RequestBody MerchantMemberRemoveDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            merchantMemberService.removeMemberFromMerchant(dto.getMerchantId(), dto.getUserId());
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
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
                        // 2. check user existed from account bank receive share
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
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }
}
