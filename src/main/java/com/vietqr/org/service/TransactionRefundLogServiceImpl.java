package com.vietqr.org.service;

import com.vietqr.org.entity.TransactionRefundLogEntity;
import com.vietqr.org.repository.TransactionRefundLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionRefundLogServiceImpl implements TransactionRefundLogService {

    @Autowired
    private TransactionRefundLogRepository repo;

    @Override
    public void insert(TransactionRefundLogEntity entity) {
        repo.save(entity);
    }

    @Override
    public TransactionRefundLogEntity getByTransactionRefundByReferenceNumber(String referencenumber) {
        return repo.getByTransactionRefundByReferenceNumber(referencenumber);
    }
}
