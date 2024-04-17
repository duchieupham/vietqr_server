package com.vietqr.org.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.repository.TransactionReceiveBranchRepository;
import com.vietqr.org.dto.TransactionRelatedDTO;
import com.vietqr.org.entity.TransactionReceiveBranchEntity;

@Service
public class TransactionReceiveBranchServiceImpl implements TransactionReceiveBranchService {

    @Autowired
    TransactionReceiveBranchRepository repo;

    @Override
    public int insertTransactionReceiveBranch(TransactionReceiveBranchEntity entity) {
        System.out.println("branchId services: " + entity.getBranchId());
        return repo.save(entity) == null ? 0 : 1;
    }

    @Override
    public TransactionReceiveBranchEntity getTransactionBranchByTransactionId(String transactionId) {
        return repo.getTransactionBranchByTransactionId(transactionId);
    }

//    @Override
//    public List<TransactionRelatedDTO> getTransactionsByBranchId(String branchId, int offset) {
//        return repo.getTransactionsByBranchId(branchId, offset);
//    }
//
//    @Override
//    public List<TransactionRelatedDTO> getTransactionsByBusinessId(String businessId, int offset) {
//        return repo.getTransactionsByBusinessId(businessId, offset);
//    }

}
