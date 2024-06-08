package com.vietqr.org.repository;

import java.util.List;

import javax.transaction.Transactional;

import com.vietqr.org.dto.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.AccountBankReceiveEntity;

@Repository
public interface AccountBankReceiveRepository extends JpaRepository<AccountBankReceiveEntity, Long> {

	@Query(value = "SELECT a.id, b.bank_code as bankCode, b.bank_name as bankName, a.bank_account_name as userBankName, a.bank_account as bankAccount, a.username, a.password "
			+ "FROM account_bank_receive a "
			+ "INNER JOIN bank_type b "
			+ "ON a.bank_type_id = b.id "
			+ "WHERE a.user_id = :userId AND is_rpa_sync = 1 ", nativeQuery = true)
	List<AccountBankReceiveRPAItemDTO> getBankAccountsRPA(@Param(value = "userId") String userId);

	@Query(value = "SELECT id FROM account_bank_receive "
			+ "WHERE bank_account = :bankAccount AND is_authenticated = true AND mms_active = true ", nativeQuery = true)
	String checkMMSBankAccount(@Param(value = "bankAccount") String bankAccount);

	@Transactional
	@Modifying
	@Query(value = "DELETE FROM account_bank_receive WHERE id = :id", nativeQuery = true)
	void deleteAccountBank(@Param(value = "id") String id);

	@Query(value = "SELECT id FROM account_bank_receive WHERE bank_account = :bankAccount AND bank_type_id = :bankTypeId AND is_authenticated = true ", nativeQuery = true)
	String checkExistedBankAccount(@Param(value = "bankAccount") String bankAccount,
			@Param(value = "bankTypeId") String bankTypeId);

	@Query(value = "SELECT a.mms_active AS mmsActive, a.bank_account AS bankAccount, "
			+ "a.id AS bankId, a.bank_account_name AS userbankName, "
			+ "b.bank_short_name AS bankShortName "
			+ "FROM account_bank_receive a "
			+ "INNER JOIN bank_type b ON a.bank_type_id = b.id "
			+ "WHERE a.user_id = :userId ", nativeQuery = true)
	List<IAccountBankReceiveDTO> getBankIdsByUserId(@Param(value = "userId") String userId);

	@Query(value = "SELECT id FROM account_bank_receive WHERE bank_account = :bankAccount AND bank_type_id = :bankTypeId AND user_id = :userId ", nativeQuery = true)
	List<String> checkExistedBankAccountSameUser(@Param(value = "bankAccount") String bankAccount,
			@Param(value = "bankTypeId") String bankTypeId, @Param(value = "userId") String userId);

	@Query(value = "SELECT * FROM account_bank_receive WHERE id = :bankId LIMIT 1", nativeQuery = true)
	AccountBankReceiveEntity getAccountBankById(@Param(value = "bankId") String bankId);

	// @Query(value = "SELECT * FROM account_bank_receive WHERE bank_account =
	// :bankAccount AND is_authenticated = true", nativeQuery = true)
	// AccountBankReceiveEntity getAccountBankByBankAccount(@Param(value =
	// "bankAccount") String bankAccount);

	@Transactional
	@Modifying
	@Query(value = "UPDATE account_bank_receive "
			+ "SET national_id = :nationalId, phone_authenticated = :phoneAuthenticated, bank_account_name = :bankAccountName, bank_account = :bankAccount, ewallet_token = :ewalletToken, is_authenticated = true "
			+ "WHERE id = :bankId", nativeQuery = true)
	void updateRegisterAuthenticationBank(@Param(value = "nationalId") String nationalId,
			@Param(value = "phoneAuthenticated") String phoneAuthenticated,
			@Param(value = "bankAccountName") String bankAccountName, @Param(value = "bankAccount") String bankAccount,
			@Param(value = "ewalletToken") String ewalletToken,
			@Param(value = "bankId") String bankId);

	@Transactional
	@Modifying
	@Query(value = "UPDATE account_bank_receive SET is_sync = :sync WHERE id = :id ", nativeQuery = true)
	void updateBankAccountSync(@Param(value = "sync") boolean sync, @Param(value = "id") String id);

	@Transactional
	@Modifying
	@Query(value = "UPDATE account_bank_receive "
			+ "SET is_authenticated = false "
			+ "WHERE bank_account = :bankAccount "
			+ "AND is_authenticated = true", nativeQuery = true)
	void unRegisterAuthenticationBank(@Param(value = "bankAccount") String bankAccount);

