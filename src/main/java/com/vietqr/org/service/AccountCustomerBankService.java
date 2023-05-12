package com.vietqr.org.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vietqr.org.entity.AccountCustomerBankEntity;

@Service
public interface AccountCustomerBankService {

    public List<AccountCustomerBankEntity> getAccountCustomerBankByBankId(String bankId);
}
