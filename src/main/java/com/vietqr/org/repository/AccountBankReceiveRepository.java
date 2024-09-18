package com.vietqr.org.repository;

import java.util.List;

import javax.transaction.Transactional;

import com.vietqr.org.dto.*;
import com.vietqr.org.dto.bidv.CustomerVaInfoDataDTO;
import com.vietqr.org.dto.qrfeed.IAccountBankDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.AccountBankReceiveEntity;

@Repository
public interface AccountBankReceiveRepository extends JpaRepository<AccountBankReceiveEntity, Long> {

	@Query(value = "SELECT a.bank_account AS bankAccount, a.bank_account_name AS bankAccountName, " +
			"a.status AS status, a.mms_active AS mmsActive, " +
            "COALESCE(a.phone_authenticated, '') AS phoneAuthenticated, " +
            "a.is_authenticated AS isAuthenticated, " +
			"COALESCE(a.national_id, '') AS nationalId, c.bank_short_name AS bankShortName, " +
            "a.valid_fee_from AS fromDate, a.valid_fee_to AS toDate, " +
			"COALESCE((a.valid_fee_to - a.valid_fee_from), 0) as activeService " +
            "FROM account_bank_receive a " +
            "INNER JOIN bank_type c ON a.bank_type_id = c.id " +
            "WHERE a.user_id = :userId ", nativeQuery = true)
	public List<IBankInfoDTO> getBankInfoByUserId(String userId);

	@Query(value = "SELECT a.bank_account AS bankAccount, a.bank_account_name AS bankAccountName, " +
            "a.status AS status, a.mms_active AS mmsActive, " +
			"a.is_authenticated AS isAuthenticated, " +
            "COALESCE(a.phone_authenticated, '') AS phoneAuthenticated, " +
            "COALESCE(a.national_id, '') AS nationalId, c.bank_short_name AS bankShortName, " +
            "a.valid_fee_from AS fromDate, a.valid_fee_to AS toDate, " +
            "COALESCE((a.valid_fee_to - a.valid_fee_from), 0) as activeService " +
            "FROM account_bank_receive a " +
            "INNER JOIN account_bank_receive_share b ON b.bank_id = a.id " +
            "INNER JOIN bank_type c ON a.bank_type_id = c.id " +
            "WHERE b.user_id = :userId AND b.is_owner = false " +
			"GROUP BY a.id, b.user_id ", nativeQuery = true)
	public List<IBankShareDTO> getBankShareInfoByUserId(String userId);

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

	@Query(value = "SELECT a.id AS bankId, a.bank_account AS bankAccount, a.bank_account_name AS bankAccountName, "
			+ "a.bank_type_id AS bankTypeId, a.is_authenticated AS isAuthenticated, "
			+ "a.is_sync AS isSync, a.is_wp_sync AS isWpSync, a.mms_active AS mmsActive, a.national_id AS nationalId, "
			+ "a.phone_authenticated AS phoneAuthenticated, a.status AS status, a.type AS type, "
			+ "a.user_id AS userId, a.is_rpa_sync AS isRpaSync "
			+ "FROM account_bank_receive a "
			+ "WHERE (a.bank_account LIKE %:value% OR a.bank_account_name LIKE %:value%) LIMIT :offset, :size ", nativeQuery = true)
	List<IListAccountBankDTO> getListBankAccounts(String value, int offset, int size);

	@Query(value = "SELECT COUNT(a.id) FROM account_bank_receive a", nativeQuery = true)
	int countListBankAccounts();


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