	@Transactional
	@Modifying
	@Query(value = "UPDATE account_bank_receive "
			+ "SET is_authenticated = false, "
			+ "ewallet_token = '' "
			+ "WHERE bank_account = :bankAccount "
			+ "AND ewallet_token = :ewalletToken "
			+ "AND is_authenticated = true", nativeQuery = true)
	void unRegisterAuthenBank(@Param(value = "bankAccount") String bankAccount,
			@Param(value = "ewalletToken") String ewalletToken);

	@Query(value = "SELECT * FROM account_bank_receive WHERE bank_account = :bankAccount AND bank_type_id = :bankTypeId AND is_authenticated = true AND status = 1", nativeQuery = true)
	AccountBankReceiveEntity getAccountBankByBankAccountAndBankTypeId(@Param(value = "bankAccount") String bankAccount,
			@Param(value = "bankTypeId") String bankTypeId);

	@Query(value = "SELECT b.id as id, b.bank_account as bankAccount, c.bank_name as bankName, b.bank_account_name as userBankName, c.img_id as imgId, b.is_authenticated as authenticated  "
			+ "FROM bank_receive_branch a "
			+ "INNER JOIN account_bank_receive b "
			+ "ON a.bank_id = b.id "
			+ "	INNER JOIN bank_type c "
			+ "	ON b.bank_type_id = c.id "
			+ "WHERE a.branch_id = :branchId", nativeQuery = true)
	BusinessBankDTO getBusinessBankByBranchId(@Param(value = "branchId") String branchId);

	@Query(value = "SELECT a.bank_id as id, b.bank_account as bankAccount, c.bank_code as bankCode, "
			+ "c.bank_name as bankName, b.bank_account_name as userBankName, c.img_id as imgId, b.is_authenticated as authenticated "
			+ "FROM bank_receive_branch a "
			+ "INNER JOIN account_bank_receive b "
			+ "ON a.bank_id = b.id "
			+ "INNER JOIN bank_type c "
			+ "ON b.bank_type_id = c.id "
			+ "WHERE a.branch_id = :branchId", nativeQuery = true)
	List<BusinessBankDTO> getBankByBranchId(@Param(value = "branchId") String branchId);

	@Transactional
	@Modifying
	@Query(value = "UPDATE account_bank_receive "
			+ "SET status = :status "
			+ "WHERE user_id = :userId", nativeQuery = true)
	void updateStatusAccountBankByUserId(@Param(value = "status") int status, @Param(value = "userId") String userId);

	@Query(value = "SELECT a.id as bankId, a.bank_account as bankAccount, a.bank_account_name as bankAccountName, a.user_id as userId, a.is_authenticated as authenticated, b.bank_code as bankCode, b.bank_name as bankName, b.img_id as imgId "
			+ "FROM account_bank_receive a "
			+ "INNER JOIN bank_type b "
			+ "ON a.bank_type_id = b.id "
			+ "WHERE a.type = 0 AND user_id = :userId AND a.status = 1", nativeQuery = true)
	List<AccountBankConnectBranchDTO> getAccountBankConnect(@Param(value = "userId") String userId);

	@Transactional
	@Modifying
	@Query(value = "UPDATE account_bank_receive "
			+ "SET type = :type "
			+ "WHERE id = :id", nativeQuery = true)
	void updateBankType(@Param(value = "id") String id, @Param(value = "type") int type);

	// get account bank receive authenticated = 1 by user_id
	@Query(value = "SELECT a.id, a.bank_account as bankAccount, a.bank_account_name as userBankName, "
			+ "a.is_wp_sync as syncAccount, b.bank_code as bankCode, b.bank_name as bankName, b.img_id as imgId "
			+ "FROM account_bank_receive a "
			+ "INNER JOIN bank_type b "
			+ "ON a.bank_type_id = b.id "
			+ "WHERE a.status = 1 "
			+ "AND a.user_id = :userId", nativeQuery = true)
	List<AccountBankWpDTO> getAccountBankReceiveWps(@Param(value = "userId") String userId);

