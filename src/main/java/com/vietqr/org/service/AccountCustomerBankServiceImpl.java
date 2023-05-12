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

}
