package com.vietqr.org.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vietqr.org.entity.TransactionRPAEntity;

@Service
public interface TransactionRPAService {

    public int insertAllTransactionRPA(List<TransactionRPAEntity> entities);

}
