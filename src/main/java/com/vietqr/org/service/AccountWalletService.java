package com.vietqr.org.service;

import org.springframework.stereotype.Service;

import com.vietqr.org.entity.AccountWalletEntity;

@Service
public interface AccountWalletService {

    public AccountWalletEntity getAccountWalletByUserId(String userId);
}
