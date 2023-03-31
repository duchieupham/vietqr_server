package com.vietqr.org.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.dto.BusinessBankDTO;
import com.vietqr.org.entity.AccountBankReceiveEntity;
import com.vietqr.org.repository.AccountBankReceiveRepository;

@Service
public class AccountBankReceiveServiceImpl implements AccountBankReceiveService {

	@Autowired
	AccountBankReceiveRepository accountBankRepo;

	@Override
	public int insertAccountBank(AccountBankReceiveEntity entity) {
		return accountBankRepo.save(entity) == null ? 0 : 1;
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
	public AccountBankReceiveEntity getAccountBankById(String bankId) {
		return accountBankRepo.getAccountBankById(bankId);
	}

	@Override
	public void updateRegisterAuthenticationBank(String nationalId, String phoneAuthenticated, String bankId) {
		accountBankRepo.updateRegisterAuthenticationBank(nationalId, phoneAuthenticated, bankId);
	}

	@Override
	public AccountBankReceiveEntity getAccountBankByBankAccount(String bankAccount) {
		return accountBankRepo.getAccountBankByBankAccount(bankAccount);
	}

	@Override
	public AccountBankReceiveEntity getAccountBankByBankAccountAndBankTypeId(String bankAccount, String bankTypeId) {
		return accountBankRepo.getAccountBankByBankAccountAndBankTypeId(bankAccount, bankTypeId);
	}

	@Override
	public List<BusinessBankDTO> getBankByBranchId(String branchId) {
		return accountBankRepo.getBankByBranchId(branchId);
	}

}
