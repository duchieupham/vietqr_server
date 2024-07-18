package com.vietqr.org.service;

import org.springframework.stereotype.Service;

import com.vietqr.org.entity.AccountShareEntity;
import com.vietqr.org.repository.AccountShareRepository;

import org.springframework.beans.factory.annotation.Autowired;

@Service
public class AccountShareServiceImpl implements AccountShareService {

    @Autowired
    AccountShareRepository repo;

    @Override
    public int insertAccountShare(AccountShareEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

}