	@Query(value = "SELECT * FROM account_bank_receive WHERE bank_account = :bankAccount AND " +
			"bank_type_id = :bankTypeId AND is_authenticated = true AND status = 1", nativeQuery = true)
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
			+ "WHEN b.is_sync = false AND b.mms_active = false THEN 1  "
			+ "END AS flow, "
			+ "d.service_fee_id AS serviceFeeId, d.short_name as serviceFeeName, "
			+ "e.address AS address "
			+ "FROM account_customer_bank a "
			+ "INNER JOIN account_bank_receive b "
			+ "ON a.bank_id = b.id  "
			+ "INNER JOIN bank_type c  "
			+ "ON b.bank_type_id = c.id  "
			+ "LEFT JOIN account_bank_fee d "
			+ "ON b.id = d.bank_id "
			+ "LEFT JOIN customer_sync e "
			+ "ON a.customer_sync_id = e.id "
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
			+ "b.bank_short_name as bankShortName, a.bank_account as bankAccount, a.is_valid_service AS isValidService, "
			+ "COALESCE(b.push_notification, 1) AS pushNotification "
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
			+ "AND b.bank_code = :bankCode AND is_authenticated = TRUE LIMIT 1", nativeQuery = true)
	AccountBankReceiveEntity getAccountBankReceiveByBankAccountAndBankCode(String bankAccount, String bankCode);

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

	@Query(value = "SELECT '' AS merchantName, "
			+ "COALESCE(b.email, 0) AS email, b.phone_no AS phoneNo, a.user_id AS userId, "
			+ "a.bank_account AS bankAccount, a.bank_account_name AS userBankName, "
			+ "c.bank_short_name AS bankShortName, a.vso AS vso "
			+ "FROM account_bank_receive a "
			+ "INNER JOIN account_login b ON a.user_id = b.id "
			+ "INNER JOIN bank_type c ON c.id = a.bank_type_id "
			+ "WHERE a.id = :bankId ", nativeQuery = true)
	IBankReceiveMapperDTO getMerchantBankMapper(String bankId);

	@Query(value = "SELECT a.vso AS vso, '' AS merchantName, '' AS platform, "
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
			+ "WHERE (a.bank_account LIKE %:value% OR a.bank_account_name LIKE %:value%) "
			+ "AND a.is_authenticated = TRUE ", nativeQuery = true)
    int countBankInvoiceByBankAccount(String value);

	@Query(value = "SELECT a.id AS bankId, c.mid AS merchantId, a.vso AS vso, "
			+ "a.bank_account_name AS userBankName, b.phone_no AS phoneNo, "
			+ "b.email AS email, a.bank_account AS bankAccount, "
			+ "a.mms_active AS mmsActive, c.title AS feePackage, "
			+ "a.bank_type_id AS bankTypeId "
			+ "FROM account_bank_receive a "
			+ "INNER JOIN account_login b ON a.user_id = b.id "
			+ "LEFT JOIN bank_receive_fee_package c ON c.bank_id = a.id "
			+ "WHERE (a.bank_account LIKE %:value% OR a.bank_account_name LIKE %:value%) AND a.is_authenticated = TRUE "
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

	@Query(value = "SELECT a.* FROM account_bank_receive a "
			+ "WHERE a.customer_id = :customerId ", nativeQuery = true)
    AccountBankReceiveEntity getAccountBankByCustomerIdAndByServiceId(String customerId);

	@Query(value = "SELECT customer_id AS customer_id, id AS bankId, "
			+ "bank_account_name AS customer_name "
			+ "FROM account_bank_receive "
			+ "WHERE customer_id = :customerId "
			+ "LIMIT 1", nativeQuery = true)
    CustomerVaInfoDataDTO getAccountCustomerInfo(String customerId);

	@Transactional
	@Modifying
	@Query(value = "UPDATE account_bank_receive SET national_id = '', phone_authenticated = '', "
			+ "customer_id = '', is_authenticated = FALSE WHERE id = :bankId AND user_id = :userId", nativeQuery = true)
	void updateRegisterAuthentication(String userId, String bankId);

	@Query(value = "SELECT a.id FROM account_bank_receive a "
			+ "INNER JOIN customer_va b ON a.id = b.bank_id "
			+ "WHERE a.user_id = :userId AND b.merchant_id = :merchantId ", nativeQuery = true)
	String getBankIdByUserIdAndMerchantId(String userId, String merchantId);

	@Query(value = "SELECT a.id AS bankId, b.merchant_id AS merchantId, "
			+ "a.user_id AS userId FROM account_bank_receive a "
			+ "INNER JOIN customer_va b ON a.id = b.bank_id "
			+ "INNER JOIN bank_type c ON c.id = a.bank_type_id "
			+ "WHERE a.bank_account = :bankAccount AND c.bank_code = :bankCode "
			+ "AND a.is_authenticated = TRUE LIMIT 1", nativeQuery = true)
	BidvUnlinkedDTO getMerchantIdByBankAccountBidvAuthen(String bankAccount, String bankCode);

	@Transactional
	@Modifying
	@Query(value = "UPDATE account_bank_receive SET national_id = '', phone_authenticated = '', "
			+ "customer_id = '', is_authenticated = FALSE WHERE id = :bankId AND user_id = :userId "
			+ "AND is_authenticated = TRUE ", nativeQuery = true)
	void updateRegisterUnlinkBidv(String userId, String bankId);

	@Query(value = "SELECT b.bank_code "
			+ "FROM account_bank_receive a "
			+ "INNER JOIN bank_type b ON a.bank_type_id = b.id "
			+ "WHERE a.id = :bankId LIMIT 1", nativeQuery = true)
	String getBankCodeByBankId(String bankId);

	@Transactional
	@Modifying
	@Query(value = "UPDATE account_bank_receive "
			+ "SET national_id = :nationalId, phone_authenticated = :phoneAuthenticated, "
			+ "bank_account_name = :bankAccountName, bank_account = :bankAccount, "
			+ "customer_id = :customerId, "
			+ "ewallet_token = :ewalletToken, is_authenticated = true "
			+ "WHERE id = :bankId", nativeQuery = true)
	void updateRegisterAuthenticationBankBIDV(String nationalId, String phoneAuthenticated,
											  String bankAccountName, String bankAccount,
											  String customerId, String ewalletToken, String bankId);

	@Query(value = "SELECT b.bank_short_name AS bankShortName, " +
			"COALESCE(COUNT(a.id), 0) AS totalAccounts, " +
			"COALESCE(SUM(CASE WHEN a.is_authenticated = true THEN 1 ELSE 0 END), 0) AS linkedAccounts, " +
			"COALESCE(SUM(CASE WHEN a.is_authenticated = false THEN 1 ELSE 0 END), 0) AS unlinkedAccounts " +
			"FROM bank_type b " +
			"LEFT JOIN account_bank_receive a ON b.id = a.bank_type_id " +
			"GROUP BY b.bank_short_name " +
			"ORDER BY b.bank_short_name", nativeQuery = true)
	List<IAccountBankMonthDTO> getBankAccountStatistics();

	@Query(value = "SELECT a.id FROM account_bank_receive a "
			+ "INNER JOIN bank_type b ON a.bank_type_id = b.id "
			+ "WHERE a.bank_account = :bankAccount "
			+ "AND b.bank_short_name = :bankShortName "
			+ "AND a.is_authenticated = TRUE LIMIT 1 ", nativeQuery = true)
    String getBankIdByBankAccount(String bankAccount, String bankShortName);

	@Query(value = "SELECT a.user_id AS userId, b.mid AS mid "
			+ "FROM account_bank_receive a "
			+ "LEFT JOIN bank_receive_fee_package b ON a.id = b.bank_id "
			+ "WHERE a.id = :bankId ", nativeQuery = true)
	BankAccountAdminDTO getUserIdAndMidByBankId(String bankId);

	@Transactional
	@Modifying
	@Query(value = "UPDATE account_bank_receive "
			+ "SET vso = :vso "
			+ "WHERE id = :bankId", nativeQuery = true)
	void updateVsoBankAccount(String vso, String bankId);

	@Query(value = "SELECT b.bank_name AS bankName, b.bank_code AS bankCode, b.bank_short_name AS bankShortName, "
			+ "a.bank_account_name AS userBankName, a.bank_account AS bankAccount, "
			+ "b.img_id AS imgId, c.cai_value AS caiValue, a.is_valid_service AS isValidService "
			+ "FROM account_bank_receive a "
			+ "INNER JOIN bank_type b ON a.bank_type_id = b.id "
			+ "INNER JOIN cai_bank c ON b.id = c.bank_type_id "
			+ "WHERE a.id = :bankId LIMIT 1", nativeQuery = true)
	BankDetailTypeCaiValueDTO getBankAccountTypeDetail(String bankId);

	@Query(value = "SELECT abr.id as bankId, abr.bank_account AS bankAccount, abr.bank_account_name AS bankAccountName, "
			+ "bt.bank_short_name AS bankShortName, abr.phone_authenticated AS phoneAuthenticated, "
			+ "abr.is_valid_service AS isValidService, abr.is_authenticated AS isAuthenticated, bt.status AS bankTypeStatus, bt.bank_code AS bankCode, "
			+ "abr.mms_active AS mmsActive, abr.national_id AS nationalId,abr.valid_fee_to AS validFeeTo, "
			+ "abr.valid_fee_from AS validFeeFrom, abr.time_created AS timeCreate, "
			+ "al.phone_no AS phoneNo, al.email AS email, abr.status AS status, abr.vso AS vso "
			+ "FROM account_bank_receive abr "
			+ "INNER JOIN bank_type bt ON abr.bank_type_id = bt.id "
			+ "INNER JOIN account_login al ON abr.user_id = al.id "
			+ "ORDER BY abr.bank_account_name ASC "
			+ "LIMIT :offset, :size", nativeQuery = true)
	List<IBankAccountResponseDTO> getAllBankAccounts(@Param("offset") int offset, @Param("size") int size);

	@Query(value = "SELECT COUNT(abr.id) FROM account_bank_receive abr", nativeQuery = true)
	int countAllBankAccounts();

	@Query(value = "SELECT COUNT(abr.id) FROM account_bank_receive abr "
			+ "WHERE abr.bank_account LIKE %:keyword%", nativeQuery = true)
	int countBankAccountsByAccount(@Param("keyword") String keyword);

	@Query(value = "SELECT COUNT(abr.id) FROM account_bank_receive abr "
			+ "WHERE abr.bank_account_name LIKE %:keyword%", nativeQuery = true)
	int countBankAccountsByAccountName(@Param("keyword") String keyword);

	@Query(value = "SELECT COUNT(abr.id) FROM account_bank_receive abr "
			+ "WHERE abr.phone_authenticated LIKE %:keyword%", nativeQuery = true)
	int countBankAccountsByPhoneAuthenticated(@Param("keyword") String keyword);

	@Query(value = "SELECT COUNT(abr.id) FROM account_bank_receive abr "
			+ "WHERE abr.national_id LIKE %:keyword%", nativeQuery = true)
	int countBankAccountsByNationalId(@Param("keyword") String keyword);

	@Query(value = "SELECT abr.* " +
			"FROM account_bank_receive abr " +
			"INNER JOIN bank_receive_fee_package brfp ON abr.id = brfp.bank_id " +
			"INNER JOIN merchant_sync ms ON brfp.mid = ms.id " +
			"WHERE ms.id = :merchantId", nativeQuery = true)
	List<AccountBankReceiveEntity> findBankAccountsByMerchantId(@Param("merchantId") String merchantId);

	@Query(value = "SELECT bank_account_name FROM account_bank_receive where bank_account = :bankAccount LIMIT 1",nativeQuery = true)
	String getBankAccountNameByBankAccount(@Param("bankAccount") String bankAccount);

	// Lọc theo thời gian tạo (thêm gần đây)
	@Query(value = "SELECT abr.id as bankId, abr.bank_account AS bankAccount, abr.bank_account_name AS bankAccountName, "
			+ "bt.bank_short_name AS bankShortName, abr.phone_authenticated AS phoneAuthenticated, "
			+ "abr.is_valid_service AS isValidService, abr.is_authenticated AS isAuthenticated, bt.status AS bankTypeStatus, bt.bank_code AS bankCode, "
			+ "abr.mms_active AS mmsActive, abr.national_id AS nationalId, abr.valid_fee_to AS validFeeTo, abr.valid_fee_from AS validFeeFrom, abr.time_created AS timeCreate,  "
			+ "al.phone_no AS phoneNo, al.email AS email, abr.status AS status, abr.vso AS vso "
			+ "FROM account_bank_receive abr "
			+ "INNER JOIN bank_type bt ON abr.bank_type_id = bt.id "
			+ "INNER JOIN account_login al ON abr.user_id = al.id "
			+ "ORDER BY abr.time_created DESC LIMIT :offset, :size", nativeQuery = true)
	List<IBankAccountResponseDTO> getBankAccountsByTimeCreate(@Param("offset") int offset, @Param("size") int size);

	@Query(value = "SELECT COUNT(*) FROM account_bank_receive", nativeQuery = true)
	int countBankAccountsByTimeCreate();


	@Query(value = "SELECT "
			+ "COALESCE(SUM(CASE WHEN  valid_fee_to != 0 and  valid_fee_to < :currentTime THEN 1 ELSE 0 END), 0) AS overdueCount, "  // Tài khoản quá hạn
			+ "COALESCE(SUM(CASE WHEN valid_fee_to BETWEEN :currentTime AND :sevenDaysLater THEN 1 ELSE 0 END), 0) AS nearlyExpireCount, "  // Tài khoản gần hết hạn (trong vòng 7 ngày tới)
			+ "COALESCE(SUM(CASE WHEN is_authenticated IS NOT NULL THEN 1 ELSE 0 END), 0) AS validCount, "
			+ "COALESCE(SUM(CASE WHEN is_authenticated = true THEN 1 ELSE 0 END), 0) AS notRegisteredCount "
			+ "FROM account_bank_receive", nativeQuery = true)
	IAdminExtraBankDTO getExtraBankDataForAllTime(@Param("currentTime") long currentTime, @Param("sevenDaysLater") long sevenDaysLater);


	// Truy vấn lấy danh sách tài khoản đã quá hạn
	@Query(value = "SELECT abr.id as bankId, abr.bank_account AS bankAccount, abr.bank_account_name AS bankAccountName, "
			+ "bt.bank_short_name AS bankShortName, abr.phone_authenticated AS phoneAuthenticated, "
			+ "abr.is_valid_service AS isValidService, abr.is_authenticated AS isAuthenticated, bt.status AS bankTypeStatus, bt.bank_code AS bankCode, "
			+ "abr.mms_active AS mmsActive, abr.national_id AS nationalId, abr.valid_fee_to AS validFeeTo, abr.valid_fee_from AS validFeeFrom, "
			+ "abr.time_create AS timeCreate, al.phone_no AS phoneNo, al.email AS email, abr.status AS status, abr.vso AS vso "
			+ "FROM account_bank_receive abr "
			+ "INNER JOIN bank_type bt ON abr.bank_type_id = bt.id "
			+ "INNER JOIN account_login al ON abr.user_id = al.id "
			+ "WHERE abr.valid_fee_to != 0 OR abr.valid_fee_to < :currentTime "
			+ "ORDER BY abr.valid_fee_to ASC LIMIT :offset, :size", nativeQuery = true)
	List<IBankAccountResponseDTO> getOverdueBankAccounts(@Param("currentTime") long currentTime, @Param("offset") int offset, @Param("size") int size);

	@Query(value = "SELECT COUNT(*) FROM account_bank_receive WHERE is_valid_service = false OR valid_fee_to < :currentTime", nativeQuery = true)
	int countOverdueBankAccounts(@Param("currentTime") long currentTime);

	// Truy vấn lấy danh sách tài khoản gần hết hạn (trong khoảng 7 ngày tới)
	@Query(value = "SELECT abr.id as bankId, abr.bank_account AS bankAccount, abr.bank_account_name AS bankAccountName, "
			+ "bt.bank_short_name AS bankShortName, abr.phone_authenticated AS phoneAuthenticated, "
			+ "abr.is_valid_service AS isValidService, abr.is_authenticated AS isAuthenticated, bt.status AS bankTypeStatus, bt.bank_code AS bankCode, "
			+ "abr.mms_active AS mmsActive, abr.national_id AS nationalId, abr.valid_fee_to AS validFeeTo, abr.valid_fee_from AS validFeeFrom, "
			+ "abr.time_create AS timeCreate, al.phone_no AS phoneNo, al.email AS email, abr.status AS status, abr.vso AS vso "
			+ "FROM account_bank_receive abr "
			+ "INNER JOIN bank_type bt ON abr.bank_type_id = bt.id "
			+ "INNER JOIN account_login al ON abr.user_id = al.id "
			+ "WHERE abr.valid_fee_to >= :currentTime AND abr.valid_fee_to <= :sevenDaysLater "
			+ "ORDER BY abr.valid_fee_to ASC LIMIT :offset, :size", nativeQuery = true)
	List<IBankAccountResponseDTO> getNearlyExpiredBankAccounts(@Param("currentTime") long currentTime, @Param("sevenDaysLater") long sevenDaysLater, @Param("offset") int offset, @Param("size") int size);

	@Query(value = "SELECT COUNT(*) FROM account_bank_receive WHERE valid_fee_to >= :currentTime AND valid_fee_to <= :sevenDaysLater", nativeQuery = true)
	int countNearlyExpiredBankAccounts(@Param("currentTime") long currentTime, @Param("sevenDaysLater") long sevenDaysLater);

	// Truy vấn lấy danh sách tài khoản còn hạn (valid_fee_to > 7 ngày)
	@Query(value = "SELECT abr.id as bankId, abr.bank_account AS bankAccount, abr.bank_account_name AS bankAccountName, "
			+ "bt.bank_short_name AS bankShortName, abr.phone_authenticated AS phoneAuthenticated, "
			+ "abr.is_valid_service AS isValidService, abr.is_authenticated AS isAuthenticated, bt.status AS bankTypeStatus, bt.bank_code AS bankCode, "
			+ "abr.mms_active AS mmsActive, abr.national_id AS nationalId, abr.valid_fee_to AS validFeeTo, abr.valid_fee_from AS validFeeFrom, "
			+ "abr.time_created AS timeCreate, al.phone_no AS phoneNo, al.email AS email, abr.status AS status, abr.vso AS vso "
			+ "FROM account_bank_receive abr "
			+ "INNER JOIN bank_type bt ON abr.bank_type_id = bt.id "
			+ "INNER JOIN account_login al ON abr.user_id = al.id "
			+ "WHERE abr.valid_fee_to > :sevenDaysLater "
			+ "ORDER BY abr.valid_fee_to ASC LIMIT :offset, :size", nativeQuery = true)
	List<IBankAccountResponseDTO> getValidBankAccounts(@Param("sevenDaysLater") long sevenDaysLater, @Param("offset") int offset, @Param("size") int size);

	@Query(value = "SELECT COUNT(*) FROM account_bank_receive WHERE valid_fee_to > :sevenDaysLater", nativeQuery = true)
	int countValidBankAccounts(@Param("sevenDaysLater") long sevenDaysLater);

	// Truy vấn lấy danh sách tài khoản chưa đăng ký dịch vụ
	@Query(value = "SELECT abr.id as bankId, abr.bank_account AS bankAccount, abr.bank_account_name AS bankAccountName, "
			+ "bt.bank_short_name AS bankShortName, abr.phone_authenticated AS phoneAuthenticated, "
			+ "abr.is_valid_service AS isValidService, abr.is_authenticated AS isAuthenticated, bt.status AS bankTypeStatus, bt.bank_code AS bankCode, "
			+ "abr.mms_active AS mmsActive, abr.national_id AS nationalId, abr.valid_fee_to AS validFeeTo, abr.valid_fee_from AS validFeeFrom, "
			+ "abr.time_created AS timeCreate, al.phone_no AS phoneNo, al.email AS email, abr.status AS status, abr.vso AS vso "
			+ "FROM account_bank_receive abr "
			+ "INNER JOIN bank_type bt ON abr.bank_type_id = bt.id "
			+ "INNER JOIN account_login al ON abr.user_id = al.id "
			+ "WHERE abr.is_valid_service = false AND abr.valid_fee_to IS NULL "
			+ "ORDER BY abr.time_created ASC LIMIT :offset, :size", nativeQuery = true)
	List<IBankAccountResponseDTO> getNotRegisteredBankAccounts(@Param("offset") int offset, @Param("size") int size);

	@Query(value = "SELECT COUNT(*) FROM account_bank_receive WHERE is_valid_service = false AND valid_fee_to IS NULL", nativeQuery = true)
	int countNotRegisteredBankAccounts();

	@Query(value = "SELECT abr.id as bankId, abr.bank_account AS bankAccount, abr.bank_account_name AS bankAccountName, "
			+ "bt.bank_short_name AS bankShortName, abr.phone_authenticated AS phoneAuthenticated, "
			+ "abr.is_valid_service AS isValidService, abr.is_authenticated AS isAuthenticated, bt.status AS bankTypeStatus, bt.bank_code AS bankCode, "
			+ "abr.mms_active AS mmsActive, abr.national_id AS nationalId, abr.valid_fee_to AS validFeeTo, abr.valid_fee_from AS validFeeFrom, abr.time_created AS timeCreate, "
			+ "al.phone_no AS phoneNo, al.email AS email, abr.status AS status, abr.vso AS vso "
			+ "FROM account_bank_receive abr "
			+ "INNER JOIN bank_type bt ON abr.bank_type_id = bt.id "
			+ "INNER JOIN account_login al ON abr.user_id = al.id "
			+ "WHERE abr.bank_account LIKE %:keyword% "
			+ "ORDER BY CASE WHEN abr.is_valid_service =true and abr.valid_fee_from != 0  and abr.valid_fee_to != 0  and  abr.valid_fee_to < :currentTime THEN 1 "  // Quá hạn
			+ "WHEN abr.valid_fee_to BETWEEN :currentTime AND :sevenDaysLater THEN 2 "  // Gần hết hạn
			+ "WHEN abr.valid_fee_to > :sevenDaysLater THEN 3 "  // Còn hạn
			+ "ELSE 4 END, abr.bank_account_name ASC "
			+ "LIMIT :offset, :size", nativeQuery = true)
	List<IBankAccountResponseDTO> getBankAccountsByAccountAndSorted(@Param("keyword") String keyword, @Param("currentTime") long currentTime, @Param("sevenDaysLater") long sevenDaysLater, @Param("offset") int offset, @Param("size") int size);


	@Query(value = "SELECT abr.id as bankId, abr.bank_account AS bankAccount, abr.bank_account_name AS bankAccountName, "
			+ "bt.bank_short_name AS bankShortName, abr.phone_authenticated AS phoneAuthenticated, "
			+ "abr.is_valid_service AS isValidService, abr.is_authenticated AS isAuthenticated, bt.status AS bankTypeStatus, bt.bank_code AS bankCode, "
			+ "abr.mms_active AS mmsActive, abr.national_id AS nationalId, abr.valid_fee_to AS validFeeTo, abr.valid_fee_from AS validFeeFrom, abr.time_created AS timeCreate, "
			+ "al.phone_no AS phoneNo, al.email AS email, abr.status AS status, abr.vso AS vso "
			+ "FROM account_bank_receive abr "
			+ "INNER JOIN bank_type bt ON abr.bank_type_id = bt.id "
			+ "INNER JOIN account_login al ON abr.user_id = al.id "
			+ "WHERE abr.bank_account_name LIKE %:keyword% "
			+ "ORDER BY CASE WHEN abr.is_valid_service =true and abr.valid_fee_from != 0  and abr.valid_fee_to != 0 and abr.valid_fee_to < :currentTime THEN 1 "  // Quá hạn
			+ "WHEN abr.valid_fee_to BETWEEN :currentTime AND :sevenDaysLater THEN 2 "  // Gần hết hạn
			+ "WHEN abr.valid_fee_to > :sevenDaysLater THEN 3 "  // Còn hạn
			+ "ELSE 4 END, abr.bank_account_name ASC "
			+ "LIMIT :offset, :size", nativeQuery = true)
	List<IBankAccountResponseDTO> getBankAccountsByAccountNameAndSorted(@Param("keyword") String keyword, @Param("currentTime") long currentTime, @Param("sevenDaysLater") long sevenDaysLater, @Param("offset") int offset, @Param("size") int size);

	@Query(value = "SELECT abr.id as bankId, abr.bank_account AS bankAccount, abr.bank_account_name AS bankAccountName, "
			+ "bt.bank_short_name AS bankShortName, abr.phone_authenticated AS phoneAuthenticated, "
			+ "abr.is_valid_service AS isValidService, abr.is_authenticated AS isAuthenticated, bt.status AS bankTypeStatus, bt.bank_code AS bankCode, "
			+ "abr.mms_active AS mmsActive, abr.national_id AS nationalId, abr.valid_fee_to AS validFeeTo, abr.valid_fee_from AS validFeeFrom, abr.time_created AS timeCreate, "
			+ "al.phone_no AS phoneNo, al.email AS email, abr.status AS status, abr.vso AS vso "
			+ "FROM account_bank_receive abr "
			+ "INNER JOIN bank_type bt ON abr.bank_type_id = bt.id "
			+ "INNER JOIN account_login al ON abr.user_id = al.id "
			+ "WHERE abr.phone_authenticated LIKE %:keyword% "
			+ "ORDER BY CASE WHEN abr.is_valid_service =true and abr.valid_fee_from != 0  and abr.valid_fee_to != 0 and abr.valid_fee_to < :currentTime THEN 1 "  // Quá hạn
			+ "WHEN abr.valid_fee_to BETWEEN :currentTime AND :sevenDaysLater THEN 2 "  // Gần hết hạn
			+ "WHEN abr.valid_fee_to > :sevenDaysLater THEN 3 "  // Còn hạn
			+ "ELSE 4 END, abr.bank_account_name ASC "
			+ "LIMIT :offset, :size", nativeQuery = true)
	List<IBankAccountResponseDTO> getBankAccountsByPhoneAuthenticatedAndSorted(@Param("keyword") String keyword, @Param("currentTime") long currentTime, @Param("sevenDaysLater") long sevenDaysLater, @Param("offset") int offset, @Param("size") int size);

	@Query(value = "SELECT abr.id as bankId, abr.bank_account AS bankAccount, abr.bank_account_name AS bankAccountName, "
			+ "bt.bank_short_name AS bankShortName, abr.phone_authenticated AS phoneAuthenticated, "
			+ "abr.is_valid_service AS isValidService, abr.is_authenticated AS isAuthenticated, bt.status AS bankTypeStatus, bt.bank_code AS bankCode, "
			+ "abr.mms_active AS mmsActive, abr.national_id AS nationalId, abr.valid_fee_to AS validFeeTo, abr.valid_fee_from AS validFeeFrom, abr.time_created AS timeCreate, "
			+ "al.phone_no AS phoneNo, al.email AS email, abr.status AS status, abr.vso AS vso "
			+ "FROM account_bank_receive abr "
			+ "INNER JOIN bank_type bt ON abr.bank_type_id = bt.id "
			+ "INNER JOIN account_login al ON abr.user_id = al.id "
			+ "WHERE abr.national_id LIKE %:keyword% "
			+ "ORDER BY CASE WHEN abr.is_valid_service = true and abr.valid_fee_from != 0  and abr.valid_fee_to < :currentTime THEN 1 "  // Quá hạn
			+ "WHEN abr.valid_fee_to != 0 and abr.valid_fee_to BETWEEN :currentTime AND :sevenDaysLater THEN 2 "  // Gần hết hạn
			+ "WHEN abr.valid_fee_to > :sevenDaysLater THEN 3 "  // Còn hạn
			+ "ELSE 4 END, abr.bank_account_name ASC "
			+ "LIMIT :offset, :size", nativeQuery = true)
	List<IBankAccountResponseDTO> getBankAccountsByNationalIdAndSorted(@Param("keyword") String keyword, @Param("currentTime") long currentTime, @Param("sevenDaysLater") long sevenDaysLater, @Param("offset") int offset, @Param("size") int size);

	@Transactional
	@Modifying
	@Query(value = "UPDATE account_bank_receive"
			+ " SET push_notification = :value"
			+ " WHERE id = :bankId"
			, nativeQuery = true)
	void updatePushNotification(@Param(value = "bankId") String bankId, @Param(value = "value") int value);

	@Transactional
	@Modifying
	@Query(value = "UPDATE account_bank_receive"
			+ " SET is_wp_sync = true "
			+ " WHERE id = :id "
			, nativeQuery = true)
	void updateSyncWpById(String id);

	@Transactional
	@Modifying
	@Query(value = "UPDATE account_bank_receive"
			+ " SET push_notification = :value"
			+ " WHERE user_id = :userId"
			, nativeQuery = true)
	void updatePushNotificationUser(@Param(value = "userId") String userId, @Param(value = "value") int value);

	@Transactional
	@Modifying
	@Query(value = "UPDATE account_bank_receive"
			+ " SET enable_sound_notification = :enableSoundNotification"
			+ " WHERE id = :bankId"
			, nativeQuery = true)
	void updateSoundNotificationByBankId(
			@Param(value = "bankId") String bankId,
			@Param(value = "enableSoundNotification") int enableSoundNotification
	);

	@Transactional
	@Modifying
	@Query(value = "UPDATE account_bank_receive "
			+ "SET enable_voice = "
			+ "CASE "
			+ "WHEN id IN (:bankIds) THEN true "
			+ "ELSE false "
			+ "END "
			+ "WHERE user_id = :userId ", nativeQuery = true)
	void updateEnableVoiceByBankIds(List<String> bankIds, String userId);

	@Query(value = "SELECT te.id AS platformId, COALESCE(te.name, '') AS platformName, COALESCE(t.chat_id, '') AS connectionDetail, 'Telegram' AS platform "
			+ "FROM telegram_account_bank t "
			+ "JOIN telegram te ON t.telegram_id = te.id "
			+ "WHERE t.bank_id = :bankId "
			+ "UNION ALL "
			+ "SELECT le.id AS platformId, COALESCE(le.name, '') AS platformName, COALESCE(l.webhook, '') AS connectionDetail, 'Lark' AS platform "
			+ "FROM lark_account_bank l "
			+ "JOIN lark le ON l.lark_id = le.id "
			+ "WHERE l.bank_id = :bankId "
			+ "UNION ALL "
			+ "SELECT gce.id AS platformId, COALESCE(gce.name, '') AS platformName, COALESCE(g.webhook, '') AS connectionDetail, 'Google Chat' AS platform "
			+ "FROM google_chat_account_bank g "
			+ "JOIN google_chat gce ON g.google_chat_id = gce.id "
			+ "WHERE g.bank_id = :bankId "
			+ "UNION ALL "
			+ "SELECT gse.id AS platformId, COALESCE(gse.name, '') AS platformName, COALESCE(gs.webhook, '') AS connectionDetail, 'Google Sheet' AS platform "
			+ "FROM google_sheet_account_bank gs "
			+ "JOIN google_sheet gse ON gs.google_sheet_id = gse.id "
			+ "WHERE gs.bank_id = :bankId "
			+ "UNION ALL "
			+ "SELECT se.id AS platformId, COALESCE(se.name, '') AS platformName, COALESCE(s.webhook, '') AS connectionDetail, 'Slack' AS platform "
			+ "FROM slack_account_bank s "
			+ "JOIN slack se ON s.slack_id = se.id "
			+ "WHERE s.bank_id = :bankId "
			+ "UNION ALL "
			+ "SELECT de.id AS platformId, COALESCE(de.name, '') AS platformName, COALESCE(d.webhook, '') AS connectionDetail, 'Discord' AS platform "
			+ "FROM discord_account_bank d "
			+ "JOIN discord de ON d.discord_id = de.id "
			+ "WHERE d.bank_id = :bankId "
			+ "ORDER BY platformName ASC "
			+ "LIMIT :offset, :size", nativeQuery = true)
	List<IPlatformConnectionDTO> getPlatformConnectionsByBankId(@Param("bankId") String bankId, @Param("offset") int offset, @Param("size") int size);


	@Query(value = "SELECT COUNT(*) FROM ("
			+ "SELECT 1 AS platform FROM telegram_account_bank t WHERE t.bank_id = :bankId "
			+ "UNION ALL "
			+ "SELECT 1 AS platform FROM lark_account_bank l WHERE l.bank_id = :bankId "
			+ "UNION ALL "
			+ "SELECT 1 AS platform FROM google_chat_account_bank g WHERE g.bank_id = :bankId "
			+ "UNION ALL "
			+ "SELECT 1 AS platform FROM google_sheet_account_bank gs WHERE gs.bank_id = :bankId "
			+ "UNION ALL "
			+ "SELECT 1 AS platform FROM slack_account_bank s WHERE s.bank_id = :bankId "
			+ "UNION ALL "
			+ "SELECT 1 AS platform FROM discord_account_bank d WHERE d.bank_id = :bankId) AS total", nativeQuery = true)
	int countPlatformConnectionsByBankId(@Param("bankId") String bankId);

	@Modifying
	@Transactional
	@Query(value = "UPDATE account_bank_receive SET notification_types = :notificationTypes " +
			"WHERE user_id = :userId AND id = :bankId", nativeQuery = true)
	void updateNotificationTypes(@Param("userId") String userId,
								 @Param("bankId") String bankId,
								 @Param("notificationTypes") String notificationTypes);

	@Query(value = "SELECT distinct abr.* FROM account_bank_receive abr " +
			"JOIN account_bank_receive_share abrs ON abr.id = abrs.bank_id " +
			"WHERE abr.is_authenticated = true AND abrs.is_owner = true AND abr.user_id = :userId", nativeQuery = true)
	List<AccountBankReceiveEntity> getFullAccountBankReceiveByUserId(@Param("userId") String userId);
}
