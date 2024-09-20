package com.vietqr.org.repository;

import java.util.List;

import com.vietqr.org.dto.BankTypeShortNameDTO;
import com.vietqr.org.dto.IBankTypeQR;
import com.vietqr.org.dto.ICaiBankTypeQR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.BankTypeEntity;

@Repository
public interface BankTypeRepository extends JpaRepository<BankTypeEntity, Long> {

	@Query(value = "SELECT * FROM bank_type", nativeQuery = true)
	List<BankTypeEntity> getBankTypes();

	@Query(value = "SELECT * FROM bank_type WHERE id = :id", nativeQuery = true)
	BankTypeEntity getBankTypeById(@Param(value = "id") String id);

	@Query(value = "SELECT id FROM bank_type WHERE bank_code = :bankCode", nativeQuery = true)
	String getBankTypeIdByBankCode(@Param(value = "bankCode") String bankCode);

	@Query(value = "SELECT bank_short_name AS bankShortName FROM bank_type WHERE bank_code = :bankCode", nativeQuery = true)
	String getBankShortNameByBankCode(String bankCode);

	@Query(value = "SELECT rpa_contain_id FROM bank_type WHERE bank_code = :bankCode", nativeQuery = true)
	Boolean getRpaContainIdByBankCode(@Param(value = "bankCode") String bankCode);

	@Query(value = "SELECT id AS bankTypeId, bank_short_name AS bankShortName "
			+ "FROM bank_type WHERE id IN (:ids)", nativeQuery = true)
    List<BankTypeShortNameDTO> getBankTypeByListId(List<String> ids);

	@Query(value = "SELECT * FROM bank_type WHERE bank_code = :bankCode LIMIT 1", nativeQuery = true)
    BankTypeEntity getBankTypeByBankCode(String bankCode);

	BankTypeEntity findByBankShortName(String bankShortName);

	@Query(value = "SELECT a.bank_code AS bankCode, a.bank_name AS bankName, a.img_id AS imgId, b.cai_value AS caiValue FROM bank_type a "
			+ "INNER JOIN (SELECT cai_value FROM cai_bank WHERE bank_type_id = :id) b "
			+ "WHERE id = :id", nativeQuery = true)
	ICaiBankTypeQR getCaiBankTypeById(@Param(value = "id") String id);

	@Query(value = "SELECT bank_code AS bankCode, bank_name AS bankName, img_id AS imgId FROM bank_type "
			+ "WHERE id = :id", nativeQuery = true)
	IBankTypeQR getBankTypeQRById(@Param(value = "id") String id);

	@Query(value = "SELECT bank_code AS bankCode, bank_name AS bankName, img_id AS imgId FROM bank_type "
			+ "WHERE bank_code = :code", nativeQuery = true)
	IBankTypeQR getBankTypeQRByCode(@Param(value = "code") String code);
}
