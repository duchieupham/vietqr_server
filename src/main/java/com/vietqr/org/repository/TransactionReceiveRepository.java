package com.vietqr.org.repository;

import java.util.List;
import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.TransactionReceiveEntity;
import com.vietqr.org.dto.TransStatisticByDateDTO;
import com.vietqr.org.dto.TransStatisticByMonthDTO;
import com.vietqr.org.dto.TransStatisticDTO;
import com.vietqr.org.dto.TransactionCheckStatusDTO;
import com.vietqr.org.dto.TransactionDetailDTO;
import com.vietqr.org.dto.TransactionRelatedDTO;

@Repository
public interface TransactionReceiveRepository extends JpaRepository<TransactionReceiveEntity, Long> {

        @Query(value = "SELECT * FROM transaction_receive WHERE id = :id", nativeQuery = true)
        TransactionReceiveEntity getTransactionReceiveById(@Param(value = "id") String id);

        @Transactional
        @Modifying
        @Query(value = "UPDATE transaction_receive SET status = :status, ref_id = :refId, reference_number = :referenceNumber, time_paid = :timePaid WHERE id = :id", nativeQuery = true)
        void updateTransactionReceiveStatus(@Param(value = "status") int status,
                        @Param(value = "refId") String refId,
                        @Param(value = "referenceNumber") String referenceNumber,
                        @Param(value = "timePaid") long timePaid,
                        @Param(value = "id") String id);

        @Query(value = "SELECT * FROM transaction_receive WHERE order_id = :orderId AND status = 0 AND amount = :amount", nativeQuery = true)
        TransactionReceiveEntity getTransactionByOrderId(@Param(value = "orderId") String orderId,
                        @Param(value = "amount") String amount);

        // @Transactional
        // @Modifying
        // @Query(value = "UPDATE transaction_receive "
        // + "SET status = :status, ref_id = :refId, "
        // + "reference_number = :referenceNumber "
        // + "WHERE id = ", nativeQuery = true)
        // void updateTransactionReceiveFromMMS();

        @Query(value = "SELECT b.id as transactionId, b.amount, b.bank_account as bankAccount, b.content, b.time, b.time_paid as timePaid, b.status, b.type, b.trans_type as transType "
                        + "FROM transaction_receive_branch a "
                        + "INNER JOIN transaction_receive b "
                        + "ON a.transaction_receive_id = b.id "
                        + "WHERE a.business_id = :businessId AND b.status != 2 "
                        + "ORDER BY b.time DESC LIMIT 5", nativeQuery = true)
        List<TransactionRelatedDTO> getRelatedTransactionReceives(@Param(value = "businessId") String businessId);

