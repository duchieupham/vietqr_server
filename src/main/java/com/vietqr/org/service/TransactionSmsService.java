package com.vietqr.org.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vietqr.org.entity.TransactionSmsEntity;

@Service
public interface TransactionSmsService {

    public int insertAll(List<TransactionSmsEntity> entities);

    public List<TransactionSmsEntity> getTransactionsBySmsId(String smsId, int offset);

    public List<TransactionSmsEntity> getTransactionsByStatus(String smsId, int status, int offset);

    public List<TransactionSmsEntity> getTransactionsByBankId(String bankId, int offset);

    public List<TransactionSmsEntity> getTransactionsByBankIdAndStatus(String bankId, int status, int offset);

    public TransactionSmsEntity getTransactionSmsDetail(String id);
}