	@Transactional
	@Modifying
	@Query(value = "UPDATE account_bank_receive "
			+ "SET is_wp_sync = "
			+ "CASE "
			+ "WHEN id = :bankId THEN true "
			+ "ELSE false "
			+ "END "
			+ "WHERE user_id = :userId AND is_authenticated = 1", nativeQuery = true)
	void updateSyncWp(@Param(value = "userId") String userId, @Param(value = "bankId") String bankId);

	@Query(value = "SELECT bank_account FROM account_bank_receive WHERE id = :bankId", nativeQuery = true)
	String getBankAccountById(@Param(value = "bankId") String bankId);

	// @Query(value = "SELECT * FROM account_bank_receive "
	// + "WHERE bank_account = :bankAccount "
	// + "AND is_authenticated = true "
	// + "AND status = true", nativeQuery = true)
	// AccountBankReceiveEntity getBankAccountAuthenticatedByAccount(@Param(value =
	// "bankAccount") String bankAccount);

	@Query(value = "SELECT user_id FROM account_bank_receive WHERE id = :bankId", nativeQuery = true)
	String getUserIdByBankId(@Param(value = "bankId") String bankId);

	// get bank account by customer sync id
	@Query(value = "SELECT a.id as accountCustomerId, a.customer_sync_id as customerSyncId, a.bank_id as bankId, b.bank_account as bankAccount, b.bank_account_name as customerBankName, "
			+ "b.bank_type_id as bankTypeId, b.national_id as nationalId, b.phone_authenticated as phoneAuthenticated, b.user_id as userId, b.is_authenticated as authenticated,  "
			+ "c.bank_code as bankCode, c.bank_short_name as bankShortName, c.img_id as imgId,  "
			+ "CASE  "
			+ "WHEN b.is_sync = true AND b.mms_active = false THEN 1  "
			+ "WHEN b.is_sync = true AND b.mms_active = true THEN 2  "
			+ "WHEN b.is_sync = false AND b.mms_active = true THEN 2  "
			+ "END as flow, "
			+ "d.service_fee_id as serviceFeeId, d.short_name as serviceFeeName "
			+ "FROM account_customer_bank a "
			+ "INNER JOIN account_bank_receive b "
			+ "ON a.bank_id = b.id  "
			+ "INNER JOIN bank_type c  "
			+ "ON b.bank_type_id = c.id  "
			+ "LEFT JOIN account_bank_fee d "
			+ "ON b.id = d.bank_id "
			+ "WHERE a.customer_sync_id = :customerSyncId "
			+ "LIMIT :offset, 20", nativeQuery = true)
	List<AccountBankReceiveByCusSyncDTO> getBankAccountsByCusSyncId(
			@Param(value = "customerSyncId") String customerSyncId,
			@Param(value = "offset") int offset);

	// check existed bankAccount
	@Query(value = "SELECT id FROM account_bank_receive "
			+ "WHERE bank_account = :bankAccount AND status = true AND is_authenticated = true ", nativeQuery = true)
	String checkExistedBankAccountByBankAccount(@Param(value = "bankAccount") String bankAccount);

	@Transactional
	@Modifying
	@Query(value = "UPDATE account_bank_receive "
			+ "SET is_sync = :sync "
			+ "WHERE id = :id ", nativeQuery = true)
	void updateSyncBank(@Param(value = "sync") boolean sync, @Param(value = "id") String id);

	@Query(value = "SELECT is_authenticated FROM account_bank_receive WHERE id = :bankId LIMIT 1", nativeQuery = true)
	Boolean getAuthenticatedByBankId(@Param(value = "bankId") String bankId);

	@Transactional
	@Modifying
	@Query(value = "UPDATE account_bank_receive "
			+ "SET is_sync = :sync, mms_active = :mmsActive "
			+ "WHERE id = :bankId ", nativeQuery = true)
	public void updateMMSActive(
			@Param(value = "sync") boolean sync,
			@Param(value = "mmsActive") boolean mmsActive,
			@Param(value = "bankId") String bankId);

	@Query(value = "SELECT user_id FROM account_bank_receive "
			+ "WHERE bank_account = :bankAccount "
			+ "AND is_authenticated = true ", nativeQuery = true)
	String getUserIdByBankAccountAuthenticated(@Param(value = "bankAccount") String bankAccount);

	@Query(value = "SELECT mms_active "
			+ "FROM account_bank_receive "
			+ "WHERE id = :bankId ", nativeQuery = true)
	Boolean getMMSActiveByBankId(@Param(value = "bankId") String bankId);

