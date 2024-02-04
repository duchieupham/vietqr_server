package com.vietqr.org.controller;

import com.vietqr.org.dto.*;
import com.vietqr.org.entity.AccountBankReceiveShareEntity;
import com.vietqr.org.service.AccountBankReceiveShareService;
import com.vietqr.org.service.AccountInformationService;
import com.vietqr.org.util.FormatUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class TerminalMemberController {
    private static final Logger logger = Logger.getLogger(TerminalMemberController.class);

    @Autowired
    private AccountBankReceiveShareService accountBankReceiveShareService;

    @Autowired
    private AccountInformationService accountInformationService;

    @PostMapping("terminal-member")
    public ResponseEntity<ResponseMessageDTO> addMemberToTerminal(@Valid @RequestBody TerminalMemberInsertDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            List<String> bankIds = accountBankReceiveShareService.getBankIdsFromTerminalId(dto.getTerminalId());
            List<AccountBankReceiveShareEntity> entities = new ArrayList<>();
            // add member to group terminal
            AccountBankReceiveShareEntity entityMember = new AccountBankReceiveShareEntity();
            entityMember.setId(UUID.randomUUID().toString());
            entityMember.setBankId("");
            entityMember.setUserId(dto.getUserId());
            entityMember.setOwner(false);
            entityMember.setQrCode("");
            entityMember.setTerminalId(dto.getTerminalId());
            entities.add(entityMember);

            // share bank to member
            if (!FormatUtil.isListNullOrEmpty(bankIds)) {
                for (String bankId : bankIds) {
                    AccountBankReceiveShareEntity entity = new AccountBankReceiveShareEntity();
                    entity.setId(UUID.randomUUID().toString());
                    entity.setBankId(bankId);
                    entity.setUserId(dto.getUserId());
                    entity.setOwner(false);
                    entity.setQrCode("");
                    entity.setTerminalId(dto.getTerminalId());
                    entities.add(entity);
                }
            }

            accountBankReceiveShareService.insertAccountBankReceiveShare(entities);

            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "Unexpected Error.");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @DeleteMapping("terminal-member/remove")
    public ResponseEntity<ResponseMessageDTO> removeMemberFromTerminal(@Valid @RequestBody TerminalMemberRemoveDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            accountBankReceiveShareService
                    .removeMemberFromTerminal(dto.getTerminalId(), dto.getUserId());

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
                        // 2. check user existed from account bank receive share
                        String checkExisted = accountBankReceiveShareService.checkUserExistedFromTerminal(terminalId, dto.getId());
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
                            String checkExisted = accountBankReceiveShareService.checkUserExistedFromTerminal(terminalId,
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
            logger.error("MEMBER: member:checkAndSearchTerminalMember ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
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
            result = accountBankReceiveShareService.getMembersFromTerminalId(terminalId);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("MEMBER: member: getMembersFromTerminalId ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

}
