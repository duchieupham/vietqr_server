package com.vietqr.org.repository;

import java.util.List;
import javax.transaction.Transactional;

import com.vietqr.org.dto.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.TransactionReceiveEntity;

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

        @Transactional
        @Modifying
        @Query(value = "UPDATE transaction_receive SET status = :status WHERE id = :id ", nativeQuery = true)
        void updateTransactionStatusById(
                        @Param(value = "status") int status,
                        @Param(value = "id") String id);

        @Transactional
        @Modifying
        @Query(value = "UPDATE transaction_receive SET note = :note WHERE id = :id ", nativeQuery = true)
        void updateTransactionReceiveNote(@Param(value = "note") String note, @Param(value = "id") String id);

        @Query(value = "SELECT * FROM transaction_receive " +
                "WHERE time >= :time AND order_id = :orderId AND status = 0 AND amount = :amount", nativeQuery = true)
        TransactionReceiveEntity getTransactionByOrderId(@Param(value = "orderId") String orderId,
                                                         @Param(value = "amount") String amount,
                                                         @Param(value = "time") long time);

        // @Transactional
        // @Modifying
        // @Query(value = "UPDATE transaction_receive "
        // + "SET status = :status, ref_id = :refId, "
        // + "reference_number = :referenceNumber "
        // + "WHERE id = ", nativeQuery = true)
        // void updateTransactionReceiveFromMMS();

