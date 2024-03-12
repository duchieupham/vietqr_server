package com.vietqr.org.repository;

import com.vietqr.org.entity.TerminalBankReceiveEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TerminalBankReceiveRepository extends JpaRepository<TerminalBankReceiveEntity, Long> {
    @Query(value = "SELECT * FROM terminal_bank_receive " +
            "WHERE terminal_id = :terminalId AND bank_id = :bankId LIMIT 1", nativeQuery = true)
    TerminalBankReceiveEntity getTerminalBankReceiveByTerminalIdAndBankId(String terminalId, String bankId);

    @Query(value = "SELECT terminal_id FROM terminal_bank_receive " +
            "WHERE trace_transfer = :traceTransfer LIMIT 1 ", nativeQuery = true)
    String getTerminalByTraceTransfer(String traceTransfer);
}
