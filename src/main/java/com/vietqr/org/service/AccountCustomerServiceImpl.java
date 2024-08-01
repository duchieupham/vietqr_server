package com.vietqr.org.service;

import com.vietqr.org.entity.MerchantSyncEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.AccountCustomerEntity;
import com.vietqr.org.repository.AccountCustomerRepository;

@Service
public class AccountCustomerServiceImpl implements AccountCustomerService {

    @Autowired
    AccountCustomerRepository repo;

    @Override
    public int insert(AccountCustomerEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

    @Override
    public String getAccessKey(String password) {
        return repo.getAccessKey(password);
    }

    @Override
    public String getAccessKeyByUsername(String username) {
        return repo.getAccessKeyByUsername(username);
    }

    @Override
    public String checkExistMerchantSyncByUsername(String username) {
        return repo.checkExistMerchantSyncByUsername(username);
    }


    @Override
    public String checkExistMerchantSyncByUsernameV2(String username) {
        return repo.checkExistMerchantSyncByUsernameV2(username);
    }

}
