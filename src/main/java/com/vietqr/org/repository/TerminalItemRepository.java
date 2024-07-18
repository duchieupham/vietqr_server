package com.vietqr.org.repository;

import com.vietqr.org.entity.TerminalItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TerminalItemRepository extends JpaRepository<TerminalItemEntity, String> {

    @Query(value = "SELECT * FROM terminal_item WHERE trace_transfer = :traceTransfer "
            + "AND amount = :amount AND service_code = :serviceCode LIMIT 1", nativeQuery = true)
    TerminalItemEntity getTerminalItemByTraceTransferAndAmount(String traceTransfer, String amount, String serviceCode);
}
