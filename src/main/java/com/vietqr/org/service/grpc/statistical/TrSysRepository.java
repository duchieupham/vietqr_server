package com.vietqr.org.service.grpc.statistical;

import com.vietqr.org.entity.TransactionReceiveEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TrSysRepository extends JpaRepository<TransactionReceiveEntity, String> {
    @Query(value = "SELECT" +
            "            COALESCE(COUNT(CASE WHEN tr.trans_type = 'C' AND tr.status = '1' THEN 1 ELSE NULL END), 0) AS totalNumberCredits," +
            "            COALESCE(SUM(CASE WHEN tr.trans_type = 'C' AND tr.status = '1' THEN tr.amount ELSE 0 END), 0) AS totalAmountCredits," +
            "            COALESCE(COUNT(CASE WHEN tr.trans_type = 'C' AND (tr.type = 0 OR tr.type = 1) AND tr.status = 1 THEN 1 ELSE NULL END), 0) AS totalNumberRecon," +
            "            COALESCE(SUM(CASE WHEN tr.trans_type = 'C' AND (tr.type = 0 OR tr.type = 1) AND tr.status = 1 THEN tr.amount ELSE 0 END), 0) AS totalAmountRecon," +
            "            COALESCE(COUNT(CASE WHEN tr.trans_type = 'C' AND tr.status = '1' THEN 1 ELSE NULL END)" +
            "                - COUNT(CASE WHEN tr.trans_type = 'C' AND (tr.type = 0 OR tr.type = 1) AND tr.status = 1 THEN 1 ELSE NULL END), 0) AS totalNumberWithoutRecon," +
            "            COALESCE(SUM(CASE WHEN tr.trans_type = 'C' AND tr.status = '1' THEN tr.amount ELSE 0 END)" +
            "                - SUM(CASE WHEN tr.trans_type = 'C' AND (tr.type = 0 OR tr.type = 1) AND tr.status = 1 THEN tr.amount ELSE 0 END), 0) AS totalAmountWithoutRecon," +
            "            COALESCE(COUNT(CASE WHEN trl.type = 1 AND trl.status_code = 400 THEN 1 ELSE NULL END), 0) AS totalNumberPushError," +
            "            COALESCE(SUM(CASE WHEN trl.type = 1 AND trl.status_code = 400 THEN tr.amount ELSE 0 END), 0) AS totalAmountPushErrorSum " +
            "            FROM transaction_receive tr" +
            "            LEFT JOIN transaction_receive_log trl ON tr.id = trl.transaction_id" +
            "            WHERE tr.time BETWEEN :startDate AND :endDate" +
            "            GROUP BY NULL", nativeQuery = true)
    ITrSysDTO getTrSysData(@Param("startDate") long startDate, @Param("endDate") long endDate);
}
