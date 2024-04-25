package com.vietqr.org.repository;

import com.vietqr.org.dto.ITrAnnualFeeDTO;
import com.vietqr.org.entity.TrAnnualFeePackageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrAnnualFeePackageRepository extends JpaRepository<TrAnnualFeePackageEntity, String> {
    @Query(value = "SELECT id AS feeId, "
            + "duration AS duration, amount AS amount, total_amount AS totalAmount, "
            + "description AS description "
            + "FROM tr_annual_fee_package "
            + "WHERE id = :id", nativeQuery = true)
    ITrAnnualFeeDTO getFeeById(String id);

    @Query(value = "SELECT id AS feeId, "
            + "duration AS duration, amount AS amount, description AS description, "
            + "total_amount AS totalAmount "
            + "FROM tr_annual_fee_package "
            + "ORDER BY duration ASC ", nativeQuery = true)
    List<ITrAnnualFeeDTO> getAllFee();

    @Query(value = "SELECT total_amount "
            + "FROM tr_annual_fee_package "
            + "WHERE id = :feeId", nativeQuery = true)
    Long getTotalAmount(String feeId);
}
