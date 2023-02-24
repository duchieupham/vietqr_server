package com.vietqr.org.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vietqr.org.entity.AccountBankPaymentEntity;


@Service
public interface AccountBankPaymentService {

	public int insertAccountBank(AccountBankPaymentEntity entity);

	public List<AccountBankPaymentEntity> getAccountBanksByUserId(String userId);

	public void deleteAccountBank(String id);

	public String checkExistedBank(String bankAccount, String bankTypeId);

	public AccountBankPaymentEntity getAccountBankById(String bankId);
}
