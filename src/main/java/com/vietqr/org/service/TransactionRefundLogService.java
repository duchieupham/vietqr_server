package com.vietqr.org.service;

import com.vietqr.org.entity.TransactionRefundLogEntity;

public interface TransactionRefundLogService {
    void insert(TransactionRefundLogEntity finalRefundLogEntity);

    TransactionRefundLogEntity getByTransactionRefundByReferenceNumber(String referencenumber);
}
