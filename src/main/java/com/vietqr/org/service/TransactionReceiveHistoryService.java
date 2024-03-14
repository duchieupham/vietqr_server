package com.vietqr.org.service;

import com.vietqr.org.entity.TransactionReceiveHistoryEntity;
import org.springframework.stereotype.Service;

@Service
public interface TransactionReceiveHistoryService {

    int insertTransactionReceiveHistory(TransactionReceiveHistoryEntity transactionReceiveHistory);

}