	// bankId
	// bankName
	// bankCode
	@Query(value = "SELECT a.id as bankId, b.bank_name as bankName, b.bank_code as bankCode, a.user_id as userId, b.bank_short_name as bankShortName "
			+ "FROM account_bank_receive a "
			+ "INNER JOIN bank_type b "
			+ "ON a.bank_type_id = b.id "
			+ "WHERE a.bank_account = :bankAccount AND a.bank_type_id = :bankTypeId "
			+ "AND a.is_authenticated = true ", nativeQuery = true)
	AccountBankReceiveForNotiDTO findAccountBankIden(@Param(value = "bankAccount") String bankAccount,
			@Param(value = "bankTypeId") String bankTypeId);

	@Query(value = "SELECT a.cai_value FROM cai_bank a " +
			"INNER JOIN bank_type b ON b.id = a.bank_type_id " +
			"INNER JOIN account_bank_receive c ON c.bank_type_id = b.id" +
			" WHERE c.id = :bankId", nativeQuery = true)
	String getCaiValueByBankId(String bankId);

	@Query(value = "SELECT id FROM account_bank_receive WHERE id = :bankId AND user_id = :userId", nativeQuery = true)
	String checkIsOwner(String bankId, String userId);

	@Query(value = "SELECT a.id as bankId, b.bank_name as bankName, b.bank_code as bankCode, a.user_id as userId, "
			+ "b.bank_short_name as bankShortName, a.bank_account as bankAccount "
			+ "FROM account_bank_receive a "
			+ "INNER JOIN bank_type b "
			+ "ON a.bank_type_id = b.id "
			+ "INNER JOIN account_bank_receive_share c "
			+ "ON a.id = c.bank_id "
			+ "WHERE a.bank_type_id = :bankTypeId "
			+ "AND a.is_authenticated = true "
			+ "AND c.trace_transfer = :traceTransfer LIMIT 1", nativeQuery = true)
	AccountBankReceiveShareForNotiDTO findAccountBankByTraceTransfer(String traceTransfer, String bankTypeId);

	@Query(value = "SELECT a.id "
			+ "FROM account_bank_receive a "
			+ "INNER JOIN bank_type b "
			+ "ON a.bank_type_id = b.id "
			+ "WHERE a.bank_account = :bankAccount "
			+ "AND b.bank_code = :bankCode "
			+ "AND a.is_authenticated = true", nativeQuery = true)
	String checkExistedBankAccountByBankAccountAndBankCode(
			@Param(value = "bankAccount") String bankAccount,
			@Param(value = "bankCode") String bankCode);

	@Query(value = "SELECT b.bank_short_name FROM account_bank_receive a " +
			"INNER JOIN bank_type b ON a.bank_type_id = b.id WHERE a.id = :bankId", nativeQuery = true)
	String getBankShortNameByBankId(String bankId);

	@Query(value = "SELECT a.* FROM account_bank_receive a "
			+ "INNER JOIN bank_type b "
			+ "ON b.id = a.bank_type_id "
			+ "WHERE a.bank_account = :bankAccount "
			+ "AND b.bank_code = :bankCode AND is_authenticated = TRUE ", nativeQuery = true)
	AccountBankReceiveEntity checkExistedBankAccountAuthenticated(String bankAccount, String bankCode);

	@Query(value = "SELECT a.bank_name FROM bank_type a WHERE a.id = :bankTypeId", nativeQuery = true)
	String getBankNameByBankId(String bankTypeId);

	@Query(value = "SELECT a.id as bankId, " +
			"a.bank_account AS bankAccount," +
			"a.bank_account_name AS userBankName, a.user_id AS userId, " +
			"b.bank_code AS bankCode, b.bank_name AS bankName, b.img_id AS imgId, " +
			"b.bank_short_name AS bankShortName "
			+ " FROM account_bank_receive a "
			+ "INNER JOIN bank_type b ON a.bank_type_id = b.id "
			+ "WHERE a.user_id = :userId AND a.is_authenticated = TRUE ", nativeQuery = true)
    List<TerminalBankReceiveDTO> getAccountBankReceiveByUseId(String userId);

