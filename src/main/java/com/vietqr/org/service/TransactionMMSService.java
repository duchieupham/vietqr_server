package com.vietqr.org.service;

import org.springframework.stereotype.Service;

import com.vietqr.org.entity.TransactionMMSEntity;

@Service
public interface TransactionMMSService {

    public int insertTransactionMMS(TransactionMMSEntity entity);

    public String checkExistedFtCode(String ftCode);
}
