package com.vietqr.org.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vietqr.org.dto.AccountBankConnectBranchDTO;
import com.vietqr.org.dto.AccountBankWpDTO;
import com.vietqr.org.dto.BusinessBankDTO;
import com.vietqr.org.entity.AccountBankReceiveEntity;

@Service
public interface AccountBankReceiveService {

	public int insertAccountBank(AccountBankReceiveEntity entity);

	public void deleteAccountBank(String id);

	public String checkExistedBank(String bankAccount, String bankTypeId);

	public AccountBankReceiveEntity getAccountBankById(String bankId);

	public void updateRegisterAuthenticationBank(String nationalId, String phoneAuthenticated, String bankAccountName,
			String bankAccount, String bankId);

	// public AccountBankReceiveEntity getAccountBankByBankAccount(String
	// bankAccount);

	public AccountBankReceiveEntity getAccountBankByBankAccountAndBankTypeId(String bankAccount, String bankTypeId);

	public List<BusinessBankDTO> getBankByBranchId(String branchId);

	public void unRegisterAuthenticationBank(String bankAccount);

	public void updateStatusAccountBankByUserId(int status, String userId);

	public List<AccountBankConnectBranchDTO> getAccountBankConnect(String userId);

	public void updateBankType(String id, int type);

	public List<AccountBankWpDTO> getAccountBankReceiveWps(String userId);

	public void updateSyncWp(String userId, String bankId);

	public String getBankAccountById(String bankId);

	public String checkExistedBankAccountSameUser(String bankAccount, String bankTypeId, String userId);

	// public AccountBankReceiveEntity getBankAccountAuthenticatedByAccount(String
	// bankAccount);
}