//        @Query(value = "SELECT b.id as transactionId, b.amount, b.bank_account as bankAccount, b.content, b.time, b.time_paid as timePaid, b.status, b.type, b.trans_type as transType "
//                        + "FROM transaction_receive_branch a "
//                        + "INNER JOIN transaction_receive b "
//                        + "ON a.transaction_receive_id = b.id "
//                        + "WHERE a.business_id = :businessId AND b.status != 2 "
//                        + "ORDER BY b.time DESC LIMIT 5", nativeQuery = true)
//        List<TransactionRelatedDTO> getRelatedTransactionReceives(@Param(value = "businessId") String businessId);

        @Query(value = "SELECT a.id as transactionId,a.amount, a.bank_account as bankAccount,a.content,a.time, a.time_paid as timePaid,a.status,a.type,a.trans_type as transType "
                        + "FROM transaction_receive a "
                        + "WHERE a.bank_id=:bankId AND a.status != 2 "
                        + "AND a.time >= :time "
                        + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
        List<TransactionRelatedDTO> getTransactions(@Param(value = "offset") int offset,
                                                    @Param(value = "bankId") String bankId,
                                                    @Param(value = "time") long time);

        @Query(value = "SELECT a.id as transactionId,a.amount, a.bank_account as bankAccount,a.content,a.time, a.time_paid as timePaid,a.status,a.type,a.trans_type as transType "
                + "FROM transaction_receive a "
                + "WHERE a.bank_id=:bankId AND a.status != 2 "
                + "AND a.time BETWEEN :fromDate AND :toDate "
                + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
        List<TransactionRelatedDTO> getTransactions(@Param(value = "offset") int offset,
                                                    @Param(value = "bankId") String bankId,
                                                    @Param(value = "fromDate") long fromDate,
                                                    @Param(value = "toDate") long toDate);

        @Query(value = "SELECT a.id as transactionId,a.amount, " +
                "a.bank_account as bankAccount,a.content,a.time, a.time_paid as timePaid," +
                "a.status,a.type,a.trans_type as transType "
                        + "FROM transaction_receive a "
                        + "WHERE a.bank_id= :bankId AND a.status = :status "
                        + "AND a.time BETWEEN :fromDate AND :toDate "
                        + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
        List<TransactionRelatedDTO> getTransactionsByStatus(@Param(value = "status") int status,
                                                            @Param(value = "offset") int offset,
                                                            @Param(value = "bankId") String bankId,
                                                            @Param(value = "fromDate") long fromDate,
                                                            @Param(value = "toDate") long toDate);

        @Query(value = "SELECT a.id as transactionId,a.amount, a.bank_account as bankAccount,a.content,a.time, a.time_paid as timePaid,a.status,a.type,a.trans_type as transType "
                + "FROM transaction_receive a "
                + "WHERE a.bank_id= :bankId AND a.status = :status "
                + "AND a.time >= :time "
                + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
        List<TransactionRelatedDTO> getTransactionsByStatus(@Param(value = "status") int status,
                                                            @Param(value = "offset") int offset,
                                                            @Param(value = "bankId") String bankId,
                                                            @Param(value = "time") long time);

        @Query(value = "SELECT a.id as transactionId,a.amount, a.bank_account as bankAccount,a.content,a.time, a.time_paid as timePaid,a.status,a.type,a.trans_type as transType "
                        + "FROM transaction_receive a "
                        + "WHERE a.bank_id= :bankId AND a.reference_number = :value "
                        + "AND a.time BETWEEN :fromDate AND :toDate "
                        + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
        List<TransactionRelatedDTO> getTransactionsByFtCode(@Param(value = "value") String value,
                                                            @Param(value = "offset") int offset,
                                                            @Param(value = "bankId") String bankId,
                                                            @Param(value = "fromDate") long fromDate,
                                                            @Param(value = "toDate") long toDate);

        @Query(value = "SELECT a.id as transactionId,a.amount, a.bank_account as bankAccount,a.content,a.time, a.time_paid as timePaid,a.status,a.type,a.trans_type as transType "
                + "FROM transaction_receive a "
                + "WHERE a.bank_id= :bankId AND a.reference_number = :value "
                + "AND a.time >= :time "
                + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
        List<TransactionRelatedDTO> getTransactionsByFtCode(@Param(value = "value") String value,
                                                            @Param(value = "offset") int offset,
                                                            @Param(value = "bankId") String bankId,
                                                            @Param(value = "time") long time);

        @Query(value = "SELECT a.id as transactionId,a.amount, a.bank_account as bankAccount,a.content,a.time, a.time_paid as timePaid,a.status,a.type,a.trans_type as transType "
                        + "FROM transaction_receive a "
                        + "WHERE a.bank_id= :bankId AND a.order_id = :value "
                        + "AND a.time BETWEEN :fromDate AND :toDate "
                        + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
        List<TransactionRelatedDTO> getTransactionsByOrderId(
                        @Param(value = "value") String value,
                        @Param(value = "offset") int offset,
                        @Param(value = "bankId") String bankId,
                        @Param(value = "fromDate") long fromDate,
                        @Param(value = "toDate") long toDate);

        @Query(value = "SELECT a.id as transactionId,a.amount, a.bank_account as bankAccount,a.content,a.time, a.time_paid as timePaid,a.status,a.type,a.trans_type as transType "
                + "FROM transaction_receive a "
                + "WHERE a.bank_id= :bankId AND a.order_id = :value "
                + "AND a.time >= :time "
                + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
        List<TransactionRelatedDTO> getTransactionsByOrderId(
                @Param(value = "value") String value,
                @Param(value = "offset") int offset,
                @Param(value = "bankId") String bankId,
                @Param(value = "time") long time);

        @Query(value = "SELECT a.id as transactionId,a.amount, a.bank_account as bankAccount,a.content,a.time, a.time_paid as timePaid,a.status,a.type,a.trans_type as transType "
                        + "FROM transaction_receive a "
                        + "WHERE a.bank_id= :bankId AND a.content LIKE %:value% "
                        + "AND a.time BETWEEN :fromDate AND :toDate "
                        + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
        List<TransactionRelatedDTO> getTransactionsByContent(
                        @Param(value = "value") String value,
                        @Param(value = "offset") int offset,
                        @Param(value = "bankId") String bankId,
                        @Param(value = "fromDate") long fromDate,
                        @Param(value = "toDate") long toDate);

        @Query(value = "SELECT a.id as transactionId,a.amount, a.bank_account as bankAccount,a.content,a.time, a.time_paid as timePaid,a.status,a.type,a.trans_type as transType "
                + "FROM transaction_receive a "
                + "WHERE a.bank_id= :bankId AND a.content LIKE %:value% "
                + "AND a.time >= :time "
                + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
        List<TransactionRelatedDTO> getTransactionsByContent(
                @Param(value = "value") String value,
                @Param(value = "offset") int offset,
                @Param(value = "bankId") String bankId,
                @Param(value = "time") long time);

        @Query(value = "SELECT a.id as transactionId,a.amount, a.bank_account as bankAccount,a.content,a.time, a.time_paid as timePaid,a.status,a.type,a.trans_type as transType "
                        + "FROM transaction_receive a "
                        + "WHERE a.bank_id= :bankId AND a.terminal_code LIKE %:value% "
                        + "AND a.time BETWEEN :fromDate AND :toDate "
                        + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
        List<TransactionRelatedDTO> getTransactionsByTerminalCode(
                        @Param(value = "value") String value,
                        @Param(value = "offset") int offset,
                        @Param(value = "fromDate") long fromDate,
                        @Param(value = "toDate") long toDate,
                        @Param(value = "bankId") String bankId);

        @Query(value = "SELECT a.id as transactionId,a.amount, a.bank_account as bankAccount,a.content,a.time, a.time_paid as timePaid,a.status,a.type,a.trans_type as transType "
                + "FROM transaction_receive a "
                + "WHERE a.bank_id= :bankId AND a.terminal_code LIKE %:value% "
                + "AND a.time >= :time "
                + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
        List<TransactionRelatedDTO> getTransactionsByTerminalCode(
                @Param(value = "value") String value,
                @Param(value = "offset") int offset,
                @Param(value = "bankId") String bankId,
                @Param(value = "time") long time);

        @Query(value = "SELECT a.id, a.amount, a.bank_id as bankId, b.bank_account as bankAccount, a.content, a.ref_id as refId, a.status, a.time, a.time_paid as timePaid, a.type, a.trace_id as traceId, a.trans_type as transType, b.bank_account_name as bankAccountName, c.bank_code as bankCode, c.bank_name as bankName, c.img_id as imgId, a.reference_number as referenceNumber, "
                        + "a.terminal_code as terminalCode, a.note, a.order_id as orderId, c.bank_short_name as bankShortName "
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
                        + "AND tr.time >= :time "
                        + "AND tr.amount = :amount "
                        + "AND tr.status = 0 "
                        + "AND tr.trans_type = :transType "
                        + "AND bt.status = 1", nativeQuery = true)
        TransactionReceiveEntity getTransactionByTraceId(@Param(value = "id") String id,
                        @Param(value = "amount") Long amount, @Param(value = "transType") String transType,
                                                         @Param(value = "time") long time);

//        @Query(value = "SELECT * "
//                        + "FROM transaction_receive "
//                        + "WHERE bank_id = :bankId AND status != 2 "
//                        + "ORDER BY time DESC LIMIT 5", nativeQuery = true)
//        List<TransactionReceiveEntity> getRelatedTransactionByBankId(@Param(value = "bankId") String bankId);

        @Query(value = "SELECT * FROM transaction_receive WHERE time >= :time AND reference_number = :referenceNumber AND trans_type = 'C'", nativeQuery = true)
        TransactionReceiveEntity findTransactionReceiveByFtCode(
                        @Param(value = "referenceNumber") String referenceNumber,
                        @Param(value = "time") long time);

        @Query(value = "SELECT * FROM transaction_receive WHERE time >= :time AND reference_number = :referenceNumber AND order_id = :orderId", nativeQuery = true)
        TransactionReceiveEntity getTransactionReceiveByRefNumberAndOrderId(
                        @Param(value = "referenceNumber") String referenceNumber,
                        @Param(value = "orderId") String orderId,
                        @Param(value = "time") long time);

        @Query(value = "SELECT a.* FROM transaction_receive a WHERE a.time >= :time AND a.reference_number = :referenceNumber", nativeQuery = true)
        TransactionReceiveEntity getTransactionReceiveByRefNumber(
                @Param(value = "referenceNumber") String referenceNumber,
                @Param(value = "time") long time);

//        TransactionReceiveEntity getTransactionReceiveByRefNumber(
//                        @Param(value = "referenceNumber") String referenceNumber,
//                        @Param(value = "partitions") List<String> partitions);

        @Query(value = "SELECT * FROM transaction_receive WHERE order_id = :orderId AND time >= :time ", nativeQuery = true)
        TransactionReceiveEntity getTransactionReceiveByOrderId(
                        @Param(value = "orderId") String orderId,
                        @Param(value = "time") long time);

        @Query(value = "SELECT a.amount, a.bank_account as bankAccount, c.bank_name as bankName, a.time, a.content, a.reference_number as referenceNumber, a.trans_type as transType, a.status, "
                        + "a.terminal_code as terminalCode, a.note "
                        + "FROM transaction_receive a "
                        + "INNER JOIN account_bank_receive b "
                        + "ON a.bank_id = b.id "
                        + "INNER JOIN bank_type c "
                        + "ON b.bank_type_id = c.id "
                        + "WHERE a.id = :transactionId ", nativeQuery = true)
        TransactionCheckStatusDTO getTransactionCheckStatus(@Param(value = "transactionId") String transactionId);

        // check statistic
        @Query(value = "SELECT COUNT(id) AS totalTrans, "
                        + "SUM(CASE WHEN trans_type = 'C' THEN amount ELSE 0 END) AS totalCashIn, "
                        + "SUM(CASE WHEN trans_type = 'D' THEN amount ELSE 0 END) AS totalCashOut, "
                        + "SUM(CASE WHEN trans_type = 'C' THEN 1 ELSE 0 END) AS totalTransC, "
                        + "SUM(CASE WHEN trans_type = 'D' THEN 1 ELSE 0 END) AS totalTransD "
                        + "FROM transaction_receive "
                        + "WHERE bank_id = :bankId AND status = 1 "
                        + "AND time BETWEEN :fromDate AND :toDate", nativeQuery = true)
        TransStatisticDTO getTransactionOverview(@Param(value = "bankId") String bankId,
                                                 @Param(value = "fromDate") long fromDate,
                                                 @Param(value = "toDate") long toDate);

        @Query(value = "SELECT DATE_FORMAT(CONVERT_TZ(FROM_UNIXTIME(a.time), '+00:00', '+07:00'), '%Y-%m-%d') AS date, "
                        + "COUNT(a.id) AS totalTrans, "
                        + "SUM(CASE WHEN a.trans_type = 'C' THEN amount ELSE 0 END) AS totalCashIn, "
                        + "SUM(CASE WHEN a.trans_type = 'D' THEN amount ELSE 0 END) AS totalCashOut, "
                        + "SUM(CASE WHEN a.trans_type = 'C' THEN 1 ELSE 0 END) AS totalTransC, "
                        + "SUM(CASE WHEN a.trans_type = 'D' THEN 1 ELSE 0 END) AS totalTransD "
                        + "FROM transaction_receive a "
                        + "INNER JOIN terminal b ON b.code = a.terminal_code "
                        + "INNER JOIN account_bank_receive_share c ON c.terminal_id = b.id "
                        + "WHERE c.bank_id = :bankId AND a.status = 1 "
                        + "AND b.code = :terminalCode "
                        + "AND c.user_id = :userId "
                        + "AND a.time BETWEEN :fromDate AND :toDate "
                        + "GROUP BY date ORDER BY date DESC ", nativeQuery = true)
        List<TransStatisticByDateDTO> getTransStatisticByTerminalId(
                @Param(value = "bankId") String bankId,
                @Param(value = "terminalCode") String terminalCode,
                @Param(value = "userId") String userId,
                @Param(value = "fromDate") long fromDate,
                @Param(value = "toDate") long toDate);

        // check statistic
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

        // check note
        @Query(value = "SELECT a.id, a.amount, a.bank_account as bankAccount, a.bank_id as bankId, a.content, a.order_id as orderId, "
                        + "a.ref_id as referenceId, a.reference_number as referenceNumber, a.sign, a.status, a.time, a.time_paid as timePaid, a.trace_id as traceId, a.trans_type as transType,  "
                        + "a.type, a.terminal_code as terminalCode, a.note "
                        + "FROM transaction_receive a  "
                        + "INNER JOIN account_customer_bank b  "
                        + "ON a.bank_id = b.bank_id  "
                        + "WHERE a.bank_id = :bankId AND b.customer_sync_id = :customerSyncId "
                        + "AND a.time >= :time "
                        + "ORDER BY a.time DESC "
                        + "LIMIT :offset, 20 ", nativeQuery = true)
        List<TransByCusSyncDTO> getTransactionsByCustomerSync(@Param(value = "bankId") String bankId,
                        @Param(value = "customerSyncId") String customerSyncId,
                        @Param(value = "offset") int offset,
                        @Param(value = "time") long time);

        @Query(value = "SELECT a.id, a.amount, a.bank_account as bankAccount, a.bank_id as bankId, a.content, a.order_id as orderId, "
                + "a.ref_id as referenceId, a.reference_number as referenceNumber, a.sign, a.status, a.time, a.time_paid as timePaid, a.trace_id as traceId, a.trans_type as transType,  "
                + "a.type, a.terminal_code as terminalCode, a.note "
                + "FROM transaction_receive a  "
                + "INNER JOIN account_customer_bank b  "
                + "ON a.bank_id = b.bank_id  "
                + "WHERE a.bank_id = :bankId AND b.customer_sync_id = :customerSyncId "
                + "AND a.time BETWEEN :fromDate AND :toDate "
                + "ORDER BY a.time DESC "
                + "LIMIT :offset, 20 ", nativeQuery = true)
        List<TransByCusSyncDTO> getTransactionsByCustomerSync(@Param(value = "bankId") String bankId,
                                                              @Param(value = "customerSyncId") String customerSyncId,
                                                              @Param(value = "offset") int offset,
                                                              @Param(value = "fromDate") long fromDate,
                                                                @Param(value = "toDate") long toDate);

        //
        //
        //
        /// ADMIN
        // check
        @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, a.content, a.order_id as orderId, a.reference_number as referenceNumber, "
                        + "a.status, a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
                        + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
                        + "a.terminal_code as terminalCode, a.note "
                        + "FROM transaction_receive a "
                        + "LEFT JOIN account_bank_receive b "
                        + "ON a.bank_id = b.id "
                        + "LEFT JOIN bank_type c "
                        + "ON b.bank_type_id = c.id "
                        + "WHERE a.bank_account = :value "
                        + "AND a.time >= :time "
                        + "ORDER BY a.time DESC "
                        + "LIMIT :offset, 20 ", nativeQuery = true)
        List<TransactionReceiveAdminListDTO> getTransByBankAccountAllDate(
                        @Param(value = "value") String value,
                        @Param(value = "offset") long offset,
                        @Param(value = "time") long time);

        @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, a.content, a.order_id as orderId, a.reference_number as referenceNumber, "
                        + "a.status, a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
                        + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
                        + "a.terminal_code as terminalCode, a.note "
                        + "FROM transaction_receive a "
                        + "LEFT JOIN account_bank_receive b "
                        + "ON a.bank_id = b.id "
                        + "LEFT JOIN bank_type c "
                        + "ON b.bank_type_id = c.id "
                        + "WHERE a.bank_account = :value "
                        + "AND a.time BETWEEN :fromDate AND :toDate "
                        + "ORDER BY a.time DESC "
                        + "LIMIT :offset, 20 ", nativeQuery = true)
        List<TransactionReceiveAdminListDTO> getTransByBankAccountFromDate(
                        @Param(value = "value") String value,
                        @Param(value = "fromDate") long fromDate,
                        @Param(value = "toDate") long toDate,
                        @Param(value = "offset") long offset);

        @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, a.content, a.order_id as orderId, a.reference_number as referenceNumber, "
                        + "a.status, a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
                        + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
                        + "a.terminal_code as terminalCode, a.note "
                        + "FROM transaction_receive a "
                        + "LEFT JOIN account_bank_receive b "
                        + "ON a.bank_id = b.id "
                        + "LEFT JOIN bank_type c "
                        + "ON b.bank_type_id = c.id "
                        + "WHERE a.terminal_code = :value "
                        + "AND a.time >= :time "
                        + "ORDER BY a.time DESC "
                        + "LIMIT :offset, 20 ", nativeQuery = true)
        List<TransactionReceiveAdminListDTO> getTransByTerminalCodeAllDate(
                        @Param(value = "value") String value,
                        @Param(value = "offset") long offset,
                        @Param(value = "time") long time);

        @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, a.content, a.order_id as orderId, a.reference_number as referenceNumber, "
                        + "a.status, a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
                        + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
                        + "a.terminal_code as terminalCode, a.note "
                        + "FROM transaction_receive a "
                        + "LEFT JOIN account_bank_receive b "
                        + "ON a.bank_id = b.id "
                        + "LEFT JOIN bank_type c "
                        + "ON b.bank_type_id = c.id "
                        + "WHERE a.terminal_code = :value "
                        + "AND a.time BETWEEN :fromDate AND :toDate "
                        + "ORDER BY a.time DESC "
                        + "LIMIT :offset, 20 ", nativeQuery = true)
        List<TransactionReceiveAdminListDTO> getTransByTerminalCodeFromDate(
                        @Param(value = "value") String value,
                        @Param(value = "fromDate") long fromDate,
                        @Param(value = "toDate") long toDate,
                        @Param(value = "offset") long offset);

        @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, a.content, a.order_id as orderId, a.reference_number as referenceNumber, "
                        + "a.status, a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
                        + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
                        + "a.terminal_code as terminalCode, a.note "
                        + "FROM transaction_receive a "
                        + "LEFT JOIN account_bank_receive b "
                        + "ON a.bank_id = b.id "
                        + "LEFT JOIN bank_type c "
                        + "ON b.bank_type_id = c.id "
                        + "WHERE a.time >= :time "
                        + "ORDER BY a.time DESC "
                        + "LIMIT :offset, 20 ", nativeQuery = true)
        List<TransactionReceiveAdminListDTO> getAllTransAllDate(
                        @Param(value = "offset") long offset,
                        @Param(value = "time") long time);

        @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, a.content, a.order_id as orderId, a.reference_number as referenceNumber, "
                        + "a.status, a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
                        + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
                        + "a.terminal_code as terminalCode, a.note "
                        + "FROM transaction_receive a "
                        + "LEFT JOIN account_bank_receive b "
                        + "ON a.bank_id = b.id "
                        + "LEFT JOIN bank_type c "
                        + "ON b.bank_type_id = c.id "
                        + "WHERE a.time BETWEEN :fromDate AND :toDate "
                        + "ORDER BY a.time DESC "
                        + "LIMIT :offset, 20 ", nativeQuery = true)
        List<TransactionReceiveAdminListDTO> getAllTransFromDate(
                        @Param(value = "fromDate") long fromDate,
                        @Param(value = "toDate") long toDate,
                        @Param(value = "offset") long offset);

        @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, a.content, a.order_id as orderId, a.reference_number as referenceNumber, "
                        + "a.status, a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
                        + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
                        + "a.terminal_code as terminalCode, a.note "
                        + "FROM transaction_receive a "
                        + "LEFT JOIN account_bank_receive b "
                        + "ON a.bank_id = b.id "
                        + "LEFT JOIN bank_type c "
                        + "ON b.bank_type_id = c.id "
                        + "WHERE a.reference_number LIKE %:value% "
                        + "AND a.time between :fromDate AND :toDate "
                        + "ORDER BY a.time DESC "
                        + "LIMIT :offset, 20 ", nativeQuery = true)
        List<TransactionReceiveAdminListDTO> getTransByFtCode(
                        @Param(value = "value") String value,
                        @Param(value = "offset") long offset,
                        @Param(value = "fromDate") long fromDate,
                        @Param(value = "toDate") long toDate);

        @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, a.content, a.order_id as orderId, a.reference_number as referenceNumber, "
                + "a.status, a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
                + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
                + "a.terminal_code as terminalCode, a.note "
                + "FROM transaction_receive a "
                + "LEFT JOIN account_bank_receive b "
                + "ON a.bank_id = b.id "
                + "LEFT JOIN bank_type c "
                + "ON b.bank_type_id = c.id "
                + "WHERE a.reference_number LIKE %:value% "
                + "AND a.time >= :time "
                + "ORDER BY a.time DESC "
                + "LIMIT :offset, 20 ", nativeQuery = true)
        List<TransactionReceiveAdminListDTO> getTransByFtCode(
                @Param(value = "value") String value,
                @Param(value = "offset") long offset,
                @Param(value = "time") long time);

        // check
        @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, a.content, a.order_id as orderId, a.reference_number as referenceNumber, "
                        + "a.status, a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
                        + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
                        + "a.terminal_code as terminalCode, a.note "
                        + "FROM transaction_receive a "
                        + "LEFT JOIN account_bank_receive b "
                        + "ON a.bank_id = b.id "
                        + "LEFT JOIN bank_type c "
                        + "ON b.bank_type_id = c.id "
                        + "WHERE a.order_id LIKE %:value% "
                        + "AND a.time BETWEEN :fromDate AND :toDate "
                        + "ORDER BY a.time DESC "
                        + "LIMIT :offset, 20 ", nativeQuery = true)
        List<TransactionReceiveAdminListDTO> getTransByOrderId(
                        @Param(value = "value") String value,
                        @Param(value = "offset") long offset,
                        @Param(value = "fromDate") long fromDate,
                        @Param(value = "toDate") long toDate);

        @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, a.content, a.order_id as orderId, a.reference_number as referenceNumber, "
                + "a.status, a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
                + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
                + "a.terminal_code as terminalCode, a.note "
                + "FROM transaction_receive a "
                + "LEFT JOIN account_bank_receive b "
                + "ON a.bank_id = b.id "
                + "LEFT JOIN bank_type c "
                + "ON b.bank_type_id = c.id "
                + "WHERE a.order_id LIKE %:value% "
                + "AND a.time >= :time "
                + "ORDER BY a.time DESC "
                + "LIMIT :offset, 20 ", nativeQuery = true)
        List<TransactionReceiveAdminListDTO> getTransByOrderId(
                @Param(value = "value") String value,
                @Param(value = "offset") long offset,
                @Param(value = "time") long time);

        // check
        @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, a.content, a.order_id as orderId, a.reference_number as referenceNumber, "
                        + "a.status, a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
                        + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
                        + "a.terminal_code as terminalCode, a.note "
                        + "FROM transaction_receive a "
                        + "LEFT JOIN account_bank_receive b "
                        + "ON a.bank_id = b.id "
                        + "LEFT JOIN bank_type c "
                        + "ON b.bank_type_id = c.id "
                        + "WHERE a.content LIKE %:value% "
                        + "AND a.time BETWEEN :fromDate AND :toDate "
                        + "ORDER BY a.time DESC "
                        + "LIMIT :offset, 20 ", nativeQuery = true)
        List<TransactionReceiveAdminListDTO> getTransByContent(
                        @Param(value = "value") String value,
                        @Param(value = "offset") long offset,
                        @Param(value = "fromDate") long fromDate,
                        @Param(value = "toDate") long toDate);

        @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, a.content, a.order_id as orderId, a.reference_number as referenceNumber, "
                + "a.status, a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
                + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
                + "a.terminal_code as terminalCode, a.note "
                + "FROM transaction_receive a "
                + "LEFT JOIN account_bank_receive b "
                + "ON a.bank_id = b.id "
                + "LEFT JOIN bank_type c "
                + "ON b.bank_type_id = c.id "
                + "WHERE a.content LIKE %:value% "
                + "AND a.time >= :time "
                + "ORDER BY a.time DESC "
                + "LIMIT :offset, 20 ", nativeQuery = true)
        List<TransactionReceiveAdminListDTO> getTransByContent(
                @Param(value = "value") String value,
                @Param(value = "offset") long offset,
                @Param(value = "time") long time);

        //////// MERCHANT WEB VIETQR
        @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, a.content, a.order_id as orderId, a.reference_number as referenceNumber, "
                        + "a.status, a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
                        + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
                        + "a.terminal_code as terminalCode, a.note "
                        + "FROM transaction_receive a "
                        + "INNER JOIN account_bank_receive b "
                        + "ON a.bank_id = b.id "
                        + "INNER JOIN bank_type c "
                        + "ON b.bank_type_id = c.id "
                        + "INNER JOIN account_customer_bank d "
                        + "ON b.id = d.bank_id  "
                        + "WHERE a.reference_number LIKE %:value% "
                        + "AND d.customer_sync_id = :merchantId "
                        + "AND a.time BETWEEN :fromDate AND :toDate "
                        + "ORDER BY a.time DESC "
                        + "LIMIT :offset, 20 ", nativeQuery = true)
        List<TransactionReceiveAdminListDTO> getTransByFtCodeAndMerchantId(
                        @Param(value = "value") String value,
                        @Param(value = "merchantId") String merchantId,
                        @Param(value = "offset") long offset,
                        @Param(value = "fromDate") long fromDate,
                        @Param(value = "toDate") long toDate);

        @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, a.content, a.order_id as orderId, a.reference_number as referenceNumber, "
                + "a.status, a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
                + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
                + "a.terminal_code as terminalCode, a.note "
                + "FROM transaction_receive a "
                + "INNER JOIN account_bank_receive b "
                + "ON a.bank_id = b.id "
                + "INNER JOIN bank_type c "
                + "ON b.bank_type_id = c.id "
                + "INNER JOIN account_customer_bank d "
                + "ON b.id = d.bank_id  "
                + "WHERE a.reference_number LIKE %:value% "
                + "AND d.customer_sync_id = :merchantId "
                + "AND a.time >= :time "
                + "ORDER BY a.time DESC "
                + "LIMIT :offset, 20 ", nativeQuery = true)
        List<TransactionReceiveAdminListDTO> getTransByFtCodeAndMerchantId(
                @Param(value = "value") String value,
                @Param(value = "merchantId") String merchantId,
                @Param(value = "offset") long offset,
                @Param(value = "time") long time);

        @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, a.content, a.order_id as orderId, a.reference_number as referenceNumber, "
                        + "a.status, a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
                        + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
                        + "a.terminal_code as terminalCode, a.note "
                        + "FROM transaction_receive a "
                        + "INNER JOIN account_bank_receive b "
                        + "ON a.bank_id = b.id "
                        + "INNER JOIN bank_type c "
                        + "ON b.bank_type_id = c.id "
                        + "INNER JOIN account_customer_bank d "
                        + "ON b.id = d.bank_id  "
                        + "WHERE a.order_id LIKE %:value% "
                        + "AND d.customer_sync_id = :merchantId "
                        + "AND a.time BETWEEN :fromDate AND :toDate "
                        + "ORDER BY a.time DESC "
                        + "LIMIT :offset, 20 ", nativeQuery = true)
        List<TransactionReceiveAdminListDTO> getTransByOrderIdAndMerchantId(
                        @Param(value = "value") String value,
                        @Param(value = "merchantId") String merchantId,
                        @Param(value = "offset") long offset,
                        @Param(value = "fromDate") long fromDate,
                        @Param(value = "toDate") long toDate);

        @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, a.content, a.order_id as orderId, a.reference_number as referenceNumber, "
                + "a.status, a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
                + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
                + "a.terminal_code as terminalCode, a.note "
                + "FROM transaction_receive a "
                + "INNER JOIN account_bank_receive b "
                + "ON a.bank_id = b.id "
                + "INNER JOIN bank_type c "
                + "ON b.bank_type_id = c.id "
                + "INNER JOIN account_customer_bank d "
                + "ON b.id = d.bank_id  "
                + "WHERE a.order_id LIKE %:value% "
                + "AND d.customer_sync_id = :merchantId "
                + "AND a.time >= :time "
                + "ORDER BY a.time DESC "
                + "LIMIT :offset, 20 ", nativeQuery = true)
        List<TransactionReceiveAdminListDTO> getTransByOrderIdAndMerchantId(
                @Param(value = "value") String value,
                @Param(value = "merchantId") String merchantId,
                @Param(value = "offset") long offset,
                @Param(value = "time") long time);

        @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, a.content, a.order_id as orderId, a.reference_number as referenceNumber, "
                        + "a.status, a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
                        + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
                        + "a.terminal_code as terminalCode, a.note "
                        + "FROM transaction_receive a "
                        + "INNER JOIN account_bank_receive b "
                        + "ON a.bank_id = b.id "
                        + "INNER JOIN bank_type c "
                        + "ON b.bank_type_id = c.id "
                        + "INNER JOIN account_customer_bank d "
                        + "ON b.id = d.bank_id  "
                        + "WHERE a.content LIKE %:value% "
                        + "AND d.customer_sync_id = :merchantId "
                        + "AND a.time BETWEEN :fromDate and :toDate "
                        + "ORDER BY a.time DESC "
                        + "LIMIT :offset, 20 ", nativeQuery = true)
        List<TransactionReceiveAdminListDTO> getTransByContentAndMerchantId(
                        @Param(value = "value") String value,
                        @Param(value = "merchantId") String merchantId,
                        @Param(value = "offset") long offset,
                        @Param(value = "fromDate") long fromDate,
                        @Param(value = "toDate") long toDate);

        @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, a.content, a.order_id as orderId, a.reference_number as referenceNumber, "
                + "a.status, a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
                + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
                + "a.terminal_code as terminalCode, a.note "
                + "FROM transaction_receive a "
                + "INNER JOIN account_bank_receive b "
                + "ON a.bank_id = b.id "
                + "INNER JOIN bank_type c "
                + "ON b.bank_type_id = c.id "
                + "INNER JOIN account_customer_bank d "
                + "ON b.id = d.bank_id  "
                + "WHERE a.content LIKE %:value% "
                + "AND d.customer_sync_id = :merchantId "
                + "AND a.time >= :time "
                + "ORDER BY a.time DESC "
                + "LIMIT :offset, 20 ", nativeQuery = true)
        List<TransactionReceiveAdminListDTO> getTransByContentAndMerchantId(
                @Param(value = "value") String value,
                @Param(value = "merchantId") String merchantId,
                @Param(value = "offset") long offset,
                @Param(value = "time") long time);

        @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, a.content, a.order_id as orderId, a.reference_number as referenceNumber, "
                        + "a.status, a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
                        + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
                        + "a.terminal_code as terminalCode, a.note "
                        + "FROM transaction_receive a "
                        + "INNER JOIN account_bank_receive b "
                        + "ON a.bank_id = b.id "
                        + "INNER JOIN bank_type c "
                        + "ON b.bank_type_id = c.id "
                        + "INNER JOIN account_customer_bank d "
                        + "ON b.id = d.bank_id  "
                        + "WHERE a.terminal_code = :value "
                        + "AND d.customer_sync_id = :merchantId "
                        + "AND a.time >= :time "
                        + "ORDER BY a.time DESC "
                        + "LIMIT :offset, 20 ", nativeQuery = true)
        List<TransactionReceiveAdminListDTO> getTransByTerminalCodeAndMerchantIdAllDate(
                        @Param(value = "value") String value,
                        @Param(value = "merchantId") String merchantId,
                        @Param(value = "offset") long offset,
                        @Param(value = "time") long time);

        @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, a.content, a.order_id as orderId, a.reference_number as referenceNumber, "
                        + "a.status, a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
                        + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
                        + "a.terminal_code as terminalCode, a.note "
                        + "FROM transaction_receive a "
                        + "INNER JOIN account_bank_receive b "
                        + "ON a.bank_id = b.id "
                        + "INNER JOIN bank_type c "
                        + "ON b.bank_type_id = c.id "
                        + "INNER JOIN account_customer_bank d "
                        + "ON b.id = d.bank_id  "
                        + "WHERE a.time BETWEEN :fromDate AND :toDate "
                        + "AND a.terminal_code = :value "
                        + "AND d.customer_sync_id = :merchantId "
                        + "ORDER BY a.time DESC "
                        + "LIMIT :offset, 20 ", nativeQuery = true)
        List<TransactionReceiveAdminListDTO> getTransByTerminalCodeAndMerchantIdFromDate(
                        @Param(value = "fromDate") long fromDate,
                        @Param(value = "toDate") long toDate,
                        @Param(value = "value") String value,
                        @Param(value = "merchantId") String merchantId,
                        @Param(value = "offset") long offset);

        @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, a.content, a.order_id as orderId, a.reference_number as referenceNumber, "
                        + "a.status, a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
                        + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
                        + "a.terminal_code as terminalCode, a.note "
                        + "FROM transaction_receive a "
                        + "INNER JOIN account_bank_receive b "
                        + "ON a.bank_id = b.id "
                        + "INNER JOIN bank_type c "
                        + "ON b.bank_type_id = c.id "
                        + "INNER JOIN account_customer_bank d "
                        + "ON b.id = d.bank_id  "
                        + "WHERE d.customer_sync_id = :merchantId "
                        + "AND a.time >= :time "
                        + "ORDER BY a.time DESC "
                        + "LIMIT :offset, 20 ", nativeQuery = true)
        List<TransactionReceiveAdminListDTO> getAllTransAllDateByMerchantId(
                        @Param(value = "merchantId") String merchantId,
                        @Param(value = "offset") long offset,
                        @Param(value = "time") long time);

        @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, a.content, a.order_id as orderId, a.reference_number as referenceNumber, "
                        + "a.status, a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
                        + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
                        + "a.terminal_code as terminalCode, a.note "
                        + "FROM transaction_receive a "
                        + "INNER JOIN account_bank_receive b "
                        + "ON a.bank_id = b.id "
                        + "INNER JOIN bank_type c "
                        + "ON b.bank_type_id = c.id "
                        + "INNER JOIN account_customer_bank d "
                        + "ON a.bank_id = d.bank_id "
                        + "WHERE a.time BETWEEN :fromDate AND :toDate "
                        + "AND d.customer_sync_id = :merchantId "
                        + "ORDER BY a.time DESC "
                        + "LIMIT :offset, 20 ", nativeQuery = true)
        List<TransactionReceiveAdminListDTO> getAllTransFromDateByMerchantId(
                        @Param(value = "fromDate") long fromDate,
                        @Param(value = "toDate") long toDate,
                        @Param(value = "merchantId") String merchantId,
                        @Param(value = "offset") long offset);
        ///////////

        //////// BANK ACCOUNT WEB VIETQR
        @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, a.content, a.order_id as orderId, a.reference_number as referenceNumber, "
                        + "a.status, a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
                        + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
                        + "a.terminal_code as terminalCode, a.note "
                        + "FROM transaction_receive a "
                        + "LEFT JOIN account_bank_receive b "
                        + "ON a.bank_id = b.id "
                        + "LEFT JOIN bank_type c "
                        + "ON b.bank_type_id = c.id "
                        + "WHERE a.reference_number LIKE %:value% "
                        + "AND a.user_id = :userId "
                        + "AND a.time BETWEEN :fromDate AND :toDate "
                        + "ORDER BY a.time DESC "
                        + "LIMIT :offset, 20 ", nativeQuery = true)
        List<TransactionReceiveAdminListDTO> getTransByFtCodeAndUserId(
                        @Param(value = "value") String value,
                        @Param(value = "userId") String userId,
                        @Param(value = "offset") long offset,
                        @Param(value = "fromDate") long fromDate,
                        @Param(value = "toDate") long toDate);

        @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, a.content, a.order_id as orderId, a.reference_number as referenceNumber, "
                + "a.status, a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
                + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
                + "a.terminal_code as terminalCode, a.note "
                + "FROM transaction_receive a "
                + "LEFT JOIN account_bank_receive b "
                + "ON a.bank_id = b.id "
                + "LEFT JOIN bank_type c "
                + "ON b.bank_type_id = c.id "
                + "WHERE a.reference_number LIKE %:value% "
                + "AND a.user_id = :userId "
                + "AND a.time >= :time "
                + "ORDER BY a.time DESC "
                + "LIMIT :offset, 20 ", nativeQuery = true)
        List<TransactionReceiveAdminListDTO> getTransByFtCodeAndUserId(
                @Param(value = "value") String value,
                @Param(value = "userId") String userId,
                @Param(value = "offset") long offset,
                @Param(value = "time") long time);

        @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, a.content, a.order_id as orderId, a.reference_number as referenceNumber, "
                        + "a.status, a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
                        + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
                        + "a.terminal_code as terminalCode, a.note "
                        + "FROM transaction_receive a "
                        + "LEFT JOIN account_bank_receive b "
                        + "ON a.bank_id = b.id "
                        + "LEFT JOIN bank_type c "
                        + "ON b.bank_type_id = c.id "
                        + "WHERE a.order_id LIKE %:value% "
                        + "AND a.user_id = :userId "
                        + "AND a.time BETWEEN :fromDate and :toDate "
                        + "ORDER BY a.time DESC "
                        + "LIMIT :offset, 20 ", nativeQuery = true)
        List<TransactionReceiveAdminListDTO> getTransByOrderIdAndUserId(
                        @Param(value = "value") String value,
                        @Param(value = "userId") String userId,
                        @Param(value = "offset") long offset,
                        @Param(value = "fromDate") long fromDate,
                        @Param(value = "toDate") long toDate);

        @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, a.content, a.order_id as orderId, a.reference_number as referenceNumber, "
                + "a.status, a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
                + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
                + "a.terminal_code as terminalCode, a.note "
                + "FROM transaction_receive a "
                + "LEFT JOIN account_bank_receive b "
                + "ON a.bank_id = b.id "
                + "LEFT JOIN bank_type c "
                + "ON b.bank_type_id = c.id "
                + "WHERE a.order_id LIKE %:value% "
                + "AND a.user_id = :userId "
                + "AND a.time >= :time "
                + "ORDER BY a.time DESC "
                + "LIMIT :offset, 20 ", nativeQuery = true)
        List<TransactionReceiveAdminListDTO> getTransByOrderIdAndUserId(
                @Param(value = "value") String value,
                @Param(value = "userId") String userId,
                @Param(value = "offset") long offset,
                @Param(value = "time") long time);

        @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, a.content, a.order_id as orderId, a.reference_number as referenceNumber, "
                        + "a.status, a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
                        + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
                        + "a.terminal_code as terminalCode, a.note "
                        + "FROM transaction_receive a "
                        + "LEFT JOIN account_bank_receive b "
                        + "ON a.bank_id = b.id "
                        + "LEFT JOIN bank_type c "
                        + "ON b.bank_type_id = c.id "
                        + "WHERE a.content LIKE %:value% "
                        + "AND a.user_id = :userId "
                        + "AND a.time between :fromDate and :toDate "
                        + "ORDER BY a.time DESC "
                        + "LIMIT :offset, 20 ", nativeQuery = true)
        List<TransactionReceiveAdminListDTO> getTransByContentAndUserId(
                        @Param(value = "value") String value,
                        @Param(value = "userId") String userId,
                        @Param(value = "offset") long offset,
                        @Param(value = "fromDate") long fromDate,
                        @Param(value = "toDate") long toDate);

        @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, a.content, a.order_id as orderId, a.reference_number as referenceNumber, "
                + "a.status, a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
                + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
                + "a.terminal_code as terminalCode, a.note "
                + "FROM transaction_receive a "
                + "LEFT JOIN account_bank_receive b "
                + "ON a.bank_id = b.id "
                + "LEFT JOIN bank_type c "
                + "ON b.bank_type_id = c.id "
                + "WHERE a.content LIKE %:value% "
                + "AND a.user_id = :userId "
                + "AND a.time >= :time "
                + "ORDER BY a.time DESC "
                + "LIMIT :offset, 20 ", nativeQuery = true)
        List<TransactionReceiveAdminListDTO> getTransByContentAndUserId(
                @Param(value = "value") String value,
                @Param(value = "userId") String userId,
                @Param(value = "offset") long offset,
                @Param(value = "time") long time);

        @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, a.content, a.order_id as orderId, a.reference_number as referenceNumber, "
                        + "a.status, a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
                        + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
                        + "a.terminal_code as terminalCode, a.note "
                        + "FROM transaction_receive a "
                        + "LEFT JOIN account_bank_receive b "
                        + "ON a.bank_id = b.id "
                        + "LEFT JOIN bank_type c "
                        + "ON b.bank_type_id = c.id "
                        + "WHERE a.terminal_code = :value "
                        + "AND a.user_id = :userId "
                        + "AND a.time >= :time "
                        + "ORDER BY a.time DESC "
                        + "LIMIT :offset, 20 ", nativeQuery = true)
        List<TransactionReceiveAdminListDTO> getTransByTerminalCodeAndUserIdAllDate(
                        @Param(value = "value") String value,
                        @Param(value = "userId") String userId,
                        @Param(value = "offset") long offset,
                        @Param(value = "time") long time);

        @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, a.content, a.order_id as orderId, a.reference_number as referenceNumber, "
                        + "a.status, a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
                        + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
                        + "a.terminal_code as terminalCode, a.note "
                        + "FROM transaction_receive a "
                        + "LEFT JOIN account_bank_receive b "
                        + "ON a.bank_id = b.id "
                        + "LEFT JOIN bank_type c "
                        + "ON b.bank_type_id = c.id "
                        + "WHERE a.time BETWEEN :fromDate AND :toDate "
                        + "AND a.terminal_code = :value "
                        + "AND a.user_id = :userId "
                        + "ORDER BY a.time DESC "
                        + "LIMIT :offset, 20 ", nativeQuery = true)
        List<TransactionReceiveAdminListDTO> getTransByTerminalCodeAndUserIdFromDate(
                        @Param(value = "fromDate") long fromDate,
                        @Param(value = "toDate") long toDate,
                        @Param(value = "value") String value,
                        @Param(value = "userId") String userId,
                        @Param(value = "offset") long offset);

        @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, a.content, a.order_id as orderId, a.reference_number as referenceNumber, "
                        + "a.status, a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
                        + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
                        + "a.terminal_code as terminalCode, a.note "
                        + "FROM transaction_receive a "
                        + "LEFT JOIN account_bank_receive b "
                        + "ON a.bank_id = b.id "
                        + "LEFT JOIN bank_type c "
                        + "ON b.bank_type_id = c.id "
                        + "WHERE a.user_id = :userId "
                        + "AND a.time >= :time "
                        + "ORDER BY a.time DESC "
                        + "LIMIT :offset, 20 ", nativeQuery = true)
        List<TransactionReceiveAdminListDTO> getAllTransAllDateByUserId(
                        @Param(value = "userId") String userId,
                        @Param(value = "offset") long offset,
                        @Param(value = "time") long time);

        @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, a.content, a.order_id as orderId, a.reference_number as referenceNumber, "
                        + "a.status, a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
                        + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
                        + "a.terminal_code as terminalCode, a.note "
                        + "FROM transaction_receive a "
                        + "LEFT JOIN account_bank_receive b "
                        + "ON a.bank_id = b.id "
                        + "LEFT JOIN bank_type c "
                        + "ON b.bank_type_id = c.id "
                        + "WHERE a.time BETWEEN :fromDate AND :toDate "
                        + "AND a.user_id = :userId "
                        + "ORDER BY a.time DESC "
                        + "LIMIT :offset, 20 ", nativeQuery = true)
        List<TransactionReceiveAdminListDTO> getAllTransFromDateByUserId(
                        @Param(value = "fromDate") long fromDate,
                        @Param(value = "toDate") long toDate,
                        @Param(value = "userId") String userId,
                        @Param(value = "offset") long offset);
        ///////////

        /////// FOR EXPORT MERCHANT

        @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, a.content, a.order_id as orderId, a.reference_number as referenceNumber, "
                        + "a.status, a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
                        + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
                        + "a.terminal_code as terminalCode, a.note "
                        + "FROM transaction_receive a "
                        + "LEFT JOIN account_bank_receive b "
                        + "ON a.bank_id = b.id "
                        + "LEFT JOIN bank_type c "
                        + "ON b.bank_type_id = c.id "
                        + "WHERE a.bank_account = :value "
                        + "AND a.time BETWEEN :fromDate AND :toDate "
                        + "ORDER BY a.time DESC ", nativeQuery = true)
        List<TransactionReceiveAdminListDTO> exportTransByBankAccountFromDate(
                        @Param(value = "value") String value,
                        @Param(value = "fromDate") long fromDate,
                        @Param(value = "toDate") long toDate);

        // check export
        @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, a.content, a.order_id as orderId, a.reference_number as referenceNumber, "
                        + "a.status, a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
                        + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
                        + "a.terminal_code as terminalCode, a.note "
                        + "FROM transaction_receive a "
                        + "LEFT JOIN account_bank_receive b "
                        + "ON a.bank_id = b.id "
                        + "LEFT JOIN bank_type c "
                        + "ON b.bank_type_id = c.id "
                        + "LEFT JOIN account_customer_bank d "
                        + "ON b.id = d.bank_id  "
                        + "WHERE a.reference_number LIKE %:value% "
                        + "AND a.time >= :time "
                        + "AND d.customer_sync_id = :merchantId "
                        + "ORDER BY a.time DESC ", nativeQuery = true)
        List<TransactionReceiveAdminListDTO> exportTransByFtCodeAndMerchantId(
                        @Param(value = "value") String value,
                        @Param(value = "merchantId") String merchantId,
                        @Param(value = "time") long time);

        @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, a.content, a.order_id as orderId, a.reference_number as referenceNumber, "
                + "a.status, a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
                + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
                + "a.terminal_code as terminalCode, a.note "
                + "FROM transaction_receive a "
                + "LEFT JOIN account_bank_receive b "
                + "ON a.bank_id = b.id "
                + "LEFT JOIN bank_type c "
                + "ON b.bank_type_id = c.id "
                + "LEFT JOIN account_customer_bank d "
                + "ON b.id = d.bank_id  "
                + "WHERE a.reference_number LIKE %:value% "
                + "AND a.time BETWEEN :fromDate AND :toDate "
                + "AND d.customer_sync_id = :merchantId "
                + "ORDER BY a.time DESC ", nativeQuery = true)
        List<TransactionReceiveAdminListDTO> exportTransByFtCodeAndMerchantId(
                @Param(value = "value") String value,
                @Param(value = "merchantId") String merchantId,
                @Param(value = "fromDate") long fromDate,
                @Param(value = "toDate") long toDate);

        // check export
        @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, a.content, a.order_id as orderId, a.reference_number as referenceNumber, "
                        + "a.status, a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
                        + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
                        + "a.terminal_code as terminalCode, a.note "
                        + "FROM transaction_receive a "
                        + "LEFT JOIN account_bank_receive b "
                        + "ON a.bank_id = b.id "
                        + "LEFT JOIN bank_type c "
                        + "ON b.bank_type_id = c.id "
                        + "LEFT JOIN account_customer_bank d "
                        + "ON b.id = d.bank_id  "
                        + "WHERE a.order_id LIKE %:value% "
                        + "AND d.customer_sync_id = :merchantId "
                        + "AND a.time >= :time "
                        + "ORDER BY a.time DESC ", nativeQuery = true)
        List<TransactionReceiveAdminListDTO> exportTransByOrderIdAndMerchantId(
                        @Param(value = "value") String value,
                        @Param(value = "merchantId") String merchantId,
                        @Param(value = "time") long time);

        @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, a.content, a.order_id as orderId, a.reference_number as referenceNumber, "
                + "a.status, a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
                + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
                + "a.terminal_code as terminalCode, a.note "
                + "FROM transaction_receive a "
                + "LEFT JOIN account_bank_receive b "
                + "ON a.bank_id = b.id "
                + "LEFT JOIN bank_type c "
                + "ON b.bank_type_id = c.id "
                + "LEFT JOIN account_customer_bank d "
                + "ON b.id = d.bank_id  "
                + "WHERE a.order_id LIKE %:value% "
                + "AND d.customer_sync_id = :merchantId "
                + "AND a.time BETWEEN :fromDate AND :toDate "
                + "ORDER BY a.time DESC ", nativeQuery = true)
        List<TransactionReceiveAdminListDTO> exportTransByOrderIdAndMerchantId(
                @Param(value = "value") String value,
                @Param(value = "merchantId") String merchantId,
                @Param(value = "fromDate") long fromDate,
                @Param(value = "toDate") long toDate);

        // check export
        @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, a.content, a.order_id as orderId, a.reference_number as referenceNumber, "
                        + "a.status, a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
                        + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
                        + "a.terminal_code as terminalCode, a.note "
                        + "FROM transaction_receive a "
                        + "LEFT JOIN account_bank_receive b "
                        + "ON a.bank_id = b.id "
                        + "LEFT JOIN bank_type c "
                        + "ON b.bank_type_id = c.id "
                        + "LEFT JOIN account_customer_bank d "
                        + "ON b.id = d.bank_id  "
                        + "WHERE a.content LIKE %:value% "
                        + "AND d.customer_sync_id = :merchantId "
                        + "AND a.time >= :time "
                        + "ORDER BY a.time DESC ", nativeQuery = true)
        List<TransactionReceiveAdminListDTO> exportTransByContentAndMerchantId(
                        @Param(value = "value") String value,
                        @Param(value = "merchantId") String merchantId,
                        @Param(value = "time") long time);

        @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, a.content, a.order_id as orderId, a.reference_number as referenceNumber, "
                + "a.status, a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
                + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
                + "a.terminal_code as terminalCode, a.note "
                + "FROM transaction_receive a "
                + "LEFT JOIN account_bank_receive b "
                + "ON a.bank_id = b.id "
                + "LEFT JOIN bank_type c "
                + "ON b.bank_type_id = c.id "
                + "LEFT JOIN account_customer_bank d "
                + "ON b.id = d.bank_id  "
                + "WHERE a.content LIKE %:value% "
                + "AND d.customer_sync_id = :merchantId "
                + "AND a.time BETWEEN :fromDate AND :toDate "
                + "ORDER BY a.time DESC ", nativeQuery = true)
        List<TransactionReceiveAdminListDTO> exportTransByContentAndMerchantId(
                @Param(value = "value") String value,
                @Param(value = "merchantId") String merchantId,
                @Param(value = "fromDate") long fromDate,
                @Param(value = "toDate") long toDate);

        @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, a.content, a.order_id as orderId, a.reference_number as referenceNumber, "
                        + "a.status, a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
                        + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
                        + "a.terminal_code as terminalCode, a.note "
                        + "FROM transaction_receive a "
                        + "LEFT JOIN account_bank_receive b "
                        + "ON a.bank_id = b.id "
                        + "LEFT JOIN bank_type c "
                        + "ON b.bank_type_id = c.id "
                        + "LEFT JOIN account_customer_bank d "
                        + "ON b.id = d.bank_id  "
                        + "AND d.customer_sync_id = :merchantId "
                        + "WHERE a.time >= :time "
                        + "ORDER BY a.time DESC ", nativeQuery = true)
        List<TransactionReceiveAdminListDTO> exportAllTransAllDateByMerchantId(
                        @Param(value = "merchantId") String merchantId,
                        @Param(value = "time") long time);

        // check export
        @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, a.content, a.order_id as orderId, a.reference_number as referenceNumber, "
                        + "a.status, a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
                        + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
                        + "a.terminal_code as terminalCode, a.note "
                        + "FROM transaction_receive a "
                        + "LEFT JOIN account_bank_receive b "
                        + "ON a.bank_id = b.id "
                        + "LEFT JOIN bank_type c "
                        + "ON b.bank_type_id = c.id "
                        + "LEFT JOIN account_customer_bank d "
                        + "ON b.id = d.bank_id  "
                        + "WHERE a.time BETWEEN :fromDate AND :toDate "
                        + "AND d.customer_sync_id = :merchantId "
                        + "ORDER BY a.time DESC ", nativeQuery = true)
        List<TransactionReceiveAdminListDTO> exportAllTransFromDateByMerchantId(
                        @Param(value = "fromDate") long fromDate,
                        @Param(value = "toDate") long toDate,
                        @Param(value = "merchantId") String merchantId);

        // check export
        @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, a.content, a.order_id as orderId, a.reference_number as referenceNumber, "
                        + "a.status, a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
                        + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
                        + "a.terminal_code as terminalCode, a.note "
                        + "FROM transaction_receive a "
                        + "LEFT JOIN account_bank_receive b "
                        + "ON a.bank_id = b.id "
                        + "LEFT JOIN bank_type c "
                        + "ON b.bank_type_id = c.id "
                        + "LEFT JOIN account_customer_bank d "
                        + "ON b.id = d.bank_id  "
                        + "WHERE a.time BETWEEN :fromDate AND :toDate "
                        + "AND a.terminal_code = :value "
                        + "AND d.customer_sync_id = :merchantId "
                        + "ORDER BY a.time DESC ", nativeQuery = true)
        List<TransactionReceiveAdminListDTO> exportTransFromDateByTerminalCodeAndMerchantId(
                        @Param(value = "fromDate") long fromDate,
                        @Param(value = "toDate") long toDate,
                        @Param(value = "value") String value,
                        @Param(value = "merchantId") String merchantId);

        //
        @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, a.content, a.order_id as orderId, a.reference_number as referenceNumber, "
                        + "a.status, a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, a.sign, a.trace_id as traceId, a.type, b.bank_account_name as userBankName,  "
                        + "b.national_id as nationalId, b.phone_authenticated as phoneAuthenticated, b.is_authenticated as sync, c.bank_code as bankCode, c.bank_short_name as bankShortName, c.bank_name as bankName, c.img_id as imgId, "
                        + "CASE  "
                        + "WHEN b.is_sync = true AND b.mms_active = false THEN 1  "
                        + "WHEN b.is_sync = true AND b.mms_active = true THEN 2  "
                        + "WHEN b.is_sync = false AND b.mms_active = true THEN 2  "
                        + "ELSE 0  "
                        + "END as flow, "
                        + "a.terminal_code as terminalCode, a.note "
                        + "FROM transaction_receive a  "
                        + "LEFT JOIN account_bank_receive b  "
                        + "ON a.bank_id = b.id  "
                        + "LEFT JOIN bank_type c  "
                        + "ON b.bank_type_id = c.id  "
                        + "WHERE a.id = :transactionId ", nativeQuery = true)
        TransReceiveAdminDetailDTO getDetailTransReceiveAdmin(@Param(value = "transactionId") String transactionId);

        @Query(value = "SELECT COUNT(b.id) AS totalTrans, "
                        + "SUM(CASE WHEN b.trans_type = 'C' AND b.status = 1 THEN b.amount ELSE 0 END) AS totalCashIn,  "
                        + "SUM(CASE WHEN b.trans_type = 'D' AND b.status = 1 THEN b.amount ELSE 0 END) AS totalCashOut, "
                        + "SUM(CASE WHEN b.trans_type = 'C' AND b.status = 1 THEN 1 ELSE 0 END) AS totalTransC,  "
                        + "SUM(CASE WHEN b.trans_type = 'D' AND b.status = 1 THEN 1 ELSE 0 END) AS totalTransD  "
                        + "FROM account_customer_bank a  "
                        + "LEFT JOIN transaction_receive b "
                        + "ON a.bank_id = b.bank_id  "
                        + "WHERE a.customer_sync_id = :customerSyncId AND b.status = 1 ", nativeQuery = true)
        TransStatisticDTO getTransStatisticCustomerSync(@Param(value = "customerSyncId") String customerSyncId);

        @Query(value = "SELECT COUNT(b.id) AS totalTrans, "
                        + "SUM(CASE WHEN b.trans_type = 'C' AND b.status = 1 THEN b.amount ELSE 0 END) AS totalCashIn,  "
                        + "SUM(CASE WHEN b.trans_type = 'D' AND b.status = 1 THEN b.amount ELSE 0 END) AS totalCashOut, "
                        + "SUM(CASE WHEN b.trans_type = 'C' AND b.status = 1 THEN 1 ELSE 0 END) AS totalTransC,  "
                        + "SUM(CASE WHEN b.trans_type = 'D' AND b.status = 1 THEN 1 ELSE 0 END) AS totalTransD  "
                        + "FROM account_customer_bank a  "
                        + "LEFT JOIN transaction_receive b "
                        + "ON a.bank_id = b.bank_id  "
                        + "WHERE a.customer_sync_id = :customerSyncId AND b.status = 1 "
                        + "AND DATE_FORMAT(CONVERT_TZ(FROM_UNIXTIME(b.time), '+00:00', '+07:00'), '%Y-%m') = :month "
                        + "GROUP BY DATE_FORMAT(CONVERT_TZ(FROM_UNIXTIME(b.time), '+00:00', '+07:00'), '%Y-%m') ", nativeQuery = true)
        TransStatisticDTO getTransStatisticCustomerSyncByMonth(@Param(value = "customerSyncId") String customerSyncId,
                        @Param(value = "month") String month);

        @Query(value = "SELECT "
                        + "DATE_FORMAT(CONVERT_TZ(FROM_UNIXTIME(time), '+00:00', '+07:00'), '%Y-%m') AS month, "
                        + "COUNT(*) as totalTrans, "
                        + "SUM(amount) as totalAmount "
                        + "FROM transaction_receive  "
                        + "WHERE bank_id = :bankId AND status = 1 "
                        + "AND DATE_FORMAT(CONVERT_TZ(FROM_UNIXTIME(time), '+00:00', '+07:00'), '%Y-%m') = :month "
                        + "GROUP BY month ", nativeQuery = true)
        TransactionFeeDTO getTransactionFeeCountingTypeAll(
                        @Param(value = "bankId") String bankId,
                        @Param(value = "month") String month);

        @Query(value = "SELECT "
                        + "DATE_FORMAT(CONVERT_TZ(FROM_UNIXTIME(time), '+00:00', '+07:00'), '%Y-%m') AS month, "
                        + "COUNT(*) as totalTrans, "
                        + "SUM(amount) as totalAmount "
                        + "FROM transaction_receive  "
                        + "WHERE bank_id = :bankId AND status = 1 AND type = 0 "
                        + "AND DATE_FORMAT(CONVERT_TZ(FROM_UNIXTIME(time), '+00:00', '+07:00'), '%Y-%m') = :month "
                        + "GROUP BY month ", nativeQuery = true)
        TransactionFeeDTO getTransactionFeeCountingTypeSystem(
                        @Param(value = "bankId") String bankId,
                        @Param(value = "month") String month);

        @Query(value = "SELECT a.id as transactionId, a.bank_id as bankId, a.amount, a.content, "
                        + "a.trans_type as transType, a.terminal_code as terminalCode, a.order_id as orderId, "
                        + "a.type, a.status, a.time as timeCreated, a.bank_account as bankAccount, b.bank_type_id as bankTypeId, "
                        + "c.bank_code as bankCode, c.bank_name as bankName, c.bank_short_name as bankShortName, c.img_id as imgId, b.bank_account_name as userBankName, "
                        + "a.qr_code as qrCode, b.mms_active as mmsActive, a.note "
                        + "FROM transaction_receive a "
                        + "LEFT JOIN account_bank_receive b "
                        + "ON a.bank_id = b.id "
                        + "LEFT JOIN bank_type c "
                        + "ON b.bank_type_id = c.id "
                        + "WHERE a.id = :id ", nativeQuery = true)
        TransactionQRDTO getTransactionQRById(@Param(value = "id") String id);

        // check
        @Query(value = "SELECT a.reference_number as referenceNumber, a.order_id as orderId, a.amount, a.content, "
                        + "a.trans_type as transType, a.status, a.type, a.time as timeCreated, a.time_paid as timePaid "
                        + "FROM transaction_receive a "
                        + "LEFT JOIN account_bank_receive b "
                        + "ON a.bank_id = b.id "
                        + "WHERE a.order_id = :value "
                        + "AND a.time >= :time "
                        + "AND a.bank_account = :bankAccount "
                        + "AND b.is_authenticated = true ", nativeQuery = true)
        List<TransReceiveResponseDTO> getTransByOrderId(
                        @Param(value = "value") String value,
                        @Param(value = "bankAccount") String bankAccount,
                        @Param(value = "time") long time);

        @Query(value = "SELECT a.reference_number as referenceNumber, a.order_id as orderId, a.amount, a.content, "
                + "a.trans_type as transType, a.status, a.type, a.time as timeCreated, a.time_paid as timePaid "
                + "FROM transaction_receive a "
                + "LEFT JOIN account_bank_receive b "
                + "ON a.bank_id = b.id "
                + "WHERE a.order_id = :value "
                + "AND a.time BETWEEN :fromDate AND :toDate "
                + "AND a.bank_account = :bankAccount "
                + "AND b.is_authenticated = true ", nativeQuery = true)
        List<TransReceiveResponseDTO> getTransByOrderId(
                @Param(value = "value") String value,
                @Param(value = "bankAccount") String bankAccount,
                @Param(value = "fromDate") long fromDate,
                @Param(value = "toDate") long toDate);

        // check
        @Query(value = "SELECT a.reference_number as referenceNumber, a.order_id as orderId, a.amount, a.content, "
                        + "a.trans_type as transType, a.status, a.type, a.time as timeCreated, a.time_paid as timePaid "
                        + "FROM transaction_receive a "
                        + "LEFT JOIN account_bank_receive b "
                        + "ON a.bank_id = b.id "
                        + "WHERE a.reference_number = :value "
                        + "AND a.bank_account = :bankAccount "
                        + "AND a.time >= :time "
                        + "AND b.is_authenticated = true ", nativeQuery = true)
        List<TransReceiveResponseDTO> getTransByReferenceNumber(
                        @Param(value = "value") String value,
                        @Param(value = "bankAccount") String bankAccount,
                        @Param(value = "time") long time);

        @Query(value = "SELECT a.reference_number as referenceNumber, a.order_id as orderId, a.amount, a.content, "
                + "a.trans_type as transType, a.status, a.type, a.time as timeCreated, a.time_paid as timePaid "
                + "FROM transaction_receive a "
                + "LEFT JOIN account_bank_receive b "
                + "ON a.bank_id = b.id "
                + "WHERE a.reference_number = :value "
                + "AND a.bank_account = :bankAccount "
                + "AND a.time BETWEEN :fromDate AND :toDate "
                + "AND b.is_authenticated = true ", nativeQuery = true)
        List<TransReceiveResponseDTO> getTransByReferenceNumber(
                @Param(value = "value") String value,
                @Param(value = "bankAccount") String bankAccount,
                @Param(value = "fromDate") long fromDate,
                @Param(value = "toDate") long toDate);

        // TransStatisticMerchantDTO
        // * sort by year, list by month, by customer_sync_id */
        @Query(value = "SELECT DATE_FORMAT(CONVERT_TZ(FROM_UNIXTIME(b.time), '+00:00', '+07:00'), '%Y-%m') as time, COUNT(b.id) AS totalTrans,  "
                        + "SUM(b.amount) AS totalAmount,  "
                        + "SUM(CASE WHEN b.trans_type = 'C' AND b.status = 1 THEN b.amount ELSE 0 END) AS totalCredit, "
                        + "SUM(CASE WHEN b.trans_type = 'D' AND b.status = 1 THEN b.amount ELSE 0 END) AS totalDebit, "
                        + "SUM(CASE WHEN b.trans_type = 'C' AND b.status = 1 THEN 1 ELSE 0 END) AS totalTransC,  "
                        + "SUM(CASE WHEN b.trans_type = 'D' AND b.status = 1 THEN 1 ELSE 0 END) AS totalTransD   "
                        + "FROM account_customer_bank a   "
                        + "LEFT JOIN transaction_receive b ON a.bank_id = b.bank_id   "
                        + "WHERE a.customer_sync_id = :id AND b.status = 1  "
                        + "AND DATE_FORMAT(CONVERT_TZ(FROM_UNIXTIME(b.time), '+00:00', '+07:00'), '%Y') = :value "
                        + "GROUP BY DATE_FORMAT(CONVERT_TZ(FROM_UNIXTIME(b.time), '+00:00', '+07:00'), '%Y-%m') "
                        + "ORDER BY time DESC", nativeQuery = true)
        List<TransStatisticMerchantDTO> getStatisticYearByMerchantId(
                        @Param(value = "id") String id,
                        @Param(value = "value") String value);

        // * sort by month, list by day, by customer_sync_id */
        @Query(value = "SELECT DATE_FORMAT(CONVERT_TZ(FROM_UNIXTIME(b.time), '+00:00', '+07:00'), '%Y-%m-%d') as time, COUNT(b.id) AS totalTrans,  "
                        + "SUM(b.amount) AS totalAmount,  "
                        + "SUM(CASE WHEN b.trans_type = 'C' AND b.status = 1 THEN b.amount ELSE 0 END) AS totalCredit, "
                        + "SUM(CASE WHEN b.trans_type = 'D' AND b.status = 1 THEN b.amount ELSE 0 END) AS totalDebit, "
                        + "SUM(CASE WHEN b.trans_type = 'C' AND b.status = 1 THEN 1 ELSE 0 END) AS totalTransC,  "
                        + "SUM(CASE WHEN b.trans_type = 'D' AND b.status = 1 THEN 1 ELSE 0 END) AS totalTransD   "
                        + "FROM account_customer_bank a   "
                        + "LEFT JOIN transaction_receive b ON a.bank_id = b.bank_id   "
                        + "WHERE a.customer_sync_id = :id AND b.status = 1  "
                        + "AND DATE_FORMAT(CONVERT_TZ(FROM_UNIXTIME(b.time), '+00:00', '+07:00'), '%Y-%m') = :value "
                        + "GROUP BY DATE_FORMAT(CONVERT_TZ(FROM_UNIXTIME(b.time), '+00:00', '+07:00'), '%Y-%m-%d') "
                        + "ORDER BY time DESC", nativeQuery = true)
        List<TransStatisticMerchantDTO> getStatisticMonthByMerchantId(
                        @Param(value = "id") String id,
                        @Param(value = "value") String value);

        // * sort by year, list by month, by bank_id */
        @Query(value = "SELECT DATE_FORMAT(CONVERT_TZ(FROM_UNIXTIME(time), '+00:00', '+07:00'), '%Y-%m') as time, COUNT(id) AS totalTrans, "
                        + "SUM(amount) AS totalAmount,  "
                        + "SUM(CASE WHEN trans_type = 'C' AND status = 1 THEN amount ELSE 0 END) AS totalCredit,   "
                        + "SUM(CASE WHEN trans_type = 'D' AND status = 1 THEN amount ELSE 0 END) AS totalDebit,  "
                        + "SUM(CASE WHEN trans_type = 'C' AND status = 1 THEN 1 ELSE 0 END) AS totalTransC,   "
                        + "SUM(CASE WHEN trans_type = 'D' AND status = 1 THEN 1 ELSE 0 END) AS totalTransD   "
                        + "FROM transaction_receive  "
                        + "WHERE bank_id = :id AND status = 1  "
                        + "AND DATE_FORMAT(CONVERT_TZ(FROM_UNIXTIME(time), '+00:00', '+07:00'), '%Y') = :value "
                        + "GROUP BY DATE_FORMAT(CONVERT_TZ(FROM_UNIXTIME(time), '+00:00', '+07:00'), '%Y-%m') "
                        + "ORDER BY time DESC ", nativeQuery = true)
        List<TransStatisticMerchantDTO> getStatisticYearByBankId(
                        @Param(value = "id") String id,
                        @Param(value = "value") String value);

        // * sort by month, list by day, by bank_id */
        @Query(value = "SELECT DATE_FORMAT(CONVERT_TZ(FROM_UNIXTIME(time), '+00:00', '+07:00'), '%Y-%m-%d') as time, COUNT(id) AS totalTrans, "
                        + "SUM(amount) AS totalAmount,  "
                        + "SUM(CASE WHEN trans_type = 'C' AND status = 1 THEN amount ELSE 0 END) AS totalCredit,   "
                        + "SUM(CASE WHEN trans_type = 'D' AND status = 1 THEN amount ELSE 0 END) AS totalDebit,  "
                        + "SUM(CASE WHEN trans_type = 'C' AND status = 1 THEN 1 ELSE 0 END) AS totalTransC,   "
                        + "SUM(CASE WHEN trans_type = 'D' AND status = 1 THEN 1 ELSE 0 END) AS totalTransD   "
                        + "FROM transaction_receive  "
                        + "WHERE bank_id = :id AND status = 1  "
                        + "AND DATE_FORMAT(CONVERT_TZ(FROM_UNIXTIME(time), '+00:00', '+07:00'), '%Y-%m') = :value "
                        + "GROUP BY DATE_FORMAT(CONVERT_TZ(FROM_UNIXTIME(time), '+00:00', '+07:00'), '%Y-%m-%d') "
                        + "ORDER BY time DESC ", nativeQuery = true)
        List<TransStatisticMerchantDTO> getStatisticMonthByBankId(
                        @Param(value = "id") String id,
                        @Param(value = "value") String value);

        // for service fee
        @Query(value = "SELECT COUNT(id) as totalTrans, SUM(amount) as totalAmount "
                        + "FROM transaction_receive "
                        + "WHERE bank_id = :bankId "
                        + "AND DATE_FORMAT(CONVERT_TZ(FROM_UNIXTIME(time), '+00:00', '+07:00'), '%Y-%m') = :month "
                        + "AND status = 1 ", nativeQuery = true)
        TransReceiveStatisticFeeDTO getTransStatisticForServiceFee(
                        @Param(value = "bankId") String bankId,
                        @Param(value = "month") String month);

        @Query(value = "SELECT COUNT(id) as totalTrans, SUM(amount) as totalAmount "
                        + "FROM transaction_receive "
                        + "WHERE bank_id = :bankId "
                        + "AND DATE_FORMAT(CONVERT_TZ(FROM_UNIXTIME(time), '+00:00', '+07:00'), '%Y-%m') = :month "
                        + "AND status = 1 AND type = 0 ", nativeQuery = true)
        TransReceiveStatisticFeeDTO getTransStatisticForServiceFeeWithSystemType(
                        @Param(value = "bankId") String bankId,
                        @Param(value = "month") String month);

        @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, a.content, a.order_id as orderId, a.reference_number as referenceNumber, "
                + "a.status, a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
                + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
                + "a.terminal_code as terminalCode, a.note "
                + "FROM transaction_receive a "
                + "LEFT JOIN account_bank_receive b "
                + "ON a.bank_id = b.id "
                + "LEFT JOIN bank_type c "
                + "ON b.bank_type_id = c.id "
                + "WHERE a.bank_account = :value "
                + "AND a.time >= :time "
                + "ORDER BY a.time DESC ", nativeQuery = true)
        List<TransactionReceiveAdminListDTO> exportTransByBankAccountAllDate(
                @Param(value = "value") String value,
                @Param(value = "time") long time);

        @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, a.content, a.order_id as orderId, a.reference_number as referenceNumber, "
                + "a.status, a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
                + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
                + "a.terminal_code as terminalCode, a.note "
                + "FROM transaction_receive a "
                + "LEFT JOIN account_bank_receive b "
                + "ON a.bank_id = b.id "
                + "LEFT JOIN bank_type c "
                + "ON b.bank_type_id = c.id "
                + "LEFT JOIN account_customer_bank d "
                + "ON b.id = d.bank_id  "
                + "WHERE a.time >= :time "
                + "AND a.terminal_code = :value "
                + "AND d.customer_sync_id = :merchantId "
                + "ORDER BY a.time DESC ", nativeQuery = true)
        List<TransactionReceiveAdminListDTO> exportTransFromDateByTerminalCodeAndMerchantId(
                @Param(value = "time") long time,
                @Param(value = "value") String value,
                @Param(value = "merchantId") String merchantId);

        @Query(value = "SELECT DISTINCT a.id as transactionId, a.amount as amount, a.bank_account as bankAccount "
                + ", a.content as content, a.time as time, a.time_paid as timePaid, a.status as status,"
                + " a.type as type, a.trans_type as transType "
                + "FROM transaction_receive a "
                + "INNER JOIN terminal c ON a.terminal_code = c.code "
                + "INNER JOIN account_bank_receive_share b ON (a.bank_id = b.bank_id AND b.terminal_id = c.id) "
                + "WHERE a.bank_id = :bankId AND a.status = :value "
                + "AND a.time >= :time AND b.user_id = :userId "
                + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
        List<TransactionRelatedDTO> getTransTerminalByStatus(
                String bankId, String userId, int value, int offset, long time);

        @Query(value = "SELECT DISTINCT a.id as transactionId, a.amount as amount, a.bank_account as bankAccount "
                + ", a.content as content, a.time as time, a.time_paid as timePaid, a.status as status,"
                + " a.type as type, a.trans_type as transType "
                + "FROM transaction_receive a "
                + "INNER JOIN terminal c ON a.terminal_code = c.code "
                + "INNER JOIN account_bank_receive_share b ON (a.bank_id = b.bank_id AND b.terminal_id = c.id) "
                + "WHERE a.bank_id = :bankId AND a.reference_number = :value "
                + "AND a.time >= :time AND b.user_id = :userId "
                + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
        List<TransactionRelatedDTO> getTransTerminalByFtCode(String bankId, String userId, String value, int offset, long time);

        @Query(value = "SELECT DISTINCT a.id as transactionId, a.amount as amount, a.bank_account as bankAccount "
                + ", a.content as content, a.time as time, a.time_paid as timePaid, a.status as status,"
                + " a.type as type, a.trans_type as transType "
                + "FROM transaction_receive a "
                + "INNER JOIN terminal c ON a.terminal_code = c.code "
                + "INNER JOIN account_bank_receive_share b ON (a.bank_id = b.bank_id AND b.terminal_id = c.id) "
                + "WHERE a.bank_id = :bankId AND a.order_id = :value "
                + "AND a.time >= :time AND b.user_id = :userId "
                + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
        List<TransactionRelatedDTO> getTransTerminalByOrderId(
                String bankId, String userId, String value, int offset, long time);

        @Query(value = "SELECT DISTINCT a.id as transactionId, a.amount as amount, a.bank_account as bankAccount "
                + ", a.content as content, a.time as time, a.time_paid as timePaid, a.status as status,"
                + " a.type as type, a.trans_type as transType "
                + "FROM transaction_receive a "
                + "INNER JOIN terminal c ON a.terminal_code = c.code "
                + "INNER JOIN account_bank_receive_share b ON (a.bank_id = b.bank_id AND b.terminal_id = c.id) "
                + "WHERE a.bank_id = :bankId AND a.content LIKE %:value% "
                + "AND a.time >= :time AND b.user_id = :userId "
                + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
        List<TransactionRelatedDTO> getTransTerminalByContent(
                String bankId, String userId, String value, int offset, long time);

        @Query(value = "SELECT DISTINCT a.id as transactionId, a.amount as amount, a.bank_account as bankAccount "
                + ", a.content as content, a.time as time, a.time_paid as timePaid, a.status as status,"
                + " a.type as type, a.trans_type as transType "
                + "FROM transaction_receive a "
                + "INNER JOIN terminal c ON a.terminal_code = c.code "
                + "INNER JOIN account_bank_receive_share b ON (a.bank_id = b.bank_id AND b.terminal_id = c.id) "
                + "WHERE a.bank_id = :bankId "
                + "AND a.time >= :time AND b.user_id = :userId "
                + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
        List<TransactionRelatedDTO> getAllTransTerminal(
                String bankId, String userId, int offset, long time);

        @Query(value = "SELECT DISTINCT a.id as transactionId, a.amount as amount, a.bank_account as bankAccount "
                + ", a.content as content, a.time as time, a.time_paid as timePaid, a.status as status,"
                + " a.type as type, a.trans_type as transType "
                + "FROM transaction_receive a "
                + "INNER JOIN terminal c ON a.terminal_code = c.code "
                + "INNER JOIN account_bank_receive_share b ON (a.bank_id = b.bank_id AND b.terminal_id = c.id) "
                + "WHERE a.bank_id = :bankId AND a.status = :value "
                + "AND a.time BETWEEN :fromDate AND :toDate AND b.user_id = :userId "
                + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
        List<TransactionRelatedDTO> getTransTerminalByStatus(String bankId, String userId, int value, int offset, long fromDate, long toDate);

        @Query(value = "SELECT DISTINCT a.id as transactionId, a.amount as amount, a.bank_account as bankAccount "
                + ", a.content as content, a.time as time, a.time_paid as timePaid, a.status as status,"
                + " a.type as type, a.trans_type as transType "
                + "FROM transaction_receive a "
                + "INNER JOIN terminal c ON a.terminal_code = c.code "
                + "INNER JOIN account_bank_receive_share b ON (a.bank_id = b.bank_id AND b.terminal_id = c.id) "
                + "WHERE a.bank_id = :bankId AND a.reference_number = :value "
                + "AND a.time BETWEEN :fromDate AND :toDate AND b.user_id = :userId "
                + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
        List<TransactionRelatedDTO> getTransTerminalByFtCode(String bankId, String userId, String value, int offset, long fromDate, long toDate);

        @Query(value = "SELECT DISTINCT a.id as transactionId, a.amount as amount, a.bank_account as bankAccount "
                + ", a.content as content, a.time as time, a.time_paid as timePaid, a.status as status,"
                + " a.type as type, a.trans_type as transType "
                + "FROM transaction_receive a "
                + "INNER JOIN terminal c ON a.terminal_code = c.code "
                + "INNER JOIN account_bank_receive_share b ON (a.bank_id = b.bank_id AND b.terminal_id = c.id) "
                + "WHERE a.bank_id = :bankId AND a.order_id = :value "
                + "AND a.time BETWEEN :fromDate AND :toDate AND b.user_id = :userId "
                + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
        List<TransactionRelatedDTO> getTransTerminalByOrderId(String bankId, String userId, String value, int offset, long fromDate, long toDate);

        @Query(value = "SELECT DISTINCT a.id as transactionId, a.amount as amount, a.bank_account as bankAccount "
                + ", a.content as content, a.time as time, a.time_paid as timePaid, a.status as status,"
                + " a.type as type, a.trans_type as transType "
                + "FROM transaction_receive a "
                + "INNER JOIN terminal c ON a.terminal_code = c.code "
                + "INNER JOIN account_bank_receive_share b ON (a.bank_id = b.bank_id AND b.terminal_id = c.id) "
                + "WHERE a.bank_id = :bankId AND a.content LIKE %:value% "
                + "AND a.time BETWEEN :fromDate AND :toDate AND b.user_id = :userId "
                + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
        List<TransactionRelatedDTO> getTransTerminalByContent(String bankId, String userId, String value, int offset, long fromDate, long toDate);

        @Query(value = "SELECT DISTINCT a.id as transactionId, a.amount as amount, a.bank_account as bankAccount "
                + ", a.content as content, a.time as time, a.time_paid as timePaid, a.status as status,"
                + " a.type as type, a.trans_type as transType "
                + "FROM transaction_receive a "
                + "INNER JOIN terminal c ON a.terminal_code = c.code "
                + "INNER JOIN account_bank_receive_share b ON (a.bank_id = b.bank_id AND b.terminal_id = c.id) "
                + "WHERE a.bank_id = :bankId "
                + "AND a.time BETWEEN :fromDate AND :toDate AND b.user_id = :userId "
                + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
        List<TransactionRelatedDTO> getAllTransTerminal(String bankId, String userId, int offset, long fromDate, long toDate);

        @Query(value = "SELECT DISTINCT a.id as transactionId, a.amount as amount, a.bank_account as bankAccount "
                + ", a.content as content, a.time as time, a.time_paid as timePaid, a.status as status,"
                + " a.type as type, a.trans_type as transType "
                + "FROM transaction_receive a "
                + "INNER JOIN account_bank_receive_share b ON a.bank_id = b.bank_id "
                + "INNER JOIN terminal c ON a.terminal_code = c.code "
                + "WHERE a.bank_id = :bankId AND a.terminal_code = :terminalCode AND a.status = :value "
                + "AND a.time >= :time AND b.user_id = :userId "
                + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
        List<TransactionRelatedDTO> getTransTerminalByStatus(String bankId, String userId, int value, String terminalCode, int offset, long time);

        @Query(value = "SELECT DISTINCT a.id as transactionId, a.amount as amount, a.bank_account as bankAccount "
                + ", a.content as content, a.time as time, a.time_paid as timePaid, a.status as status,"
                + " a.type as type, a.trans_type as transType "
                + "FROM transaction_receive a "
                + "INNER JOIN account_bank_receive_share b ON a.bank_id = b.bank_id "
                + "INNER JOIN terminal c ON a.terminal_code = c.code "
                + "WHERE a.bank_id = :bankId AND a.terminal_code = :terminalCode AND a.reference_number = :value "
                + "AND a.time >= :time AND b.user_id = :userId "
                + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
        List<TransactionRelatedDTO> getTransTerminalByFtCode(String bankId, String userId, String value, String terminalCode, int offset, long time);

        @Query(value = "SELECT DISTINCT a.id as transactionId, a.amount as amount, a.bank_account as bankAccount "
                + ", a.content as content, a.time as time, a.time_paid as timePaid, a.status as status,"
                + " a.type as type, a.trans_type as transType "
                + "FROM transaction_receive a "
                + "INNER JOIN account_bank_receive_share b ON a.bank_id = b.bank_id "
                + "INNER JOIN terminal c ON a.terminal_code = c.code "
                + "WHERE a.bank_id = :bankId AND a.terminal_code = :terminalCode AND a.order_id = :value "
                + "AND a.time >= :time AND b.user_id = :userId "
                + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
        List<TransactionRelatedDTO> getTransTerminalByOrderId(String bankId, String userId, String value, String terminalCode, int offset, long time);

        @Query(value = "SELECT DISTINCT a.id as transactionId, a.amount as amount, a.bank_account as bankAccount "
                + ", a.content as content, a.time as time, a.time_paid as timePaid, a.status as status,"
                + " a.type as type, a.trans_type as transType "
                + "FROM transaction_receive a "
                + "INNER JOIN account_bank_receive_share b ON a.bank_id = b.bank_id "
                + "INNER JOIN terminal c ON a.terminal_code = c.code "
                + "WHERE a.bank_id = :bankId AND a.terminal_code = :terminalCode AND a.content LIKE %:value% "
                + "AND a.time >= :time AND b.user_id = :userId "
                + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
        List<TransactionRelatedDTO> getTransTerminalByContent(String bankId, String userId, String value, String terminalCode, int offset, long time);

        @Query(value = "SELECT DISTINCT a.id as transactionId, a.amount as amount, a.bank_account as bankAccount "
                + ", a.content as content, a.time as time, a.time_paid as timePaid, a.status as status,"
                + " a.type as type, a.trans_type as transType "
                + "FROM transaction_receive a "
                + "INNER JOIN account_bank_receive_share b ON a.bank_id = b.bank_id "
                + "INNER JOIN terminal c ON a.terminal_code = c.code "
                + "WHERE a.bank_id = :bankId AND a.terminal_code = :terminalCode "
                + "AND a.time >= :time AND b.user_id = :userId "
                + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
        List<TransactionRelatedDTO> getAllTransTerminal(String bankId, String userId, String terminalCode, int offset, long time);

        @Query(value = "SELECT DISTINCT a.id as transactionId, a.amount as amount, a.bank_account as bankAccount "
                + ", a.content as content, a.time as time, a.time_paid as timePaid, a.status as status,"
                + " a.type as type, a.trans_type as transType "
                + "FROM transaction_receive a "
                + "INNER JOIN account_bank_receive_share b ON a.bank_id = b.bank_id "
                + "INNER JOIN terminal c ON a.terminal_code = c.code "
                + "WHERE a.bank_id = :bankId AND a.terminal_code = :terminalCode AND a.status = :value "
                + "AND a.time BETWEEN :fromDate AND :toDate AND b.user_id = :userId "
                + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
        List<TransactionRelatedDTO> getTransTerminalByStatus(String bankId, String userId, int value, String terminalCode, int offset, long fromDate, long toDate);

        @Query(value = "SELECT DISTINCT a.id as transactionId, a.amount as amount, a.bank_account as bankAccount "
                + ", a.content as content, a.time as time, a.time_paid as timePaid, a.status as status,"
                + " a.type as type, a.trans_type as transType "
                + "FROM transaction_receive a "
                + "INNER JOIN account_bank_receive_share b ON a.bank_id = b.bank_id "
                + "INNER JOIN terminal c ON a.terminal_code = c.code "
                + "WHERE a.bank_id = :bankId AND a.terminal_code = :terminalCode AND a.reference_number = :value "
                + "AND a.time BETWEEN :fromDate AND :toDate AND b.user_id = :userId "
                + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
        List<TransactionRelatedDTO> getTransTerminalByFtCode(String bankId, String userId, String value, String terminalCode, int offset, long fromDate, long toDate);

        @Query(value = "SELECT DISTINCT a.id as transactionId, a.amount as amount, a.bank_account as bankAccount "
                + ", a.content as content, a.time as time, a.time_paid as timePaid, a.status as status,"
                + " a.type as type, a.trans_type as transType "
                + "FROM transaction_receive a "
                + "INNER JOIN account_bank_receive_share b ON a.bank_id = b.bank_id "
                + "INNER JOIN terminal c ON a.terminal_code = c.code "
                + "WHERE a.bank_id = :bankId AND a.terminal_code = :terminalCode AND a.order_id = :value "
                + "AND a.time BETWEEN :fromDate AND :toDate AND b.user_id = :userId "
                + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
        List<TransactionRelatedDTO> getTransTerminalByOrderId(String bankId, String userId, String value, String terminalCode, int offset, long fromDate, long toDate);

        @Query(value = "SELECT DISTINCT a.id as transactionId, a.amount as amount, a.bank_account as bankAccount "
                + ", a.content as content, a.time as time, a.time_paid as timePaid, a.status as status,"
                + " a.type as type, a.trans_type as transType "
                + "FROM transaction_receive a "
                + "INNER JOIN account_bank_receive_share b ON a.bank_id = b.bank_id "
                + "INNER JOIN terminal c ON a.terminal_code = c.code "
                + "WHERE a.bank_id = :bankId AND a.terminal_code = :terminalCode AND a.content LIKE %:value% "
                + "AND a.time BETWEEN :fromDate AND :toDate AND b.user_id = :userId "
                + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
        List<TransactionRelatedDTO> getTransTerminalByContent(String bankId, String userId, String value, String terminalCode, int offset, long fromDate, long toDate);

        @Query(value = "SELECT DISTINCT a.id as transactionId, a.amount as amount, a.bank_account as bankAccount "
                + ", a.content as content, a.time as time, a.time_paid as timePaid, a.status as status,"
                + " a.type as type, a.trans_type as transType "
                + "FROM transaction_receive a "
                + "INNER JOIN account_bank_receive_share b ON a.bank_id = b.bank_id "
                + "INNER JOIN terminal c ON a.terminal_code = c.code "
                + "WHERE a.bank_id = :bankId AND a.terminal_code = :terminalCode "
                + "AND a.time BETWEEN :fromDate AND :toDate AND b.user_id = :userId "
                + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
        List<TransactionRelatedDTO> getAllTransTerminal(String bankId, String userId, String terminalCode, int offset, long fromDate, long toDate);

        @Query(value = "SELECT DATE_FORMAT(CONVERT_TZ(FROM_UNIXTIME(a.time), '+00:00', '+07:00'), '%Y-%m-%d') AS date, "
                + "COUNT(a.id) AS totalTrans, "
                + "SUM(CASE WHEN a.trans_type = 'C' THEN amount ELSE 0 END) AS totalCashIn, "
                + "SUM(CASE WHEN a.trans_type = 'D' THEN amount ELSE 0 END) AS totalCashOut, "
                + "SUM(CASE WHEN a.trans_type = 'C' THEN 1 ELSE 0 END) AS totalTransC, "
                + "SUM(CASE WHEN a.trans_type = 'D' THEN 1 ELSE 0 END) AS totalTransD "
                + "FROM transaction_receive a "
                + "INNER JOIN terminal b ON b.code = a.terminal_code "
                + "INNER JOIN account_bank_receive_share c ON c.terminal_id = b.id "
                + "WHERE c.bank_id = :bankId AND a.status = 1 "
                + "AND c.user_id = :userId "
                + "AND a.time BETWEEN :fromDate AND :toDate "
                + "GROUP BY date ORDER BY date DESC ", nativeQuery = true)
        List<TransStatisticByDateDTO> getTransStatisticByTerminalId(String bankId, String userId, long fromDate, long toDate);

        @Query(value = "SELECT DATE_FORMAT(CONVERT_TZ(FROM_UNIXTIME(time), '+00:00', '+07:00'), '%Y-%m-%d') AS date, "
                + "COUNT(id) AS totalTrans, "
                + "SUM(CASE WHEN trans_type = 'C' THEN amount ELSE 0 END) AS totalCashIn, "
                + "SUM(CASE WHEN trans_type = 'D' THEN amount ELSE 0 END) AS totalCashOut, "
                + "SUM(CASE WHEN trans_type = 'C' THEN 1 ELSE 0 END) AS totalTransC, "
                + "SUM(CASE WHEN trans_type = 'D' THEN 1 ELSE 0 END) AS totalTransD "
                + "FROM transaction_receive "
                + "WHERE bank_id = :bankId AND status = 1 "
                + "AND time BETWEEN :fromDate AND :toDate "
                + "GROUP BY date ORDER BY date DESC ", nativeQuery = true)
        List<TransStatisticByDateDTO> getTransStatisticByBankId(String bankId, long fromDate, long toDate);

        @Query(value = "SELECT COUNT(a.id) AS totalTrans, "
                + "SUM(CASE WHEN a.trans_type = 'C' THEN a.amount ELSE 0 END) AS totalCashIn, "
                + "SUM(CASE WHEN a.trans_type = 'D' THEN a.amount ELSE 0 END) AS totalCashOut, "
                + "SUM(CASE WHEN a.trans_type = 'C' THEN 1 ELSE 0 END) AS totalTransC, "
                + "SUM(CASE WHEN a.trans_type = 'D' THEN 1 ELSE 0 END) AS totalTransD "
                + "FROM transaction_receive a "
                + "INNER JOIN terminal b ON b.code = a.terminal_code "
                + "INNER JOIN account_bank_receive_share c ON c.terminal_id = b.id "
                + "WHERE c.bank_id = :bankId AND a.status = 1 "
                + "AND c.user_id = :userId "
                + "AND a.time BETWEEN :fromDate AND :toDate "
                + "AND b.code = :terminalCode", nativeQuery = true)
        TransStatisticDTO getTransactionOverview(String bankId, String terminalCode, String userId, long fromDate, long toDate);

        @Query(value = "SELECT COUNT(a.id) AS totalTrans, "
                + "SUM(CASE WHEN a.trans_type = 'C' THEN a.amount ELSE 0 END) AS totalCashIn, "
                + "SUM(CASE WHEN a.trans_type = 'D' THEN a.amount ELSE 0 END) AS totalCashOut, "
                + "SUM(CASE WHEN a.trans_type = 'C' THEN 1 ELSE 0 END) AS totalTransC, "
                + "SUM(CASE WHEN a.trans_type = 'D' THEN 1 ELSE 0 END) AS totalTransD "
                + "FROM transaction_receive a "
                + "INNER JOIN terminal b ON b.code = a.terminal_code "
                + "INNER JOIN account_bank_receive_share c ON c.terminal_id = b.id "
                + "WHERE c.bank_id = :bankId AND a.status = 1 "
                + "AND c.user_id = :userId "
                + "AND a.time BETWEEN :fromDate AND :toDate ", nativeQuery = true)
        TransStatisticDTO getTransactionOverview(String bankId, String userId, long fromDate, long toDate);

        @Query(value = "SELECT DATE_FORMAT(CONVERT_TZ(FROM_UNIXTIME(time), '+00:00', '+07:00'), '%Y-%m-%d') AS date, "
                + "COUNT(id) AS totalTrans, "
                + "SUM(CASE WHEN trans_type = 'C' THEN amount ELSE 0 END) AS totalCashIn, "
                + "SUM(CASE WHEN trans_type = 'D' THEN amount ELSE 0 END) AS totalCashOut, "
                + "SUM(CASE WHEN trans_type = 'C' THEN 1 ELSE 0 END) AS totalTransC, "
                + "SUM(CASE WHEN trans_type = 'D' THEN 1 ELSE 0 END) AS totalTransD "
                + "FROM transaction_receive "
                + "WHERE bank_id = :bankId AND status = 1 "
                + "AND terminal_code = :terminalCode "
                + "AND time BETWEEN :fromDate AND :toDate "
                + "GROUP BY date ORDER BY date DESC ", nativeQuery = true)
        List<TransStatisticByDateDTO> getTransStatisticByTerminalIdNotSync(String bankId,
                                                                           String terminalCode,
                                                                           long fromDate,
                                                                           long toDate);

        @Query(value = "SELECT COUNT(id) AS totalTrans, "
                + "SUM(CASE WHEN trans_type = 'C' THEN amount ELSE 0 END) AS totalCashIn, "
                + "SUM(CASE WHEN trans_type = 'D' THEN amount ELSE 0 END) AS totalCashOut, "
                + "SUM(CASE WHEN trans_type = 'C' THEN 1 ELSE 0 END) AS totalTransC, "
                + "SUM(CASE WHEN trans_type = 'D' THEN 1 ELSE 0 END) AS totalTransD "
                + "FROM transaction_receive "
                + "WHERE bank_id = :bankId AND status = 1 "
                + "AND terminal_code = :terminalCode "
                + "AND time BETWEEN :fromDate AND :toDate", nativeQuery = true)
        TransStatisticDTO getTransactionOverviewNotSync(String bankId, String terminalCode, long fromDate, long toDate);

        @Query(value = "SELECT DATE_FORMAT(CONVERT_TZ(FROM_UNIXTIME((FLOOR(time / 3600))*3600), '+00:00', '+07:00'), '%Y-%m-%d %H:00') AS timeDate, "
                + "COUNT(id) AS totalTrans, "
                + "SUM(CASE WHEN trans_type = 'C' THEN amount ELSE 0 END) AS totalCashIn, "
                + "SUM(CASE WHEN trans_type = 'D' THEN amount ELSE 0 END) AS totalCashOut, "
                + "SUM(CASE WHEN trans_type = 'C' THEN 1 ELSE 0 END) AS totalTransC, "
                + "SUM(CASE WHEN trans_type = 'D' THEN 1 ELSE 0 END) AS totalTransD "
                + "FROM transaction_receive "
                + "WHERE bank_id = :bankId AND status = 1 "
                + "AND time BETWEEN :fromDate AND :toDate "
                + "GROUP BY timeDate ORDER BY timeDate DESC ", nativeQuery = true)
        List<TransStatisticByTimeDTO> getTransStatisticByBankIdAndDate(String bankId, long fromDate, long toDate);

        @Query(value = "SELECT DATE_FORMAT(CONVERT_TZ(FROM_UNIXTIME((FLOOR(a.time / 3600))*3600), '+00:00', '+07:00'), '%Y-%m-%d %H:00') AS timeDate, "
                + "COUNT(a.id) AS totalTrans, "
                + "SUM(CASE WHEN a.trans_type = 'C' THEN amount ELSE 0 END) AS totalCashIn, "
                + "SUM(CASE WHEN a.trans_type = 'D' THEN amount ELSE 0 END) AS totalCashOut, "
                + "SUM(CASE WHEN a.trans_type = 'C' THEN 1 ELSE 0 END) AS totalTransC, "
                + "SUM(CASE WHEN a.trans_type = 'D' THEN 1 ELSE 0 END) AS totalTransD "
                + "FROM transaction_receive a "
                + "INNER JOIN terminal b ON b.code = a.terminal_code "
                + "INNER JOIN account_bank_receive_share c ON c.terminal_id = b.id "
                + "WHERE c.bank_id = :bankId AND a.status = 1 "
                + "AND c.user_id = :userId "
                + "AND a.time BETWEEN :fromDate AND :toDate "
                + "GROUP BY timeDate ORDER BY timeDate DESC ", nativeQuery = true)
        List<TransStatisticByTimeDTO> getTransStatisticByTerminalIdAndDate(String bankId, String userId, long fromDate, long toDate);

        @Query(value = "SELECT DATE_FORMAT(CONVERT_TZ(FROM_UNIXTIME((FLOOR(a.time / 3600))*3600), '+00:00', '+07:00'), '%Y-%m-%d %H:00') AS timeDate, "
                + "COUNT(a.id) AS totalTrans, "
                + "SUM(CASE WHEN a.trans_type = 'C' THEN amount ELSE 0 END) AS totalCashIn, "
                + "SUM(CASE WHEN a.trans_type = 'D' THEN amount ELSE 0 END) AS totalCashOut, "
                + "SUM(CASE WHEN a.trans_type = 'C' THEN 1 ELSE 0 END) AS totalTransC, "
                + "SUM(CASE WHEN a.trans_type = 'D' THEN 1 ELSE 0 END) AS totalTransD "
                + "FROM transaction_receive a "
                + "INNER JOIN terminal b ON b.code = a.terminal_code "
                + "INNER JOIN account_bank_receive_share c ON c.terminal_id = b.id "
                + "WHERE c.bank_id = :bankId AND a.status = 1 "
                + "AND b.code = :terminalCode "
                + "AND c.user_id = :userId "
                + "AND a.time BETWEEN :fromDate AND :toDate "
                + "GROUP BY timeDate ORDER BY timeDate DESC ", nativeQuery = true)
        List<TransStatisticByTimeDTO> getTransStatisticByTerminalIdAndDate(String bankId, String terminalCode, String userId, long fromDate, long toDate);

        @Query(value = "SELECT DATE_FORMAT(CONVERT_TZ(FROM_UNIXTIME((FLOOR(time / 3600))*3600), '+00:00', '+07:00'), '%Y-%m-%d %H:00') AS timeDate, "
                + "COUNT(id) AS totalTrans, "
                + "SUM(CASE WHEN trans_type = 'C' THEN amount ELSE 0 END) AS totalCashIn, "
                + "SUM(CASE WHEN trans_type = 'D' THEN amount ELSE 0 END) AS totalCashOut, "
                + "SUM(CASE WHEN trans_type = 'C' THEN 1 ELSE 0 END) AS totalTransC, "
                + "SUM(CASE WHEN trans_type = 'D' THEN 1 ELSE 0 END) AS totalTransD "
                + "FROM transaction_receive "
                + "WHERE bank_id = :bankId AND status = 1 "
                + "AND terminal_code = :terminalCode "
                + "AND time BETWEEN :fromDate AND :toDate "
                + "GROUP BY timeDate ORDER BY timeDate DESC ", nativeQuery = true)
        List<TransStatisticByTimeDTO> getTransStatisticByTerminalIdNotSyncByDate(String bankId, String terminalCode, long fromDate, long toDate);

        @Query(value = "SELECT DISTINCT a.id AS transactionId, a.amount AS amount, a.bank_account AS bankAccount "
                + ", a.content AS content, a.time AS time, a.time_paid AS timePaid, a.status AS status, "
                + "a.type AS type, d.bank_name AS bankName, d.bank_short_name AS bankShortName, "
                + "d.bank_code AS bankCode, a.note AS note, a.reference_number AS referenceNumber, "
                + "a.order_id AS orderId, a.terminal_code AS terminalCode "
                + "FROM transaction_receive a "
                + "INNER JOIN terminal b ON a.terminal_code = b.code "
                + "INNER JOIN account_bank_receive c ON a.bank_id = c.id "
                + "INNER JOIN bank_type d ON d.id = c.bank_type_id "
                + "WHERE b.id = :terminalId AND a.reference_number = :value "
                + "AND a.time BETWEEN :fromDate AND :toDate "
                + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
        List<ITransactionRelatedDetailDTO> getTransTerminalByIdAndByFtCode(String terminalId, String value,
                                                                      long fromDate, long toDate, int offset);

        @Query(value = "SELECT DISTINCT a.id AS transactionId, a.amount AS amount, a.bank_account AS bankAccount "
                + ", a.content AS content, a.time AS time, a.time_paid AS timePaid, a.status AS status, "
                + "a.type AS type, d.bank_name AS bankName, d.bank_short_name AS bankShortName, "
                + "d.bank_code AS bankCode, a.note AS note, a.reference_number AS referenceNumber, "
                + "a.order_id AS orderId, a.terminal_code AS terminalCode "
                + "FROM transaction_receive a "
                + "INNER JOIN terminal b ON a.terminal_code = b.code "
                + "INNER JOIN account_bank_receive c ON a.bank_id = c.id "
                + "INNER JOIN bank_type d ON d.id = c.bank_type_id "
                + "WHERE b.id = :terminalId AND a.order_id = :value "
                + "AND a.time BETWEEN :fromDate AND :toDate "
                + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
        List<ITransactionRelatedDetailDTO> getTransTerminalByIdAndByOrderId(String terminalId, String value,
                                                                           long fromDate, long toDate, int offset);

        @Query(value = "SELECT DISTINCT a.id AS transactionId, a.amount AS amount, a.bank_account AS bankAccount "
                + ", a.content AS content, a.time AS time, a.time_paid AS timePaid, a.status AS status, "
                + "a.type AS type, d.bank_name AS bankName, d.bank_short_name AS bankShortName, "
                + "d.bank_code AS bankCode, a.note AS note, a.reference_number AS referenceNumber, "
                + "a.order_id AS orderId, a.terminal_code AS terminalCode "
                + "FROM transaction_receive a "
                + "INNER JOIN terminal b ON a.terminal_code = b.code "
                + "INNER JOIN account_bank_receive c ON a.bank_id = c.id "
                + "INNER JOIN bank_type d ON d.id = c.bank_type_id "
                + "WHERE b.id = :terminalId AND a.content LIKE %:value% "
                + "AND a.time BETWEEN :fromDate AND :toDate "
                + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
        List<ITransactionRelatedDetailDTO> getTransTerminalByIdAndByContent(String terminalId, String value,
                                                                           long fromDate, long toDate, int offset);

        @Query(value = "SELECT DISTINCT a.id AS transactionId, a.amount AS amount, a.bank_account AS bankAccount "
                + ", a.content AS content, a.time AS time, a.time_paid AS timePaid, a.status AS status, "
                + "a.type AS type, d.bank_name AS bankName, d.bank_short_name AS bankShortName, "
                + "d.bank_code AS bankCode, a.note AS note, a.reference_number AS referenceNumber, "
                + "a.order_id AS orderId, a.terminal_code AS terminalCode "
                + "FROM transaction_receive a "
                + "INNER JOIN terminal b ON a.terminal_code = b.code "
                + "INNER JOIN account_bank_receive c ON a.bank_id = c.id "
                + "INNER JOIN bank_type d ON d.id = c.bank_type_id "
                + "WHERE b.id = :terminalId AND a.status = :status "
                + "AND a.time BETWEEN :fromDate AND :toDate "
                + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
        List<ITransactionRelatedDetailDTO> getTransTerminalByIdAndByStatus(String terminalId, int status,
                                                                          long fromDate, long toDate, int offset);

        @Query(value = "SELECT DISTINCT a.id AS transactionId, a.amount AS amount, a.bank_account AS bankAccount "
                + ", a.content AS content, a.time AS time, a.time_paid AS timePaid, a.status AS status, "
                + "a.type AS type, d.bank_name AS bankName, d.bank_short_name AS bankShortName, "
                + "d.bank_code AS bankCode, a.note AS note, a.reference_number AS referenceNumber, "
                + "a.order_id AS orderId, a.terminal_code AS terminalCode "
                + "FROM transaction_receive a "
                + "INNER JOIN terminal b ON a.terminal_code = b.code "
                + "INNER JOIN account_bank_receive c ON a.bank_id = c.id "
                + "INNER JOIN bank_type d ON d.id = c.bank_type_id "
                + "WHERE b.id = :terminalId "
                + "AND a.time BETWEEN :fromDate AND :toDate "
                + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
        List<ITransactionRelatedDetailDTO> getAllTransTerminalById(String terminalId, long fromDate, long toDate, int offset);

        @Transactional
        @Modifying
        @Query(value = "INSERT INTO transaction_receive (id, amount, bank_account, bank_id, content, " +
                "customer_bank_account, customer_bank_code, customer_name, order_id, " +
                "ref_id, reference_number, sign, status, time, time_paid, trace_id, trans_type, " +
                "type, terminal_code, qr_code, user_id, note) " +
                "SELECT :id, :amount, :bankAccount, :bankId, :content, :customerBankAccount, :customerBankCode, " +
                ":customerName, :orderId, :refId, :referenceNumber, :sign, :status, " +
                ":time, :timePaid, :traceId, :transType, :type, :terminalCode, :qrCode, :userId, :note " +
                "WHERE NOT EXISTS (" +
                "SELECT 1 " +
                "FROM transaction_receive " +
                "WHERE reference_number = :referenceNumber " +
                "AND trans_type = :transType) " +
                "LIMIT 1;", nativeQuery = true)
        int insertWithCheckDuplicated(String id, long amount, String bankAccount, String bankId, String content,
                                      String customerBankAccount, String customerBankCode, String customerName,
                                      String orderId, String refId, String referenceNumber, String sign, int status,
                                      long time, long timePaid, String traceId, String transType, int type,
                                      String terminalCode, String qrCode, String userId, String note);
}

