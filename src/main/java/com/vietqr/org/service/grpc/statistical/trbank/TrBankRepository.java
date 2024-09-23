package com.vietqr.org.service.grpc.statistical.trbank;

import com.vietqr.org.entity.TransactionReceiveEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TrBankRepository extends JpaRepository<TransactionReceiveEntity, String> {

    @Query(value = "SELECT " +
            "    bt.bank_short_name AS bankShortName, " +
            "    COALESCE(SUM(CASE WHEN tr.trans_type = 'C' AND tr.status = '1' THEN tr.amount ELSE 0 END), 0) AS totalAmountCredits, " +
            "    COALESCE(SUM(CASE WHEN tr.trans_type = 'C' AND (tr.type = 0 OR tr.type = 1) AND tr.status = '1' THEN tr.amount ELSE 0 END), 0) AS totalAmountRecon, " +
            "    COALESCE(COUNT(CASE WHEN tr.trans_type = 'C' AND tr.status = '1' THEN 1 ELSE NULL END), 0) AS totalNumberCredits, " +
            "    COALESCE(COUNT(CASE WHEN tr.trans_type = 'C' AND (tr.type = 0 OR tr.type = 1) AND tr.status = '1' THEN 1 ELSE NULL END), 0) AS totalNumberRecon " +
            "FROM transaction_receive tr " +
            "JOIN account_bank_receive abr ON tr.bank_account = abr.bank_account " +
            "JOIN bank_type bt ON abr.bank_type_id = bt.id " +
            "WHERE bt.bank_short_name IN ('MBBank', 'BIDV') " +
            "    AND tr.time BETWEEN :startDate AND :endDate " +
            "GROUP BY bt.bank_short_name;",
            nativeQuery = true)
    ITrBankDTO getTrBankData(@Param("startDate") long startDate, @Param("endDate") long endDate);
}

