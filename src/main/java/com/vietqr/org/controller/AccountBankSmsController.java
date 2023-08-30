package com.vietqr.org.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vietqr.org.dto.AccountBankSmsInsertDTO;
import com.vietqr.org.dto.AccountBankSmsItemDTO;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.entity.AccountBankSmsEntity;
import com.vietqr.org.service.AccountBankSmsService;
import com.vietqr.org.service.BankTypeService;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class AccountBankSmsController {
    private static final Logger logger = Logger.getLogger(AccountBankSmsController.class);

    @Autowired
    AccountBankSmsService accountBankSmsService;

    @Autowired
    BankTypeService bankTypeService;

    // api insert

    // api sync
    @PostMapping("account-bank/sms-sync")
    public ResponseEntity<ResponseMessageDTO> syncBankAccountSms(@RequestBody AccountBankSmsInsertDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null && !dto.getBankAccounts().isEmpty()) {
                List<AccountBankSmsEntity> entities = new ArrayList<>();
                for (AccountBankSmsItemDTO item : dto.getBankAccounts()) {
                    String bankTypeId = bankTypeService.getBankTypeIdByBankCode(item.getBankCode());
                    if (bankTypeId != null && !bankTypeId.trim().isEmpty()) {
                        UUID uuid = UUID.randomUUID();
                        AccountBankSmsEntity entity = new AccountBankSmsEntity();
                        entity.setId(uuid.toString());
                        entity.setSmsId(dto.getSmsId());
                        entity.setBankAccount(item.getBankAccount());
                        entity.setBankAccountName(item.getUserBankName());
                        entity.setBankTypeId(bankTypeId);
                        entity.setStatus(true);
                        entity.setType(0);
                        entities.add(entity);
                    }
                }
                //
                if (entities != null && !entities.isEmpty()) {
                    accountBankSmsService.insertAll(entities);
                }
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            } else {
                logger.error("syncBankAccountSms: INVALID REQUEST BODY");
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("syncBankAccountSms: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // api get list

    // api detail
}
