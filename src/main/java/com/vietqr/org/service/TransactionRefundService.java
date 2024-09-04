package com.vietqr.org.service;

import com.vietqr.org.dto.IRefundCheckOrderDTO;
import com.vietqr.org.dto.TransactionCheckMultiTimesDTO;
import com.vietqr.org.entity.TransactionRefundEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TransactionRefundService {
    List<IRefundCheckOrderDTO> getTotalRefundedByTransactionId(List<String> transactionId);

    void insert(TransactionRefundEntity entity);

    TransactionCheckMultiTimesDTO getTransactionRefundCheck(String bankAccount, String referenceNumber);

    String checkExistRefundTransaction(String bankAccount, String referenceNumber);
}
