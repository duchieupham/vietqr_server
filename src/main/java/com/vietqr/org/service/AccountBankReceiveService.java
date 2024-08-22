package com.vietqr.org.service;

import java.util.List;

import com.vietqr.org.dto.*;
import com.vietqr.org.dto.bidv.CustomerVaInfoDataDTO;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.AccountBankReceiveEntity;

@Service
public interface AccountBankReceiveService {

	public List<IBankShareDTO> getBankShareInfoByUserId(String userId);
	public List<IBankInfoDTO> getBankInfoByUserId(String userId);

	public List<IListAccountBankDTO> getListBankAccounts(String value, int offset, int size);

	public int countListBankAccounts();

	public List<IAccountBankReceiveDTO> getBankIdsByUserId(String userId);

	public int insertAccountBank(AccountBankReceiveEntity entity);

	public void deleteAccountBank(String id);

	public String checkExistedBank(String bankAccount, String bankTypeId);

	public AccountBankReceiveEntity getAccountBankById(String bankId);

	public KeyBankReceiveActiveDTO getAccountBankKeyById(String bankId);

	public String getCaiValueByBankId(String bankId);

	public void updateRegisterAuthenticationBank(String nationalId, String phoneAuthenticated, String bankAccountName,
			String bankAccount, String ewalletToken, String bankId);

	// public AccountBankReceiveEntity getAccountBankByBankAccount(String
	// bankAccount);

	public AccountBankReceiveEntity getAccountBankByBankAccountAndBankTypeId(String bankAccount, String bankTypeId);

	public List<BusinessBankDTO> getBankByBranchId(String branchId);

	public void unRegisterAuthenticationBank(String bankAccount);

	public void unRegisterAuthenBank(String bankAccount, String ewalletToken);

	public void updateStatusAccountBankByUserId(int status, String userId);

	public List<AccountBankConnectBranchDTO> getAccountBankConnect(String userId);

	public void updateBankType(String id, int type);

	public List<AccountBankWpDTO> getAccountBankReceiveWps(String userId);

	public void updateSyncWp(String userId, String bankId);

	public String getBankAccountById(String bankId);

	public List<String> checkExistedBankAccountSameUser(String bankAccount, String bankTypeId, String userId);

	public String getUserIdByBankId(String bankId);

	public List<AccountBankReceiveRPAItemDTO> getBankAccountsRPA(String userId);

	// public AccountBankReceiveEntity getBankAccountAuthenticatedByAccount(String
	// bankAccount);

	public List<AccountBankReceiveByCusSyncDTO> getBankAccountsByCusSyncId(String customerSyncId, int offset);

	// for test env
	public String checkExistedBankAccountByBankAccount(String bankAccount);

	public void updateBankAccountSync(boolean sync, String id);

	public String checkMMSBankAccount(String bankAccount);

	public void updateBankSync(boolean sync, String id);

	public Boolean getAuthenticatedByBankId(String bankId);

	public void updateMMSActive(boolean sync, boolean mmsActive, String bankId);

	public String getUserIdByBankAccountAuthenticated(String bankAccount);

	public Boolean getMMSActiveByBankId(String bankId);

	public AccountBankReceiveForNotiDTO findAccountBankIden(String bankAccount, String bankTypeId);

	String checkIsOwner(String bankId, String userId);

	AccountBankReceiveShareForNotiDTO findAccountBankByTraceTransfer(String traceTransfer, String bankTypeId);

	public String checkExistedBankAccountByBankAccountAndBankCode(String bankAccount, String bankCode);

	String getBankShortNameByBankId(String bankId);

	AccountBankReceiveEntity checkExistedBankAccountAuthenticated(String bankAccount, String bankCode);

	String getBankNameByBankId(String bankTypeId);

    List<TerminalBankReceiveDTO> getAccountBankReceiveByUseId(String userId);

    BankReceiveCheckDTO checkBankReceiveActive(String bankId);

	int updateActiveBankReceive(String bankId, long validFeeFrom, long validFeeTo);

    boolean checkIsActiveService(String bankId);

    IBankAccountInfoDTO getAccountBankInfoById(String bankId);

	IAccountBankReceiveDTO getAccountBankInfoResById(String bankAccount, String bankCode);

	IBankReceiveMapperDTO getMerchantBankMapper(String bankId);

    List<ICustomerDetailDTO> getCustomerDetailByBankId(String bankId);

    int countBankInvoiceByBankAccount(String value);

	List<IBankAccountInvoiceInfoDTO> getBankInvoiceByBankAccount(String value, int offset, int size);

	AccountBankDetailAdminDTO getAccountBankDetailAdmin(String bankId);

    IBankAccountInvoicesDTO getBankAccountInvoices(String bankId);

	IAccountBankReceiveDTO getAccountBankInfoResById(String bankId);

    BankAccountRechargeDTO getBankAccountRecharge(String bankId);

    IBankReceiveFeePackageDTO getCustomerBankDetailByBankId(String bankId);

    List<IAccountBankReceiveDTO> getBankIdsByBankId(String bankId);

    AccountBankReceiveEntity getAccountBankByCustomerIdAndByServiceId(String customerId);

    CustomerVaInfoDataDTO getAccountCustomerInfo(String customerId);

	void updateRegisterAuthentication(String userId, String merchantId);

	String getBankIdByUserIdAndMerchantId(String userId, String merchantId);

	BidvUnlinkedDTO getMerchantIdByBankAccountBidvAuthen(String bankAccount, String bankCode);

	void updateRegisterUnlinkBidv(String userId, String bankId);

    String getBankCodeByBankId(String bankId);

	void updateRegisterAuthenticationBankBIDV(String nationalId, String phoneAuthenticated, String bankAccountName, String bankAccount,
											  String customerId, String ewalletToken, String bankId);

    String getBankIdByBankAccount(String bankAccount, String bankShortName);

	BankAccountAdminDTO getUserIdAndMidByBankId(String bankId);

	void updateVsoBankAccount(String vso, String bankId);

	List<IAccountBankMonthDTO> getBankAccountStatistics();

	List<AccountBankReceiveEntity> findBankAccountsByMerchantId(String merchantId);
}
