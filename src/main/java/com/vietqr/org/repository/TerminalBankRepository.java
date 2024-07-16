package com.vietqr.org.repository;

import com.vietqr.org.dto.QrBoxDynamicDTO;
import com.vietqr.org.dto.QrBoxListDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.TerminalBankEntity;

import java.util.List;

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

    @Query(value = "SELECT terminal_address FROM terminal_bank WHERE terminal_name = :terminalName", nativeQuery = true)
    String getTerminalAddress(String terminalName);

    @Query(value = "SELECT terminal_name FROM terminal_bank ", nativeQuery = true)
    List<String> getListTerminalNames();

    @Query(value = "SELECT COUNT(id) as totalTerminal "
            + "FROM terminal_bank ", nativeQuery = true)
    Integer getTerminalCounting();

    @Query(value = "SELECT bank_account_raw_number FROM terminal_bank WHERE terminal_id = :terminalLabel ", nativeQuery = true)
    String getBankAccountByTerminalLabel(@Param(value = "terminalLabel") String terminalLabel);

    @Query(value = "SELECT a.id AS subTerminalId, a.raw_terminal_code AS boxCode, "
            + "a.terminal_code AS subTerminalCode, COALESCE(sub_terminal_address, '') AS subTerminalAddress, "
            + "CASE WHEN a.data1 != '' THEN a.data1 ELSE a.data2 END AS qrCode, "
            + "b.id AS bankId, b.bank_account AS bankAccount, "
            + "b.bank_account_name AS userBankName, c.bank_code AS bankCode, "
            + "c.bank_short_name AS bankShortName "
            + "FROM terminal_bank_receive a "
            + "INNER JOIN account_bank_receive b ON a.bank_id = b.id "
            + "INNER JOIN bank_type c ON b.bank_type_id = c.id "
            + "WHERE b.id = :bankId AND type_of_qr = 2 ", nativeQuery = true)
    List<QrBoxListDTO> getQrBoxListByBankId(String bankId);

    @Query(value = "SELECT a.id AS subTerminalId, a.raw_terminal_code AS subRawTerminalCode, "
            + "a.terminal_code AS subTerminalCode, COALESCE(b.name, '') AS terminalName, "
            + "COALESCE(a.sub_terminal_address, '') AS subTerminalAddress "
            + "FROM terminal_bank_receive a "
            + "LEFT JOIN terminal b ON a.terminal_id = b.id "
            + "WHERE a.bank_id = :bankId AND type_of_qr = 2 ", nativeQuery = true)
    List<QrBoxDynamicDTO> getQrBoxDynamicQrByBankId(String bankId);
}

