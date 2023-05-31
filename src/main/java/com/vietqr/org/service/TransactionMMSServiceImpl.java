package com.vietqr.org.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.TransactionMMSEntity;
import com.vietqr.org.repository.TransactionMMSRepository;

@Service
public class TransactionMMSServiceImpl implements TransactionMMSService {

    @Autowired
    TransactionMMSRepository repository;

    @Override
    public int insertTransactionMMS(TransactionMMSEntity entity) {
        return repository.save(entity) == null ? 0 : 1;
    }

    @Override
    public String checkExistedFtCode(String ftCode) {
        return repository.checkExistedFtCode(ftCode);
    }

}
