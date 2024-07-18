package com.vietqr.org.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.AccountBankPaymentEntity;
import com.vietqr.org.repository.AccountBankPaymentRepository;

@Service
public class AccountBankPaymentServiceImpl implements AccountBankPaymentService{

	@Autowired
	AccountBankPaymentRepository accountBankRepo;

	@Override
	public int insertAccountBank(AccountBankPaymentEntity entity) {
		return accountBankRepo.save(entity) == null ? 0: 1;
	}

	@Override
	public List<AccountBankPaymentEntity> getAccountBanksByUserId(String userId) {
		return accountBankRepo.getAccountBank(userId);
	}

	@Override
	public void deleteAccountBank(String id) {
		accountBankRepo.deleteAccountBank(id);
	}

	@Override
	public String checkExistedBank(String bankAccount, String bankTypeId) {
		return accountBankRepo.checkExistedBankAccount(bankAccount, bankTypeId);
	}

	@Override
	public AccountBankPaymentEntity getAccountBankById(String bankId) {
		return accountBankRepo.getAccountBankById(bankId);
	}
}
