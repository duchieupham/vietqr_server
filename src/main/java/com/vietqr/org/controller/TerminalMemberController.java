package com.vietqr.org.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vietqr.org.dto.*;
import com.vietqr.org.entity.*;
import com.vietqr.org.service.*;
import com.vietqr.org.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class TerminalMemberController {
    private static final Logger logger = Logger.getLogger(TerminalMemberController.class);

    @Autowired
    private AccountBankReceiveShareService accountBankReceiveShareService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private MerchantMemberService merchantMemberService;

    @Autowired
    private AccountInformationService accountInformationService;

    @Autowired
    private FirebaseMessagingService firebaseMessagingService;

    @Autowired
    private TerminalService terminalService;

    @Autowired
    private MerchantMemberRoleService merchantMemberRoleService;

    @Autowired
    private TerminalBankReceiveService terminalBankReceiveService;

    @Autowired
    private FcmTokenService fcmTokenService;

    @Autowired
    private SocketHandler socketHandler;

    // sync
    @PostMapping("terminal-member")
    public ResponseEntity<ResponseMessageDTO> addMemberToTerminal(@Valid @RequestBody TerminalMemberInsertDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            // old
            List<BankQRTerminalDTO> bankIds = accountBankReceiveShareService.getBankIdsFromTerminalId(dto.getTerminalId());
            List<AccountBankReceiveShareEntity> entities = new ArrayList<>();
            // add member to group terminal
            AccountBankReceiveShareEntity entity1 = new AccountBankReceiveShareEntity();
            entity1.setId(UUID.randomUUID().toString());
            entity1.setBankId("");
            entity1.setUserId(dto.getUserId());
            entity1.setOwner(false);
            entity1.setQrCode("");
            entity1.setTraceTransfer("");
            entity1.setTerminalId(dto.getTerminalId());
            entities.add(entity1);

            // share bank to member
            if (!FormatUtil.isListNullOrEmpty(bankIds)) {
                for (BankQRTerminalDTO bankId : bankIds) {
                    AccountBankReceiveShareEntity entity = new AccountBankReceiveShareEntity();
                    entity.setId(UUID.randomUUID().toString());
                    entity.setBankId(bankId.getBankId());
                    entity.setUserId(dto.getUserId());
                    entity.setOwner(false);
                    entity.setQrCode(bankId.getQrCode() + "");
                    entity.setTraceTransfer(bankId.getTraceTransfer() + "");
                    entity.setTerminalId(dto.getTerminalId());
                    entities.add(entity);
                }
            }
            accountBankReceiveShareService.insertAccountBankReceiveShare(entities);

            AccountBankReceiveShareEntity entityMember = accountBankReceiveShareService
                    .getAccountAlreadyShare(dto.getTerminalId(), dto.getUserId());
            if (entityMember == null) {
                TerminalBankReceiveEntity terminalBankReceiveEntity = terminalBankReceiveService
                        .getTerminalBankByTerminalId(dto.getTerminalId());
                entityMember = new AccountBankReceiveShareEntity();
                entityMember.setId(UUID.randomUUID().toString());
                if (terminalBankReceiveEntity != null) {
                    entityMember.setBankId(terminalBankReceiveEntity.getBankId());
                } else {
                    entityMember.setBankId("");
                }
                entityMember.setUserId(dto.getUserId());
                entityMember.setOwner(false);
                entityMember.setQrCode("");
                entityMember.setTraceTransfer("");
                entityMember.setTerminalId("");
            }
            accountBankReceiveShareService.insertAccountBankReceiveShare(entityMember);

            // new
            ObjectMapper mapper = new ObjectMapper();
            MerchantMemberEntity merchantMemberEntity = new MerchantMemberEntity();
            String merchantMemberId = UUID.randomUUID().toString();
            merchantMemberEntity.setId(merchantMemberId);
            if (dto.getMerchantId() == null || dto.getMerchantId().isEmpty()) {
                TerminalEntity terminalEntity = terminalService.findTerminalById(dto.getTerminalId());
                merchantMemberEntity.setMerchantId(terminalEntity.getMerchantId());
            } else {
                merchantMemberEntity.setMerchantId(dto.getMerchantId());
            }
            merchantMemberEntity.setMerchantId(dto.getMerchantId());
            merchantMemberEntity.setUserId(dto.getUserId());
            merchantMemberEntity.setTerminalId(dto.getTerminalId());
            merchantMemberEntity.setActive(true);
            merchantMemberEntity.setTimeAdded(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));

            MerchantMemberRoleEntity merchantMemberRoleEntity = new MerchantMemberRoleEntity();
            merchantMemberRoleEntity.setId(UUID.randomUUID().toString());
            List<String> roleReceives = new ArrayList<>();
            List<String> roleRefunds = new ArrayList<>();
            // default role
            roleReceives.add(EnvironmentUtil.getOnlyReadReceiveTerminalRoleId());
            merchantMemberRoleEntity.setTransReceiveRoleIds(mapper
                    .writeValueAsString(roleReceives));
            merchantMemberRoleEntity.setTransRefundRoleIds(mapper
                    .writeValueAsString(roleRefunds));
            merchantMemberRoleEntity.setMerchantMemberId(merchantMemberId);
            merchantMemberRoleEntity.setUserId(dto.getUserId());
            merchantMemberService.insert(merchantMemberEntity);
            merchantMemberRoleService.insert(merchantMemberRoleEntity);
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "Unexpected Error.");
            httpStatus = HttpStatus.BAD_REQUEST;
        } finally {
            if (httpStatus != null && httpStatus.is2xxSuccessful()) {
                Thread thread = new Thread(() -> {
                    TerminalEntity terminalEntity = terminalService.findTerminalById(dto.getTerminalId());
                    Map<String, String> data = new HashMap<>();
                    // insert notification
                    UUID notificationUUID = UUID.randomUUID();
                    LocalDateTime currentDateTime = LocalDateTime.now();
                    long time = currentDateTime.toEpochSecond(ZoneOffset.UTC);
                    String title = NotificationUtil.getNotiTitleAddMember();
                    String message = String.format(NotificationUtil.getNotiDescAddMember(), terminalEntity.getName());
                    NotificationEntity notiEntity = new NotificationEntity();
                    notiEntity.setId(notificationUUID.toString());
                    notiEntity.setRead(false);
                    notiEntity.setMessage(message);
                    notiEntity.setTime(time);
                    notiEntity.setType(NotificationUtil.getNotiTypeAddMember());
                    notiEntity.setUserId(dto.getUserId());
                    notiEntity.setData(terminalEntity.getCode());
                    // data thay đổi
                    data.put("notificationType", NotificationUtil.getNotiTypeAddMember());
                    data.put("notificationId", notificationUUID.toString());
                    data.put("terminalCode", terminalEntity.getCode());
                    data.put("terminalName", terminalEntity.getName());
                    pushNotification(title, message, notiEntity, data, dto.getUserId());

                });
                thread.start();
            }
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @DeleteMapping("terminal-member/remove")
    public ResponseEntity<ResponseMessageDTO> removeMemberFromTerminal(@Valid @RequestBody TerminalMemberRemoveDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            // old
            accountBankReceiveShareService
                    .removeMemberFromTerminal(dto.getTerminalId(), dto.getUserId());

            // new
            merchantMemberRoleService.deleteMerchantMemberRoleByUserIdAndTerminalId(dto.getTerminalId(), dto.getUserId());
            merchantMemberService.removeMemberFromTerminal(dto.getTerminalId(), dto.getUserId());

            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("terminal-member/search")
    public ResponseEntity<Object> checkAndSearchTerminalMember(
            @RequestParam(value = "type") int type,
            @RequestParam(value = "value") String value,
            @RequestParam(value = "terminalId") String terminalId) {
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
                        //old
                        // 2. check user existed from account bank receive share
//                        String checkExisted = accountBankReceiveShareService.checkUserExistedFromTerminal(terminalId, dto.getId());

                        // new
                        // 2. check user existed from terminal
                        TerminalEntity terminalEntity = terminalService.findTerminalById(terminalId);
                        String checkExisted = merchantMemberService
                                .checkUserExistedFromTerminal(terminalEntity.getMerchantId(), terminalId, dto.getId());
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
                        TerminalEntity terminalEntity = terminalService.findTerminalById(terminalId);
                        for (AccountSearchDTO search : dtos) {
                            // new
                            // 2. check user existed from terminal
                            String checkExisted = merchantMemberService
                                    .checkUserExistedFromTerminal(terminalEntity.getMerchantId(), terminalId, search.getId());
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
            logger.error("MEMBER: member:checkAndSearchTerminalMember ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<Object>(result, httpStatus);
    }

    // get list member by terminal id
    @GetMapping("terminal-member/{terminalId}")
    public ResponseEntity<List<AccountMemberDTO>> getMembersFromTerminalId(
            @Valid @PathVariable("terminalId") String terminalId) {
        List<AccountMemberDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            // old
//            result = accountBankReceiveShareService.getMembersFromTerminalId(terminalId);

            // new
            String merchantId = "";
            TerminalEntity entity = terminalService.findTerminalById(terminalId);
            if (entity != null) {
                merchantId = entity.getMerchantId();
                result = merchantMemberService.getMembersFromTerminalId(merchantId, terminalId);
            } else {
                result = new ArrayList<>();
            }
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("MEMBER: member: getMembersFromTerminalId ERROR: " + e.getMessage() +
                    " at: " + System.currentTimeMillis());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    private void pushNotification(String title, String message, NotificationEntity notiEntity, Map<String, String> data,
                                  String userId) {
        try {
            if (notiEntity != null) {
                notificationService.insertNotification(notiEntity);
            }
            List<FcmTokenEntity> fcmTokens = new ArrayList<>();
            fcmTokens = fcmTokenService.getFcmTokensByUserId(userId);
            firebaseMessagingService.sendUsersNotificationWithData(data,
                    fcmTokens,
                    title, message);
            socketHandler.sendMessageToUser(userId,
                    data);
        } catch (IOException e) {
            logger.error(
                    "Add member to terminal: WS: push Notification - RECHARGE ERROR: "
                            + e.toString());
        }
    }

}
