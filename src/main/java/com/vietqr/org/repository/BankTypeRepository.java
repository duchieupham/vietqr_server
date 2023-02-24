package com.vietqr.org.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.BankTypeEntity;

@Repository
public interface BankTypeRepository extends JpaRepository<BankTypeEntity, Long>{

	@Query(value ="SELECT * FROM bank_type", nativeQuery = true)
	List<BankTypeEntity> getBankTypes();

	@Query(value = "SELECT * FROM bank_type WHERE id = :id", nativeQuery = true)
	BankTypeEntity getBankTypeById(@Param(value = "id") String id);

	@Query(value = "SELECT id FROM bank_type WHERE bank_code = :bankCode", nativeQuery = true)
	String getBankTypeIdByBankCode(@Param(value = "bankCode") String bankCode);

}
