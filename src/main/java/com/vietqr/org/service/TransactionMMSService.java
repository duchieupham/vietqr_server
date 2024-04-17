package com.vietqr.org.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vietqr.org.entity.TransactionMMSEntity;

@Service
public interface TransactionMMSService {

    public int insertTransactionMMS(TransactionMMSEntity entity);

    public String checkExistedFtCode(String ftCode);

    public TransactionMMSEntity getTransactionMMSByFtCode(String ftCode, String payDate);

    public List<TransactionMMSEntity> getTransactionMMSByDate(String date);
}
