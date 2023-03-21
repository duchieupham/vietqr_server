package com.vietqr.org.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.repository.TransactionReceiveRepository;
import java.util.List;
import com.vietqr.org.entity.TransactionReceiveEntity;
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
    public void updateTransactionReceiveStatus(int status, String refId, String id) {
        repo.updateTransactionReceiveStatus(status, refId, id);
    }

    @Override
    public List<TransactionRelatedDTO> getRelatedTransactionReceives(String businessId) {
        return repo.getRelatedTransactionReceives(businessId);
    }

    @Override
    public TransactionReceiveEntity getTransactionById(String id) {
        return repo.getTransactionById(id);
    }

    @Override
    public TransactionReceiveEntity getTransactionByTraceId(String id) {
        return repo.getTransactionByTraceId(id);
    }

}
