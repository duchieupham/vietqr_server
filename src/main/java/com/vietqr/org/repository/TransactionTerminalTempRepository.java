package com.vietqr.org.repository;

import com.vietqr.org.dto.*;
import com.vietqr.org.entity.TransactionTerminalTempEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionTerminalTempRepository extends JpaRepository<TransactionTerminalTempEntity, Long> {
    @Query(value = "SELECT t FROM transaction_terminal_temp t WHERE " +
            "t.terminal_code = :terminalCode AND t.time BETWEEN :fromTime AND :toTime", nativeQuery = true)
    List<TransactionTerminalTempEntity> findAllByTerminalCodeAndTimeBetween(String terminalCode, long fromTime, long toTime);

    @Query(value = "SELECT COALESCE(COUNT(id), 0) AS totalTrans, COALESCE(SUM(amount), 0) AS totalAmount " +
            "FROM transaction_terminal_temp " +
            "WHERE terminal_code IN (:terminalCode) AND time BETWEEN :fromTime AND :toTime", nativeQuery = true)
    RevenueTerminalDTO getTotalTranByTerminalCodeAndTimeBetween(List<String> terminalCode, long fromTime, long toTime);

    @Query(value = "SELECT COALESCE(COUNT(a.id), 0) AS totalTrans, COALESCE(SUM(a.amount), 0) AS totalAmount " +
            "FROM transaction_terminal_temp a " +
            "WHERE a.terminal_code IN (:terminalCode) AND time >= :fromDate AND time <= :toDate LIMIT 1", nativeQuery = true)
    IStatisticMerchantDTO getStatisticMerchantByDate(List<String> terminalCode, long fromDate, long toDate);

    @Query(value = "SELECT COALESCE(COUNT(a.id), 0) AS totalTrans, COALESCE(SUM(a.amount), 0) AS totalAmount, " +
            "DATE_FORMAT(CONVERT_TZ(FROM_UNIXTIME((FLOOR(time / 7200))*7200), '+00:00', '+07:00'), '%Y-%m-%d %H:00') AS timeDate " +
            "FROM transaction_terminal_temp a " +
            "INNER JOIN terminal b ON b.code = a.terminal_code " +
            "INNER JOIN account_bank_receive_share c ON c.terminal_id = b.id " +
            "WHERE c.user_id = :userId AND time >= :fromDate AND time <= :toDate " +
            "GROUP BY timeDate ORDER BY timeDate ASC ", nativeQuery = true)
    List<IStatisticTerminalDTO> getStatisticMerchantByDateEveryHour(String userId, long fromDate, long toDate);

    @Query(value = "SELECT COALESCE(a.totalTrans, 0) AS totalTrans, COALESCE(a.totalAmount, 0) AS totalAmount, " +
            "b.code AS terminalCode, b.name AS terminalName, b.id AS terminalId, b.address AS terminalAddress " +
            "FROM terminal b " +
            "LEFT JOIN (SELECT terminal_code, COALESCE(COUNT(id), 0) AS totalTrans, " +
            "COALESCE(SUM(amount), 0) AS totalAmount FROM transaction_terminal_temp " +
            "WHERE time >= :fromDate AND time <= :toDate GROUP BY terminal_code) a ON b.code = a.terminal_code " +
            "INNER JOIN account_bank_receive_share c ON c.terminal_id = b.id " +
            "WHERE c.user_id = :userId " +
            "ORDER BY totalAmount DESC LIMIT :pageSize", nativeQuery = true)
    List<ITopTerminalDTO> getTopTerminalByDate(String userId, long fromDate, long toDate, int pageSize);

    @Query(value = "SELECT COALESCE(COUNT(ab.id), 0) AS totalTrans, COALESCE(SUM(ab.amount), 0) AS totalAmount " +
            "FROM (SELECT a.id, a.amount FROM transaction_terminal_temp a " +
            "WHERE a.terminal_code IN (:terminalCode) AND time >= :fromDate AND time <= :toDate) AS ab", nativeQuery = true)
    RevenueTerminalDTO getTotalTranByUserAndTimeBetween(List<String> terminalCode, long fromDate, long toDate);

    @Query(value = "SELECT COALESCE(a.totalTrans, 0) AS totalTrans, COALESCE(a.totalAmount, 0) AS totalAmount, " +
            "b.code AS terminalCode, b.name AS terminalName, b.id AS terminalId, b.address AS terminalAddress " +
            "FROM terminal b " +
            "LEFT JOIN (SELECT terminal_code, COALESCE(COUNT(id), 0) AS totalTrans, " +
            "COALESCE(SUM(amount), 0) AS totalAmount FROM transaction_terminal_temp " +
            "WHERE time >= :fromDate AND time <= :toDate GROUP BY terminal_code) a ON b.code = a.terminal_code " +
            "INNER JOIN (SELECT DISTINCT c.user_id, c.terminal_id FROM account_bank_receive_share c WHERE c.user_id = :userId) c ON c.terminal_id = b.id " +
            "ORDER BY b.code ASC LIMIT :offset, 10", nativeQuery = true)
    List<IStatisticTerminalOverViewDTO> getStatisticMerchantByDateEveryTerminal(String userId, long fromDate, long toDate, int offset);

    @Query(value = "SELECT * FROM transaction_terminal_temp "
            + "WHERE transaction_id = :transactionId LIMIT 1", nativeQuery = true)
    TransactionTerminalTempEntity findByTransactionId(String transactionId);

//    IStatisticMerchantDTO getStatisticMerchantByMerchantAndUserId(String merchantId, String userId, long fromDate, long toDate);
}