package com.vietqr.org.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.TerminalAddressEntity;

import javax.transaction.Transactional;

@Repository
public interface TerminalAddressRepository extends JpaRepository<TerminalAddressEntity, Long> {

    @Query(value = "SELECT * FROM terminal_address WHERE terminal_bank_id = :terminalBankId", nativeQuery = true)
    List<TerminalAddressEntity> getTerminalAddressByTerminalBankId(
            @Param(value = "terminalBankId") String terminalBankId);

    @Query(value = "SELECT * FROM terminal_address WHERE "
            + "bank_id = :bankId AND (cusomer_sync_id = :customerSyncId "
            + "OR cusomer_sync_id = 'c45156b9-8756-4c05-9e39-259e7d549918' "
            + "OR cusomer_sync_id = 'dbbb30dd-58d0-455b-963c-120d3b19f9bf') LIMIT 1", nativeQuery = true)
    TerminalAddressEntity getTerminalAddressByBankIdAndCustomerSyncId(String bankId, String customerSyncId);

    @Query(value = "SELECT a.* FROM terminal_address a "
            + "LEFT JOIN terminal_bank b ON a.terminal_bank_id = b.id "
            + "LEFT JOIN account_bank_receive c ON a.bank_id = c.id "
            + "WHERE a.bank_id = :bankId ", nativeQuery = true)
    TerminalAddressEntity getTerminalAddressByBankIdAndTerminalBankId(String bankId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM terminal_address WHERE bank_id = :bankId AND cusomer_sync_id = :customerSyncId", nativeQuery = true)
    void removeBankAccountFromCustomerSync(String bankId, String customerSyncId);
}