	@Query(value = "SELECT id AS bankId, user_id AS userId, "
			+ "is_authenticated AS authenticated, is_valid_service AS isValidService, "
			+ "valid_fee_from as validFrom, valid_fee_to as validTo "
			+ "FROM account_bank_receive "
			+ "WHERE id = :bankId", nativeQuery = true)
    BankReceiveCheckDTO checkBankReceiveActive(@Param(value = "bankId") String bankId);

	@Query(value = "SELECT id AS bankId, user_id AS userId, "
			+ "COALESCE(valid_fee_from, 0) AS validFeeFrom, "
			+ "COALESCE(valid_fee_to, 0) AS validFeeTo, "
			+ "is_valid_service AS isValidService "
			+ "FROM account_bank_receive "
			+ "WHERE id = :bankId", nativeQuery = true)
    KeyBankReceiveActiveDTO getAccountBankKeyById(@Param(value = "bankId") String bankId);

	@Transactional
	@Modifying
	@Query(value = "UPDATE account_bank_receive SET valid_fee_from = :validFeeFrom, "
			+ "valid_fee_to = :validFeeTo, is_valid_service = TRUE WHERE id = :bankId ", nativeQuery = true)
	int updateActiveBankReceive(String bankId, long validFeeFrom, long validFeeTo);

	@Query(value = "SELECT is_valid_service FROM account_bank_receive WHERE id = :bankId", nativeQuery = true)
    boolean checkIsActiveService(String bankId);

	@Query(value = "SELECT a.id AS bankId, "
			+ "a.bank_account AS bankAccount, "
			+ "a.bank_account_name AS userBankName, "
			+ "b.bank_short_name AS bankShortName "
			+ "FROM account_bank_receive a "
			+ "INNER JOIN bank_type b ON a.bank_type_id = b.id "
			+ "WHERE a.id = :bankId ", nativeQuery = true)
    IBankAccountInfoDTO getAccountBankInfoById(String bankId);

	@Query(value = "SELECT a.id AS bankId, a.bank_account AS bankAccount, "
			+"a.is_authenticated AS isAuthenticated, a.mms_active AS isMmsActive, "
			+ "a.bank_account_name AS userBankName, b.bank_short_name AS bankShortName, "
			+ "b.bank_code AS bankCode "
			+ "FROM account_bank_receive a "
			+ "INNER JOIN bank_type b "
			+ "ON a.bank_type_id = b.id "
			+ "WHERE a.bank_account = :bankAccount "
			+ "AND b.bank_code = :bankCode AND a.is_authenticated = TRUE "
			+ "LIMIT 1 ", nativeQuery = true)
	IAccountBankReceiveDTO getAccountBankInfoResById(String bankAccount, String bankCode);

	@Query(value = "SELECT a.id AS bankId, a.bank_account AS bankAccount, "
			+"a.is_authenticated AS isAuthenticated, a.mms_active AS isMmsActive, "
			+ "a.bank_account_name AS userBankName, b.bank_short_name AS bankShortName, "
			+ "b.bank_code AS bankCode "
			+ "FROM account_bank_receive a "
			+ "INNER JOIN bank_type b "
			+ "ON a.bank_type_id = b.id "
			+ "WHERE a.id = :bankId AND a.is_authenticated = TRUE "
			+ "LIMIT 1 ", nativeQuery = true)
	IAccountBankReceiveDTO getAccountBankInfoResById(String bankId);

	@Query(value = "SELECT '' AS vso, '' AS merchantName, "
			+ "COALESCE(b.email, 0) AS email, b.phone_no AS phoneNo, a.user_id AS userId, "
			+ "a.bank_account AS bankAccount, a.bank_account_name AS userBankName, "
			+ "c.bank_short_name AS bankShortName "
			+ "FROM account_bank_receive a "
			+ "INNER JOIN account_login b ON a.user_id = b.id "
			+ "INNER JOIN bank_type c ON c.id = a.bank_type_id "
			+ "WHERE a.id = :bankId ", nativeQuery = true)
	IBankReceiveMapperDTO getMerchantBankMapper(String bankId);

