package com.vietqr.org.service;

import org.springframework.stereotype.Service;

import com.vietqr.org.entity.AccountBankReceiveEntity;

@Service
public interface AccountBankReceiveService {

	public int insertAccountBank(AccountBankReceiveEntity entity);

	public void deleteAccountBank(String id);

	public String checkExistedBank(String bankAccount, String bankTypeId);

	public AccountBankReceiveEntity getAccountBankById(String bankId);

	public void updateRegisterAuthenticationBank(String nationalId, String phoneAuthenticated, String bankId);

	public AccountBankReceiveEntity getAccountBankByBankAccount(String bankAccount);

	public AccountBankReceiveEntity getAccountBankByBankAccountAndBankTypeId(String bankAccount, String bankTypeId);
}
