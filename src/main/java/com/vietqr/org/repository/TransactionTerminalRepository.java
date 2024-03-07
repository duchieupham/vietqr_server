package com.vietqr.org.repository;

import com.vietqr.org.dto.RevenueTerminalDTO;
import com.vietqr.org.entity.TransactionTerminalTempEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionTerminalRepository extends JpaRepository<TransactionTerminalTempEntity, Long> {
    @Query(value = "SELECT t FROM transaction_terminal t WHERE " +
            "t.terminal_code = :terminalCode AND t.time BETWEEN :fromTime AND :toTime", nativeQuery = true)
    List<TransactionTerminalTempEntity> findAllByTerminalCodeAndTimeBetween(String terminalCode, long fromTime, long toTime);

    @Query(value = "SELECT COALESCE(COUNT(id), 0) AS totalTrans, COALESCE(SUM(amount), 0) AS totalAmount " +
            "FROM transaction_terminal " +
            "WHERE terminal_code = :terminalCode AND time BETWEEN :fromTime AND :toTime", nativeQuery = true)
    RevenueTerminalDTO getTotalTranByTerminalCodeAndTimeBetween(String terminalCode, long fromTime, long toTime);
}