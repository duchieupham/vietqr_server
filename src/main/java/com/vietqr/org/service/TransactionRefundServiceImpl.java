package com.vietqr.org.service;

import com.vietqr.org.dto.IRefundCheckOrderDTO;
import com.vietqr.org.dto.TransactionCheckMultiTimesDTO;
import com.vietqr.org.entity.TransactionRefundEntity;
import com.vietqr.org.repository.TransactionRefundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionRefundServiceImpl implements TransactionRefundService {

    @Autowired
    private TransactionRefundRepository repo;

    @Override
    public List<IRefundCheckOrderDTO> getTotalRefundedByTransactionId(List<String> transactionIds) {
        return repo.getTotalRefundedByTransactionId(transactionIds);
    }

    @Override
    public void insert(TransactionRefundEntity entity) {
        repo.save(entity);
    }

    @Override
    public TransactionCheckMultiTimesDTO getTransactionRefundCheck(String bankAccount, String referenceNumber) {
        return repo.getTransactionRefundCheck(bankAccount, referenceNumber);
    }

    @Override
    public String checkExistRefundTransaction(String bankAccount, String referenceNumber) {
        return repo.checkExistRefundTransaction(bankAccount, referenceNumber);
    }
}
