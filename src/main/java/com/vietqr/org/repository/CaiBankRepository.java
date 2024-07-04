package com.vietqr.org.repository;

import com.vietqr.org.dto.CaiValueDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.CaiBankEntity;

import java.util.List;

@Repository
public interface CaiBankRepository extends JpaRepository<CaiBankEntity, Long> {

	@Query(value = "SELECT cai_value FROM cai_bank WHERE bank_type_id = :bankTypeId", nativeQuery = true)
	String getCaiValue(@Param(value = "bankTypeId") String bankTypeId);

	@Query(value = "SELECT * FROM cai_bank WHERE cai_value = :caiValue", nativeQuery = true)
	CaiBankEntity getCaiBankByCaiValue(@Param(value = "caiValue") String caiValue);

	@Query(value = "SELECT a.bank_type_id AS bankTypeId, "
			+ "b.bank_short_name AS bankShortName, b.bank_code AS bankCode, "
			+ "b.bank_name AS bankName, b.img_id AS imgId, "
			+ "b.unlinked_type AS unlinkedType, "
			+ "b.status AS bankTypeStatus, a.cai_value AS caiValue "
			+ "FROM cai_bank a "
			+ "INNER JOIN bank_type b ON a.bank_type_id = b.id "
			+ "WHERE bank_type_id IN (:bankTypeIds)", nativeQuery = true)
	List<CaiValueDTO> getCaiValues(List<String> bankTypeIds);
}
