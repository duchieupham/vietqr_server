package com.vietqr.org.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vietqr.org.entity.TransactionWalletEntity;

import org.springframework.beans.factory.annotation.Autowired;
import com.vietqr.org.repository.TransactionWalletRepository;

@Service
public class TransactionWalletServiceImpl implements TransactionWalletService {

    @Autowired
    TransactionWalletRepository repo;

    @Override
    public int insertTransactionWallet(TransactionWalletEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

    @Override
    public TransactionWalletEntity getTransactionWalletById(String id) {
        return repo.getTransactionWalletById(id);
    }

    @Override
    public List<TransactionWalletEntity> getTransactionWalletsByUserId(String userId) {
        return repo.getTransactionWalletsByUserId(userId);
    }

    @Override
    public List<TransactionWalletEntity> getTransactionWallets() {
        return getTransactionWallets();
    }

    @Override
    public void updateTransactionWalletStatus(int status, String billNumber, String amount) {
        repo.updateTransactionWalletStatus(status, billNumber, amount);
    }

}
