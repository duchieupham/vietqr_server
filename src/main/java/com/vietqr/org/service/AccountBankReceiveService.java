package com.vietqr.org.service;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vietqr.org.dto.*;
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

	public AccountBankReceiveEntity getAccountBankByBankAccountAndBankTypeId(String bankAccount, String bankTypeId);

	public void unRegisterAuthenticationBank(String bankAccount);

	public void updateStatusAccountBankByUserId(int status, String userId);

	public List<AccountBankWpDTO> getAccountBankReceiveWps(String userId);

	public void updateSyncWp(String userId, String bankId);

	public String getBankAccountById(String bankId);

	public List<String> checkExistedBankAccountSameUser(String bankAccount, String bankTypeId, String userId);

	public String getUserIdByBankId(String bankId);

	public List<AccountBankReceiveRPAItemDTO> getBankAccountsRPA(String userId);

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

	String checkIsOwner(String bankId, String userId);

	AccountBankReceiveShareForNotiDTO findAccountBankByTraceTransfer(String traceTransfer, String bankTypeId);

	String getBankShortNameByBankId(String bankId);

	AccountBankReceiveEntity getAccountBankReceiveByBankAccountAndBankCode(String bankAccount, String bankCode);

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

    IBankReceiveFeePackageDTO getCustomerBankDetailByBankId(String bankId);

    List<IAccountBankReceiveDTO> getBankIdsByBankId(String bankId);

    AccountBankReceiveEntity getAccountBankByCustomerIdAndByServiceId(String customerId);

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

	BankDetailTypeCaiValueDTO getBankAccountTypeDetail(String bankId);

	List<BankDetailTypeCaiValueDTO> getBankAccountTypeDetail(List<String> bankIds);
	int countAllBankAccounts();
	int countBankAccountsByAccount(String keyword);
	int countBankAccountsByAccountName(String keyword);

	int countBankAccountsByPhoneAuthenticated(String keyword);

	int countBankAccountsByNationalId(String keyword);
	List<BankAccountResponseDTO> getAllBankAccount(int offset, int size);

	List<BankAccountResponseDTO> getBankAccountsByAccounts(String keyword, int offset, int size);

	List<BankAccountResponseDTO> getBankAccountsByAccountNames(String keyword, int offset, int size);

	List<BankAccountResponseDTO> getBankAccountsByNationalIds(String keyword, int offset, int size);

	List<BankAccountResponseDTO> getBankAccountsByPhoneAuthenticated(String keyword, int offset, int size);

	List<AccountBankReceiveEntity> findBankAccountsByMerchantId(String merchantId);

	String getBankAccountNameByBankAccount(String bankAccount);

	List<BankAccountResponseDTO> getBankAccountsByValidFeeToAndIsValidService(int offset, int size);
	int countBankAccountsByValidFeeToAndIsValidService();

	List<BankAccountResponseDTO> getBankAccountsByTimeCreate(int offset, int size);

	int countBankAccountsByTimeCreate();
	IAdminExtraBankDTO getExtraBankDataForAllTime();

	List<BankAccountResponseDTO> getBankAccountsByValidFeeToAndIsValidServiceWithSearch(Integer searchType, String value, int offset, int size);
	int countBankAccountsByValidFeeToAndIsValidServiceWithSearch(Integer searchType, String value);
	List<BankAccountResponseDTO> getBankAccountsByTimeCreateWithSearch(Integer searchType, String value, int offset, int size);
	int countBankAccountsByTimeCreateWithSearch(Integer searchType, String value);
	void updatePushNotification(String bankId, int value);

	void updateSyncWpById(String id);
	void updatePushNotificationUser(String userId, int value);

	void updateEnableVoiceByBankIds(List<String> bankIds, String userId);
	List<PlatformConnectionDTO> getPlatformConnectionsByBankId(String bankId, int offset, int size);
	int countPlatformConnectionsByBankId(String bankId);


	void updateNotificationTypes(String userId, String bankId, List<String> notificationTypes) throws JsonProcessingException;

	List<BankNotificationDTO> getFullAccountBankReceiveByUserId(String userId);

	AccountBankGenerateBIDVDTO getAccountBankBIDVByBankAccountAndBankTypeId(String bankAccount, String bankTypeId);

	IAccountBankQR getAccountBankQR(String bankAccount, String bankTypeId);

	IAccountBankInfoQR getAccountBankQRByAccountAndId(String bankAccount, String bankTypeId);

	IAccountBankUserQR getAccountBankUserQRById(String bankId);

	IAccountBankReceiveQR getAccountBankReceiveQRByAccountAndId(String bankAccount, String bankTypeId);

	IAccountBankReceiveMMS getAccountBankReceiveQRByBankAccountAndBankCode(String bankAccount, String bankCode);

	List<String> getListBankIdByUserId(String userId);
}
