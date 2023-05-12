package com.vietqr.org.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.vietqr.org.entity.TransactionReceiveEntity;
import com.vietqr.org.dto.TransactionDetailDTO;
import com.vietqr.org.dto.TransactionRelatedDTO;

@Service
public interface TransactionReceiveService {

    public int insertTransactionReceive(TransactionReceiveEntity entity);

    public void updateTransactionReceiveStatus(int status, String refId, String referenceNumber, String id);

    public List<TransactionRelatedDTO> getRelatedTransactionReceives(String businessId);

    public TransactionDetailDTO getTransactionById(String id);

    public TransactionReceiveEntity getTransactionByTraceIdAndAmount(String id, String amount);

    public List<TransactionReceiveEntity> getTransactionByBankId(String bankId);

    public List<TransactionRelatedDTO> getTransactions(int offset, String bankId);

    public TransactionReceiveEntity getTransactionReceiveById(String id);

}
