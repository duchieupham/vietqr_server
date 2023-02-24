package com.vietqr.org.service;

import org.springframework.stereotype.Service;

import com.vietqr.org.entity.TransactionCreateEntity;

@Service
public interface TransactionCreateService {

	public int insertTransactionCreate(TransactionCreateEntity entity);
}
