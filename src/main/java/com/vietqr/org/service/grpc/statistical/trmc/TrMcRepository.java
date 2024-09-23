package com.vietqr.org.service.grpc.statistical.trmc;

import com.vietqr.org.entity.TransactionReceiveEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrMcRepository extends JpaRepository<TransactionReceiveEntity, String> {
    @Query(value = "SELECT " +
            "    cs.merchant AS merchantName, " +
            "    COALESCE(COUNT(CASE WHEN tr.trans_type = 'C' AND tr.status = '1' THEN 1 ELSE NULL END), 0) AS totalNumberCredits, " +
            "    COALESCE(SUM(CASE WHEN tr.trans_type = 'C' AND tr.status = '1' THEN tr.amount ELSE 0 END), 0) AS totalAmountCredits, " +
            "    COALESCE(COUNT(CASE WHEN tr.trans_type = 'C' AND (tr.type = 0 OR tr.type = 1) AND tr.status = '1' THEN 1 ELSE NULL END), 0) AS totalReconTransactions, " +
            "    COALESCE(SUM(CASE WHEN tr.trans_type = 'C' AND (tr.type = 0 OR tr.type = 1) AND tr.status = '1' THEN tr.amount ELSE 0 END), 0) AS totalAmountRecon " +
            "FROM transaction_receive tr " +
            "JOIN account_bank_receive abr ON tr.bank_account = abr.bank_account " +
            "JOIN account_customer_bank acb ON abr.bank_account = acb.bank_account " +
            "JOIN customer_sync cs ON acb.customer_sync_id = cs.id " +
            "WHERE tr.time BETWEEN :startDate AND :endDate " +
            "GROUP BY cs.merchant", nativeQuery = true)
    List<ITrMcDTO> getTrMcData(@Param("startDate") long startDate, @Param("endDate") long endDate);


}
