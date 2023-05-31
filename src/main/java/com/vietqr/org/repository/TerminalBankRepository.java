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

}
