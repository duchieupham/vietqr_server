package com.vietqr.org.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.AccountSystemEntity;
import com.vietqr.org.repository.AccountSystemRepository;

@Service
public class AccountSystemServiceImpl implements AccountSystemService {

    @Autowired
    AccountSystemRepository repo;

    @Override
    public int insertNewAdmin(AccountSystemEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

    @Override
    public AccountSystemEntity loginAdmin(String username, String password) {
        return repo.loginAdmin(username, password);
    }

}
