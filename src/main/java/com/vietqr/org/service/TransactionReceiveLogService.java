package com.vietqr.org.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vietqr.org.entity.TransactionReceiveLogEntity;

@Service
public interface TransactionReceiveLogService {

    public int insert(TransactionReceiveLogEntity entity);

    public List<TransactionReceiveLogEntity> getTransactionReceiveLogsByTransId(String transactionId);
}