	@Query(value = "SELECT '' AS vso, '' AS merchantName, '' AS platform, "
			+ "COALESCE(b.email, '') AS email, b.phone_no AS phoneNo, a.user_id AS userId, "
			+ "a.bank_account AS bankAccount, a.bank_account_name AS userBankName, "
			+ "c.bank_short_name AS bankShortName, a.mms_active AS mmsActive "
			+ "FROM account_bank_receive a "
			+ "INNER JOIN account_login b ON a.user_id = b.id "
			+ "INNER JOIN bank_type c ON c.id = a.bank_type_id "
			+ "WHERE a.id = :bankId ", nativeQuery = true)
    List<ICustomerDetailDTO> getCustomerDetailByBankId(String bankId);

	@Query(value = "SELECT COUNT(a.id) "
			+ "FROM account_bank_receive a "
			+ "INNER JOIN account_login b ON a.user_id = b.id "
			+ "WHERE a.bank_account LIKE %:value% "
			+ "AND a.is_authenticated = TRUE ", nativeQuery = true)
    int countBankInvoiceByBankAccount(String value);

	@Query(value = "SELECT a.id AS bankId, c.mid AS merchantId, "
			+ "a.bank_account_name AS userBankName, b.phone_no AS phoneNo, "
			+ "b.email AS email, a.bank_account AS bankAccount, "
			+ "a.mms_active AS mmsActive, c.title AS feePackage, "
			+ "a.bank_type_id AS bankTypeId "
			+ "FROM account_bank_receive a "
			+ "INNER JOIN account_login b ON a.user_id = b.id "
			+ "LEFT JOIN bank_receive_fee_package c ON c.bank_id = a.id "
			+ "WHERE a.bank_account LIKE %:value% AND a.is_authenticated = TRUE "
			+ "LIMIT :offset, :size ", nativeQuery = true)
	List<IBankAccountInvoiceInfoDTO> getBankInvoiceByBankAccount(String value, int offset, int size);

	@Query(value = "SELECT a.id AS bankId, a.bank_account AS bankAccount, "
			+ "b.bank_short_name AS bankShortName, c.phone_no AS phoneNo, "
			+ "a.bank_account_name AS userBankName, c.email AS email, "
			+ "a.mms_active AS mmsActive "
			+ "FROM account_bank_receive a "
			+ "INNER JOIN bank_type b ON a.bank_type_id = b.id "
			+ "INNER JOIN account_login c ON c.id = a.user_id "
			+ "WHERE a.id = :bankId ", nativeQuery = true)
	AccountBankDetailAdminDTO getAccountBankDetailAdmin(String bankId);

	@Query(value = "SELECT a.id AS bankId, b.phone_no AS phoneNo, a.mms_active AS mmsActive, "
			+ "COALESCE(b.email, '') AS email, a.bank_account AS bankAccount, "
			+ "a.bank_account_name AS userBankName, c.bank_short_name AS bankShortName "
			+ "FROM account_bank_receive a "
			+ "INNER JOIN account_login b ON a.user_id = b.id "
			+ "INNER JOIN bank_type c ON a.bank_type_id = c.id "
			+ "WHERE a.id = :bankId LIMIT 1 ", nativeQuery = true)
    IBankAccountInvoicesDTO getBankAccountInvoices(String bankId);

	@Query(value = "SELECT a.id AS bankId, a.bank_account AS bankAccount, "
			+ "a.user_id AS userId "
			+ "FROM account_bank_receive a "
			+ "WHERE a.id = :bankId ", nativeQuery = true)
    BankAccountRechargeDTO getBankAccountRecharge(String bankId);

	@Query(value = "SELECT a.bank_account AS bankAccount, b.bank_short_name AS bankShortName, "
			+ "a.bank_account_name AS userBankName, a.mms_active AS mmsActive "
			+ "FROM account_bank_receive a "
			+ "INNER JOIN bank_type b ON a.bank_type_id = b.id "
			+ "WHERE a.id = :bankId", nativeQuery = true)
    IBankReceiveFeePackageDTO getCustomerBankDetailByBankId(String bankId);

	@Query(value = "SELECT COALESCE(a.mms_active, false) AS mmsActive, a.bank_account AS bankAccount, "
			+ "a.id AS bankId, a.bank_account_name AS userbankName, "
			+ "b.bank_short_name AS bankShortName "
			+ "FROM account_bank_receive a "
			+ "INNER JOIN bank_type b ON a.bank_type_id = b.id "
			+ "WHERE a.id = :bankId ", nativeQuery = true)
	List<IAccountBankReceiveDTO> getBankIdsByBankId(String bankId);
}
