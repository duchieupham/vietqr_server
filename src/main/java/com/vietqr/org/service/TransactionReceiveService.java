package com.vietqr.org.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.vietqr.org.entity.TransactionReceiveEntity;
import com.vietqr.org.dto.TransactionRelatedDTO;

@Service
public interface TransactionReceiveService {

    public int insertTransactionReceive(TransactionReceiveEntity entity);

    public void updateTransactionReceiveStatus(int status, String refId, String id);

    public List<TransactionRelatedDTO> getRelatedTransactionReceives(String businessId);

    public TransactionReceiveEntity getTransactionById(String id);

    public TransactionReceiveEntity getTransactionByTraceId(String id);

    public List<TransactionReceiveEntity> getTransactionByBankId(String bankId);
}
