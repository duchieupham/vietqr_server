package com.vietqr.org.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.MobileCarrierEntity;

@Repository
public interface MobileCarrierRepository extends JpaRepository<MobileCarrierEntity, Long> {

    @Query(value = "SELECT type_id FROM mobile_carrier WHERE prefix = :prefix", nativeQuery = true)
    String getTypeIdByPrefix(@Param(value = "prefix") String prefix);
}
