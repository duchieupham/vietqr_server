package com.vietqr.org.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.TerminalBankEntity;

@Repository
public interface TerminalBankRepository extends JpaRepository<TerminalBankEntity, Long> {

    @Query(value = "SELECT * FROM terminal_bank WHERE terminal_id = :terminalId ", nativeQuery = true)
    TerminalBankEntity getTerminalBankByTerminalId(@Param(value = "terminalId") String terminalId);

    @Query(value = "SELECT * FROM terminal_bank WHERE bank_account_raw_number = :bankAccount LIMIT 1", nativeQuery = true)
    TerminalBankEntity getTerminalBankByBankAccount(@Param(value = "bankAccount") String bankAccount);

    @Query(value = "SELECT terminal_id FROM terminal_bank WHERE bank_account_raw_number = :bankAccount", nativeQuery = true)
    String getTerminalIdByBankAccount(@Param(value = "bankAccount") String bankAccount);

    @Query(value = "SELECT terminal_address FROM terminal_bank WHERE terminal_address = :address", nativeQuery = true)
    String checkExistedTerminalAddress(@Param(value = "address") String address);

    @Query(value = "SELECT COUNT(id) as totalTerminal "
            + "FROM terminal_bank ", nativeQuery = true)
    Integer getTerminalCounting();

    @Query(value = "SELECT bank_account_raw_number FROM terminal_bank WHERE terminal_id = :terminalLabel ", nativeQuery = true)
    String getBankAccountByTerminalLabel(@Param(value = "terminalLabel") String terminalLabel);
}

