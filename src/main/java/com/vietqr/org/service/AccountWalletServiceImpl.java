package com.vietqr.org.service;

import com.vietqr.org.dto.IBalanceAndScoreDTO;
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
    public IBalanceAndScoreDTO getBalanceAndScore(String userId) {
        return repo.getBalanceAndScore(userId);
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

    @Override
    public String getUserIdByWalletId(String walletId) {
        return repo.getUserIdByWalletId(walletId);
    }

    @Override
    public void updateAmount(String amount, String id) {
        repo.updateAmount(amount, id);
    }
}
