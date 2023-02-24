package com.vietqr.org.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.vietqr.org.entity.TransactionCreateEntity;
import com.vietqr.org.repository.TransactionCreateRepository;

public class TransactionCreateServiceImpl implements TransactionCreateService {

	@Autowired
	TransactionCreateRepository repository;

	@Override
	public int insertTransactionCreate(TransactionCreateEntity entity) {
		return repository.save(entity) == null ? 0 : 1;
	}

}
