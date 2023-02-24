package com.vietqr.org.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.CaiBankEntity;

@Repository
public interface CaiBankRepository extends JpaRepository<CaiBankEntity, Long>{

	@Query(value = "SELECT cai_value FROM cai_bank WHERE bank_type_id = :bankTypeId", nativeQuery = true)
	String getCaiValue(@Param(value = "bankTypeId") String bankTypeId);

}
