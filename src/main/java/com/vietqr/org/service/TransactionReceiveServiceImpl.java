package com.vietqr.org.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.repository.TransactionReceiveRepository;
import java.util.List;
import com.vietqr.org.entity.TransactionReceiveEntity;
import com.vietqr.org.dto.TransactionDetailDTO;
import com.vietqr.org.dto.TransactionRelatedDTO;

@Service
public class TransactionReceiveServiceImpl implements TransactionReceiveService {

    @Autowired
    TransactionReceiveRepository repo;

    @Override
    public int insertTransactionReceive(TransactionReceiveEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

    @Override
    public void updateTransactionReceiveStatus(int status, String refId, String referenceNumber, String id) {
        repo.updateTransactionReceiveStatus(status, refId, referenceNumber, id);
    }

    @Override
    public List<TransactionRelatedDTO> getRelatedTransactionReceives(String businessId) {
        return repo.getRelatedTransactionReceives(businessId);
    }

    @Override
    public TransactionDetailDTO getTransactionById(String id) {
        return repo.getTransactionById(id);
    }

    @Override
    public TransactionReceiveEntity getTransactionByTraceIdAndAmount(String id, String amount, String transType) {
        Long amountParsed = Long.parseLong(amount);
        return repo.getTransactionByTraceId(id, amountParsed, transType);
    }

    @Override
    public List<TransactionReceiveEntity> getTransactionByBankId(String bankId) {
        return repo.getRelatedTransactionByBankId(bankId);
    }

    @Override
    public List<TransactionRelatedDTO> getTransactions(int offset, String bankId) {
        return repo.getTransactions(offset, bankId);
    }

    @Override
    public TransactionReceiveEntity getTransactionReceiveById(String id) {
        return repo.getTransactionReceiveById(id);
    }

}
