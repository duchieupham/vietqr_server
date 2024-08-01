package com.vietqr.org.service;

import org.springframework.stereotype.Service;

import com.vietqr.org.entity.AccountCustomerEntity;

@Service
public interface AccountCustomerService {

    public int insert(AccountCustomerEntity entity);

    public String getAccessKey(String password);

    String getAccessKeyByUsername(String username);

    String checkExistMerchantSyncByUsername(String username);
    String checkExistMerchantSyncByUsernameV2(String username);
}
