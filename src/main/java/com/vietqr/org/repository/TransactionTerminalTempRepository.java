package com.vietqr.org.repository;

import com.vietqr.org.dto.IStatisticMerchantDTO;
import com.vietqr.org.dto.IStatisticTerminalDTO;
import com.vietqr.org.dto.ITopTerminalDTO;
import com.vietqr.org.dto.RevenueTerminalDTO;
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
            "WHERE terminal_code = :terminalCode AND time BETWEEN :fromTime AND :toTime", nativeQuery = true)
    RevenueTerminalDTO getTotalTranByTerminalCodeAndTimeBetween(String terminalCode, long fromTime, long toTime);

    @Query(value = "SELECT COALESCE(COUNT(a.id), 0) AS totalTrans, COALESCE(SUM(a.amount), 0) AS totalAmount " +
            "FROM transaction_terminal_temp a " +
            "INNER JOIN terminal b ON b.code = a.terminal_code " +
            "INNER JOIN account_bank_receive_share c ON c.terminal_id = b.id " +
            "WHERE c.user_id = :userId AND time >= :fromDate AND time <= :toDate", nativeQuery = true)
    IStatisticMerchantDTO getStatisticMerchantByDate(String userId, long fromDate, long toDate);

    List<IStatisticTerminalDTO> getStatisticMerchantByDateEveryHour(String userId, long fromDate, long toDate);

    List<ITopTerminalDTO> getTop5TerminalByDate(String userId, long fromDate, long toDate);
}