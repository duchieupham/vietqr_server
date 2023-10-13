package com.vietqr.org.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.dto.TransactionSMSDetailDTO;
import com.vietqr.org.entity.TransactionSmsEntity;
import com.vietqr.org.repository.TransactionSmsRepository;

@Service
public class TransactionSmsServiceImpl implements TransactionSmsService {

    @Autowired
    TransactionSmsRepository repo;

    @Override
    public int insertAll(List<TransactionSmsEntity> entities) {
        return repo.saveAll(entities) == null ? 0 : 1;
    }

    @Override
    public List<TransactionSmsEntity> getTransactionsBySmsId(String smsId, int offset) {
        return repo.getTransactionSmsList(smsId, offset);
    }

    @Override
    public List<TransactionSmsEntity> getTransactionsByStatus(String smsId, int status, int offset) {
        return repo.getTransactionSmsListByStatus(smsId, status, offset);
    }

    @Override
    public List<TransactionSmsEntity> getTransactionsByBankId(String bankId, int offset) {
        return repo.getTransactionSmsListByBankId(bankId, offset);
    }

    @Override
    public List<TransactionSmsEntity> getTransactionsByBankIdAndStatus(String bankId, int status, int offset) {
        return repo.getTransactionSmsListByBankIdAndStatus(bankId, status, offset);
    }

    @Override
    public TransactionSMSDetailDTO getTransactionSmsDetail(String id) {
        return repo.getTransactionSmsDetail(id);
    }

}
