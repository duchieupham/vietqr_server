package com.vietqr.org.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.dto.AccountBankConnectBranchDTO;
import com.vietqr.org.dto.AccountBankWpDTO;
import com.vietqr.org.dto.BusinessBankDTO;
import com.vietqr.org.entity.AccountBankReceiveEntity;

@Repository
public interface AccountBankReceiveRepository extends JpaRepository<AccountBankReceiveEntity, Long> {

	@Transactional
	@Modifying
	@Query(value = "DELETE FROM account_bank_receive WHERE id = :id", nativeQuery = true)
	void deleteAccountBank(@Param(value = "id") String id);

	@Query(value = "SELECT id FROM account_bank_receive WHERE bank_account = :bankAccount AND bank_type_id = :bankTypeId AND is_authenticated = true ", nativeQuery = true)
	String checkExistedBankAccount(@Param(value = "bankAccount") String bankAccount,
			@Param(value = "bankTypeId") String bankTypeId);

	@Query(value = "SELECT id FROM account_bank_receive WHERE bank_account = :bankAccount AND bank_type_id = :bankTypeId AND user_id = :userId ", nativeQuery = true)
	String checkExistedBankAccountSameUser(@Param(value = "bankAccount") String bankAccount,
			@Param(value = "bankTypeId") String bankTypeId, @Param(value = "userId") String userId);

	@Query(value = "SELECT * FROM account_bank_receive WHERE id = :bankId", nativeQuery = true)
	AccountBankReceiveEntity getAccountBankById(@Param(value = "bankId") String bankId);

	// @Query(value = "SELECT * FROM account_bank_receive WHERE bank_account =
	// :bankAccount AND is_authenticated = true", nativeQuery = true)
	// AccountBankReceiveEntity getAccountBankByBankAccount(@Param(value =
	// "bankAccount") String bankAccount);

	@Transactional
	@Modifying
	@Query(value = "UPDATE account_bank_receive "
			+ "SET national_id = :nationalId, phone_authenticated = :phoneAuthenticated, bank_account_name = :bankAccountName, bank_account = :bankAccount, is_authenticated = true "
			+ "WHERE id = :bankId", nativeQuery = true)
	void updateRegisterAuthenticationBank(@Param(value = "nationalId") String nationalId,
			@Param(value = "phoneAuthenticated") String phoneAuthenticated,
			@Param(value = "bankAccountName") String bankAccountName, @Param(value = "bankAccount") String bankAccount,
			@Param(value = "bankId") String bankId);

	@Transactional
	@Modifying
	@Query(value = "UPDATE account_bank_receive "
			+ "SET is_authenticated = false "
			+ "WHERE bank_account = :bankAccount AND is_authenticated = true", nativeQuery = true)
	void unRegisterAuthenticationBank(@Param(value = "bankAccount") String bankAccount);

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
			+ "WHERE a.is_authenticated = 1 AND a.status = 1 "
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
}
