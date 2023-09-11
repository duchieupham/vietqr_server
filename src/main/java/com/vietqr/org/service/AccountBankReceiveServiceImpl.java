package com.vietqr.org.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.dto.AccountBankConnectBranchDTO;
import com.vietqr.org.dto.AccountBankReceiveByCusSyncDTO;
import com.vietqr.org.dto.AccountBankReceiveRPAItemDTO;
import com.vietqr.org.dto.AccountBankWpDTO;
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
	public List<String> checkExistedBank(String bankAccount, String bankTypeId) {
		return accountBankRepo.checkExistedBankAccount(bankAccount, bankTypeId);
	}

	@Override
	public AccountBankReceiveEntity getAccountBankById(String bankId) {
		return accountBankRepo.getAccountBankById(bankId);
	}

	@Override
	public void updateRegisterAuthenticationBank(String nationalId, String phoneAuthenticated, String bankAccountName,
			String bankAccount, String bankId) {
		accountBankRepo.updateRegisterAuthenticationBank(nationalId, phoneAuthenticated, bankAccountName, bankAccount,
				bankId);
	}

	// @Override
	// public AccountBankReceiveEntity getAccountBankByBankAccount(String
	// bankAccount) {
	// return accountBankRepo.getAccountBankByBankAccount(bankAccount);
	// }

	@Override
	public AccountBankReceiveEntity getAccountBankByBankAccountAndBankTypeId(String bankAccount, String bankTypeId) {
		return accountBankRepo.getAccountBankByBankAccountAndBankTypeId(bankAccount, bankTypeId);
	}

	@Override
	public List<BusinessBankDTO> getBankByBranchId(String branchId) {
		return accountBankRepo.getBankByBranchId(branchId);
	}

	@Override
	public void unRegisterAuthenticationBank(String bankAccount) {
		accountBankRepo.unRegisterAuthenticationBank(bankAccount);
	}

	@Override
	public void updateStatusAccountBankByUserId(int status, String userId) {
		accountBankRepo.updateStatusAccountBankByUserId(status, userId);
	}

	@Override
	public List<AccountBankConnectBranchDTO> getAccountBankConnect(String userId) {
		return accountBankRepo.getAccountBankConnect(userId);
	}

	@Override
	public void updateBankType(String id, int type) {
		accountBankRepo.updateBankType(id, type);
	}

	@Override
	public List<AccountBankWpDTO> getAccountBankReceiveWps(String userId) {
		return accountBankRepo.getAccountBankReceiveWps(userId);
	}

	@Override
	public void updateSyncWp(String userId, String bankId) {
		accountBankRepo.updateSyncWp(userId, bankId);
	}

	@Override
	public String getBankAccountById(String bankId) {
		return accountBankRepo.getBankAccountById(bankId);
	}

	@Override
	public List<String> checkExistedBankAccountSameUser(String bankAccount, String bankTypeId, String userId) {
		return accountBankRepo.checkExistedBankAccountSameUser(bankAccount, bankTypeId, userId);
	}

	@Override
	public String getUserIdByBankId(String bankId) {
		return accountBankRepo.getUserIdByBankId(bankId);
	}

	@Override
	public List<AccountBankReceiveRPAItemDTO> getBankAccountsRPA(String userId) {
		return accountBankRepo.getBankAccountsRPA(userId);
	}

	@Override
	public List<AccountBankReceiveByCusSyncDTO> getBankAccountsByCusSyncId(String customerSyncId) {
		return accountBankRepo.getBankAccountsByCusSyncId(customerSyncId);
	}

	@Override
	public String checkExistedBankAccountByBankAccount(String bankAccount) {
		return accountBankRepo.checkExistedBankAccountByBankAccount(bankAccount);
	}

	@Override
	public void updateBankAccountSync(boolean sync, String id) {
		accountBankRepo.updateBankAccountSync(sync, id);
	}

	@Override
	public String checkMMSBankAccount(String bankAccount) {
		return accountBankRepo.checkMMSBankAccount(bankAccount);
	}

}
