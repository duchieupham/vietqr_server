package com.vietqr.org.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.TerminalAddressEntity;

@Repository
public interface TerminalAddressRepository extends JpaRepository<TerminalAddressEntity, Long> {

    @Query(value = "SELECT * FROM terminal_address WHERE terminal_bank_id = :terminalBankId", nativeQuery = true)
    List<TerminalAddressEntity> getTerminalAddressByTerminalBankId(
            @Param(value = "terminalBankId") String terminalBankId);

    @Query(value = "SELECT * FROM terminal_address WHERE " +
            "bank_id = :bankId AND cusomer_sync_id = :customerSyncId LIMIT 1", nativeQuery = true)
    TerminalAddressEntity getTerminalAddressByBankIdAndCustomerSyncId(String bankId, String customerSyncId);
}
