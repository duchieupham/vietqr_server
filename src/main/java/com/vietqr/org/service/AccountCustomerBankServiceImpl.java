package com.vietqr.org.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.dto.AccountBankReceiveServiceItemDTO;
import com.vietqr.org.dto.AccountCustomerBankInfoDTO;
import com.vietqr.org.dto.AnnualFeeBankDTO;
import com.vietqr.org.entity.AccountCustomerBankEntity;
import com.vietqr.org.repository.AccountCustomerBankRepository;

@Service
public class AccountCustomerBankServiceImpl implements AccountCustomerBankService {

    @Autowired
    AccountCustomerBankRepository repo;

    @Override
    public List<AccountCustomerBankEntity> getAccountCustomerBankByBankId(String bankId) {
        return repo.getAccountCustomerBankByBankId(bankId);
    }

    @Override
    public String checkSecretKey(String bankAccount, String customerSyncId) {
        return repo.getSecretKey(bankAccount, customerSyncId);
    }

    @Override
    public int insert(AccountCustomerBankEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

    @Override
    public String checkExistedAccountCustomerBank(String bankId, String customerSyncId) {
        return repo.checkExistedAccountCustomerBank(bankId, customerSyncId);
    }

    @Override
    public List<String> checkExistedAccountCustomerBankByBankAccount(String bankAccount, String customerSyncId) {
        return repo.checkExistedAccountCustomerBankByBankAccount(bankAccount, customerSyncId);
    }

    @Override
    public List<String> checkExistedCustomerSyncByUsername(String username) {
        return repo.checkExistedCustomerSyncByUsername(username);
    }

    @Override
    public String findUsernameByBankAccount(String bankAccount) {
        Optional<String> usernameOpt = repo.getUsernameByBankAccount(bankAccount);
        return usernameOpt.orElse(null);
    }

    @Override
    public void removeBankAccountFromCustomerSync(String bankId, String customerSyncId) {
        repo.removeBankAccountFromCustomerSync(bankId, customerSyncId);
    }

    @Override
    public List<String> getBankIdsByCustomerSyncId(String customerSyncId) {
        return repo.getBankIdsByCustomerSyncId(customerSyncId);
    }

    @Override
    public List<AnnualFeeBankDTO> getBanksAnnualFee(String customerSyncId) {
        return repo.getBanksAnnualFee(customerSyncId);
    }

    @Override
    public List<AccountBankReceiveServiceItemDTO> getBankAccountsByMerchantId(String customerSyncId) {
        return repo.getBankAccountsByMerchantId(customerSyncId);
    }

    @Override
    public String getMerchantByBankId(String bankId) {
        return repo.getMerchantByBankId(bankId);
    }

    @Override
    public String checkExistedByBankIdAndCustomerSyncId(String bankId, String customerSyncId) {
        return repo.checkExistedByBankIdAndCustomerSyncId(bankId, customerSyncId);
    }

    @Override
    public AccountCustomerBankInfoDTO getBankSizeAndAddressByCustomerSyncId(String customerSyncId) {
        return repo.getBankSizeAndAddressByCustomerSyncId(customerSyncId);
    }

    @Override
    public String checkExistedBankAccountIntoMerchant(String bankAccount, String customerSyncId) {
        return repo.checkExistedBankAccountIntoMerchant(bankAccount, customerSyncId);
    }

    @Override
    public AccountCustomerBankEntity getAccountCustomerBankByBankIdAndMerchantId(String bankId, String customerSyncId) {
        return repo.getAccountCustomerBankByBankIdAndMerchantId(bankId, customerSyncId);
    }

}
