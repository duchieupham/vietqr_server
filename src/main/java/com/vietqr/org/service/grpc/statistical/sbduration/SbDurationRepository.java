package com.vietqr.org.service.grpc.statistical.sbduration;

import com.vietqr.org.entity.AccountBankReceiveEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SbDurationRepository extends JpaRepository<AccountBankReceiveEntity, String> {
    @Query(value = "SELECT" +
            " COALESCE(SUM(CASE WHEN valid_fee_to != 0 AND valid_fee_to < :expired THEN 1 ELSE 0 END), 0) AS overdueCount," +
            " COALESCE(SUM(CASE WHEN valid_fee_to != 0 AND valid_fee_to >= :expired AND valid_fee_to <= :nearing THEN 1 ELSE 0 END), 0) AS nearlyExpireCount" +
            " FROM account_bank_receive", nativeQuery = true)
    ISbDurationDTO getSbDurationData(@Param("expired") long expired, @Param("nearing") long nearing);
}

