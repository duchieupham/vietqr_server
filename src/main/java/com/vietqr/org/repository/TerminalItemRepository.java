package com.vietqr.org.repository;

import com.vietqr.org.entity.TerminalItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface TerminalItemRepository extends JpaRepository<TerminalItemEntity, String> {

    @Query(value = "SELECT * FROM terminal_item WHERE trace_transfer = :traceTransfer "
            + "AND amount = :amount AND service_code = :serviceCode LIMIT 1", nativeQuery = true)
    TerminalItemEntity getTerminalItemByTraceTransferAndAmount(String traceTransfer, String amount, String serviceCode);

    @Query(value = "SELECT * FROM terminal_item WHERE bank_id = :bankId "
            + "AND terminal_code = :terminalCode AND raw_service_code = :serviceCode LIMIT 1", nativeQuery = true)
    TerminalItemEntity getItemByBankAndServiceCode(String bankId, String serviceCode, String terminalCode);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM terminal_item WHERE id = :id LIMIT 1 ", nativeQuery = true)
    void removeById(String id);

    @Query(value = "SELECT id FROM terminal_item WHERE bank_id = :bankId "
            + "AND terminal_code = :terminalCode AND raw_service_code = :serviceCode", nativeQuery = true)
    String existsByIdServiceCodeTerminalCode(String bankId, String serviceCode, String terminalCode);

    @Query(value = "SELECT * FROM terminal_item "
            + "WHERE service_code = :serviceCode AND amount = :amount LIMIT 1", nativeQuery = true)
    TerminalItemEntity getTerminalItemByServiceCode(String serviceCode, long amount);
}
