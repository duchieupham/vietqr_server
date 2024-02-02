package com.vietqr.org.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vietqr.org.dto.TransactionRelatedDTO;
import com.vietqr.org.entity.TransactionReceiveBranchEntity;

@Service
public interface TransactionReceiveBranchService {
    public int insertTransactionReceiveBranch(TransactionReceiveBranchEntity entity);

    public TransactionReceiveBranchEntity getTransactionBranchByTransactionId(String transactionId);

//    public List<TransactionRelatedDTO> getTransactionsByBranchId(String branchId, int offset);
//
//    public List<TransactionRelatedDTO> getTransactionsByBusinessId(String businessId, int offset);
}
