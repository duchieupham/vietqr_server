package com.vietqr.org.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.BankTextFormEntity;

@Repository
public interface BankTextFormRepository extends JpaRepository<BankTextFormEntity, Long>{

	@Query(value = "SELECT * FROM bank_text_form WHERE bank_id = :bankId", nativeQuery = true)
	List<BankTextFormEntity> getBankTextFormsByBankId(@Param(value = "bankId")String bankId);

	@Transactional
	@Modifying
	@Query(value = "DELETE FROM bank_text_form WHERE id = :id", nativeQuery = true)
	void deleteBankTextForm(@Param(value = "id")String id);

}
