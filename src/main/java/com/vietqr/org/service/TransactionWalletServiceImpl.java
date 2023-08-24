package com.vietqr.org.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vietqr.org.dto.TransWalletListDTO;
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
    public void updateTransactionWalletStatus(int status, long timePaid, String id) {
        repo.updateTransactionWalletStatus(status, timePaid, id);
    }

    @Override
    public TransactionWalletEntity getTransactionWalletByBillNumber(String billNumber) {
        return repo.getTransactionWalletByBillNumber(billNumber);
    }

    @Override
    public void updateTransactionWallet(int status, long timePaid, String amount, String userId, String otp,
            int paymentType) {
        repo.updateTransactionWallet(status, timePaid, amount, userId, otp, paymentType);
    }

    @Override
    public String checkExistedTransactionnWallet(String otp, String userId, int paymentType) {
        return repo.checkExistedTransactionnWallet(otp, userId, paymentType);
    }

    @Override
    public void updateTransactionWalletConfirm(long timeCreated, String amount, String userId, String otp,
            int paymentType) {
        repo.updateTransactionWalletConfirm(timeCreated, amount, userId, otp, paymentType);
    }

    @Override
    public List<TransWalletListDTO> getTransactionWalletList(String userId, int offset) {
        return repo.getTransactionWalletList(userId, offset);
    }

    @Override
    public List<TransWalletListDTO> getTransactionWalletListByStatus(String userId, int status, int offset) {
        return repo.getTransactionWalletListByStatus(userId, status, offset);
    }

}
