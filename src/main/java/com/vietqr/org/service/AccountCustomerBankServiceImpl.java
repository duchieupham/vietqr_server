package com.vietqr.org.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public void removeBankAccountFromCustomerSync(String bankId, String customerSyncId) {
        repo.removeBankAccountFromCustomerSync(bankId, customerSyncId);
    }

    @Override
    public List<String> getBankIdsByCustomerSyncId(String customerSyncId) {
        return repo.getBankIdsByCustomerSyncId(customerSyncId);
    }

}