        @Query(value = "SELECT a.id as transactionId,a.amount, a.bank_account as bankAccount,a.content,a.time, a.time_paid as timePaid,a.status,a.type,a.trans_type as transType "
                        + "FROM transaction_receive a "
                        + "WHERE a.bank_id=:bankId AND a.status != 2 "
                        + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
        List<TransactionRelatedDTO> getTransactions(@Param(value = "offset") int offset,
                        @Param(value = "bankId") String bankId);

        @Query(value = "SELECT a.id as transactionId,a.amount, a.bank_account as bankAccount,a.content,a.time, a.time_paid as timePaid,a.status,a.type,a.trans_type as transType "
                        + "FROM transaction_receive a "
                        + "WHERE a.bank_id=:bankId AND a.status = :status "
                        + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
        List<TransactionRelatedDTO> getTransactionsByStatus(@Param(value = "status") int status,
                        @Param(value = "offset") int offset,
                        @Param(value = "bankId") String bankId);

        @Query(value = "SELECT a.id, a.amount, a.bank_id as bankId, b.bank_account as bankAccount, a.content, a.ref_id as refId, a.status, a.time, a.time_paid as timePaid, a.type, a.trace_id as traceId, a.trans_type as transType, b.bank_account_name as bankAccountName, c.bank_code as bankCode, c.bank_name as bankName, c.img_id as imgId, a.reference_number as referenceNumber "
                        + "FROM transaction_receive a "
                        + "INNER JOIN account_bank_receive b "
                        + "ON a.bank_id = b.id "
                        + "INNER JOIN bank_type c "
                        + "ON b.bank_type_id = c.id "
                        + " WHERE a.id = :id", nativeQuery = true)
        TransactionDetailDTO getTransactionById(@Param(value = "id") String id);

        @Query(value = "SELECT tr.* "
                        + "FROM transaction_receive tr "
                        + "JOIN account_bank_receive abr ON tr.bank_id = abr.id "
                        + "JOIN bank_type bt ON abr.bank_type_id = bt.id "
                        + "WHERE tr.trace_id = :id "
                        + "AND tr.amount = :amount "
                        + "AND tr.status = 0 "
                        + "AND tr.trans_type = :transType "
                        + "AND bt.status = 1", nativeQuery = true)
        TransactionReceiveEntity getTransactionByTraceId(@Param(value = "id") String id,
                        @Param(value = "amount") Long amount, @Param(value = "transType") String transType);

        @Query(value = "SELECT * "
                        + "FROM transaction_receive "
                        + "WHERE bank_id = :bankId AND status != 2 "
                        + "ORDER BY time DESC LIMIT 5", nativeQuery = true)
        List<TransactionReceiveEntity> getRelatedTransactionByBankId(@Param(value = "bankId") String bankId);

        @Query(value = "SELECT * FROM transaction_receive WHERE reference_number = :referenceNumber AND trans_type = 'C'", nativeQuery = true)
        TransactionReceiveEntity findTransactionReceiveByFtCode(
                        @Param(value = "referenceNumber") String referenceNumber);

        @Query(value = "SELECT * FROM transaction_receive WHERE reference_number = :referenceNumber AND order_id = :orderId", nativeQuery = true)
        TransactionReceiveEntity getTransactionReceiveByRefNumberAndOrderId(
                        @Param(value = "referenceNumber") String referenceNumber,
                        @Param(value = "orderId") String orderId);

        @Query(value = "SELECT * FROM transaction_receive WHERE reference_number = :referenceNumber", nativeQuery = true)
        TransactionReceiveEntity getTransactionReceiveByRefNumber(
                        @Param(value = "referenceNumber") String referenceNumber);

        @Query(value = "SELECT * FROM transaction_receive WHERE order_id = :orderId", nativeQuery = true)
        TransactionReceiveEntity getTransactionReceiveByOrderId(
                        @Param(value = "orderId") String orderId);

        @Query(value = "SELECT a.amount, a.bank_account as bankAccount, c.bank_name as bankName, a.time, a.content, a.reference_number as referenceNumber, a.trans_type as transType, a.status "
                        + "FROM transaction_receive a "
                        + "INNER JOIN account_bank_receive b "
                        + "ON a.bank_id = b.id "
                        + "INNER JOIN bank_type c "
                        + "ON b.bank_type_id = c.id "
                        + "WHERE a.id = :transactionId ", nativeQuery = true)
        TransactionCheckStatusDTO getTransactionCheckStatus(@Param(value = "transactionId") String transactionId);

        @Query(value = "SELECT COUNT(*) AS totalTrans, "
                        + "SUM(CASE WHEN trans_type = 'C' THEN amount ELSE 0 END) AS totalCashIn, "
                        + "SUM(CASE WHEN trans_type = 'D' THEN amount ELSE 0 END) AS totalCashOut, "
                        + "SUM(CASE WHEN trans_type = 'C' THEN 1 ELSE 0 END) AS totalTransC, "
                        + "SUM(CASE WHEN trans_type = 'D' THEN 1 ELSE 0 END) AS totalTransD "
                        + "FROM transaction_receive "
                        + "WHERE bank_id = :bankId AND status = 1", nativeQuery = true)
        TransStatisticDTO getTransactionOverview(@Param(value = "bankId") String bankId);

        @Query(value = "SELECT DATE_FORMAT(CONVERT_TZ(FROM_UNIXTIME(time), '+00:00', '+07:00'), '%Y-%m-%d') AS date, "
                        + "COUNT(*) AS totalTrans, "
                        + "SUM(CASE WHEN trans_type = 'C' THEN amount ELSE 0 END) AS totalCashIn, "
                        + "SUM(CASE WHEN trans_type = 'D' THEN amount ELSE 0 END) AS totalCashOut, "
                        + "SUM(CASE WHEN trans_type = 'C' THEN 1 ELSE 0 END) AS totalTransC, "
                        + "SUM(CASE WHEN trans_type = 'D' THEN 1 ELSE 0 END) AS totalTransD "
                        + "FROM transaction_receive "
                        + "WHERE bank_id = :bankId AND status = 1 "
                        + "GROUP BY date ORDER BY date DESC ", nativeQuery = true)
        List<TransStatisticByDateDTO> getTransStatisticByDate(@Param(value = "bankId") String bankId);

        @Query(value = "SELECT DATE_FORMAT(CONVERT_TZ(FROM_UNIXTIME(time), '+00:00', '+07:00'), '%Y-%m') AS month, "
                        + "COUNT(*) AS totalTrans, "
                        + "SUM(CASE WHEN trans_type = 'C' THEN amount ELSE 0 END) AS totalCashIn, "
                        + "SUM(CASE WHEN trans_type = 'D' THEN amount ELSE 0 END) AS totalCashOut, "
                        + "SUM(CASE WHEN trans_type = 'C' THEN 1 ELSE 0 END) AS totalTransC, "
                        + "SUM(CASE WHEN trans_type = 'D' THEN 1 ELSE 0 END) AS totalTransD "
                        + "FROM transaction_receive "
                        + "WHERE bank_id = :bankId AND status = 1 "
                        + "GROUP BY month ORDER BY month DESC ", nativeQuery = true)
        List<TransStatisticByMonthDTO> getTransStatisticByMonth(@Param(value = "bankId") String bankId);

}
