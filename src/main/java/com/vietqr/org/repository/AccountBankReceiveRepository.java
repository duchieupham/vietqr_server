package com.vietqr.org.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.AccountBankReceiveEntity;

@Repository
public interface AccountBankReceiveRepository extends JpaRepository<AccountBankReceiveEntity, Long>{

	@Transactional
	@Modifying
	@Query(value = "DELETE FROM account_bank_receive WHERE id = :id", nativeQuery = true)
	void deleteAccountBank(@Param(value = "id")String id);

	@Query(value = "SELECT type FROM account_bank_receive WHERE bank_account = :bankAccount AND bank_type_id = :bankTypeId", nativeQuery =true)
	List<Integer> checkExistedBankAccount(@Param(value ="bankAccount") String bankAccount, @Param(value = "bankTypeId")String bankTypeId);

	@Query(value = "SELECT * FROM account_bank_receive WHERE id = :bankId", nativeQuery = true)
	AccountBankReceiveEntity getAccountBankById(@Param(value = "bankId") String bankId);
		
}
