package com.vietqr.org.service;

import org.springframework.stereotype.Service;
import java.util.List;
import com.vietqr.org.entity.TransactionWalletEntity;

@Service
public interface TransactionWalletService {

    public int insertTransactionWallet(TransactionWalletEntity entity);

    public TransactionWalletEntity getTransactionWalletById(String id);

    public TransactionWalletEntity getTransactionWalletByBillNumber(String billNumber);

    public List<TransactionWalletEntity> getTransactionWalletsByUserId(String userId);

    public List<TransactionWalletEntity> getTransactionWallets();

    public void updateTransactionWalletStatus(int status, long timePaid, String id);
}
