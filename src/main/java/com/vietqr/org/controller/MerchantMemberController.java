package com.vietqr.org.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vietqr.org.dto.*;
import com.vietqr.org.entity.MerchantMemberEntity;
import com.vietqr.org.entity.MerchantMemberRoleEntity;
import com.vietqr.org.service.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class MerchantMemberController {

    private static final Logger logger = Logger.getLogger(MerchantMemberController.class);
    private static final String MERCHANT = "Đại lý";
    private static final String TERMINAL = "Cửa hàng";

    @Autowired
    private MerchantMemberService merchantMemberService;

    @Autowired
    private TerminalService terminalService;

    @Autowired
    private AccountInformationService accountInformationService;

    @Autowired
    private MerchantMemberRoleService merchantMemberRoleService;

    @Autowired
    private TransReceiveRoleService transReceiveRoleService;

    // get search detail of merchant member
    @GetMapping("merchant-member/search/{merchantId}")
    private ResponseEntity<Object> getMerchantMemberDetailDTO(@RequestParam String phoneNo,
                                                              @PathVariable String merchantId) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            AccountSearchByPhoneNoDTO dto = accountInformationService.findAccountByPhoneNo(phoneNo);
            if (dto == null) {
                result = new ResponseMessageDTO("CHECK", "C01");
                httpStatus = HttpStatus.valueOf(201);
            } else {
                MerchantMemberDetailDTO merchantMemberDetailDTO = new MerchantMemberDetailDTO();
                merchantMemberDetailDTO.setUserId(dto.getUserId());
                merchantMemberDetailDTO.setPhoneNo(dto.getPhoneNo());
                merchantMemberDetailDTO.setFullName(dto.getFullName());
                merchantMemberDetailDTO.setImgId(dto.getImgId());
                // 2. check user existed from account bank receive share
                IMerchantMemberDetailDTO memberDTO = merchantMemberService.getUserExistedFromMerchant(merchantId, dto.getUserId());
                if (memberDTO != null) {
                    merchantMemberDetailDTO.setExisted(1);
                    if (memberDTO.getTerminalId() == null || memberDTO.getTerminalId().trim().isEmpty()) {
                        merchantMemberDetailDTO.setLevel(0);
                    } else {
                        merchantMemberDetailDTO.setLevel(1);
                    }
                    List<String> receiveRoles = mapper.readValue(memberDTO.getTransReceiveRoles(), List.class);
//                    List<String> transReceiveRoles = transReceiveRoleService.getRoleByIds(receiveRoles);
                    merchantMemberDetailDTO.setTransReceiveRoles(receiveRoles);
                    merchantMemberDetailDTO.setTransRefundRoles(new ArrayList<>());
                    List<TerminalMapperDTO> terminalMapperDTOS = terminalService.getTerminalsByUserIdAndMerchantId(dto.getUserId(), merchantId);
                    merchantMemberDetailDTO.setTerminals(terminalMapperDTOS);
                } else {
                    merchantMemberDetailDTO.setExisted(0);
                    merchantMemberDetailDTO.setLevel(0);
                    merchantMemberDetailDTO.setTransReceiveRoles(new ArrayList<>());
                    merchantMemberDetailDTO.setTransRefundRoles(new ArrayList<>());
                    merchantMemberDetailDTO.setTerminals(new ArrayList<>());
                }
                result = merchantMemberDetailDTO;
                httpStatus = HttpStatus.OK;
            }

        } catch (Exception e) {
            logger.error("getMerchantMemberDetailDTO: ERROR: " + e.getMessage() + " at: " + System.currentTimeMillis());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("merchant-member")
    public ResponseEntity<ResponseMessageDTO> addNewMemberToMerchant(
            @Valid @RequestBody MerchantMemberCreateDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<MerchantMemberRoleEntity> merchantMemberRoleEntities = new ArrayList<>();
            List<MerchantMemberEntity> merchantMemberEntities = new ArrayList<>();
            MerchantMemberEntity entity = new MerchantMemberEntity();
            String merchantMemberId = UUID.randomUUID().toString();
            entity.setId(merchantMemberId);
            entity.setMerchantId(dto.getMerchantId());
            entity.setUserId(dto.getUserId());
            entity.setActive(true);
            LocalDateTime now = LocalDateTime.now();
            long time = now.toEpochSecond(java.time.ZoneOffset.UTC);
            entity.setTimeAdded(time);
            String transRole = "";
            String transRefundRole = "";
            if (dto.getTerminalIds() !=null && !dto.getTerminalIds().isEmpty()) {
                for (String terminalId : dto.getTerminalIds()) {
                    entity.setId(UUID.randomUUID().toString());
                    entity.setTerminalId(terminalId);
                    transRole = mapper.writeValueAsString(dto.getRoleIds());
                    merchantMemberEntities.add(entity);
                }
            } else {
                entity.setTerminalId("");
                transRole = mapper.writeValueAsString(dto.getRoleIds());
                merchantMemberEntities.add(entity);
            }
            if (!merchantMemberEntities.isEmpty()) {
                for (MerchantMemberEntity memberEntity : merchantMemberEntities) {
                    MerchantMemberRoleEntity merchantMemberRoleEntity = new MerchantMemberRoleEntity();
                    merchantMemberRoleEntity.setId(UUID.randomUUID().toString());
                    merchantMemberRoleEntity.setMerchantMemberId(memberEntity.getId());
                    merchantMemberRoleEntity.setUserId(dto.getUserId());
                    merchantMemberRoleEntity.setTransReceiveRoleIds(transRole);
                    merchantMemberRoleEntity.setTransRefundRoleIds(transRefundRole);
                    merchantMemberRoleEntities.add(merchantMemberRoleEntity);
                }
            }
            merchantMemberService.insertAll(merchantMemberEntities);
            merchantMemberRoleService.insertAll(merchantMemberRoleEntities);

            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("addNewMemberToMerchant: ERROR: " + e.getMessage()
                    + " - Request: " + dto.toString() + " - MerchantId: " +
                    dto.getMerchantId() + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("merchant-member/update")
    public ResponseEntity<ResponseMessageDTO> updateMemberMemberRoleOfMerchant(
            @Valid @RequestBody MerchantMemberCreateDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<MerchantMemberRoleEntity> merchantMemberRoleEntities = new ArrayList<>();
            List<MerchantMemberEntity> merchantMemberEntities = new ArrayList<>();
            MerchantMemberEntity entity = new MerchantMemberEntity();
            String merchantMemberId = UUID.randomUUID().toString();
            entity.setId(merchantMemberId);
            entity.setMerchantId(dto.getMerchantId());
            entity.setUserId(dto.getUserId());
            entity.setActive(true);
            LocalDateTime now = LocalDateTime.now();
            long time = now.toEpochSecond(java.time.ZoneOffset.UTC);
            entity.setTimeAdded(time);
            List<String> roles = new ArrayList<>();
            String transRole = "";
            String transRefundRole = "";
            if (dto.getTerminalIds()!=null && !dto.getTerminalIds().isEmpty()) {
                for (String terminalId : dto.getTerminalIds()) {
                    entity.setId(UUID.randomUUID().toString());
                    entity.setTerminalId(terminalId);
                    transRefundRole = mapper.writeValueAsString(dto.getRoleIds());
                    merchantMemberEntities.add(entity);
                }
            } else {
                entity.setTerminalId("");
                transRole = mapper.writeValueAsString(dto.getRoleIds());
                merchantMemberEntities.add(entity);
            }
            if (!merchantMemberEntities.isEmpty()) {
                for (MerchantMemberEntity memberEntity : merchantMemberEntities) {
                    MerchantMemberRoleEntity merchantMemberRoleEntity = new MerchantMemberRoleEntity();
                    merchantMemberRoleEntity.setId(UUID.randomUUID().toString());
                    merchantMemberRoleEntity.setMerchantMemberId(memberEntity.getId());
                    merchantMemberRoleEntity.setUserId(dto.getUserId());
                    merchantMemberRoleEntity.setTransReceiveRoleIds(transRole);
                    merchantMemberRoleEntity.setTransRefundRoleIds(transRefundRole);
                    merchantMemberRoleEntities.add(merchantMemberRoleEntity);
                }
            }
            merchantMemberRoleService.deleteMerchantMemberRoleByUserIdAndMerchantId(dto.getMerchantId(), dto.getUserId());
            merchantMemberService.deleteMerchantMemberByUserIdAndMerchantId(dto.getMerchantId(), dto.getUserId());
            merchantMemberService.insertAll(merchantMemberEntities);
            merchantMemberRoleService.insertAll(merchantMemberRoleEntities);

            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("updateMemberMemberRoleOfMerchant: ERROR: " + e.getMessage() + " Request: "
                    + dto.toString() + " - MerchantId: " +
                    dto.getMerchantId() + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @DeleteMapping("merchant-member/remove/{merchantId}")
    public ResponseEntity<ResponseMessageDTO> deleteMemberFromMerchant(
            @Valid @RequestParam String userId, @PathVariable String merchantId) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            merchantMemberRoleService.deleteMerchantMemberRoleByUserIdAndMerchantId(merchantId, userId);
            merchantMemberService.deleteMerchantMemberByUserIdAndMerchantId(merchantId, userId);
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("deleteMemberFromMerchant: ERROR: " + e.getMessage() + " Request: "
                    + userId + " - MerchantId: " +
                    merchantId + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // click detail of a member in merchant
    @GetMapping("merchant-member/detail/{merchantId}")
    public ResponseEntity<Object> getMerchantMemberDetail(@PathVariable String merchantId,
                                                          @RequestParam String userId) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            IMerchantMemberDetailDTO dto = merchantMemberService.getUserExistedFromMerchant(merchantId, userId);
            if (dto == null) {
                result = new ResponseMessageDTO("CHECK", "C01");
            } else {
                AccountSearchByPhoneNoDTO accountSearchByPhoneNoDTO =
                        accountInformationService.findAccountByUserId(userId);
                MerchantMemberResponseDTO responseDTO = new MerchantMemberResponseDTO();
                responseDTO.setMerchantId(merchantId);
                responseDTO.setFullName(accountSearchByPhoneNoDTO.getFullName());
                responseDTO.setPhoneNo(accountSearchByPhoneNoDTO.getPhoneNo());
                responseDTO.setImgId(accountSearchByPhoneNoDTO.getImgId());
                responseDTO.setUserId(userId);
                if (dto.getTerminalId() == null || dto.getTerminalId().isEmpty()) {
                    responseDTO.setLevel(MERCHANT);
                } else {
                    responseDTO.setLevel(TERMINAL);
                    List<TerminalMapperDTO> terminalMapperDTOS = terminalService.getTerminalsByUserIdAndMerchantId(userId, merchantId);
                    responseDTO.setTerminals(terminalMapperDTOS);
                }
                List<String> receiveRoles = mapper.readValue(dto.getTransRefundRoles(), List.class);
                List<IRoleMemberDTO> transReceiveRoles = transReceiveRoleService.getRoleByIds(receiveRoles);
                responseDTO.setTransReceiveRoles(transReceiveRoles);
                responseDTO.setTransRefundRoles(new ArrayList<>());
                result = responseDTO;
            }
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("getMerchantMemberDetail: ERROR: " + e.getMessage() + " at: " + System.currentTimeMillis());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // get list merchant member
    @GetMapping("merchant-member/{merchantId}")
    public ResponseEntity<Object> getMerchantMembersByMerchantId(@PathVariable String merchantId,
                                                                 @RequestParam int type,
                                                                 @RequestParam String value,
                                                                 @RequestParam int page,
                                                                 @RequestParam int size) {
        PageResultDTO result = null;
        HttpStatus httpStatus = null;
        List<MerchantMemberResponseDTO> merchantMemberResponseDTOs = new ArrayList<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<IMerchantMemberDTO> dtos = new ArrayList<>();
            switch (type) {
                case 0:
                    dtos = merchantMemberService.findMerchantMemberByMerchantId(merchantId, value, (page - 1) * size, size);
                    break;
                case 1:
                    dtos = merchantMemberService.findMerchantMemberByMerchantId(merchantId, value, (page - 1) * size, size);
                    break;
                default:
                    break;
            }
            int total = merchantMemberService.countMerchantMemberByMerchantId(merchantId);
            merchantMemberResponseDTOs = dtos.stream().map(dto -> {
                MerchantMemberResponseDTO responseDTO = new MerchantMemberResponseDTO();
                responseDTO.setMerchantId(dto.getMerchantId());
                responseDTO.setUserId(dto.getUserId());
                responseDTO.setPhoneNo(dto.getPhoneNo());
                responseDTO.setFullName(dto.getFullName());
                responseDTO.setImgId(dto.getImgId());
                List<TerminalMapperDTO> terminalMapperDTOS = terminalService
                        .getTerminalsByUserIdAndMerchantId(dto.getUserId(), dto.getMerchantId());
                if (terminalMapperDTOS != null && !terminalMapperDTOS.isEmpty()) {
                    responseDTO.setLevel(TERMINAL);
                    responseDTO.setTerminals(terminalMapperDTOS);
                } else {
                    responseDTO.setLevel(MERCHANT);
                    responseDTO.setTerminals(new ArrayList<>());
                }
                List<String> receiveRoles = new ArrayList<>();
                try {
                    if (dto.getTransReceiveRoles() != null && !dto.getTransReceiveRoles().isEmpty()) {
                        receiveRoles = mapper.readValue(dto.getTransReceiveRoles(), List.class);
                    }
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                List<IRoleMemberDTO> transReceiveRoles = transReceiveRoleService.getRoleByIds(receiveRoles);
                responseDTO.setTransReceiveRoles(transReceiveRoles);
                responseDTO.setTransRefundRoles(new ArrayList<>());
                return responseDTO;
            }).collect(Collectors.toList());
            double totalPage = (double) total / size;
            result = new PageResultDTO(page, size, (int) Math.ceil(totalPage), total, merchantMemberResponseDTOs);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("getMerchantMembersByMerchantId: ERROR: " + e.getMessage() + "Request: " +
                    merchantId + " " + size + " " + page + " at: " + System.currentTimeMillis());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }
}
