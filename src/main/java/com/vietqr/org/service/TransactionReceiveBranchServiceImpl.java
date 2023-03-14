package com.vietqr.org.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.repository.TransactionReceiveBranchRepository;
import com.vietqr.org.entity.TransactionReceiveBranchEntity;

@Service
public class TransactionReceiveBranchServiceImpl implements TransactionReceiveBranchService {

    @Autowired
    TransactionReceiveBranchRepository repo;

    @Override
    public int insertTransactionReceiveBranch(TransactionReceiveBranchEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }
}
