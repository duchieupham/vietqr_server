package com.vietqr.org.service;

import com.vietqr.org.dto.IRefundCheckOrderDTO;
import com.vietqr.org.entity.TransactionRefundEntity;
import com.vietqr.org.entity.TransactionRefundLogEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TransactionRefundService {
    List<IRefundCheckOrderDTO> getTotalRefundedByTransactionId(List<String> transactionId);

    void insert(TransactionRefundEntity entity);
}
