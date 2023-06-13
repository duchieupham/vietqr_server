package com.vietqr.org.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vietqr.org.entity.AccountCustomerBankEntity;

@Service
public interface AccountCustomerBankService {

    public int insert(AccountCustomerBankEntity entity);

    public List<AccountCustomerBankEntity> getAccountCustomerBankByBankId(String bankId);

    public String checkExistedAccountCustomerBank(String bankId, String customerSyncId);
}
