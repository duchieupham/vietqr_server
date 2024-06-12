package com.vietqr.org.service;

import com.vietqr.org.entity.TransactionBidvEntity;
import com.vietqr.org.repository.TransactionBidvRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionBidvServiceImpl implements TransactionBidvService {

    @Autowired
    private TransactionBidvRepository repo;

    @Override
    public void insert(TransactionBidvEntity entity) {
        repo.save(entity);
    }
}
