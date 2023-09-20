package com.vietqr.org.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.TransactionReceiveLogEntity;
import com.vietqr.org.repository.TransactionReceiveLogRepository;

@Service
public class TransactionReceiveLogServiceImpl implements TransactionReceiveLogService {

    @Autowired
    TransactionReceiveLogRepository repo;

    @Override
    public int insert(TransactionReceiveLogEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

    @Override
    public List<TransactionReceiveLogEntity> getTransactionReceiveLogsByTransId(String transactionId) {
        return repo.getTransactionReceiveLogs(transactionId);
    }

}
