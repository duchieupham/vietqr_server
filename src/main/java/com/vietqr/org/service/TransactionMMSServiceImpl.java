package com.vietqr.org.service;

import java.util.List;

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

    @Override
    public TransactionMMSEntity getTransactionMMSByFtCode(String ftCode, String payDate) {
        return repository.getTransactionMMSByFtCode(ftCode, payDate);
    }

    @Override
    public List<TransactionMMSEntity> getTransactionMMSByDate(String date) {
        return repository.getTransactionMMSByDate(date);
    }

}
