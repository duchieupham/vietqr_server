package com.vietqr.org.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.AccountWalletEntity;
import com.vietqr.org.repository.AccountWalletRepository;

@Service
public class AccountWalletServiceImpl implements AccountWalletService {
    @Autowired
    AccountWalletRepository repo;

    @Override
    public AccountWalletEntity getAccountWalletByUserId(String userId) {
        return repo.getAccountWallet(userId);
    }
}
