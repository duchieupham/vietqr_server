package com.vietqr.org.repository;

import com.vietqr.org.entity.TerminalBankReceiveEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TerminalBankReceiveRepository extends JpaRepository<TerminalBankReceiveEntity, Long> {
    @Query(value = "SELECT * FROM terminal_bank_receive " +
            "WHERE terminal_id = :terminalId AND bank_id = :bankId LIMIT 1", nativeQuery = true)
    TerminalBankReceiveEntity getTerminalBankReceiveByTerminalIdAndBankId(String terminalId, String bankId);

    @Query(value = "SELECT terminal_id FROM terminal_bank_receive " +
            "WHERE trace_transfer = :traceTransfer LIMIT 1 ", nativeQuery = true)
    String getTerminalByTraceTransfer(String traceTransfer);

    @Query(value = "SELECT * FROM terminal_bank_receive " +
            "WHERE terminal_id = :terminalId LIMIT 1", nativeQuery = true)
    TerminalBankReceiveEntity getTerminalBankReceiveByTerminalId(String terminalId);

    @Query(value = "SELECT terminal_code FROM terminal_bank_receive " +
            "WHERE terminal_code = :code LIMIT 1 ", nativeQuery = true)
    String checkExistedTerminalCode(String code);

    @Query(value = "SELECT * FROM terminal_bank_receive " +
            "WHERE terminal_id = :terminalId LIMIT 1", nativeQuery = true)
    TerminalBankReceiveEntity getTerminalBankByTerminalId(String terminalId);

    @Query(value = "SELECT terminal_code FROM terminal_bank_receive " +
            "WHERE raw_terminal_code = :value LIMIT 1", nativeQuery = true)
    String getTerminalCodeByRawTerminalCode(String value);

    @Query(value = "SELECT a.terminal_code FROM terminal_bank_receive a "
            + "INNER JOIN terminal b ON b.id = a.terminal_id "
            + "WHERE b.code = :terminalCodeForSearch AND a.terminal_code != '' "
            + "AND a.terminal_code IS NOT NULL", nativeQuery = true)
    List<String> getTerminalCodeByMainTerminalCode(String terminalCodeForSearch);

    @Query(value = "SELECT * FROM terminal_bank_receive " +
            "WHERE trace_transfer = :traceTransfer AND trace_transfer != '' LIMIT 1", nativeQuery = true)
    TerminalBankReceiveEntity getTerminalBankReceiveByTraceTransfer(String traceTransfer);

    @Query(value = "SELECT a.terminal_code FROM terminal_bank_receive a "
            + "INNER JOIN terminal b ON b.id = a.terminal_id "
            + "WHERE b.code IN (:terminalCodeAccess) AND a.terminal_code != '' "
            + "AND a.terminal_code IS NOT NULL", nativeQuery = true)
    List<String> getTerminalCodeByMainTerminalCodeList(List<String> terminalCodeAccess);

    @Query(value = "SELECT raw_terminal_code FROM terminal_bank_receive " +
            "WHERE terminal_code = :terminalCode LIMIT 1", nativeQuery = true)
    String getTerminalBankReceiveByTerminalCode(String terminalCode);
}
