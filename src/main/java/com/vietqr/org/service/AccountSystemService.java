package com.vietqr.org.service;

import org.springframework.stereotype.Service;

import com.vietqr.org.entity.AccountSystemEntity;

@Service
public interface AccountSystemService {
    public int insertNewAdmin(AccountSystemEntity entity);

    public AccountSystemEntity loginAdmin(String username, String password);

    public String checkExistedAdmin(String id);
}
