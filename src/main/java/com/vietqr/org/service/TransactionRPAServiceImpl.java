package com.vietqr.org.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.TransactionRPAEntity;
import com.vietqr.org.repository.TransactionRPARepository;

@Service
public class TransactionRPAServiceImpl implements TransactionRPAService {

    @Autowired
    TransactionRPARepository repo;

    @Override
    public int insertAllTransactionRPA(List<TransactionRPAEntity> entities) {
        return repo.saveAll(entities) == null ? 0 : 1;
    }

    @Override
    public List<String> checkExistedTransaction(String referenceNumber) {
        return repo.checkExistedTransaction(referenceNumber);
    }

}
