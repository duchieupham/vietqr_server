package com.vietqr.org.service;

import com.vietqr.org.entity.MerchantSyncEntity;
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
