package com.vietqr.org.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.AccountBankPaymentEntity;

@Repository
public interface AccountBankPaymentRepository extends JpaRepository<AccountBankPaymentEntity, Long>{

	@Query(value =  "SELECT * FROM account_bank_payment WHERE user_id = :userId", nativeQuery = true)
	List<AccountBankPaymentEntity> getAccountBank(@Param(value = "userId")String userId);

	@Transactional
	@Modifying
	@Query(value = "DELETE FROM account_bank_payment WHERE id = :id", nativeQuery = true)
	void deleteAccountBank(@Param(value = "id")String id);

	@Query(value = "SELECT id FROM account_bank_payment WHERE bank_account = :bankAccount AND bank_type_id = :bankTypeId", nativeQuery =true)
	String checkExistedBankAccount(@Param(value ="bankAccount") String bankAccount, @Param(value = "bankTypeId")String bankTypeId);

	@Query(value = "SELECT * FROM account_bank_payment WHERE id = :bankId", nativeQuery = true)
	AccountBankPaymentEntity getAccountBankById(@Param(value = "bankId") String bankId);
}
