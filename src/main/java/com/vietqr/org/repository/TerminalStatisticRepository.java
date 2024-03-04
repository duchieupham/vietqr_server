package com.vietqr.org.repository;

import com.vietqr.org.entity.TerminalStatisticEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface TerminalStatisticRepository extends JpaRepository<TerminalStatisticEntity, String> {
    @Query(value = "SELECT * FROM terminal_statistic t " +
            "WHERE t.terminal_id = :terminalId AND t.time = :time LIMIT 1", nativeQuery = true)
    TerminalStatisticEntity findByTerminalIdAndTime(String terminalId, long time);

    @Transactional
    @Modifying
    @Query(value = "UPDATE terminal_statistic t " +
            "SET t.total = :totalTrans, t.total_amount = :totalAmount " +
            "WHERE t.terminal_id = :terminalId AND t.time = :time AND t.version = :version", nativeQuery = true)
    int updateByTerminalIdAndTimeAndVersion(String terminalId, long time, int version, int totalTrans, long totalAmount);

    @Query(value = "SELECT t.total_amount FROM terminal_statistic t " +
            "WHERE t.terminal_id = :terminalId AND t.time = :time", nativeQuery = true)
    long getTotalAmountPrevious(String terminalId, long time);
}
