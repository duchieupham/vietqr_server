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

    @Override
    public int insertAccountWallet(AccountWalletEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

    @Override
    public void updatePointBySharingCode(long point, String sharingCode) {
        repo.updatePointBySharingCode(point, sharingCode);
    }

    @Override
    public String checkExistedWalletId(String walletId) {
        return repo.checkExistedWalletId(walletId);
    }

    @Override
    public String checkExistedSharingCode(String sharingCode) {
        return repo.checkExistedSharingCode(sharingCode);
    }

    @Override
    public void deleteAllAccountWallet() {
        repo.deleteAllAccountWallet();
    }
}
