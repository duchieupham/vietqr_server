package com.vietqr.org.controller;

import java.util.List;
import java.util.UUID;

import com.vietqr.org.entity.*;
import com.vietqr.org.service.*;
import com.vietqr.org.util.StringUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vietqr.org.dto.AccountCusBankInsertDTO;
import com.vietqr.org.dto.AccountCusBankRemoveDTO;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.util.EnvironmentUtil;

@RestController
@CrossOrigin
@RequestMapping("/api/admin")
public class AccountCustomerBankController {
    private static final Logger logger = Logger.getLogger(AccountCustomerBankController.class);

    @Autowired
    AccountBankReceiveService accountBankReceiveService;

    @Autowired
    AccountCustomerBankService accountCustomerBankService;

    @Autowired
    MerchantConnectionService merchantConnectionService;

    @Autowired
    TerminalAddressService terminalAddressService;

    @Autowired
    BankTypeService bankTypeService;

    @Autowired
    TerminalBankService terminalBankService;

    @Autowired
    BankReceiveConnectionService bankReceiveConnectionService;

    @Autowired
    AccountBankReceivePersonalService accountBankReceivePersonalService;

    // api add bank into customer sync
    // bankAccount, customerSyncId
    @PostMapping("customer-bank")
    public ResponseEntity<ResponseMessageDTO> insertAccountCustomerBank(
            @RequestBody AccountCusBankInsertDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null) {
                // 1. check env
                if (EnvironmentUtil.isProduction() == false) {
                    String checkExistedBankAccount = "";
                    /// 1.1. test
                    // 2. check existed bank account
                    if (!StringUtil.isNullOrEmpty(dto.getBankCode())) {
                        checkExistedBankAccount = accountBankReceiveService
                                .checkExistedBankAccountByBankAccountAndBankCode(dto.getBankAccount(), dto.getBankCode());
                    } else {
                        checkExistedBankAccount = accountBankReceiveService
                                .checkExistedBankAccountByBankAccount(dto.getBankAccount());
                    }
                    if (checkExistedBankAccount != null && !checkExistedBankAccount.trim().isEmpty()) {
                        // 2.1. if existed:
                        // 2.1.1. if existed, check whether added into acc_cus_sync
                        List<String> checkExistedAccCusBank = accountCustomerBankService
                                .checkExistedAccountCustomerBankByBankAccount(dto.getBankAccount(),
                                        dto.getCustomerSyncId());
                        if (checkExistedAccCusBank != null && !checkExistedAccCusBank.isEmpty()) {
                            // 2.1.1.1. if added - show msg
                            logger.info("insertAccountCustomerBank: BANK IS ADDED INTO ACC_CUS_BANK");
                            result = new ResponseMessageDTO("FAILED", "E83");
                            httpStatus = HttpStatus.BAD_REQUEST;
                        } else {
                            // update is_sync = true in account_bank_receive
                            accountBankReceiveService.updateBankSync(true, checkExistedBankAccount);
                            // 2.1.1.2. if not
                            // added - do add
                            // acc_cus_bank
                            UUID uuid = UUID.randomUUID();
                            AccountCustomerBankEntity accountCustomerBankEntity = new AccountCustomerBankEntity();
                            accountCustomerBankEntity.setId(uuid.toString());
                            accountCustomerBankEntity.setBankAccount(dto.getBankAccount());
                            accountCustomerBankEntity.setBankId(checkExistedBankAccount);
                            accountCustomerBankEntity.setAccountCustomerId(dto.getAccountCustomerId());
                            accountCustomerBankEntity.setCustomerSyncId(dto.getCustomerSyncId());
                            accountCustomerBankService.insert(accountCustomerBankEntity);

                            try {
                                List<String> merchantConnectionByMids = merchantConnectionService.getIdMerchantConnectionByMid(dto.getCustomerSyncId());
                                if (merchantConnectionByMids != null && !merchantConnectionByMids.isEmpty()) {
                                    BankReceiveConnectionEntity bankReceiveConnectionEntity = new BankReceiveConnectionEntity();
                                    bankReceiveConnectionEntity.setId(uuid.toString());
                                    bankReceiveConnectionEntity.setBankId(checkExistedBankAccount);
                                    bankReceiveConnectionEntity.setMid(dto.getCustomerSyncId());
                                    bankReceiveConnectionEntity.setMidConnectId(merchantConnectionByMids.get(0));
                                    bankReceiveConnectionEntity.setTerminalBankId("");
                                    bankReceiveConnectionEntity.setData("{}");
                                    bankReceiveConnectionEntity.setActive(false);
                                    bankReceiveConnectionService.insert(bankReceiveConnectionEntity);
                                }
                            } catch (Exception ignored) {
                            }
                            result = new ResponseMessageDTO("SUCCESS", "");
                            httpStatus = HttpStatus.OK;
                        }
                    } else {
                        // 2.2. if not existed - do insert bank account
                        UUID uuid = UUID.randomUUID();
                        AccountBankReceiveEntity accountBankReceiveEntity = new AccountBankReceiveEntity();
                        accountBankReceiveEntity.setId(uuid.toString());
                        accountBankReceiveEntity.setBankAccount(dto.getBankAccount());
                        accountBankReceiveEntity.setBankAccountName(dto.getUserBankName());
                        String bankTypeId = EnvironmentUtil.getBankTypeIdRecharge();
                        if (!StringUtil.isNullOrEmpty(dto.getBankCode())) {
                            bankTypeId = bankTypeService.getBankTypeIdByBankCode(dto.getBankCode());
                        }
                        accountBankReceiveEntity.setBankTypeId(bankTypeId);
                        accountBankReceiveEntity.setAuthenticated(true);
                        accountBankReceiveEntity.setSync(true);
                        accountBankReceiveEntity.setWpSync(false);
                        accountBankReceiveEntity.setMmsActive(false);
                        accountBankReceiveEntity.setNationalId("");
                        accountBankReceiveEntity.setPhoneAuthenticated("");
                        accountBankReceiveEntity.setStatus(true);
                        accountBankReceiveEntity.setUserId(EnvironmentUtil.getDefaultUserIdTest());
                        accountBankReceiveEntity.setRpaSync(false);
                        accountBankReceiveEntity.setPassword("");
                        accountBankReceiveEntity.setUsername("");
                        accountBankReceiveEntity.setTerminalLength(10);
                        accountBankReceiveEntity.setValidFeeTo(0L);
                        accountBankReceiveEntity.setValidFeeFrom(0L);
                        accountBankReceiveEntity.setValidService(false);
                        accountBankReceiveService.insertAccountBank(accountBankReceiveEntity);
                        //
                        UUID uuid2 = UUID.randomUUID();
                        BankReceivePersonalEntity bankReceivePersonalEntity = new BankReceivePersonalEntity();
                        bankReceivePersonalEntity.setId(uuid2.toString());
                        bankReceivePersonalEntity.setBankId(uuid.toString());
                        bankReceivePersonalEntity.setUserId(EnvironmentUtil.getDefaultUserIdTest());
                        accountBankReceivePersonalService.insertAccountBankReceivePersonal(bankReceivePersonalEntity);
                        //
                        // 2.3 do insert acc_cus_bank
                        AccountCustomerBankEntity accountCustomerBankEntity = new AccountCustomerBankEntity();
                        UUID uuid3 = UUID.randomUUID();
                        accountCustomerBankEntity.setId(uuid3.toString());
                        accountCustomerBankEntity.setAccountCustomerId(dto.getAccountCustomerId());
                        accountCustomerBankEntity.setBankAccount(dto.getBankAccount());
                        accountCustomerBankEntity.setBankId(uuid.toString());
                        accountCustomerBankEntity.setCustomerSyncId(dto.getCustomerSyncId());
                        accountCustomerBankService.insert(accountCustomerBankEntity);
                        // merchant v2
                        try {
                            List<String> merchantConnectionByMids = merchantConnectionService.getIdMerchantConnectionByMid(dto.getCustomerSyncId());
                            if (merchantConnectionByMids != null && !merchantConnectionByMids.isEmpty()) {
                                BankReceiveConnectionEntity bankReceiveConnectionEntity = new BankReceiveConnectionEntity();
                                bankReceiveConnectionEntity.setId(uuid3.toString());
                                bankReceiveConnectionEntity.setBankId(checkExistedBankAccount);
                                bankReceiveConnectionEntity.setMid(dto.getCustomerSyncId());
                                bankReceiveConnectionEntity.setMidConnectId(merchantConnectionByMids.get(0));
                                bankReceiveConnectionEntity.setTerminalBankId("");
                                bankReceiveConnectionEntity.setData("{}");
                                bankReceiveConnectionEntity.setActive(false);
                                bankReceiveConnectionService.insert(bankReceiveConnectionEntity);
                            }
                        } catch (Exception ignored) {
                        }

                        //
                        result = new ResponseMessageDTO("SUCCESS", "");
                        httpStatus = HttpStatus.OK;
                    }
                } else {
                    /// 1.2. prod
                    // 2. check existed bank account and linked not not
                    String checkExistedBankAccount = accountBankReceiveService
                            .checkExistedBankAccountByBankAccount(dto.getBankAccount());
                    if (checkExistedBankAccount != null && !checkExistedBankAccount.trim().isEmpty()) {
                        // 2.1.1. if exsisted and linked
                        //
                        // 2.1.2. check existed acc_cus_bank
                        List<String> checkExistedAccCusBank = accountCustomerBankService
                                .checkExistedAccountCustomerBankByBankAccount(dto.getBankAccount(),
                                        dto.getCustomerSyncId());
                        if (checkExistedAccCusBank != null && !checkExistedAccCusBank.isEmpty()) {
                            // 2.1.2.1. if existed acc_cus_bank - show msg
                            logger.info("insertAccountCustomerBank: BANK IS ADDED INTO ACC_CUS_BANK");
                            result = new ResponseMessageDTO("FAILED", "E83");
                            httpStatus = HttpStatus.BAD_REQUEST;
                        } else {
                            // update is_sync = true in account bank_receive
                            accountBankReceiveService.updateBankSync(true, checkExistedBankAccount);
                            // 2.1.2.2. if not existed - do add acc_cus_bank
                            UUID uuid = UUID.randomUUID();
                            AccountCustomerBankEntity accountCustomerBankEntity = new AccountCustomerBankEntity();
                            accountCustomerBankEntity.setId(uuid.toString());
                            accountCustomerBankEntity.setBankAccount(dto.getBankAccount());
                            accountCustomerBankEntity.setBankId(checkExistedBankAccount);
                            accountCustomerBankEntity.setAccountCustomerId(dto.getAccountCustomerId());
                            accountCustomerBankEntity.setCustomerSyncId(dto.getCustomerSyncId());
                            accountCustomerBankService.insert(accountCustomerBankEntity);

                            String terminalBankIdExisted = "";
                            if (StringUtil.getValueNullChecker(dto.getBankCode()).equals("MB")) {
                                terminalBankIdExisted = terminalBankService.getIdByBankAccount(dto.getBankAccount());
                                if (!StringUtil.isNullOrEmpty(terminalBankIdExisted)) {
                                    TerminalAddressEntity terminalAddressEntity = new TerminalAddressEntity();
                                    terminalAddressEntity.setId(UUID.randomUUID().toString());
                                    terminalAddressEntity.setBankAccount(dto.getBankAccount());
                                    terminalAddressEntity.setTerminalBankId(terminalBankIdExisted);
                                    terminalAddressEntity.setBankId(checkExistedBankAccount);
                                    terminalAddressEntity.setCustomerSyncId(dto.getCustomerSyncId());

                                    terminalAddressService.insert(terminalAddressEntity);
                                }
                            }

                            try {
                                List<String> merchantConnectionByMids = merchantConnectionService.getIdMerchantConnectionByMid(dto.getCustomerSyncId());
                                if (merchantConnectionByMids != null && !merchantConnectionByMids.isEmpty()) {
                                    BankReceiveConnectionEntity bankReceiveConnectionEntity = new BankReceiveConnectionEntity();
                                    bankReceiveConnectionEntity.setId(uuid.toString());
                                    bankReceiveConnectionEntity.setBankId(checkExistedBankAccount);
                                    bankReceiveConnectionEntity.setMid(dto.getCustomerSyncId());
                                    bankReceiveConnectionEntity.setMidConnectId(merchantConnectionByMids.get(0));
                                    bankReceiveConnectionEntity.setTerminalBankId(terminalBankIdExisted);
                                    bankReceiveConnectionEntity.setData("{}");
                                    bankReceiveConnectionEntity.setActive(false);
                                    bankReceiveConnectionService.insert(bankReceiveConnectionEntity);
                                }
                            } catch (Exception ignored) {
                            }
                            result = new ResponseMessageDTO("SUCCESS", "");
                            httpStatus = HttpStatus.OK;
                        }
                    } else {
                        // 2.2.1. if not existed - show msg user not linked account
                        logger.info("insertAccountCustomerBank: BANK IS NOT LINKED INTO SYSTEM BEFORE.");
                        result = new ResponseMessageDTO("FAILED", "E84");
                        httpStatus = HttpStatus.BAD_REQUEST;
                    }
                }
            } else {
                logger.error("insertAccountCustomerBank: INVALID REQUEST BODY");
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }

        } catch (Exception e) {
            logger.error("insertAccountCustomerBank: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // api remove bank from customer sync
    @DeleteMapping("customer-bank")
    public ResponseEntity<ResponseMessageDTO> removeBankFromCustomerSync(
            @RequestBody AccountCusBankRemoveDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null) {
                accountCustomerBankService.removeBankAccountFromCustomerSync(dto.getBankId(), dto.getCustomerSyncId());
                terminalAddressService.removeBankAccountFromCustomerSync(dto.getBankId(), dto.getCustomerSyncId());
                bankReceiveConnectionService.removeBankAccountFromCustomerSync(dto.getBankId(), dto.getCustomerSyncId());
                List<AccountCustomerBankEntity> checkExistedBankAccountFromMerchant = accountCustomerBankService
                        .getAccountCustomerBankByBankId(dto.getBankId());
                if (checkExistedBankAccountFromMerchant == null || checkExistedBankAccountFromMerchant.isEmpty()) {
                    accountBankReceiveService.updateBankAccountSync(false, dto.getBankId());
                }
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            } else {
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("removeBankFromCustomerSync: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

}
