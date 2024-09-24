package com.vietqr.org.repository;

import java.util.List;
import javax.transaction.Transactional;

import com.vietqr.org.dto.*;
import com.vietqr.org.dto.bidv.CustomerInvoiceInfoDataDTO;
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
    @Query(value = "UPDATE transaction_receive SET status = :status, ref_id = :refId, " +
            "reference_number = :referenceNumber, time_paid = :timePaid WHERE id = :id", nativeQuery = true)
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

    @Query(value = "SELECT a.id as transactionId,a.amount, a.bank_account as bankAccount, a.content, a.time, "
            + "a.time_paid as timePaid, a.status, a.type, a.trans_type as transType, "
            + "a.reference_number as referenceNumber, a.terminal_code as terminalCode, "
            + "a.note, a.order_id as orderId "
            + "FROM transaction_receive a "
            + "WHERE a.bank_id=:bankId AND a.status != 2 "
            + "AND a.time >= :time "
            + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
    List<TransactionRelatedDTO> getTransactions(@Param(value = "offset") int offset,
                                                @Param(value = "bankId") String bankId,
                                                @Param(value = "time") long time);

    @Query(value = "SELECT a.id as transactionId ,a.amount, a.bank_account as bankAccount, a.content, a.time, "
            + "a.time_paid as timePaid, a.status, a.type, a.trans_type as transType, "
            + "a.reference_number as referenceNumber, a.terminal_code as terminalCode, a.sub_code AS subCode, "
            + "a.note, a.order_id as orderId "
            + "FROM transaction_receive a "
            + "WHERE a.bank_id=:bankId AND a.status != 2 "
            + "AND a.time BETWEEN :fromDate AND :toDate "
            + "AND (a.time_paid BETWEEN :fromTimePaid AND :toTimePaid OR a.time_paid = 0) "
            + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
    List<TransactionRelatedDTO> getTransactions(@Param(value = "offset") int offset,
                                                @Param(value = "bankId") String bankId,
                                                @Param(value = "fromDate") long fromDate,
                                                @Param(value = "toDate") long toDate,
                                                @Param(value = "fromTimePaid") long fromTimePaid,
                                                @Param(value = "toTimePaid") long toTimePaid);

    @Query(value = "SELECT a.id as transactionId, a.amount, "
            + "a.bank_account as bankAccount, a.content, a.time, a.time_paid as timePaid, "
            + "a.status, a.type, a.trans_type as transType, "
            + "a.reference_number as referenceNumber, a.terminal_code as terminalCode, a.sub_code AS subCode, "
            + "a.note, a.order_id as orderId "
            + "FROM transaction_receive a "
            + "WHERE a.bank_id = :bankId AND a.status = :status "
            + "AND a.time BETWEEN :fromDate AND :toDate "
            + "AND (a.time_paid BETWEEN :fromTimePaid AND :toTimePaid OR a.time_paid = 0) "
            + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
    List<TransactionRelatedDTO> getTransactionsByStatus(@Param(value = "status") int status,
                                                        @Param(value = "offset") int offset,
                                                        @Param(value = "bankId") String bankId,
                                                        @Param(value = "fromDate") long fromDate,
                                                        @Param(value = "toDate") long toDate,
                                                        @Param(value = "fromTimePaid") long fromTimePaid,
                                                        @Param(value = "toTimePaid") long toTimePaid);

    @Query(value = "SELECT a.id as transactionId,a.amount, a.bank_account as bankAccount, a.content, a.time, "
            + "a.time_paid as timePaid,a.status,a.type,a.trans_type as transType, "
            + "a.reference_number as referenceNumber, a.terminal_code as terminalCode, "
            + "a.note, a.order_id as orderId "
            + "FROM transaction_receive a "
            + "WHERE a.bank_id= :bankId AND a.status = :status "
            + "AND a.time >= :time "
            + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
    List<TransactionRelatedDTO> getTransactionsByStatus(@Param(value = "status") int status,
                                                        @Param(value = "offset") int offset,
                                                        @Param(value = "bankId") String bankId,
                                                        @Param(value = "time") long time);

    @Query(value = "SELECT a.id as transactionId,a.amount, a.bank_account as bankAccount,a.content,a.time, "
            + "a.time_paid as timePaid,a.status,a.type,a.trans_type as transType, "
            + "a.reference_number as referenceNumber, a.terminal_code as terminalCode, a.sub_code AS subCode, "
            + "a.note, a.order_id as orderId "
            + "FROM transaction_receive a "
            + "WHERE a.bank_id= :bankId AND a.reference_number = :value "
            + "AND a.time BETWEEN :fromDate AND :toDate "
            + "AND (a.time_paid BETWEEN :fromTimePaid AND :toTimePaid OR a.time_paid = 0) "
            + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
    List<TransactionRelatedDTO> getTransactionsByFtCode(@Param(value = "value") String value,
                                                        @Param(value = "offset") int offset,
                                                        @Param(value = "bankId") String bankId,
                                                        @Param(value = "fromDate") long fromDate,
                                                        @Param(value = "toDate") long toDate,
                                                        @Param(value = "fromTimePaid") long fromTimePaid,
                                                        @Param(value = "toTimePaid") long toTimePaid);

    @Query(value = "SELECT a.id as transactionId,a.amount, a.bank_account as bankAccount,a.content,a.time, "
            + "a.time_paid as timePaid,a.status,a.type,a.trans_type as transType, "
            + "a.reference_number as referenceNumber, a.terminal_code as terminalCode, "
            + "a.note, a.order_id as orderId "
            + "FROM transaction_receive a "
            + "WHERE a.bank_id= :bankId AND a.reference_number = :value "
            + "AND a.time >= :time "
            + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
    List<TransactionRelatedDTO> getTransactionsByFtCode(@Param(value = "value") String value,
                                                        @Param(value = "offset") int offset,
                                                        @Param(value = "bankId") String bankId,
                                                        @Param(value = "time") long time);

    @Query(value = "SELECT a.id as transactionId,a.amount, a.bank_account as bankAccount, a.content, a.time, "
            + "a.time_paid as timePaid,a.status,a.type,a.trans_type as transType, "
            + "a.reference_number as referenceNumber, a.terminal_code as terminalCode, a.sub_code AS subCode, "
            + "a.note, a.order_id as orderId "
            + "FROM transaction_receive a "
            + "WHERE a.bank_id = :bankId AND a.order_id = :value "
            + "AND a.time BETWEEN :fromDate AND :toDate "
            + "AND (a.time_paid BETWEEN :fromTimePaid AND :toTimePaid OR a.time_paid = 0) "
            + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
    List<TransactionRelatedDTO> getTransactionsByOrderId(
            @Param(value = "value") String value,
            @Param(value = "offset") int offset,
            @Param(value = "bankId") String bankId,
            @Param(value = "fromDate") long fromDate,
            @Param(value = "toDate") long toDate,
            @Param(value = "fromTimePaid") long fromTimePaid,
            @Param(value = "toTimePaid") long toTimePaid);

    @Query(value = "SELECT a.id as transactionId,a.amount, a.bank_account as bankAccount,a.content, a.time, "
            + "a.time_paid as timePaid,a.status,a.type,a.trans_type as transType, "
            + "a.reference_number as referenceNumber, a.terminal_code as terminalCode, "
            + "a.note, a.order_id as orderId "
            + "FROM transaction_receive a "
            + "WHERE a.bank_id= :bankId AND a.order_id = :value "
            + "AND a.time >= :time "
            + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
    List<TransactionRelatedDTO> getTransactionsByOrderId(
            @Param(value = "value") String value,
            @Param(value = "offset") int offset,
            @Param(value = "bankId") String bankId,
            @Param(value = "time") long time);

    @Query(value = "SELECT a.id as transactionId,a.amount, a.bank_account as bankAccount,a.content,"
            + "a.time, a.time_paid as timePaid,a.status,a.type,a.trans_type as transType, "
            + "a.reference_number as referenceNumber, a.terminal_code as terminalCode, a.sub_code AS subCode, "
            + "a.note, a.order_id as orderId "
            + "FROM transaction_receive a "
            + "WHERE a.bank_id= :bankId AND a.content LIKE %:value% "
            + "AND a.time BETWEEN :fromDate AND :toDate "
            + "AND (a.time_paid BETWEEN :fromTimePaid AND :toTimePaid OR a.time_paid = 0) "
            + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
    List<TransactionRelatedDTO> getTransactionsByContent(
            @Param(value = "value") String value,
            @Param(value = "offset") int offset,
            @Param(value = "bankId") String bankId,
            @Param(value = "fromDate") long fromDate,
            @Param(value = "toDate") long toDate,
            @Param(value = "fromTimePaid") long fromTimePaid,
            @Param(value = "toTimePaid") long toTimePaid);

    @Query(value = "SELECT a.id as transactionId,a.amount, a.bank_account as bankAccount,a.content, a.time, "
            + "a.time_paid as timePaid, a.status, a.type, a.trans_type as transType, "
            + "a.reference_number as referenceNumber, a.terminal_code as terminalCode, "
            + "a.note, a.order_id as orderId "
            + "FROM transaction_receive a "
            + "WHERE a.bank_id= :bankId AND a.content LIKE %:value% "
            + "AND a.time >= :time "
            + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
    List<TransactionRelatedDTO> getTransactionsByContent(
            @Param(value = "value") String value,
            @Param(value = "offset") int offset,
            @Param(value = "bankId") String bankId,
            @Param(value = "time") long time);

    @Query(value = "SELECT a.id as transactionId,a.amount, a.bank_account as bankAccount, a.content, a.time, "
            + "a.time_paid as timePaid, a.status, a.type,a.trans_type as transType, "
            + "a.reference_number as referenceNumber, a.terminal_code as terminalCode, a.sub_code AS subCode, "
            + "a.note, a.order_id as orderId "
            + "FROM transaction_receive a "
            + "WHERE a.bank_id = :bankId AND a.terminal_code LIKE %:value% "
            + "AND a.time BETWEEN :fromDate AND :toDate "
            + "AND (a.time_paid BETWEEN :fromTimePaid AND :toTimePaid OR a.time_paid = 0) "
            + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
    List<TransactionRelatedDTO> getTransactionsByTerminalCode(
            @Param(value = "value") String value,
            @Param(value = "offset") int offset,
            @Param(value = "fromDate") long fromDate,
            @Param(value = "toDate") long toDate,
            @Param(value = "fromTimePaid") long fromTimePaid,
            @Param(value = "toTimePaid") long toTimePaid,
            @Param(value = "bankId") String bankId);

    @Query(value = "SELECT a.id as transactionId,a.amount, a.bank_account as bankAccount, a.content, a.time, "
            + "a.time_paid as timePaid, a.status, a.type,a.trans_type as transType, "
            + "a.reference_number as referenceNumber, a.terminal_code as terminalCode, a.sub_code AS subCode, "
            + "a.note, a.order_id as orderId "
            + "FROM transaction_receive a "
            + "WHERE a.bank_id = :bankId AND a.sub_code LIKE %:value% "
            + "AND a.time BETWEEN :fromDate AND :toDate "
            + "AND (a.time_paid BETWEEN :fromTimePaid AND :toTimePaid OR a.time_paid = 0) "
            + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
    List<TransactionRelatedDTO> getTransactionsBySubCode(
            @Param(value = "value") String value,
            @Param(value = "offset") int offset,
            @Param(value = "fromDate") long fromDate,
            @Param(value = "toDate") long toDate,
            @Param(value = "fromTimePaid") long fromTimePaid,
            @Param(value = "toTimePaid") long toTimePaid,
            @Param(value = "bankId") String bankId);

    @Query(value = "SELECT a.id as transactionId, a.amount, a.bank_account as bankAccount, a.content, a.time, "
            + "a.time_paid as timePaid, a.status, a.type, a.trans_type as transType, "
            + "a.reference_number as referenceNumber, a.terminal_code as terminalCode, "
            + "a.note, a.order_id as orderId "
            + "FROM transaction_receive a "
            + "WHERE a.bank_id= :bankId AND a.terminal_code LIKE %:value% "
            + "AND a.time >= :time "
            + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
    List<TransactionRelatedDTO> getTransactionsByTerminalCode(
            @Param(value = "value") String value,
            @Param(value = "offset") int offset,
            @Param(value = "bankId") String bankId,
            @Param(value = "time") long time);

    @Query(value = "SELECT a.id, a.amount, a.bank_id as bankId, b.bank_account as bankAccount, a.content, a.ref_id as refId, a.status, " +
            "a.time, a.time_paid as timePaid, a.type, a.trace_id as traceId, a.trans_type as transType, b.bank_account_name as bankAccountName, " +
            "c.bank_code as bankCode, c.bank_name as bankName, c.img_id as imgId, a.reference_number as referenceNumber, "
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

//        @Query(value = "SELECT * "
//                        + "FROM transaction_receive a "
//                        + "WHERE a.id = :id ", nativeQuery = true)
//        TransactionReceiveEntity getTransactionReceiveById(String id);

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

    @Query(value = "SELECT COUNT(a.id) AS totalCount, SUM(a.amount) AS totalAmount, a.bank_id, a.time "
            + "FROM transaction_receive a "
            + "WHERE a.time BETWEEN :startTime AND :endTime "
            + "AND a.bankId IN (:bankIds) AND a.status = 1 "
            + "AND a.trans_type = 'C' AND (a.type = 0 OR a.type = 1) GROUP BY a.bank_id ", nativeQuery = true)
    List<FeePackageResponseDTO> getFeePackageResponse(@Param(value = "startTime") long startTime,
                                                      @Param(value = "endTime") long endTime,
                                                      @Param(value = "bankIds") List<String> bankIds);

    @Query(value = "SELECT * FROM transaction_receive WHERE order_id = :orderId AND time >= :time ", nativeQuery = true)
    TransactionReceiveEntity getTransactionReceiveByOrderId(
            @Param(value = "orderId") String orderId,
            @Param(value = "time") long time);

    @Query(value = "SELECT * FROM transaction_receive WHERE "
            + "bill_id = :billId AND time >= :time ", nativeQuery = true)
    TransactionReceiveEntity getTransactionReceiveByBillId(
            @Param(value = "billId") String billId,
            @Param(value = "time") long time);

    @Query(value = "SELECT a.amount, a.bank_account as bankAccount, c.bank_name as bankName, a.time, a.content, "
            + "a.reference_number as referenceNumber, a.trans_type as transType, a.status, "
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
            + "INNER JOIN account_bank_receive_share c ON (c.terminal_id = b.id AND a.bank_id = c.bank_id) "
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
    @Query(value = "SELECT DISTINCT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, a.content, a.order_id as orderId, a.reference_number as referenceNumber, "
            + "a.status, a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
            + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
            + "a.terminal_code as terminalCode, a.note "
            + "FROM transaction_receive a "
            + "LEFT JOIN account_bank_receive b "
            + "ON a.bank_id = b.id "
            + "LEFT JOIN bank_type c "
            + "ON b.bank_type_id = c.id "
            + "LEFT JOIN terminal d "
            + "ON a.terminal_code = d.code "
            + "LEFT JOIN account_bank_receive_share e "
            + "ON (a.bank_id = e.bank_id AND e.terminal_id = d.id) "
            + "WHERE a.reference_number LIKE %:value% "
            + "AND (e.user_id = :userId OR a.user_id = :userId) "
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

    @Query(value = "SELECT DISTINCT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, a.content, a.order_id as orderId, a.reference_number as referenceNumber, "
            + "a.status, a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
            + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
            + "a.terminal_code as terminalCode, a.note "
            + "FROM transaction_receive a "
            + "LEFT JOIN account_bank_receive b "
            + "ON a.bank_id = b.id "
            + "LEFT JOIN bank_type c "
            + "ON b.bank_type_id = c.id "
            + "LEFT JOIN terminal d "
            + "ON a.terminal_code = d.code "
            + "LEFT JOIN account_bank_receive_share e "
            + "ON (a.bank_id = e.bank_id AND e.terminal_id = d.id) "
            + "WHERE a.order_id LIKE %:value% "
            + "AND (e.user_id = :userId OR a.user_id = :userId) "
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

    @Query(value = "SELECT DISTINCT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, a.content, a.order_id as orderId, a.reference_number as referenceNumber, "
            + "a.status, a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
            + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
            + "a.terminal_code as terminalCode, a.note "
            + "FROM transaction_receive a "
            + "LEFT JOIN account_bank_receive b "
            + "ON a.bank_id = b.id "
            + "LEFT JOIN bank_type c "
            + "ON b.bank_type_id = c.id "
            + "LEFT JOIN terminal d "
            + "ON a.terminal_code = d.code "
            + "LEFT JOIN account_bank_receive_share e "
            + "ON (a.bank_id = e.bank_id AND e.terminal_id = d.id) "
            + "WHERE a.content LIKE %:value% "
            + "AND (e.user_id = :userId OR a.user_id = :userId) "
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

    @Query(value = "SELECT DISTINCT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, a.content, a.order_id as orderId, a.reference_number as referenceNumber, "
            + "a.status, a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
            + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
            + "a.terminal_code as terminalCode, a.note "
            + "FROM transaction_receive a "
            + "LEFT JOIN account_bank_receive b "
            + "ON a.bank_id = b.id "
            + "LEFT JOIN bank_type c "
            + "ON b.bank_type_id = c.id "
            + "LEFT JOIN terminal d "
            + "ON a.terminal_code = d.code "
            + "LEFT JOIN account_bank_receive_share e "
            + "ON (a.bank_id = e.bank_id AND e.terminal_id = d.id) "
            + "WHERE a.time BETWEEN :fromDate AND :toDate "
            + "AND (e.user_id = :userId OR a.user_id = :userId) "
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
    @Query(value = "SELECT a.id AS transactionId, a.reference_number as referenceNumber, a.order_id as orderId, a.amount, a.content, "
            + "a.trans_type as transType, a.status, a.type, a.time as timeCreated, a.time_paid as timePaid "
            + "FROM transaction_receive a "
            + "WHERE a.order_id = :value AND a.bank_account = :bankAccount "
            + "AND a.time >= :time ", nativeQuery = true)
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
    @Query(value = "SELECT a.id AS transactionId, a.reference_number as referenceNumber, a.order_id as orderId, a.amount, a.content, "
            + "a.trans_type as transType, a.status, a.type, a.time as timeCreated, a.time_paid as timePaid "
            + "FROM transaction_receive a "
            + "WHERE a.reference_number = :value "
            + "AND a.bank_account = :bankAccount "
            + "AND a.time >= :time ", nativeQuery = true)
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
            + " a.type as type, a.trans_type as transType, "
            + "a.reference_number as referenceNumber, a.terminal_code as terminalCode, "
            + "a.note, a.order_id as orderId "
            + "FROM transaction_receive a "
            + "INNER JOIN terminal c ON a.terminal_code = c.code "
            + "INNER JOIN account_bank_receive_share b ON (a.bank_id = b.bank_id AND b.terminal_id = c.id) "
            + "WHERE a.bank_id = :bankId AND a.status = :value "
            + "AND a.time >= :time AND b.user_id = :userId "
            + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
    List<TransactionRelatedDTO> getTransTerminalByStatus(
            String bankId, String userId, int value, int offset, long time);

    @Query(value = "SELECT DISTINCT a.id as transactionId, a.amount as amount, a.bank_account as bankAccount, "
            + "a.content as content, a.time as time, a.time_paid as timePaid, a.status as status, "
            + "a.type as type, a.trans_type as transType, "
            + "a.reference_number as referenceNumber, a.terminal_code as terminalCode, "
            + "a.note, a.order_id as orderId "
            + "FROM transaction_receive a "
            + "INNER JOIN terminal c ON a.terminal_code = c.code "
            + "INNER JOIN account_bank_receive_share b ON (a.bank_id = b.bank_id AND b.terminal_id = c.id) "
            + "WHERE a.bank_id = :bankId AND a.reference_number = :value "
            + "AND a.time >= :time AND b.user_id = :userId "
            + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
    List<TransactionRelatedDTO> getTransTerminalByFtCode(String bankId, String userId, String value, int offset, long time);

    @Query(value = "SELECT DISTINCT a.id as transactionId, a.amount as amount, a.bank_account as bankAccount "
            + ", a.content as content, a.time as time, a.time_paid as timePaid, a.status as status,"
            + " a.type as type, a.trans_type as transType, "
            + "a.reference_number as referenceNumber, a.terminal_code as terminalCode, "
            + "a.note, a.order_id as orderId "
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
            + " a.type as type, a.trans_type as transType, "
            + "a.reference_number as referenceNumber, a.terminal_code as terminalCode, "
            + "a.note, a.order_id as orderId "
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
            + " a.type as type, a.trans_type as transType, "
            + "a.reference_number as referenceNumber, a.terminal_code as terminalCode, "
            + "a.note, a.order_id as orderId "
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
            + " a.type as type, a.trans_type as transType, "
            + "a.reference_number as referenceNumber, a.terminal_code as terminalCode, "
            + "a.note, a.order_id as orderId "
            + "FROM transaction_receive a "
            + "INNER JOIN terminal c ON a.terminal_code = c.code "
            + "INNER JOIN account_bank_receive_share b ON (a.bank_id = b.bank_id AND b.terminal_id = c.id) "
            + "WHERE a.bank_id = :bankId AND a.status = :value "
            + "AND a.time BETWEEN :fromDate AND :toDate AND b.user_id = :userId "
            + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
    List<TransactionRelatedDTO> getTransTerminalByStatus(String bankId, String userId, int value, int offset, long fromDate, long toDate);

    @Query(value = "SELECT DISTINCT a.id as transactionId, a.amount as amount, a.bank_account as bankAccount "
            + ", a.content as content, a.time as time, a.time_paid as timePaid, a.status as status,"
            + " a.type as type, a.trans_type as transType, "
            + "a.reference_number as referenceNumber, a.terminal_code as terminalCode, "
            + "a.note, a.order_id as orderId "
            + "FROM transaction_receive a "
            + "INNER JOIN terminal c ON a.terminal_code = c.code "
            + "INNER JOIN account_bank_receive_share b ON (a.bank_id = b.bank_id AND b.terminal_id = c.id) "
            + "WHERE a.bank_id = :bankId AND a.reference_number = :value "
            + "AND a.time BETWEEN :fromDate AND :toDate AND b.user_id = :userId "
            + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
    List<TransactionRelatedDTO> getTransTerminalByFtCode(String bankId, String userId, String value, int offset, long fromDate, long toDate);

    @Query(value = "SELECT DISTINCT a.id as transactionId, a.amount as amount, a.bank_account as bankAccount "
            + ", a.content as content, a.time as time, a.time_paid as timePaid, a.status as status,"
            + " a.type as type, a.trans_type as transType, "
            + "a.reference_number as referenceNumber, a.terminal_code as terminalCode, "
            + "a.note, a.order_id as orderId "
            + "FROM transaction_receive a "
            + "INNER JOIN terminal c ON a.terminal_code = c.code "
            + "INNER JOIN account_bank_receive_share b ON (a.bank_id = b.bank_id AND b.terminal_id = c.id) "
            + "WHERE a.bank_id = :bankId AND a.order_id = :value "
            + "AND a.time BETWEEN :fromDate AND :toDate AND b.user_id = :userId "
            + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
    List<TransactionRelatedDTO> getTransTerminalByOrderId(String bankId, String userId, String value, int offset, long fromDate, long toDate);

    @Query(value = "SELECT DISTINCT a.id as transactionId, a.amount as amount, a.bank_account as bankAccount "
            + ", a.content as content, a.time as time, a.time_paid as timePaid, a.status as status,"
            + " a.type as type, a.trans_type as transType, "
            + "a.reference_number as referenceNumber, a.terminal_code as terminalCode, "
            + "a.note, a.order_id as orderId "
            + "FROM transaction_receive a "
            + "INNER JOIN terminal c ON a.terminal_code = c.code "
            + "INNER JOIN account_bank_receive_share b ON (a.bank_id = b.bank_id AND b.terminal_id = c.id) "
            + "WHERE a.bank_id = :bankId AND a.content LIKE %:value% "
            + "AND a.time BETWEEN :fromDate AND :toDate AND b.user_id = :userId "
            + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
    List<TransactionRelatedDTO> getTransTerminalByContent(String bankId, String userId, String value, int offset, long fromDate, long toDate);

    @Query(value = "SELECT DISTINCT a.id as transactionId, a.amount as amount, a.bank_account as bankAccount "
            + ", a.content as content, a.time as time, a.time_paid as timePaid, a.status as status,"
            + " a.type as type, a.trans_type as transType, "
            + "a.reference_number as referenceNumber, a.terminal_code as terminalCode, "
            + "a.note, a.order_id as orderId "
            + "FROM transaction_receive a "
            + "INNER JOIN terminal c ON a.terminal_code = c.code "
            + "INNER JOIN account_bank_receive_share b ON (a.bank_id = b.bank_id AND b.terminal_id = c.id) "
            + "WHERE a.bank_id = :bankId "
            + "AND a.time BETWEEN :fromDate AND :toDate AND b.user_id = :userId "
            + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
    List<TransactionRelatedDTO> getAllTransTerminal(String bankId, String userId, int offset, long fromDate, long toDate);

    @Query(value = "SELECT DISTINCT a.id as transactionId, a.amount as amount, a.bank_account as bankAccount "
            + ", a.content as content, a.time as time, a.time_paid as timePaid, a.status as status,"
            + " a.type as type, a.trans_type as transType, "
            + "a.reference_number as referenceNumber, a.terminal_code as terminalCode, "
            + "a.note, a.order_id as orderId "
            + "FROM transaction_receive a "
            + "INNER JOIN account_bank_receive_share b ON a.bank_id = b.bank_id "
            + "INNER JOIN terminal c ON a.terminal_code = c.code "
            + "WHERE a.bank_id = :bankId AND a.terminal_code = :terminalCode AND a.status = :value "
            + "AND a.time >= :time AND b.user_id = :userId "
            + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
    List<TransactionRelatedDTO> getTransTerminalByStatus(String bankId, String userId, int value, String terminalCode, int offset, long time);

    @Query(value = "SELECT DISTINCT a.id as transactionId, a.amount as amount, a.bank_account as bankAccount "
            + ", a.content as content, a.time as time, a.time_paid as timePaid, a.status as status,"
            + " a.type as type, a.trans_type as transType, "
            + "a.reference_number as referenceNumber, a.terminal_code as terminalCode, "
            + "a.note, a.order_id as orderId "
            + "FROM transaction_receive a "
            + "INNER JOIN account_bank_receive_share b ON a.bank_id = b.bank_id "
            + "INNER JOIN terminal c ON a.terminal_code = c.code "
            + "WHERE a.bank_id = :bankId AND a.terminal_code = :terminalCode AND a.reference_number = :value "
            + "AND a.time >= :time AND b.user_id = :userId "
            + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
    List<TransactionRelatedDTO> getTransTerminalByFtCode(String bankId, String userId, String value, String terminalCode, int offset, long time);

    @Query(value = "SELECT DISTINCT a.id as transactionId, a.amount as amount, a.bank_account as bankAccount "
            + ", a.content as content, a.time as time, a.time_paid as timePaid, a.status as status,"
            + " a.type as type, a.trans_type as transType, "
            + "a.reference_number as referenceNumber, a.terminal_code as terminalCode, "
            + "a.note, a.order_id as orderId "
            + "FROM transaction_receive a "
            + "INNER JOIN account_bank_receive_share b ON a.bank_id = b.bank_id "
            + "INNER JOIN terminal c ON a.terminal_code = c.code "
            + "WHERE a.bank_id = :bankId AND a.terminal_code = :terminalCode AND a.order_id = :value "
            + "AND a.time >= :time AND b.user_id = :userId "
            + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
    List<TransactionRelatedDTO> getTransTerminalByOrderId(String bankId, String userId, String value, String terminalCode, int offset, long time);

    @Query(value = "SELECT DISTINCT a.id as transactionId, a.amount as amount, a.bank_account as bankAccount "
            + ", a.content as content, a.time as time, a.time_paid as timePaid, a.status as status,"
            + " a.type as type, a.trans_type as transType, "
            + "a.reference_number as referenceNumber, a.terminal_code as terminalCode, "
            + "a.note, a.order_id as orderId "
            + "FROM transaction_receive a "
            + "INNER JOIN account_bank_receive_share b ON a.bank_id = b.bank_id "
            + "INNER JOIN terminal c ON a.terminal_code = c.code "
            + "WHERE a.bank_id = :bankId AND a.terminal_code = :terminalCode AND a.content LIKE %:value% "
            + "AND a.time >= :time AND b.user_id = :userId "
            + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
    List<TransactionRelatedDTO> getTransTerminalByContent(String bankId, String userId, String value, String terminalCode, int offset, long time);

    @Query(value = "SELECT DISTINCT a.id as transactionId, a.amount as amount, a.bank_account as bankAccount "
            + ", a.content as content, a.time as time, a.time_paid as timePaid, a.status as status,"
            + " a.type as type, a.trans_type as transType, "
            + "a.reference_number as referenceNumber, a.terminal_code as terminalCode, "
            + "a.note, a.order_id as orderId "
            + "FROM transaction_receive a "
            + "INNER JOIN account_bank_receive_share b ON a.bank_id = b.bank_id "
            + "INNER JOIN terminal c ON a.terminal_code = c.code "
            + "WHERE a.bank_id = :bankId AND a.terminal_code = :terminalCode "
            + "AND a.time >= :time AND b.user_id = :userId "
            + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
    List<TransactionRelatedDTO> getAllTransTerminal(String bankId, String userId, String terminalCode, int offset, long time);

    @Query(value = "SELECT DISTINCT a.id as transactionId, a.amount as amount, a.bank_account as bankAccount "
            + ", a.content as content, a.time as time, a.time_paid as timePaid, a.status as status,"
            + " a.type as type, a.trans_type as transType, "
            + "a.reference_number as referenceNumber, a.terminal_code as terminalCode, "
            + "a.note, a.order_id as orderId "
            + "FROM transaction_receive a "
            + "WHERE a.bank_id = :bankId AND a.terminal_code IN (:terminalCode) AND a.status = :value "
            + "AND a.time BETWEEN :fromDate AND :toDate "
            + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
    List<TransactionRelatedDTO> getTransTerminalByStatus(String bankId, int value, List<String> terminalCode, int offset, long fromDate, long toDate);

    @Query(value = "SELECT a.id as transactionId, a.amount as amount, a.bank_account as bankAccount "
            + ", a.content as content, a.time as time, a.time_paid as timePaid, a.status as status,"
            + " a.type as type, a.trans_type as transType, "
            + "a.reference_number as referenceNumber, a.terminal_code as terminalCode, "
            + "a.note, a.order_id as orderId "
            + "FROM transaction_receive a "
            + "WHERE a.bank_id = :bankId AND a.terminal_code IN (:terminalCode) AND a.reference_number = :value "
            + "AND a.time BETWEEN :fromDate AND :toDate "
            + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
    List<TransactionRelatedDTO> getTransTerminalByFtCode(String bankId, String value, List<String> terminalCode, int offset, long fromDate, long toDate);

    @Query(value = "SELECT a.id as transactionId, a.amount as amount, a.bank_account as bankAccount "
            + ", a.content as content, a.time as time, a.time_paid as timePaid, a.status as status,"
            + " a.type as type, a.trans_type as transType, "
            + "a.reference_number as referenceNumber, a.terminal_code as terminalCode, "
            + "a.note, a.order_id as orderId "
            + "FROM transaction_receive a "
            + "WHERE a.bank_id = :bankId AND a.terminal_code IN (:terminalCode) AND a.order_id = :value "
            + "AND a.time BETWEEN :fromDate AND :toDate "
            + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
    List<TransactionRelatedDTO> getTransTerminalByOrderId(String bankId, String value, List<String> terminalCode, int offset, long fromDate, long toDate);

    @Query(value = "SELECT a.id as transactionId, a.amount as amount, a.bank_account as bankAccount "
            + ", a.content as content, a.time as time, a.time_paid as timePaid, a.status as status,"
            + " a.type as type, a.trans_type as transType, "
            + "a.reference_number as referenceNumber, a.terminal_code as terminalCode, "
            + "a.note, a.order_id as orderId "
            + "FROM transaction_receive a "
            + "WHERE a.bank_id = :bankId AND a.terminal_code IN (:terminalCode) AND a.content LIKE %:value% "
            + "AND a.time BETWEEN :fromDate AND :toDate "
            + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
    List<TransactionRelatedDTO> getTransTerminalByContent(String bankId, String value, List<String> terminalCode, int offset, long fromDate, long toDate);

    @Query(value = "SELECT a.id as transactionId, a.amount as amount, a.bank_account as bankAccount "
            + ", a.content as content, a.time as time, a.time_paid as timePaid, a.status as status,"
            + " a.type as type, a.trans_type as transType, "
            + "a.reference_number as referenceNumber, a.terminal_code as terminalCode, "
            + "a.note, a.order_id as orderId "
            + "FROM transaction_receive a "
            + "WHERE a.bank_id = :bankId AND a.terminal_code IN (:terminalCode) "
            + "AND a.time BETWEEN :fromDate AND :toDate "
            + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
    List<TransactionRelatedDTO> getAllTransTerminal(String bankId, List<String> terminalCode, int offset, long fromDate, long toDate);

    @Query(value = "SELECT DATE_FORMAT(CONVERT_TZ(FROM_UNIXTIME(a.time), '+00:00', '+07:00'), '%Y-%m-%d') AS date, "
            + "COUNT(a.id) AS totalTrans, "
            + "SUM(CASE WHEN a.trans_type = 'C' THEN amount ELSE 0 END) AS totalCashIn, "
            + "SUM(CASE WHEN a.trans_type = 'D' THEN amount ELSE 0 END) AS totalCashOut, "
            + "SUM(CASE WHEN a.trans_type = 'C' THEN 1 ELSE 0 END) AS totalTransC, "
            + "SUM(CASE WHEN a.trans_type = 'D' THEN 1 ELSE 0 END) AS totalTransD "
            + "FROM transaction_receive a "
            + "INNER JOIN terminal b ON b.code = a.terminal_code "
            + "INNER JOIN account_bank_receive_share c ON (c.terminal_id = b.id AND a.bank_id = c.bank_id) "
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
            + "INNER JOIN account_bank_receive_share c ON (c.terminal_id = b.id AND a.bank_id = c.bank_id) "
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
            + "INNER JOIN account_bank_receive_share c ON (c.terminal_id = b.id AND a.bank_id = c.bank_id) "
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
            + "INNER JOIN account_bank_receive_share c ON (c.terminal_id = b.id AND a.bank_id = c.bank_id) "
            + "WHERE c.bank_id = :bankId AND a.status = 1 "
            + "AND c.user_id = :userId "
            + "AND a.time BETWEEN :fromDate AND :toDate "
            + "GROUP BY timeDate ORDER BY timeDate DESC ", nativeQuery = true)
    List<TransStatisticByTimeDTO> getTransStatisticByTerminalIdAndDate(String bankId, String userId,
                                                                       long fromDate, long toDate);

    @Query(value = "SELECT DATE_FORMAT(CONVERT_TZ(FROM_UNIXTIME((FLOOR(a.time / 3600))*3600), '+00:00', '+07:00'), '%Y-%m-%d %H:00') AS timeDate, "
            + "COUNT(a.id) AS totalTrans, "
            + "SUM(CASE WHEN a.trans_type = 'C' THEN amount ELSE 0 END) AS totalCashIn, "
            + "SUM(CASE WHEN a.trans_type = 'D' THEN amount ELSE 0 END) AS totalCashOut, "
            + "SUM(CASE WHEN a.trans_type = 'C' THEN 1 ELSE 0 END) AS totalTransC, "
            + "SUM(CASE WHEN a.trans_type = 'D' THEN 1 ELSE 0 END) AS totalTransD "
            + "FROM transaction_receive a "
            + "INNER JOIN terminal b ON b.code = a.terminal_code "
            + "INNER JOIN account_bank_receive_share c ON (c.terminal_id = b.id AND a.bank_id = c.bank_id) "
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
            + "a.order_id AS orderId, a.terminal_code AS terminalCode, a.trans_type AS transType "
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
            + "a.order_id AS orderId, a.terminal_code AS terminalCode, a.trans_type AS transType "
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
            + "a.order_id AS orderId, a.terminal_code AS terminalCode, a.trans_type AS transType "
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
            + "a.order_id AS orderId, a.terminal_code AS terminalCode, a.trans_type AS transType "
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
            + "a.order_id AS orderId, a.terminal_code AS terminalCode, a.trans_type AS transType "
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
            "AND time >= :timeCheck " +
            "AND trans_type = :transType) " +
            "LIMIT 1;", nativeQuery = true)
    int insertWithCheckDuplicated(String id, long amount, String bankAccount, String bankId, String content,
                                  String customerBankAccount, String customerBankCode, String customerName,
                                  String orderId, String refId, String referenceNumber, String sign, int status,
                                  long time, long timePaid, String traceId, String transType, int type,
                                  String terminalCode, String qrCode, String userId, String note, long timeCheck);

    @Query(value = "SELECT DISTINCT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, a.content, a.order_id as orderId, a.reference_number as referenceNumber, "
            + "a.status, a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
            + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
            + "a.terminal_code as terminalCode, a.note "
            + "FROM transaction_receive a "
            + "LEFT JOIN account_bank_receive b "
            + "ON a.bank_id = b.id "
            + "LEFT JOIN bank_type c "
            + "ON b.bank_type_id = c.id "
            + "LEFT JOIN terminal d "
            + "ON a.terminal_code = d.code "
            + "LEFT JOIN account_bank_receive_share e "
            + "ON (a.bank_id = e.bank_id AND e.terminal_id = d.id) "
            + "WHERE a.time BETWEEN :fromDate AND :toDate "
            + "AND a.terminal_code = :value "
            + "AND e.user_id = :userId "
            + "ORDER BY a.time DESC "
            + "LIMIT :offset, 20 ", nativeQuery = true)
    List<TransactionReceiveAdminListDTO> getTransByTerminalCodeAndUserIdFromDateTerminal(long fromDate, long toDate, String value, String userId, int offset);

    @Query(value = "SELECT DISTINCT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, a.content, a.order_id as orderId, a.reference_number as referenceNumber, "
            + "a.status, a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
            + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
            + "a.terminal_code as terminalCode, a.note "
            + "FROM transaction_receive a "
            + "LEFT JOIN account_bank_receive b "
            + "ON a.bank_id = b.id "
            + "LEFT JOIN bank_type c "
            + "ON b.bank_type_id = c.id "
            + "LEFT JOIN terminal d "
            + "ON a.terminal_code = d.code "
            + "LEFT JOIN account_bank_receive_share e "
            + "ON (a.bank_id = e.bank_id AND e.terminal_id = d.id) "
            + "WHERE a.bank_account = :value "
            + "AND e.user_id = :userId "
            + "AND a.time BETWEEN :fromDate AND :toDate "
            + "ORDER BY a.time DESC "
            + "LIMIT :offset, 20 ", nativeQuery = true)
    List<TransactionReceiveAdminListDTO> getTransByBankAccountFromDateTerminal(String userId, String value, long fromDate, long toDate, int offset);

    //        @Query(value = "SELECT a.id as transactionId,a.amount, a.bank_account as bankAccount,a.content,a.time, a.time_paid as timePaid,a.status,a.type,a.trans_type as transType "
//                + "FROM transaction_receive a "
//                + "WHERE a.bank_id=:bankId AND a.status != 2 "
//                + "AND a.time BETWEEN :fromDate AND :toDate "
//                + "ORDER BY a.time DESC LIMIT :offset, 20"
    @Query(value = "SELECT a.id as id, a.amount AS amount, a.bank_account AS bankAccount, "
            + "a.content AS content, a.time AS timeCreated, a.time_paid AS timePaid, a.status AS status, "
            + "a.type AS type, a.trans_type as transType, a.bank_id AS bankId, a.order_id AS orderId, "
            + "a.reference_number AS referenceNumber, a.note AS note, a.terminal_code AS terminalCode, "
            + "b.bank_account_name AS userBankName, c.bank_short_name AS bankShortName, a.trans_status AS statusResponse "
            + "FROM transaction_receive a INNER JOIN account_bank_receive b ON a.bank_id = b.id "
            + "INNER JOIN bank_type c ON c.id = b.bank_type_id "
            + "WHERE a.bank_id = :bankId AND a.status = 1 AND a.trans_type = 'C' AND a.type = 2 "
            + "AND a.time >= :fromDate AND a.time <= :toDate "
            + "LIMIT :offset, 20", nativeQuery = true)
    List<TransactionReceiveAdminListDTO> getUnsettledTransactions(String bankId, long fromDate, long toDate, int offset);

    @Query(value = "SELECT a.id as id, a.amount AS amount, a.bank_account AS bankAccount, "
            + "a.content AS content, a.time AS timeCreated, a.time_paid AS timePaid, a.status AS status, "
            + "a.type AS type, a.trans_type as transType, a.bank_id AS bankId, a.order_id AS orderId, "
            + "a.reference_number AS referenceNumber, a.note AS note, a.terminal_code AS terminalCode, "
            + "b.bank_account_name AS userBankName, c.bank_short_name AS bankShortName, a.trans_status AS statusResponse "
            + "FROM transaction_receive a INNER JOIN account_bank_receive b ON a.bank_id = b.id "
            + "INNER JOIN bank_type c ON c.id = b.bank_type_id "
            + "WHERE a.bank_id = :bankId AND a.status = 1 AND a.trans_type = 'C' AND a.type = 2 "
            + "AND a.time >= :fromDate AND a.time <= :toDate AND a.reference_number = :value "
            + "LIMIT :offset, 20", nativeQuery = true)
    List<TransactionReceiveAdminListDTO> getUnsettledTransactionsByFtCode(String bankId, String value, long fromDate, long toDate, int offset);

    @Query(value = "SELECT a.id as id, a.amount AS amount, a.bank_account AS bankAccount, "
            + "a.content AS content, a.time AS timeCreated, a.time_paid AS timePaid, a.status AS status, "
            + "a.type AS type, a.trans_type as transType, a.bank_id AS bankId, a.order_id AS orderId, "
            + "a.reference_number AS referenceNumber, a.note AS note, a.terminal_code AS terminalCode, "
            + "b.bank_account_name AS userBankName, c.bank_short_name AS bankShortName, a.trans_status AS statusResponse "
            + "FROM transaction_receive a INNER JOIN account_bank_receive b ON a.bank_id = b.id "
            + "INNER JOIN bank_type c ON c.id = b.bank_type_id "
            + "WHERE a.bank_id = :bankId AND a.status = 1 AND a.trans_type = 'C' AND a.type = 2 "
            + "AND a.time >= :fromDate AND a.time <= :toDate AND a.order_id = :value "
            + "LIMIT :offset, 20", nativeQuery = true)
    List<TransactionReceiveAdminListDTO> getUnsettledTransactionsByOrderId(String bankId, String value, long fromDate, long toDate, int offset);

    @Query(value = "SELECT a.id as id, a.amount AS amount, a.bank_account AS bankAccount, "
            + "a.content AS content, a.time AS timeCreated, a.time_paid AS timePaid, a.status AS status, "
            + "a.type AS type, a.trans_type as transType, a.bank_id AS bankId, a.order_id AS orderId, "
            + "a.reference_number AS referenceNumber, a.note AS note, a.terminal_code AS terminalCode, "
            + "b.bank_account_name AS userBankName, c.bank_short_name AS bankShortName, a.trans_status AS statusResponse "
            + "FROM transaction_receive a INNER JOIN account_bank_receive b ON a.bank_id = b.id "
            + "INNER JOIN bank_type c ON c.id = b.bank_type_id "
            + "WHERE a.bank_id = :bankId AND a.status = 1 AND a.trans_type = 'C' AND a.type = 2 "
            + "AND a.time >= :fromDate AND a.time <= :toDate AND a.content LIKE %:value% "
            + "LIMIT :offset, 20", nativeQuery = true)
    List<TransactionReceiveAdminListDTO> getUnsettledTransactionsByContent(String bankId, String value, long fromDate, long toDate, int offset);

    @Query(value = "SELECT a.id as id, a.amount AS amount, a.bank_account AS bankAccount, "
            + "a.content AS content, a.time AS timeCreated, a.time_paid AS timePaid, a.status AS status, "
            + "a.type AS type, a.trans_type as transType, a.bank_id AS bankId, a.order_id AS orderId, "
            + "a.reference_number AS referenceNumber, a.note AS note, a.terminal_code AS terminalCode, "
            + "b.bank_account_name AS userBankName, c.bank_short_name AS bankShortName, a.trans_status AS statusResponse "
            + "FROM transaction_receive a INNER JOIN account_bank_receive b ON a.bank_id = b.id "
            + "INNER JOIN bank_type c ON c.id = b.bank_type_id "
            + "WHERE a.bank_id = :bankId AND a.status = 1 AND a.trans_type = 'C' AND a.type = 2 "
            + "AND a.time >= :fromDate AND a.time <= :toDate AND a.terminal_code = :value "
            + "LIMIT :offset, 20", nativeQuery = true)
    List<TransactionReceiveAdminListDTO> getUnsettledTransactionsByTerminalCode(String bankId, String value, long fromDate, long toDate, int offset);

    @Transactional
    @Modifying
    @Query(value = "UPDATE transaction_receive SET terminal_code = :terminalCode, type = :type, trans_status = 2 WHERE id = :transactionId ", nativeQuery = true)
    void updateTransactionReceiveTerminalCode(String transactionId, String terminalCode, int type);

    @Query(value = "SELECT a.* FROM transaction_receive a WHERE a.id = :transactionId "
            + "AND a.user_id = :userId", nativeQuery = true)
    TransactionReceiveEntity getTransactionReceiveByIdAndUserId(String transactionId, String userId);

    @Query(value = "SELECT a.id as transactionId, a.amount, a.bank_account as bankAccount, a.content, a.time, "
            + "a.time_paid as timePaid, a.status, a.type, a.trans_type as transType, "
            + "a.reference_number as referenceNumber, a.terminal_code as terminalCode, "
            + "a.note, a.order_id as orderId "
            + "FROM transaction_receive a "
            + "WHERE a.bank_id= :bankId AND a.terminal_code IN (:allTerminalCode) "
            + "AND a.time >= :time "
            + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
    List<TransactionRelatedDTO> getTransactionsByTerminalCodeAllDateListCode(List<String> allTerminalCode, int offset, String bankId, long time);

    @Query(value = "SELECT a.id as transactionId,a.amount, a.bank_account as bankAccount, a.content, a.time, "
            + "a.time_paid as timePaid, a.status, a.type,a.trans_type as transType, "
            + "a.reference_number as referenceNumber, a.terminal_code as terminalCode, a.sub_code AS subCode, "
            + "a.note, a.order_id as orderId "
            + "FROM transaction_receive a "
            + "WHERE a.bank_id = :bankId AND a.terminal_code IN (:allTerminalCode) "
            + "AND a.time BETWEEN :fromDate AND :toDate "
            + "AND (a.time_paid BETWEEN :fromTimePaid AND :toTimePaid OR a.time_paid = 0) "
            + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
    List<TransactionRelatedDTO> getTransactionsByTerminalCodeAndDateListCode(List<String> allTerminalCode, int offset, String bankId, long fromDate, long toDate, long fromTimePaid, long toTimePaid);

    @Query(value = "SELECT a.id AS transactionId, a.amount, a.bank_account AS bankAccount, a.content, a.time, "
            + "a.time_paid AS timePaid, a.status, a.type,a.trans_type AS transType, "
            + "a.reference_number AS referenceNumber, a.terminal_code AS terminalCode, "
            + "a.note, a.order_id AS orderId, 'MBBank' AS bankShortName "
            + "FROM transaction_receive a "
            + "WHERE a.terminal_code IN (:codes) "
            + "AND a.time BETWEEN :fromDate AND :toDate "
            + "ORDER BY a.time DESC LIMIT :offset, :size", nativeQuery = true)
    List<TransactionRelatedDTO> getSubTerminalTransactions(List<String> codes, long fromDate,
                                                           long toDate, int offset, int size);

    @Query(value = "SELECT COUNT(a.id) "
            + "FROM transaction_receive a "
            + "WHERE a.terminal_code IN (:codes) "
            + "AND a.time BETWEEN :fromDate AND :toDate ", nativeQuery = true)
    int getTotalSubTerminalTransactions(List<String> codes, long fromDate, long toDate);

    @Query(value = "SELECT a.id AS transactionId, a.amount, a.bank_account AS bankAccount, a.content, a.time, "
            + "a.time_paid AS timePaid, a.status, a.type,a.trans_type AS transType, "
            + "a.reference_number AS referenceNumber, a.terminal_code AS terminalCode, "
            + "a.note, a.order_id AS orderId "
            + "FROM transaction_receive a "
            + "WHERE a.terminal_code  IN (:codes) "
            + "AND a.time BETWEEN :fromDate AND :toDate "
            + "AND a.reference_number = :value "
            + "ORDER BY a.time DESC LIMIT :offset, :size", nativeQuery = true)
    List<TransactionRelatedDTO> getSubTerminalTransactionsByFtCode(List<String> codes, String value,
                                                                   long fromDate, long toDate,
                                                                   int offset, int size);

    @Query(value = "SELECT COUNT(a.id) "
            + "FROM transaction_receive a "
            + "WHERE a.terminal_code  IN (:codes) "
            + "AND a.reference_number = :value "
            + "AND a.time BETWEEN :fromDate AND :toDate ", nativeQuery = true)
    int getTotalSubTerminalTransactionsByFtCode(List<String> codes, String value, long fromDate, long toDate);

    @Query(value = "SELECT a.id AS transactionId, a.amount, a.bank_account AS bankAccount, a.content, a.time, "
            + "a.time_paid AS timePaid, a.status, a.type,a.trans_type AS transType, "
            + "a.reference_number AS referenceNumber, a.terminal_code AS terminalCode, "
            + "a.note, a.order_id AS orderId "
            + "FROM transaction_receive a "
            + "WHERE a.terminal_code  IN (:codes) "
            + "AND a.time BETWEEN :fromDate AND :toDate "
            + "AND a.order_id = :value "
            + "ORDER BY a.time DESC LIMIT :offset, :size", nativeQuery = true)
    List<TransactionRelatedDTO> getSubTerminalTransactionsByOrderId(List<String> codes, String value,
                                                                    long fromDate, long toDate, int offset, int size);

    @Query(value = "SELECT COUNT(a.id) "
            + "FROM transaction_receive a "
            + "WHERE a.terminal_code  IN (:codes) "
            + "AND a.order_id = :value "
            + "AND a.time BETWEEN :fromDate AND :toDate ", nativeQuery = true)
    int getTotalSubTerminalTransactionsByOrderId(List<String> codes, String value, long fromDate, long toDate);

    @Query(value = "SELECT a.id AS transactionId, a.amount, a.bank_account AS bankAccount, a.content, a.time, "
            + "a.time_paid AS timePaid, a.status, a.type,a.trans_type AS transType, "
            + "a.reference_number AS referenceNumber, a.terminal_code AS terminalCode, "
            + "a.note, a.order_id AS orderId "
            + "FROM transaction_receive a "
            + "WHERE a.terminal_code  IN (:codes) "
            + "AND a.time BETWEEN :fromDate AND :toDate "
            + "AND a.content LIKE %:value% "
            + "ORDER BY a.time DESC LIMIT :offset, :size", nativeQuery = true)
    List<TransactionRelatedDTO> getSubTerminalTransactionsByContent(List<String> codes, String value,
                                                                    long fromDate, long toDate, int offset, int size);

    @Query(value = "SELECT COUNT(a.id) "
            + "FROM transaction_receive a "
            + "WHERE a.terminal_code  IN (:codes) "
            + "AND a.content LIKE %:value% "
            + "AND a.time BETWEEN :fromDate AND :toDate ", nativeQuery = true)
    int getTotalSubTerminalTransactionsByContent(List<String> codes, String value, long fromDate, long toDate);

    @Query(value = "SELECT a.id AS transactionId, a.amount, a.bank_account AS bankAccount, a.content, a.time, "
            + "a.time_paid AS timePaid, a.status, a.type,a.trans_type AS transType, "
            + "a.reference_number AS referenceNumber, a.terminal_code AS terminalCode, "
            + "a.note, a.order_id AS orderId "
            + "FROM transaction_receive a "
            + "WHERE a.terminal_code  IN (:codes) "
            + "AND a.time BETWEEN :fromDate AND :toDate "
            + "AND a.amount = :value "
            + "ORDER BY a.time DESC LIMIT :offset, :size", nativeQuery = true)
    List<TransactionRelatedDTO> getSubTerminalTransactionsByAmount(List<String> codes, int value,
                                                                   long fromDate, long toDate, int offset, int size);

    @Query(value = "SELECT COUNT(a.id) "
            + "FROM transaction_receive a "
            + "WHERE a.terminal_code IN (:codes) "
            + "AND a.amount = :value "
            + "AND a.time BETWEEN :fromDate AND :toDate ", nativeQuery = true)
    int getTotalSubTerminalTransactionsByAmount(List<String> codes, int value, long fromDate, long toDate);

    @Query(value = "SELECT COUNT(id) AS totalTrans, "
            + "SUM(CASE WHEN trans_type = 'C' THEN amount ELSE 0 END) AS totalCashIn, "
            + "SUM(CASE WHEN trans_type = 'D' THEN amount ELSE 0 END) AS totalCashOut, "
            + "SUM(CASE WHEN trans_type = 'C' THEN 1 ELSE 0 END) AS totalTransC, "
            + "SUM(CASE WHEN trans_type = 'D' THEN 1 ELSE 0 END) AS totalTransD "
            + "FROM transaction_receive "
            + "WHERE terminal_code IN (:subTerminalCodes) AND status = 1 "
            + "AND time BETWEEN :fromDate AND :toDate ", nativeQuery = true)
    TransStatisticDTO getTransactionOverviewBySubTerminalCode(List<String> subTerminalCodes, long fromDate, long toDate);

    @Query(value = "SELECT COUNT(id) AS totalTrans, "
            + "SUM(CASE WHEN trans_type = 'C' THEN amount ELSE 0 END) AS totalCashIn, "
            + "SUM(CASE WHEN trans_type = 'D' THEN amount ELSE 0 END) AS totalCashOut, "
            + "SUM(CASE WHEN trans_type = 'C' THEN 1 ELSE 0 END) AS totalTransC, "
            + "SUM(CASE WHEN trans_type = 'D' THEN 1 ELSE 0 END) AS totalTransD "
            + "FROM transaction_receive "
            + "WHERE terminal_code = :subTerminalCode AND status = 1 "
            + "AND time BETWEEN :fromDate AND :toDate ", nativeQuery = true)
    TransStatisticDTO getTransactionOverviewBySubTerminalCode(String subTerminalCode, long fromDate, long toDate);

    @Query(value = "SELECT DATE_FORMAT(CONVERT_TZ(FROM_UNIXTIME((FLOOR(time / 3600))*3600), '+00:00', '+07:00'), '%Y-%m-%d %H:00') AS timeDate, "
            + "COUNT(id) AS totalTrans, "
            + "SUM(CASE WHEN trans_type = 'C' THEN amount ELSE 0 END) AS totalCashIn, "
            + "SUM(CASE WHEN trans_type = 'D' THEN amount ELSE 0 END) AS totalCashOut, "
            + "SUM(CASE WHEN trans_type = 'C' THEN 1 ELSE 0 END) AS totalTransC, "
            + "SUM(CASE WHEN trans_type = 'D' THEN 1 ELSE 0 END) AS totalTransD "
            + "FROM transaction_receive "
            + "WHERE terminal_code IN (:codes) AND status = 1 "
            + "AND time BETWEEN :fromDate AND :toDate "
            + "GROUP BY timeDate ORDER BY timeDate ASC ", nativeQuery = true)
    List<TransStatisticByTimeDTO> getTransStatisticSubTerminalByTerminalCodeDate(List<String> codes, long fromDate, long toDate);

    @Query(value = "SELECT DATE_FORMAT(CONVERT_TZ(FROM_UNIXTIME((FLOOR(time / 3600))*3600), '+00:00', '+07:00'), '%Y-%m-%d %H:00') AS timeDate, "
            + "COUNT(id) AS totalTrans, "
            + "SUM(CASE WHEN trans_type = 'C' THEN amount ELSE 0 END) AS totalCashIn, "
            + "SUM(CASE WHEN trans_type = 'D' THEN amount ELSE 0 END) AS totalCashOut, "
            + "SUM(CASE WHEN trans_type = 'C' THEN 1 ELSE 0 END) AS totalTransC, "
            + "SUM(CASE WHEN trans_type = 'D' THEN 1 ELSE 0 END) AS totalTransD "
            + "FROM transaction_receive "
            + "WHERE terminal_code = :subTerminalCode AND status = 1 "
            + "AND time BETWEEN :fromDate AND :toDate "
            + "GROUP BY timeDate ORDER BY timeDate ASC ", nativeQuery = true)
    List<TransStatisticByTimeDTO> getTransStatisticSubTerminalByTerminalCodeDate(String subTerminalCode, long fromDate, long toDate);

    @Query(value = "SELECT DATE_FORMAT(CONVERT_TZ(FROM_UNIXTIME(time), '+00:00', '+07:00'), '%Y-%m-%d') AS timeDate, "
            + "COUNT(id) AS totalTrans, "
            + "SUM(CASE WHEN trans_type = 'C' THEN amount ELSE 0 END) AS totalCashIn, "
            + "SUM(CASE WHEN trans_type = 'D' THEN amount ELSE 0 END) AS totalCashOut, "
            + "SUM(CASE WHEN trans_type = 'C' THEN 1 ELSE 0 END) AS totalTransC, "
            + "SUM(CASE WHEN trans_type = 'D' THEN 1 ELSE 0 END) AS totalTransD "
            + "FROM transaction_receive "
            + "WHERE terminal_code IN (:codes) AND status = 1 "
            + "AND time BETWEEN :fromDate AND :toDate "
            + "GROUP BY timeDate ORDER BY timeDate ASC ", nativeQuery = true)
    List<TransStatisticByTimeDTO> getTransStatisticSubTerminalByTerminalCodeMonth(List<String> codes, long fromDate, long toDate);

    @Query(value = "SELECT DATE_FORMAT(CONVERT_TZ(FROM_UNIXTIME(time), '+00:00', '+07:00'), '%Y-%m-%d') AS timeDate, "
            + "COUNT(id) AS totalTrans, "
            + "SUM(CASE WHEN trans_type = 'C' THEN amount ELSE 0 END) AS totalCashIn, "
            + "SUM(CASE WHEN trans_type = 'D' THEN amount ELSE 0 END) AS totalCashOut, "
            + "SUM(CASE WHEN trans_type = 'C' THEN 1 ELSE 0 END) AS totalTransC, "
            + "SUM(CASE WHEN trans_type = 'D' THEN 1 ELSE 0 END) AS totalTransD "
            + "FROM transaction_receive "
            + "WHERE terminal_code = :subTerminalCode AND status = 1 "
            + "AND time BETWEEN :fromDate AND :toDate "
            + "GROUP BY timeDate ORDER BY timeDate ASC ", nativeQuery = true)
    List<TransStatisticByTimeDTO> getTransStatisticSubTerminalByTerminalCodeMonth(String subTerminalCode, long fromDate, long toDate);

    @Query(value = "SELECT a.id AS transactionId, a.amount, a.bank_account AS bankAccount, a.content, a.time, "
            + "a.time_paid AS timePaid, a.status, a.type,a.trans_type AS transType, "
            + "a.reference_number AS referenceNumber, a.terminal_code AS terminalCode, "
            + "a.note, a.order_id AS orderId "
            + "FROM transaction_receive a "
            + "WHERE a.terminal_code  IN (:codes) "
            + "AND a.time BETWEEN :fromDate AND :toDate "
            + "AND a.status = :value "
            + "ORDER BY a.time DESC LIMIT :offset, :size", nativeQuery = true)
    List<TransactionRelatedDTO> getSubTerminalTransactionsByStatus(List<String> codes, int value, long fromDate, long toDate, int offset, int size);

    @Query(value = "SELECT COUNT(a.id) "
            + "FROM transaction_receive a "
            + "WHERE a.terminal_code IN (:codes) "
            + "AND a.status = :value "
            + "AND a.time BETWEEN :fromDate AND :toDate ", nativeQuery = true)
    int getTotalSubTerminalTransactionsByStatus(List<String> codes, int value, long fromDate, long toDate);

    @Query(value = "SELECT a.id AS transactionId, a.amount AS amount, a.bank_account AS bankAccount, "
            + "a.content AS content, a.time AS time, a.time_paid AS timePaid, a.status AS status, "
            + "a.type AS type, d.bank_name AS bankName, d.bank_short_name AS bankShortName, a.sub_code AS subCode, "
            + "d.bank_code AS bankCode, a.note AS note, a.reference_number AS referenceNumber, "
            + "a.order_id AS orderId, a.terminal_code AS terminalCode, a.trans_type AS transType "
            + "FROM transaction_receive a "
            + "INNER JOIN account_bank_receive c ON a.bank_id = c.id "
            + "INNER JOIN bank_type d ON d.id = c.bank_type_id "
            + "WHERE a.time BETWEEN :fromDate AND :toDate AND a.bank_id = :bankId "
            + "AND (a.time_paid BETWEEN :fromTimePaid AND :toTimePaid OR a.time_paid = 0) "
            + "ORDER BY a.time DESC ", nativeQuery = true)
    List<ITransactionRelatedDetailDTO> getTransByBankId(String bankId, long fromDate, long toDate, long fromTimePaid, long toTimePaid);

    @Query(value = "SELECT a.id AS transactionId, a.amount AS amount, a.bank_account AS bankAccount "
            + ", a.content AS content, a.time AS time, a.time_paid AS timePaid, a.status AS status, "
            + "a.type AS type, d.bank_name AS bankName, d.bank_short_name AS bankShortName, "
            + "d.bank_code AS bankCode, a.note AS note, a.reference_number AS referenceNumber, "
            + "a.order_id AS orderId, a.sub_code AS subCode, a.terminal_code AS terminalCode, a.trans_type AS transType "
            + "FROM transaction_receive a "
            + "INNER JOIN account_bank_receive c ON a.bank_id = c.id "
            + "INNER JOIN bank_type d ON d.id = c.bank_type_id "
            + "WHERE a.time BETWEEN :fromDate AND :toDate AND a.terminal_code = :terminalCode "
            + "AND (a.time_paid BETWEEN :fromTimePaid AND :toTimePaid OR a.time_paid = 0) "
            + "ORDER BY a.time DESC ", nativeQuery = true)
    List<ITransactionRelatedDetailDTO> getTransByTerminalCode(String terminalCode, long fromDate, long toDate, long fromTimePaid, long toTimePaid);

    @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, a.content, "
            + "a.order_id as orderId, a.reference_number as referenceNumber, "
            + "a.status, a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
            + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
            + "a.terminal_code as terminalCode, a.note "
            + "FROM transaction_receive a "
            + "INNER JOIN account_bank_receive b "
            + "ON a.bank_id = b.id "
            + "INNER JOIN bank_type c "
            + "ON b.bank_type_id = c.id "
            + "WHERE a.time BETWEEN :fromDate AND :toDate "
            + "AND a.bank_id = :bankId AND a.type = 2 AND trans_status = 1 "
            + "ORDER BY a.time DESC "
            + "LIMIT :offset, :size ", nativeQuery = true)
    List<TransactionReceiveAdminListDTO> getTransactionReceiveWithRequest(String bankId,
                                                                          long fromDate, long toDate, int offset, int size);

    @Query(value = "SELECT COUNT(a.id) "
            + "FROM transaction_receive a "
            + "WHERE a.time BETWEEN :fromDate AND :toDate "
            + "AND a.bank_id = :bankId AND a.type = 2 AND trans_status = 1 ", nativeQuery = true)
    int countTransactionReceiveWithRequest(String bankId, long fromDate, long toDate);

    @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, a.content, "
            + "a.order_id as orderId, a.reference_number as referenceNumber, "
            + "a.status, a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
            + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
            + "a.terminal_code as terminalCode, a.note "
            + "FROM transaction_receive a "
            + "INNER JOIN account_bank_receive b "
            + "ON a.bank_id = b.id "
            + "INNER JOIN bank_type c "
            + "ON b.bank_type_id = c.id "
            + "WHERE a.time BETWEEN :fromDate AND :toDate "
            + "AND a.bank_id = :bankId AND a.type = 2 AND trans_status = 1 "
            + "AND a.id = :value "
            + "ORDER BY a.time DESC "
            + "LIMIT :offset, :size ", nativeQuery = true)
    List<TransactionReceiveAdminListDTO> getTransactionReceiveWithRequestById(String bankId, long fromDate, long toDate, int offset, int size, String value);

    @Query(value = "SELECT COALESCE(COUNT(a.id)) AS totalTrans, "
            + "COALESCE(SUM(a.amount), 0) AS totalCashIn, "
            + "COALESCE(COUNT(CASE WHEN a.type != 2 THEN 1 ELSE NULL END), 0) AS totalSettled, "
            + "COALESCE(SUM(CASE WHEN a.type != 2 THEN a.amount ELSE 0 END), 0) AS totalCashSettled, "
            + "COALESCE(COUNT(CASE WHEN a.type = 2 THEN 1 ELSE NULL END), 0) AS totalUnsettled, "
            + "COALESCE(SUM(CASE WHEN a.type = 2 THEN a.amount ELSE 0 END), 0) AS totalCashUnsettled "
            + "FROM transaction_receive a "
            + "WHERE a.time BETWEEN :fromDate AND :toDate AND trans_type = 'C' "
            + "AND a.time_paid BETWEEN :fromTimePaid AND :toTimePaid "
            + "AND a.bank_id = :bankId AND a.status = 1 LIMIT 1", nativeQuery = true)
    ITransStatisticResponseWebDTO getTransactionWebOverview(String bankId, long fromDate, long toDate, long fromTimePaid, long toTimePaid);

    @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, a.content, "
            + "a.order_id as orderId, a.reference_number as referenceNumber, "
            + "a.status, a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
            + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
            + "a.terminal_code as terminalCode, a.note "
            + "FROM transaction_receive a "
            + "INNER JOIN account_bank_receive b "
            + "ON a.bank_id = b.id "
            + "INNER JOIN bank_type c "
            + "ON b.bank_type_id = c.id "
            + "WHERE a.time BETWEEN :fromDate AND :toDate "
            + "AND a.bank_id = :bankId AND a.type = 2 AND trans_status = 1 "
            + "AND a.reference_number = :value "
            + "ORDER BY a.time DESC "
            + "LIMIT :offset, :size ", nativeQuery = true)
    List<TransactionReceiveAdminListDTO> getTransactionReceiveWithRequestByFtCode(String bankId, long fromDate, long toDate, int offset, int size, String value);

    @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, a.content, "
            + "a.order_id as orderId, a.reference_number as referenceNumber, "
            + "a.status, a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
            + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
            + "a.terminal_code as terminalCode, a.note "
            + "FROM transaction_receive a "
            + "INNER JOIN account_bank_receive b "
            + "ON a.bank_id = b.id "
            + "INNER JOIN bank_type c "
            + "ON b.bank_type_id = c.id "
            + "WHERE a.time BETWEEN :fromDate AND :toDate "
            + "AND a.bank_id = :bankId AND a.type = 2 AND trans_status = 1 "
            + "AND a.order_id = :value "
            + "ORDER BY a.time DESC "
            + "LIMIT :offset, :size ", nativeQuery = true)
    List<TransactionReceiveAdminListDTO> getTransactionReceiveWithRequestByOrderId(String bankId, long fromDate, long toDate, int offset, int size, String value);

    @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, a.content, "
            + "a.order_id as orderId, a.reference_number as referenceNumber, "
            + "a.status, a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
            + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
            + "a.terminal_code as terminalCode, a.note "
            + "FROM transaction_receive a "
            + "INNER JOIN account_bank_receive b "
            + "ON a.bank_id = b.id "
            + "INNER JOIN bank_type c "
            + "ON b.bank_type_id = c.id "
            + "WHERE a.time BETWEEN :fromDate AND :toDate "
            + "AND a.bank_id = :bankId AND a.type = 2 AND trans_status = 1 "
            + "AND a.terminal_code = :value "
            + "ORDER BY a.time DESC "
            + "LIMIT :offset, :size ", nativeQuery = true)
    List<TransactionReceiveAdminListDTO> getTransactionReceiveWithRequestByTerminalCode(String bankId, long fromDate, long toDate, int offset, int size, String value);

    @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, a.content, "
            + "a.order_id as orderId, a.reference_number as referenceNumber, "
            + "a.status, a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
            + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
            + "a.terminal_code as terminalCode, a.note "
            + "FROM transaction_receive a "
            + "INNER JOIN account_bank_receive b "
            + "ON a.bank_id = b.id "
            + "INNER JOIN bank_type c "
            + "ON b.bank_type_id = c.id "
            + "WHERE a.time BETWEEN :fromDate AND :toDate "
            + "AND a.bank_id = :bankId AND a.type = 2 AND trans_status = 1 "
            + "AND a.status = :value "
            + "ORDER BY a.time DESC "
            + "LIMIT :offset, :size ", nativeQuery = true)
    List<TransactionReceiveAdminListDTO> getTransactionReceiveWithRequestByStatus(String bankId, long fromDate, long toDate, int offset, int size, int value);

    @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, a.content, "
            + "a.order_id as orderId, a.reference_number as referenceNumber, "
            + "a.status, a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
            + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
            + "a.terminal_code as terminalCode, a.note "
            + "FROM transaction_receive a "
            + "INNER JOIN account_bank_receive b "
            + "ON a.bank_id = b.id "
            + "INNER JOIN bank_type c "
            + "ON b.bank_type_id = c.id "
            + "WHERE a.time BETWEEN :fromDate AND :toDate "
            + "AND a.bank_id = :bankId AND a.type = 2 AND trans_status = 1 "
            + "AND a.content LIKE %:value% "
            + "ORDER BY a.time DESC "
            + "LIMIT :offset, :size ", nativeQuery = true)
    List<TransactionReceiveAdminListDTO> getTransactionReceiveWithRequestByContent(String bankId, long fromDate, long toDate, int offset, int size, String value);

    @Query(value = "SELECT COUNT(a.id) "
            + "FROM transaction_receive a "
            + "WHERE a.time BETWEEN :fromDate AND :toDate "
            + "AND a.status = :value "
            + "AND a.bank_id = :bankId AND a.type = 2 AND trans_status = 1 ", nativeQuery = true)
    int countTransactionReceiveWithRequestByStatus(String bankId, long fromDate, long toDate, int value);

    @Query(value = "SELECT COUNT(a.id) "
            + "FROM transaction_receive a "
            + "WHERE a.time BETWEEN :fromDate AND :toDate "
            + "AND a.terminal_code = :value "
            + "AND a.bank_id = :bankId AND a.type = 2 AND trans_status = 1 ", nativeQuery = true)
    int countTransactionReceiveWithRequestByTerminalCode(String bankId, long fromDate, long toDate, String value);

    @Query(value = "SELECT COUNT(a.id) "
            + "FROM transaction_receive a "
            + "WHERE a.time BETWEEN :fromDate AND :toDate "
            + "AND a.content LIKE %:value% "
            + "AND a.bank_id = :bankId AND a.type = 2 AND trans_status = 1 ", nativeQuery = true)
    int countTransactionReceiveWithRequestByContent(String bankId, long fromDate, long toDate, String value);

    @Query(value = "SELECT COUNT(a.id) "
            + "FROM transaction_receive a "
            + "WHERE a.time BETWEEN :fromDate AND :toDate "
            + "AND a.order_id = :value "
            + "AND a.bank_id = :bankId AND a.type = 2 AND trans_status = 1 ", nativeQuery = true)
    int countTransactionReceiveWithRequestByOrderId(String bankId, long fromDate, long toDate, String value);

    @Query(value = "SELECT COUNT(a.id) "
            + "FROM transaction_receive a "
            + "WHERE a.time BETWEEN :fromDate AND :toDate "
            + "AND a.reference_number = :value "
            + "AND a.bank_id = :bankId AND a.type = 2 AND trans_status = 1 ", nativeQuery = true)
    int countTransactionReceiveWithRequestByFtCode(String bankId, long fromDate, long toDate, String value);

    @Query(value = "SELECT a.id as id, a.amount AS amount, a.bank_account AS bankAccount, "
            + "a.content AS content, a.time AS timeCreated, a.time_paid AS timePaid, a.status AS status, "
            + "a.type AS type, a.trans_type as transType, a.bank_id AS bankId, a.order_id AS orderId, "
            + "a.reference_number AS referenceNumber, a.note AS note, a.terminal_code AS terminalCode, "
            + "b.bank_account_name AS userBankName, c.bank_short_name AS bankShortName, a.trans_status AS statusResponse "
            + "FROM transaction_receive a INNER JOIN account_bank_receive b ON a.bank_id = b.id "
            + "INNER JOIN bank_type c ON c.id = b.bank_type_id "
            + "WHERE a.bank_id = :bankId AND a.status = 1 AND a.trans_type = 'C' AND a.type = 2 "
            + "AND a.time >= :fromDate AND a.time <= :toDate AND a.status = :value "
            + "LIMIT :offset, 20", nativeQuery = true)
    List<TransactionReceiveAdminListDTO> getUnsettledTransactionsByStatus(String bankId, int value, long fromDate,
                                                                          long toDate, int offset);

    @Query(value = "SELECT a.id as transactionId, a.amount as amount, a.bank_account as bankAccount "
            + ", a.content as content, a.time as time, a.time_paid as timePaid, a.status as status,"
            + " a.type as type, a.trans_type as transType, "
            + "a.reference_number as referenceNumber, a.terminal_code as terminalCode, "
            + "a.note, a.order_id as orderId "
            + "FROM transaction_receive a "
            + "WHERE a.bank_id = :bankId AND (a.terminal_code IN (:listCode) "
            + "OR (a.terminal_code IS NULL)) AND a.reference_number = :value "
            + "AND a.time BETWEEN :fromDate AND :toDate AND status = 1 "
            + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
    List<TransactionRelatedDTO> getTransTerminalWithType2ByFtCode(String bankId,
                                                                  String value, List<String> listCode,
                                                                  int offset, long fromDate, long toDate);

    @Query(value = "SELECT a.id as transactionId, a.amount as amount, a.bank_account as bankAccount "
            + ", a.content as content, a.time as time, a.time_paid as timePaid, a.status as status,"
            + " a.type as type, a.trans_type as transType, "
            + "a.reference_number as referenceNumber, a.terminal_code as terminalCode, "
            + "a.note, a.order_id as orderId "
            + "FROM transaction_receive a "
            + "WHERE a.bank_id = :bankId AND (a.terminal_code IN (:listCode) "
            + "OR (a.terminal_code IS NULL)) AND a.order_id = :value "
            + "AND a.time BETWEEN :fromDate AND :toDate AND status = 1 "
            + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
    List<TransactionRelatedDTO> getTransTerminalWithType2ByOrderId(String bankId,
                                                                   String value, List<String> listCode,
                                                                   int offset, long fromDate, long toDate);

    @Query(value = "SELECT a.id as transactionId, a.amount as amount, a.bank_account as bankAccount "
            + ", a.content as content, a.time as time, a.time_paid as timePaid, a.status as status,"
            + " a.type as type, a.trans_type as transType, "
            + "a.reference_number as referenceNumber, a.terminal_code as terminalCode, "
            + "a.note, a.order_id as orderId "
            + "FROM transaction_receive a "
            + "WHERE a.bank_id = :bankId AND (a.terminal_code IN (:listCode) "
            + "OR (a.terminal_code IS NULL)) AND a.content LIKE %:value% "
            + "AND a.time BETWEEN :fromDate AND :toDate AND status = 1 "
            + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
    List<TransactionRelatedDTO> getTransTerminalWithType2ByContent(String bankId,
                                                                   String value, List<String> listCode,
                                                                   int offset, long fromDate, long toDate);

    @Query(value = "SELECT a.id as transactionId, a.amount as amount, a.bank_account as bankAccount "
            + ", a.content as content, a.time as time, a.time_paid as timePaid, a.status as status,"
            + " a.type as type, a.trans_type as transType, "
            + "a.reference_number as referenceNumber, a.terminal_code as terminalCode, "
            + "a.note, a.order_id as orderId "
            + "FROM transaction_receive a "
            + "WHERE a.bank_id = :bankId AND (a.terminal_code IN (:listCode) "
            + "OR (a.terminal_code IS NULL)) AND a.status = :value "
            + "AND a.time BETWEEN :fromDate AND :toDate "
            + "ORDER BY a.time DESC LIMIT :offset, 20", nativeQuery = true)
    List<TransactionRelatedDTO> getTransTerminalWithType2ByStatus(String bankId,
                                                                  int value, List<String> listCode, int offset,
                                                                  long fromDate, long toDate);

    @Query(value = "SELECT a.id as transactionId, a.amount as amount, a.bank_account as bankAccount "
            + ", a.content as content, a.time as time, a.time_paid as timePaid, a.status as status,"
            + " a.type as type, a.trans_type as transType, "
            + "a.reference_number as referenceNumber, a.terminal_code as terminalCode, "
            + "a.note, a.order_id as orderId "
            + "FROM transaction_receive a "
            + "WHERE a.bank_id = :bankId AND (a.terminal_code IN (:listCode) "
            + "OR (a.terminal_code IS NULL)) "
            + "AND a.time BETWEEN :fromDate AND :toDate AND status = 1 "
            + "ORDER BY a.time DESC LIMIT :offset, 20 ", nativeQuery = true)
    List<TransactionRelatedDTO> getAllTransTerminalWithType2(String bankId, List<String> listCode,
                                                             int offset, long fromDate, long toDate);

    @Query(value = "SELECT a.id AS id, a.amount AS amount, "
            + "a.content AS content "
            + "FROM transaction_receive a "
            + "WHERE a.order_id = :billNumber "
            + "AND a.bank_id = :bankId "
            + "AND a.time >= :fromTime AND a.time <= :toTime "
            + "LIMIT 1", nativeQuery = true)
    TransactionReceiveUpdateDTO getTransactionUpdateByBillNumber(String billNumber, String bankId, long fromTime, long toTime);

    @Transactional
    @Modifying
    @Query(value = "UPDATE transaction_receive SET qr_code = :qr, amount = :totalAmountAfterVat WHERE id = :id LIMIT 1 ", nativeQuery = true)
    int updateTransactionReceiveForInvoice(long totalAmountAfterVat, String qr, String id);

    @Query(value = "SELECT reference_number FROM transaction_receive "
            + "WHERE reference_number = :billId LIMIT 1", nativeQuery = true)
    String checkExistedBillId(String billId);

    @Query(value = "SELECT 1 AS type, a.amount AS amount, a.bill_id AS bill_id "
            + "FROM transaction_receive a "
            + "WHERE a.status = 0 AND a.bill_id IS NOT NULL AND a.bill_id != '' "
            + "ORDER BY RAND() "
            + "LIMIT 1", nativeQuery = true)
    CustomerInvoiceInfoDataDTO getTransactionReceiveCusInfo(String customerId);

    @Query(value = "SELECT a.* FROM transaction_receive a WHERE a.time >= :time "
            + "AND a.reference_number = :referenceNumber "
            + "AND a.trans_type = :transType LIMIT 1", nativeQuery = true)
    TransactionReceiveEntity getTransactionReceiveByRefNumber(String referenceNumber, String transType, long time);

    @Query(value = "SELECT a.id AS transactionId, a.amount AS amount, a.terminal_code AS terminalCode, "
            + "a.qr_code AS qrCode, a.time AS time, a.time_paid AS timePaid, "
            + "a.status AS status, a.type AS type, a.trans_type AS transType, "
            + "a.reference_number AS referenceNumber, a.order_id AS orderId, "
            + "a.content AS content, a.bank_account AS bankAccount "
            + "FROM transaction_receive a "
            + "WHERE "
            + "a.trans_type IN (:transType) AND a.bank_id = :bankId "
            + "AND a.time BETWEEN :fromDate AND :toDate "
            + "ORDER BY a.time DESC "
            + "LIMIT :offset, 20", nativeQuery = true)
    List<TransactionRelatedV2DTO> getTransactionsV2(String bankId, List<String> transType, long fromDate,
                                                    long toDate, int offset);

    @Query(value = "SELECT COALESCE(SUM(CASE WHEN a.trans_type = 'C' AND a.status = 1 THEN a.amount END), 0) AS totalCredit, "
            + "COALESCE(SUM(CASE WHEN a.trans_type = 'D' AND a.status = 1 THEN a.amount END), 0) AS totalDebit "
            + "FROM transaction_receive a "
            + "WHERE a.bank_id = :bankId "
            + "AND a.time BETWEEN :fromDate AND :toDate ", nativeQuery = true)
    ITransStatisticListExtra getExtraTransactionsV2(String bankId, long fromDate, long toDate);

    @Query(value = "SELECT COALESCE(COUNT(a.id), 0) "
            + "FROM transaction_receive a "
            + "WHERE "
            + "a.trans_type IN (:transType) AND a.bank_id = :bankId "
            + "AND a.time BETWEEN :fromDate AND :toDate ", nativeQuery = true)
    int countTransactionsV2(String bankId, List<String> transType, long fromDate, long toDate);

    @Query(value = "SELECT a.id, a.amount, a.bank_id as bankId, b.bank_account as bankAccount, a.content, a.status, "
            + "a.time, a.time_paid as timePaid, a.type, a.trans_type as transType, b.bank_account_name as userBankName, "
            + "c.img_id as imgId, a.reference_number as referenceNumber, a.service_code AS serviceCode, "
            + "a.hash_tag AS hashTag, a.qr_code AS qrCode, c.bank_code AS bankCode, c.bank_name AS bankName, "
            + "a.terminal_code as terminalCode, a.note, a.order_id as orderId, c.bank_short_name as bankShortName "
            + "FROM transaction_receive a "
            + "INNER JOIN account_bank_receive b "
            + "ON a.bank_id = b.id "
            + "INNER JOIN bank_type c "
            + "ON b.bank_type_id = c.id "
            + " WHERE a.id = :id LIMIT 1", nativeQuery = true)
    TransactionDetailV2DTO getTransactionV2ById(String id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE transaction_receive SET hash_tag = :hashTag "
            + "WHERE id = :transactionId LIMIT 1 ", nativeQuery = true)
    void updateHashTagTransaction(String hashTag, String transactionId);

    @Query(value = "SELECT a.id AS transactionId, a.amount AS amount, a.terminal_code AS terminalCode, "
            + "a.qr_code AS qrCode, a.time AS time, a.time_paid AS timePaid, "
            + "a.status AS status, a.type AS type, a.trans_type AS transType, "
            + "a.reference_number AS referenceNumber, a.order_id AS orderId, "
            + "a.content AS content, a.bank_account AS bankAccount "
            + "FROM transaction_receive a "
            + "WHERE "
            + "a.trans_type IN (:transType) AND a.bank_id = :bankId "
            + "AND a.terminal_code IN (:terminalCodes) "
            + "AND a.time BETWEEN :fromDate AND :toDate "
            + "ORDER BY a.time DESC "
            + "LIMIT :offset, 20", nativeQuery = true)
    List<TransactionRelatedV2DTO> getTransactionsListCodeV2(String bankId, List<String> terminalCodes, List<String> transType,
                                                            long fromDate, long toDate, int offset);

    @Query(value = "SELECT a.id AS transactionId, a.amount AS amount, a.terminal_code AS terminalCode, "
            + "a.qr_code AS qrCode, a.time AS time, a.time_paid AS timePaid, "
            + "a.status AS status, a.type AS type, a.trans_type AS transType, "
            + "a.reference_number AS referenceNumber, a.order_id AS orderId, "
            + "a.content AS content, a.bank_account AS bankAccount "
            + "FROM transaction_receive a "
            + "WHERE a.amount = :value "
            + "AND a.trans_type IN (:transType) AND a.bank_id = :bankId "
            + "AND a.time BETWEEN :fromDate AND :toDate "
            + "ORDER BY a.time DESC "
            + "LIMIT :offset, 20", nativeQuery = true)
    List<TransactionRelatedV2DTO> getTransactionsV2ByAmount(String bankId, List<String> transType, String value,
                                                            long fromDate, long toDate, int offset);

    @Query(value = "SELECT a.id AS transactionId, a.amount AS amount, a.terminal_code AS terminalCode, "
            + "a.qr_code AS qrCode, a.time AS time, a.time_paid AS timePaid, "
            + "a.status AS status, a.type AS type, a.trans_type AS transType, "
            + "a.reference_number AS referenceNumber, a.order_id AS orderId, "
            + "a.content AS content, a.bank_account AS bankAccount "
            + "FROM transaction_receive a "
            + "WHERE a.status = :value "
            + "AND a.trans_type IN (:transType) AND a.bank_id = :bankId "
            + "AND a.time BETWEEN :fromDate AND :toDate "
            + "ORDER BY a.time DESC "
            + "LIMIT :offset, 20", nativeQuery = true)
    List<TransactionRelatedV2DTO> getTransactionsV2ByStatus(String bankId, List<String> transType, String value,
                                                            long fromDate, long toDate, int offset);

    @Query(value = "SELECT a.id AS transactionId, a.amount AS amount, a.terminal_code AS terminalCode, "
            + "a.qr_code AS qrCode, a.time AS time, a.time_paid AS timePaid, "
            + "a.status AS status, a.type AS type, a.trans_type AS transType, "
            + "a.reference_number AS referenceNumber, a.order_id AS orderId, "
            + "a.content AS content, a.bank_account AS bankAccount "
            + "FROM transaction_receive a "
            + "WHERE a.terminal_code IN (:terminalCodes) "
            + "AND a.trans_type IN (:transType) AND a.bank_id = :bankId "
            + "AND a.time BETWEEN :fromDate AND :toDate "
            + "ORDER BY a.time DESC "
            + "LIMIT :offset, 20", nativeQuery = true)
    List<TransactionRelatedV2DTO> getTransactionsV2ByTerminalCode(String bankId, List<String> transType,
                                                                  List<String> terminalCodes, long fromDate,
                                                                  long toDate, int offset);

    @Query(value = "SELECT a.id AS transactionId, a.amount AS amount, a.terminal_code AS terminalCode, "
            + "a.qr_code AS qrCode, a.time AS time, a.time_paid AS timePaid, "
            + "a.status AS status, a.type AS type, a.trans_type AS transType, "
            + "a.reference_number AS referenceNumber, a.order_id AS orderId, "
            + "a.content AS content, a.bank_account AS bankAccount "
            + "FROM transaction_receive a "
            + "WHERE a.content LIKE %:value% "
            + "AND a.trans_type IN (:transType) AND a.bank_id = :bankId "
            + "AND a.time BETWEEN :fromDate AND :toDate "
            + "ORDER BY a.time DESC "
            + "LIMIT :offset, 20", nativeQuery = true)
    List<TransactionRelatedV2DTO> getTransactionsV2ByContent(String bankId, List<String> transType, String value,
                                                             long fromDate, long toDate, int offset);

    @Query(value = "SELECT a.id AS transactionId, a.amount AS amount, a.terminal_code AS terminalCode, "
            + "a.qr_code AS qrCode, a.time AS time, a.time_paid AS timePaid, "
            + "a.status AS status, a.type AS type, a.trans_type AS transType, "
            + "a.reference_number AS referenceNumber, a.order_id AS orderId, "
            + "a.content AS content, a.bank_account AS bankAccount "
            + "FROM transaction_receive a "
            + "WHERE a.reference_number = :value "
            + "AND a.trans_type IN (:transType) AND a.bank_id = :bankId "
            + "AND a.time BETWEEN :fromDate AND :toDate "
            + "ORDER BY a.time DESC "
            + "LIMIT :offset, 20", nativeQuery = true)
    List<TransactionRelatedV2DTO> getTransactionsV2ByFtCode(String bankId, List<String> transType, String value,
                                                            long fromDate, long toDate, int offset);

    @Query(value = "SELECT a.id AS transactionId, a.amount AS amount, a.terminal_code AS terminalCode, "
            + "a.qr_code AS qrCode, a.time AS time, a.time_paid AS timePaid, "
            + "a.status AS status, a.type AS type, a.trans_type AS transType, "
            + "a.reference_number AS referenceNumber, a.order_id AS orderId, "
            + "a.content AS content, a.bank_account AS bankAccount "
            + "FROM transaction_receive a "
            + "WHERE (a.order_id = :value OR a.content LIKE %:value%) "
            + "AND a.trans_type IN (:transType) AND a.bank_id = :bankId "
            + "AND a.time BETWEEN :fromDate AND :toDate "
            + "ORDER BY a.time DESC "
            + "LIMIT :offset, 20", nativeQuery = true)
    List<TransactionRelatedV2DTO> getTransactionsV2ByOrderId(String bankId, List<String> transType, String value,
                                                             long fromDate, long toDate, int offset);

    @Query(value = "SELECT a.id AS transactionId, a.amount AS amount, a.terminal_code AS terminalCode, "
            + "a.qr_code AS qrCode, a.time AS time, a.time_paid AS timePaid, "
            + "a.status AS status, a.type AS type, a.trans_type AS transType, "
            + "a.reference_number AS referenceNumber, a.order_id AS orderId, "
            + "a.content AS content, a.bank_account AS bankAccount "
            + "FROM transaction_receive a "
            + "WHERE a.amount = :value "
            + "AND a.trans_type IN (:transType) AND a.bank_id = :bankId "
            + "AND a.terminal_code IN (:terminalCodes) "
            + "AND a.time BETWEEN :fromDate AND :toDate "
            + "ORDER BY a.time DESC "
            + "LIMIT :offset, 20", nativeQuery = true)
    List<TransactionRelatedV2DTO> getTransactionsListCodeV2ByAmount(String bankId, List<String> terminalCodes,
                                                                    List<String> transType, String value,
                                                                    long fromDate, long toDate, int offset);

    @Query(value = "SELECT a.id AS transactionId, a.amount AS amount, a.terminal_code AS terminalCode, "
            + "a.qr_code AS qrCode, a.time AS time, a.time_paid AS timePaid, "
            + "a.status AS status, a.type AS type, a.trans_type AS transType, "
            + "a.reference_number AS referenceNumber, a.order_id AS orderId, "
            + "a.content AS content, a.bank_account AS bankAccount "
            + "FROM transaction_receive a "
            + "WHERE a.status = :value "
            + "AND a.trans_type IN (:transType) AND a.bank_id = :bankId "
            + "AND a.terminal_code IN (:terminalCodes) "
            + "AND a.time BETWEEN :fromDate AND :toDate "
            + "ORDER BY a.time DESC "
            + "LIMIT :offset, 20", nativeQuery = true)
    List<TransactionRelatedV2DTO> getTransactionsListCodeV2ByStatus(String bankId, List<String> terminalCodes,
                                                                    List<String> transType, String value,
                                                                    long fromDate, long toDate, int offset);

    @Query(value = "SELECT a.id AS transactionId, a.amount AS amount, a.terminal_code AS terminalCode, "
            + "a.qr_code AS qrCode, a.time AS time, a.time_paid AS timePaid, "
            + "a.status AS status, a.type AS type, a.trans_type AS transType, "
            + "a.reference_number AS referenceNumber, a.order_id AS orderId, "
            + "a.content AS content, a.bank_account AS bankAccount "
            + "FROM transaction_receive a "
            + "WHERE a.terminal_code IN (:value) "
            + "AND a.trans_type IN (:transType) AND a.bank_id = :bankId "
            + "AND a.terminal_code IN (:terminalCodes) "
            + "AND a.time BETWEEN :fromDate AND :toDate "
            + "ORDER BY a.time DESC "
            + "LIMIT :offset, 20", nativeQuery = true)
    List<TransactionRelatedV2DTO> getTransactionsListCodeV2ByTerminalCode(String bankId, List<String> terminalCodes,
                                                                          List<String> transType, List<String> value,
                                                                          long fromDate, long toDate, int offset);

    @Query(value = "SELECT a.id AS transactionId, a.amount AS amount, a.terminal_code AS terminalCode, "
            + "a.qr_code AS qrCode, a.time AS time, a.time_paid AS timePaid, "
            + "a.status AS status, a.type AS type, a.trans_type AS transType, "
            + "a.reference_number AS referenceNumber, a.order_id AS orderId, "
            + "a.content AS content, a.bank_account AS bankAccount "
            + "FROM transaction_receive a "
            + "WHERE a.content LIKE %:value% "
            + "AND a.trans_type IN (:transType) AND a.bank_id = :bankId "
            + "AND a.terminal_code IN (:terminalCodes) "
            + "AND a.time BETWEEN :fromDate AND :toDate "
            + "ORDER BY a.time DESC "
            + "LIMIT :offset, 20", nativeQuery = true)
    List<TransactionRelatedV2DTO> getTransactionsListCodeV2ByContent(String bankId, List<String> terminalCodes,
                                                                     List<String> transType, String value,
                                                                     long fromDate, long toDate, int offset);

    @Query(value = "SELECT a.id AS transactionId, a.amount AS amount, a.terminal_code AS terminalCode, "
            + "a.qr_code AS qrCode, a.time AS time, a.time_paid AS timePaid, "
            + "a.status AS status, a.type AS type, a.trans_type AS transType, "
            + "a.reference_number AS referenceNumber, a.order_id AS orderId, "
            + "a.content AS content, a.bank_account AS bankAccount "
            + "FROM transaction_receive a "
            + "WHERE (a.content LIKE %:value% OR a.order_id = :value) "
            + "AND a.trans_type IN (:transType) AND a.bank_id = :bankId "
            + "AND a.terminal_code IN (:terminalCodes) "
            + "AND a.time BETWEEN :fromDate AND :toDate "
            + "ORDER BY a.time DESC "
            + "LIMIT :offset, 20", nativeQuery = true)
    List<TransactionRelatedV2DTO> getTransactionsListCodeV2ByOrderId(String bankId, List<String> terminalCodes,
                                                                     List<String> transType, String value,
                                                                     long fromDate, long toDate, int offset);

    @Query(value = "SELECT a.id AS transactionId, a.amount AS amount, a.terminal_code AS terminalCode, "
            + "a.qr_code AS qrCode, a.time AS time, a.time_paid AS timePaid, "
            + "a.status AS status, a.type AS type, a.trans_type AS transType, "
            + "a.reference_number AS referenceNumber, a.order_id AS orderId, "
            + "a.content AS content, a.bank_account AS bankAccount "
            + "FROM transaction_receive a "
            + "WHERE a.reference_number = :value "
            + "AND a.trans_type IN (:transType) AND a.bank_id = :bankId "
            + "AND a.terminal_code IN (:terminalCodes) "
            + "AND a.time BETWEEN :fromDate AND :toDate "
            + "ORDER BY a.time DESC "
            + "LIMIT :offset, 20", nativeQuery = true)
    List<TransactionRelatedV2DTO> getTransactionsListCodeV2ByReferenceNumber(String bankId, List<String> terminalCodes,
                                                                             List<String> transType, String value,
                                                                             long fromDate, long toDate, int offset);

    @Query(value = "SELECT COALESCE(SUM(CASE WHEN a.trans_type = 'C' AND a.status = 1 THEN a.amount END), 0) AS totalCredit, "
            + "COALESCE(SUM(CASE WHEN a.trans_type = 'D' AND a.status = 1 THEN a.amount END), 0) AS totalDebit "
            + "FROM transaction_receive a "
            + "WHERE a.bank_id = :bankId AND a.terminal_code IN (:terminalCodes) "
            + "AND a.time BETWEEN :fromDate AND :toDate ", nativeQuery = true)
    ITransStatisticListExtra getExtraTransactionsByListCodeV2(String bankId, List<String> terminalCodes, long fromDate, long toDate);

    @Query(value = "SELECT id FROM transaction_receive "
            + "WHERE bank_id = :bankId AND order_id = :orderId AND status != 2 LIMIT 1", nativeQuery = true)
    String checkExistedOrderId(String bankId, String orderId);

    @Query(value = "SELECT "
            + "COALESCE(SUM(CASE WHEN status = 1 AND trans_type = 'C' then amount END), 0) AS totalCredit, "
            + "COALESCE(COUNT(CASE WHEN status = 1 AND trans_type = 'C' then 1 END), 0) AS countCredit, "
            + "COALESCE(SUM(CASE WHEN status = 1 AND trans_type = 'D' then amount END), 0) AS totalDebit, "
            + "COALESCE(COUNT(CASE WHEN status = 1 AND trans_type = 'D' then 1 END), 0) AS countDebit "
            + "FROM transaction_receive "
            + "WHERE bank_id = :bankId AND time BETWEEN :fromDate AND :toDate LIMIT 1", nativeQuery = true)
    TransStatisticV2DTO getTransactionOverviewV2(String bankId, long fromDate, long toDate);

    @Query(value = "SELECT a.id AS transactionId, a.amount AS amount, "
            + " a.time AS time, a.time_paid AS timePaid, "
            + "a.status AS status, a.type AS type, a.trans_type AS transType "
            + "FROM transaction_receive a "
            + "WHERE a.bank_id = :bankId "
            + "AND a.time >= :time "
            + "ORDER BY a.time DESC "
            + "LIMIT :limit", nativeQuery = true)
    List<ITransactionLatestDTO> getTransactionLastest(String bankId, int limit, long time);

    @Transactional
    @Modifying
    @Query(value = "UPDATE transaction_receive SET type = :type, sub_code = :subCode, " +
            "terminal_code = :terminalCode, order_id = :orderId WHERE reference_number = :ftCode AND trans_type = 'D' LIMIT 1 ", nativeQuery = true)
    void updateTransactionRefundStatus(String ftCode, String subCode, String terminalCode, String orderId, int type);


    @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, "
            + "a.content, a.order_id as orderId, a.reference_number as referenceNumber, a.status, "
            + "a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
            + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
            + "a.terminal_code as terminalCode, a.note "
            + "FROM transaction_receive a "
            + "LEFT JOIN account_bank_receive b ON a.bank_id = b.id "
            + "LEFT JOIN bank_type c ON b.bank_type_id = c.id "
            + "WHERE a.bank_account = :value AND a.time >= :time "
            + "ORDER BY a.time DESC LIMIT :offset, :size", nativeQuery = true)
    List<TransactionReceiveAdminListDTO> getTransByBankAccountAllDate(
            @Param("value") String value,
            @Param("offset") int offset,
            @Param("size") int size,
            @Param("time") long time);

    @Query(value = "SELECT COUNT(a.id) FROM transaction_receive a "
            + "WHERE a.bank_account = :value AND a.time >= :time", nativeQuery = true)
    int countTransByBankAccountAllDate(@Param("value") String value, @Param("time") long time);


    @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, "
            + "a.content, a.order_id as orderId, a.reference_number as referenceNumber, a.status, "
            + "a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
            + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
            + "a.terminal_code as terminalCode, a.note "
            + "FROM transaction_receive a "
            + "LEFT JOIN account_bank_receive b ON a.bank_id = b.id "
            + "LEFT JOIN bank_type c ON b.bank_type_id = c.id "
            + "WHERE a.bank_account = :value AND a.time BETWEEN :fromDate AND :toDate "
            + "ORDER BY a.time DESC LIMIT :offset, :size", nativeQuery = true)
    List<TransactionReceiveAdminListDTO> getTransByBankAccountFromDate(
            @Param("value") String value,
            @Param("fromDate") long fromDate,
            @Param("toDate") long toDate,
            @Param("offset") int offset,
            @Param("size") int size);


    @Query(value = "SELECT COUNT(a.id) FROM transaction_receive a "
            + "WHERE a.bank_account = :value AND a.time BETWEEN :fromDate AND :toDate", nativeQuery = true)
    int countTransByBankAccountFromDate(@Param("value") String value,
                                        @Param("fromDate") long fromDate,
                                        @Param("toDate") long toDate);

    @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, "
            + "a.content, a.order_id as orderId, a.reference_number as referenceNumber, a.status, "
            + "a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
            + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
            + "a.terminal_code as terminalCode, a.note "
            + "FROM transaction_receive a "
            + "LEFT JOIN account_bank_receive b ON a.bank_id = b.id "
            + "LEFT JOIN bank_type c ON b.bank_type_id = c.id "
            + "WHERE a.reference_number LIKE %:value% AND a.time >= :time "
            + "ORDER BY a.time DESC LIMIT :offset, :size", nativeQuery = true)
    List<TransactionReceiveAdminListDTO> getTransByFtCode(
            @Param("value") String value,
            @Param("offset") int offset,
            @Param("size") int size,
            @Param("time") long time);


    @Query(value = "SELECT COUNT(a.id) FROM transaction_receive a "
            + "WHERE a.reference_number LIKE %:value% AND a.time >= :time", nativeQuery = true)
    int countTransByFtCode(@Param("value") String value, @Param("time") long time);


    @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, "
            + "a.content, a.order_id as orderId, a.reference_number as referenceNumber, a.status, "
            + "a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
            + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
            + "a.terminal_code as terminalCode, a.note "
            + "FROM transaction_receive a "
            + "LEFT JOIN account_bank_receive b ON a.bank_id = b.id "
            + "LEFT JOIN bank_type c ON b.bank_type_id = c.id "
            + "WHERE a.reference_number LIKE %:value% AND a.time BETWEEN :fromDate AND :toDate "
            + "ORDER BY a.time DESC LIMIT :offset, :size", nativeQuery = true)
    List<TransactionReceiveAdminListDTO> getTransByFtCode(
            @Param("value") String value,
            @Param("offset") int offset,
            @Param("fromDate") long fromDate,
            @Param("toDate") long toDate,
            @Param("size") int size);

    @Query(value = "SELECT COUNT(a.id) FROM transaction_receive a "
            + "WHERE a.reference_number LIKE %:value% AND a.time BETWEEN :fromDate AND :toDate", nativeQuery = true)
    int countTransByFtCode(@Param("value") String value,
                           @Param("fromDate") long fromDate,
                           @Param("toDate") long toDate);

    @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, "
            + "a.content, a.order_id as orderId, a.reference_number as referenceNumber, a.status, "
            + "a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
            + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
            + "a.terminal_code as terminalCode, a.note "
            + "FROM transaction_receive a "
            + "LEFT JOIN account_bank_receive b ON a.bank_id = b.id "
            + "LEFT JOIN bank_type c ON b.bank_type_id = c.id "
            + "WHERE a.order_id LIKE %:value% AND a.time >= :time "
            + "ORDER BY a.time DESC LIMIT :offset, :size", nativeQuery = true)
    List<TransactionReceiveAdminListDTO> getTransByOrderId(
            @Param("value") String value,
            @Param("offset") int offset,
            @Param("size") int size,
            @Param("time") long time);

    @Query(value = "SELECT COUNT(a.id) FROM transaction_receive a "
            + "WHERE a.order_id LIKE %:value% AND a.time >= :time", nativeQuery = true)
    int countTransByOrderId(@Param("value") String value, @Param("time") long time);

    @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, "
            + "a.content, a.order_id as orderId, a.reference_number as referenceNumber, a.status, "
            + "a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
            + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
            + "a.terminal_code as terminalCode, a.note "
            + "FROM transaction_receive a "
            + "LEFT JOIN account_bank_receive b ON a.bank_id = b.id "
            + "LEFT JOIN bank_type c ON b.bank_type_id = c.id "
            + "WHERE a.content LIKE %:value% AND a.time >= :time "
            + "ORDER BY a.time DESC LIMIT :offset, :size", nativeQuery = true)
    List<TransactionReceiveAdminListDTO> getTransByContent(
            @Param("value") String value,
            @Param("offset") int offset,
            @Param("size") int size,
            @Param("time") long time);

    @Query(value = "SELECT COUNT(a.id) FROM transaction_receive a "
            + "WHERE a.content LIKE %:value% AND a.time >= :time", nativeQuery = true)
    int countTransByContent(@Param("value") String value, @Param("time") long time);

    @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, "
            + "a.content, a.order_id as orderId, a.reference_number as referenceNumber, a.status, "
            + "a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
            + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
            + "a.terminal_code as terminalCode, a.note "
            + "FROM transaction_receive a "
            + "LEFT JOIN account_bank_receive b ON a.bank_id = b.id "
            + "LEFT JOIN bank_type c ON b.bank_type_id = c.id "
            + "WHERE a.content LIKE %:value% AND a.time BETWEEN :fromDate AND :toDate "
            + "ORDER BY a.time DESC LIMIT :offset, :size", nativeQuery = true)
    List<TransactionReceiveAdminListDTO> getTransByContent(
            @Param("value") String value,
            @Param("fromDate") long fromDate,
            @Param("toDate") long toDate,
            @Param("offset") int offset,
            @Param("size") int size);

    @Query(value = "SELECT COUNT(a.id) FROM transaction_receive a "
            + "WHERE a.content LIKE %:value% AND a.time BETWEEN :fromDate AND :toDate", nativeQuery = true)
    int countTransByContent(@Param("value") String value,
                            @Param("fromDate") long fromDate,
                            @Param("toDate") long toDate);

    @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, "
            + "a.content, a.order_id as orderId, a.reference_number as referenceNumber, a.status, "
            + "a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
            + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
            + "a.terminal_code as terminalCode, a.note "
            + "FROM transaction_receive a "
            + "LEFT JOIN account_bank_receive b ON a.bank_id = b.id "
            + "LEFT JOIN bank_type c ON b.bank_type_id = c.id "
            + "WHERE a.terminal_code LIKE %:value% AND a.time >= :time "
            + "ORDER BY a.time DESC LIMIT :offset, :size", nativeQuery = true)
    List<TransactionReceiveAdminListDTO> getTransByTerminalCode(
            @Param("value") String value,
            @Param("offset") int offset,
            @Param("size") int size,
            @Param("time") long time);

    @Query(value = "SELECT COUNT(a.id) FROM transaction_receive a "
            + "WHERE a.terminal_code LIKE %:value% AND a.time >= :time", nativeQuery = true)
    int countTransByTerminalCode(@Param("value") String value, @Param("time") long time);


    @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, "
            + "a.content, a.order_id as orderId, a.reference_number as referenceNumber, a.status, "
            + "a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
            + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
            + "a.terminal_code as terminalCode, a.note "
            + "FROM transaction_receive a "
            + "LEFT JOIN account_bank_receive b ON a.bank_id = b.id "
            + "LEFT JOIN bank_type c ON b.bank_type_id = c.id "
            + "WHERE a.terminal_code LIKE %:value% AND a.time BETWEEN :fromDate AND :toDate "
            + "ORDER BY a.time DESC LIMIT :offset, :size", nativeQuery = true)
    List<TransactionReceiveAdminListDTO> getTransByTerminalCode(
            @Param("value") String value,
            @Param("fromDate") long fromDate,
            @Param("toDate") long toDate,
            @Param("offset") int offset,
            @Param("size") int size);

    @Query(value = "SELECT COUNT(a.id) FROM transaction_receive a "
            + "WHERE a.terminal_code LIKE %:value% AND a.time BETWEEN :fromDate AND :toDate", nativeQuery = true)
    int countTransByTerminalCode(@Param("value") String value,
                                 @Param("fromDate") long fromDate,
                                 @Param("toDate") long toDate);


    @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, "
            + "a.content, a.order_id as orderId, a.reference_number as referenceNumber, a.status, "
            + "a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
            + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
            + "a.terminal_code as terminalCode, a.note "
            + "FROM transaction_receive a "
            + "LEFT JOIN account_bank_receive b ON a.bank_id = b.id "
            + "LEFT JOIN bank_type c ON b.bank_type_id = c.id "
            + "WHERE a.time >= :time "
            + "ORDER BY a.time DESC LIMIT :offset, :size", nativeQuery = true)
    List<TransactionReceiveAdminListDTO> getAllTransAllDate(
            @Param("offset") int offset,
            @Param("size") int size,
            @Param("time") long time);

    @Query(value = "SELECT COUNT(a.id) FROM transaction_receive a WHERE a.time >= :time", nativeQuery = true)
    int countAllTransAllDate(@Param("time") long time);

    @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, "
            + "a.content, a.order_id as orderId, a.reference_number as referenceNumber, a.status, "
            + "a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
            + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
            + "a.terminal_code as terminalCode, a.note "
            + "FROM transaction_receive a "
            + "LEFT JOIN account_bank_receive b ON a.bank_id = b.id "
            + "LEFT JOIN bank_type c ON b.bank_type_id = c.id "
            + "WHERE a.time BETWEEN :fromDate AND :toDate "
            + "ORDER BY a.time DESC LIMIT :offset, :size", nativeQuery = true)
    List<TransactionReceiveAdminListDTO> getAllTransFromDate(
            @Param("fromDate") long fromDate,
            @Param("toDate") long toDate,
            @Param("offset") int offset,
            @Param("size") int size);

    @Query(value = "SELECT COUNT(a.id) FROM transaction_receive a WHERE a.time BETWEEN :fromDate AND :toDate", nativeQuery = true)
    int countAllTransFromDate(@Param("fromDate") long fromDate, @Param("toDate") long toDate);

    @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, "
            + "a.content, a.order_id as orderId, a.reference_number as referenceNumber, a.status, "
            + "a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
            + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
            + "a.terminal_code as terminalCode, a.note "
            + "FROM transaction_receive a "
            + "LEFT JOIN account_bank_receive b ON a.bank_id = b.id "
            + "LEFT JOIN bank_type c ON b.bank_type_id = c.id "
            + "WHERE a.terminal_code = :value AND a.time BETWEEN :fromDate AND :toDate "
            + "ORDER BY a.time DESC LIMIT :offset, :size", nativeQuery = true)
    List<TransactionReceiveAdminListDTO> getTransByTerminalCodeFromDate(
            @Param("value") String value,
            @Param("fromDate") long fromDate,
            @Param("toDate") long toDate,
            @Param("offset") int offset,
            @Param("size") int size);

    @Query(value = "SELECT COUNT(a.id) FROM transaction_receive a "
            + "WHERE a.terminal_code = :value AND a.time BETWEEN :fromDate AND :toDate", nativeQuery = true)
    int countTransByTerminalCodeFromDate(
            @Param("value") String value,
            @Param("fromDate") long fromDate,
            @Param("toDate") long toDate);

    @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.amount, a.bank_id as bankId, "
            + "a.content, a.order_id as orderId, a.reference_number as referenceNumber, a.status, "
            + "a.time as timeCreated, a.time_paid as timePaid, a.trans_type as transType, "
            + "a.type, b.bank_account_name as userBankName, c.bank_short_name as bankShortName, "
            + "a.terminal_code as terminalCode, a.note "
            + "FROM transaction_receive a "
            + "LEFT JOIN account_bank_receive b ON a.bank_id = b.id "
            + "LEFT JOIN bank_type c ON b.bank_type_id = c.id "
            + "WHERE a.terminal_code = :value AND a.time >= :time "
            + "ORDER BY a.time DESC LIMIT :offset, :size", nativeQuery = true)
    List<TransactionReceiveAdminListDTO> getTransByTerminalCodeAllDate(
            @Param("value") String value,
            @Param("offset") int offset,
            @Param("size") int size,
            @Param("time") long time);

    @Query(value = "SELECT COUNT(a.id) FROM transaction_receive a "
            + "WHERE a.terminal_code = :value AND a.time >= :time", nativeQuery = true)
    int countTransByTerminalCodeAllDate(
            @Param("value") String value,
            @Param("time") long time);

    @Query(value = "SELECT "
            + "COALESCE(SUM(CASE WHEN status = 1 AND trans_type = 'C' then amount END), 0) AS totalCredit, "
            + "COALESCE(COUNT(CASE WHEN status = 1 AND trans_type = 'C' then 1 END), 0) AS countCredit, "
            + "COALESCE(SUM(CASE WHEN status = 1 AND trans_type = 'D' then amount END), 0) AS totalDebit, "
            + "COALESCE(COUNT(CASE WHEN status = 1 AND trans_type = 'D' then 1 END), 0) AS countDebit "
            + "FROM transaction_receive "
            + "WHERE bank_id = :bankId AND time BETWEEN :fromDate AND :toDate AND terminal_code = :terminalCode LIMIT 1", nativeQuery = true)
    TransStatisticV2DTO getTransactionOverviewV2ByTerminalCode(String bankId, String terminalCode, long fromDate, long toDate);

    @Query(value = "SELECT "
            + "id AS transactionId, "
            + "amount AS amount, "
            + "time_paid AS timePaid, "
            + "reference_number AS referenceNumber, "
            + "content AS content, "
            + "order_id AS orderId "
            + "FROM transaction_receive "
            + "WHERE bank_account = :bankAccount "
            + "AND (time BETWEEN :fromDate AND :toDate) "
            + "AND status = 1"
            , nativeQuery = true)
    List<ITransactionReceiveAdminInfoDTO> getTransactionReceiveToMapInvoice(
            @Param("bankAccount") String bankAccount,
            @Param("fromDate") long fromDate,
            @Param("toDate") long toDate
    );
}

