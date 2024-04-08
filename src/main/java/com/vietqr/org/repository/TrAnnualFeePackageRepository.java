package com.vietqr.org.repository;

import com.vietqr.org.dto.TrAnnualFeeDTO;
import com.vietqr.org.entity.TrAnnualFeePackageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TrAnnualFeePackageRepository extends JpaRepository<TrAnnualFeePackageEntity, String> {
    @Query(value = "SELECT id AS feeId, "
            + "duration AS duration, amount AS amount "
            + "FROM tr_annual_fee_package "
            + "WHERE id = :id", nativeQuery = true)
    TrAnnualFeeDTO getFeeById(String id);
}
