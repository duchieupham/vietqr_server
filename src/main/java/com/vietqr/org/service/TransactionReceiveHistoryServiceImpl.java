package com.vietqr.org.service;

import com.vietqr.org.entity.TransactionReceiveHistoryEntity;
import com.vietqr.org.repository.TransactionReceiveHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionReceiveHistoryServiceImpl implements TransactionReceiveHistoryService {

    @Autowired
    private TransactionReceiveHistoryRepository repo;

    @Override
    public int insertTransactionReceiveHistory(TransactionReceiveHistoryEntity transactionReceiveHistory) {
        return repo.save(transactionReceiveHistory) != null ? 1 : 0;
    }
}
