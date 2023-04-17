package com.vietqr.org.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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

	@Query(value = "SELECT * FROM account_bank_receive WHERE id = :bankId", nativeQuery = true)
	AccountBankReceiveEntity getAccountBankById(@Param(value = "bankId") String bankId);

	@Query(value = "SELECT * FROM account_bank_receive WHERE bank_account = :bankAccount", nativeQuery = true)
	AccountBankReceiveEntity getAccountBankByBankAccount(@Param(value = "bankAccount") String bankAccount);

	@Transactional
	@Modifying
	@Query(value = "UPDATE account_bank_receive "
			+ "SET national_id = :nationalId, phone_authenticated = :phoneAuthenticated, bank_account_name = :bankAccountName, bank_account = :bankAccount, is_authenticated = true "
			+ "WHERE id = :bankId", nativeQuery = true)
	void updateRegisterAuthenticationBank(@Param(value = "nationalId") String nationalId,
			@Param(value = "phoneAuthenticated") String phoneAuthenticated,
			@Param(value = "bankAccountName") String bankAccountName, @Param(value = "bankAccount") String bankAccount,
			@Param(value = "bankId") String bankId);

	@Query(value = "SELECT * FROM account_bank_receive WHERE bank_account = :bankAccount AND bank_type_id = :bankTypeId ", nativeQuery = true)
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
}
