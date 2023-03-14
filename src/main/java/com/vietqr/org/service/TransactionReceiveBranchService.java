package com.vietqr.org.service;

import org.springframework.stereotype.Service;

import com.vietqr.org.entity.TransactionReceiveBranchEntity;

@Service
public interface TransactionReceiveBranchService {
    public int insertTransactionReceiveBranch(TransactionReceiveBranchEntity entity);
}
