package com.vietqr.org.service;

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

}
