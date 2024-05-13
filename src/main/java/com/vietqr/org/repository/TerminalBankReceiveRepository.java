package com.vietqr.org.repository;

import com.vietqr.org.dto.*;
import com.vietqr.org.entity.TerminalBankReceiveEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
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
            "WHERE terminal_id = :terminalId AND type_of_qr = 0 LIMIT 1", nativeQuery = true)
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

    @Query(value = "SELECT raw_terminal_code AS rawCode, type_of_qr AS qrType, id AS boxId FROM terminal_bank_receive " +
            "WHERE terminal_code = :terminalCode LIMIT 1", nativeQuery = true)
    ISubTerminalCodeDTO getSubTerminalCodeByTerminalCode(String terminalCode);

    @Query(value = "SELECT * FROM terminal_bank_receive " +
            "WHERE raw_terminal_code = :machineCode LIMIT 1", nativeQuery = true)
    TerminalBankReceiveEntity getTerminalBankReceiveByRawTerminalCode(String machineCode);

    @Query(value = "SELECT id AS subTerminalId, bank_id AS bankId, "
            + "data1 AS qrCode1, data2 AS qrCode2, trace_transfer AS traceTransfer, "
            + "terminal_code AS terminalCode, raw_terminal_code AS rawTerminalCode "
            + "FROM terminal_bank_receive "
            + "WHERE terminal_id = :terminalId "
            + "AND raw_terminal_code LIKE %:value% "
            + "AND type_of_qr = 1 "
            + "ORDER BY raw_terminal_code ASC "
            + "LIMIT :offset, :size ", nativeQuery = true)
    List<ISubTerminalDTO> getListSubTerminalByTerminalId(String terminalId,
                                                         int offset, int size, String value);

    @Query(value = "SELECT id AS subTerminalId, bank_id AS bankId, "
            + "data1 AS qrCode1, data2 AS qrCode2, trace_transfer AS traceTransfer, "
            + "terminal_code AS terminalCode, raw_terminal_code AS rawTerminalCode "
            + "FROM terminal_bank_receive "
            + "WHERE id = :subTerminalId LIMIT 1", nativeQuery = true)
    ISubTerminalDTO getSubTerminalDetailBySubTerminalId(String subTerminalId);

    @Query(value = "SELECT COUNT(id) "
            + "FROM terminal_bank_receive "
            + "WHERE terminal_id = :terminalId "
            + "AND raw_terminal_code LIKE %:value% "
            + "AND type_of_qr = 1 ", nativeQuery = true)
    int countSubTerminalByTerminalId(String terminalId, String value);

    @Query(value = "SELECT id AS subTerminalId, bank_id AS bankId, "
            + "data1 AS qrCode1, data2 AS qrCode2, trace_transfer AS traceTransfer, "
            + "terminal_code AS terminalCode, raw_terminal_code AS rawTerminalCode "
            + "FROM terminal_bank_receive "
            + "WHERE terminal_id = :terminalId "
            + "AND type_of_qr = 1 "
            + "ORDER BY raw_terminal_code ASC ", nativeQuery = true)
    List<ISubTerminalDTO> getListSubTerminalByTerminalId(String terminalId);

    @Query(value = "SELECT id AS subTerminalId, "
            + "terminal_code AS subTerminalCode, raw_terminal_code AS subRawTerminalCode, "
            + "raw_terminal_code AS subTerminalName, '' AS subTerminalAddress, terminal_id AS terminalId "
            + "FROM terminal_bank_receive "
            + "WHERE terminal_id = :terminalId "
            + "AND type_of_qr = 1 "
            + "AND terminal_code IS NOT NULL AND terminal_code != '' "
            + "ORDER BY raw_terminal_code ASC ", nativeQuery = true)
    List<ISubTerminalResponseDTO> getListSubTerminalByTerId(String terminalId);

    @Query(value = "SELECT b.id as bankId, b.bank_account as bankAccount, "
            + "b.bank_account_name as userBankName, "
            + "c.bank_name as bankName, c.bank_short_name as bankShortName, "
            + " c.bank_code as bankCode, c.img_id as imgId, "
            + "b.user_id as userId "
            + "FROM account_bank_receive b "
            + "INNER JOIN bank_type c "
            + "ON b.bank_type_id = c.id "
            + "INNER JOIN terminal_bank_receive d ON d.bank_id = b.id "
            + "WHERE d.terminal_id = :terminalId AND b.is_authenticated = true ", nativeQuery = true)
    List<TerminalBankReceiveDTO> getTerminalBankReceiveResponseByTerminalId(String terminalId);

    @Query(value = "SELECT DISTINCT b.terminal_id as terminalId, c.id as bankId, d.bank_name as bankName, "
            + "d.bank_code as bankCode, c.bank_account as bankAccount, c.bank_account_name as userBankName, "
            + "d.bank_short_name as bankShortName, d.img_id as imgId, "
            + "CASE WHEN b.trace_transfer = '' THEN b.data1 ELSE b.data2 END AS qrCode "
            + "FROM terminal a "
            + "INNER JOIN terminal_bank_receive b ON a.id = b.terminal_id "
            + "INNER JOIN account_bank_receive c "
            + "ON c.id = b.bank_id "
            + "INNER JOIN bank_type d "
            + "ON d.id = c.bank_type_id "
            + "WHERE a.id = :terminalId "
            + "AND (b.terminal_code IS NULL OR b.terminal_code = '') "
            + "LIMIT 1", nativeQuery = true)
    ITerminalBankResponseDTO getTerminalBanksByTerminalId(String terminalId);

    @Query(value = "SELECT DISTINCT b.terminal_id as terminalId, c.id as bankId, d.bank_name as bankName, "
            + "d.bank_code as bankCode, c.bank_account as bankAccount, c.bank_account_name as userBankName, "
            + "d.bank_short_name as bankShortName, d.img_id as imgId, "
            + "CASE b.trace_transfer WHEN b.trace_transfer = '' THEN b.data1 ELSE b.data2 END AS qrCode "
            + "FROM terminal a "
            + "INNER JOIN terminal_bank_receive b "
            + "ON a.id = b.terminal_id "
            + "INNER JOIN account_bank_receive c "
            + "ON c.id = b.bank_id "
            + "INNER JOIN bank_type d "
            + "ON d.id = c.bank_type_id "
            + "WHERE a.id IN :terminalIds AND b.type_of_qr != 1 ", nativeQuery = true)
    List<ITerminalBankResponseDTO> getTerminalBanksByTerminalIds(List<String> terminalIds);

    @Query(value = "SELECT DISTINCT a.id as bankId, c.bank_name as bankName, " +
            "a.bank_account as bankAccount, a.bank_account_name as userBankName, " +
            "c.bank_code as bankCode, c.bank_short_name as bankShortName, c.img_id as imgId " +
            "FROM account_bank_receive a " +
            "INNER JOIN terminal_bank_receive b " +
            "ON a.id = b.bank_id " +
            "INNER JOIN bank_type c " +
            "ON a.bank_type_id = c.id " +
            "WHERE a.user_id = :userId AND b.type_of_qr != 1 " +
            "LIMIT :offset, 20", nativeQuery = true)
    List<IBankShareResponseDTO> getTerminalBankByUserId(String userId, int offset);

    @Query(value = "SELECT count(DISTINCT a.bank_id) FROM terminal_bank_receive a "
            + "INNER JOIN account_bank_receive b ON a.bank_id = b.id "
            + "WHERE b.user_id = :userId AND a.type_of_qr != 1 ", nativeQuery = true)
    int countNumberOfBankShareByUserId(String userId);

    @Query(value = "SELECT DISTINCT a.id as bankId, c.bank_name as bankName, " +
            "a.bank_account as bankAccount, a.bank_account_name as userBankName, " +
            "c.bank_code as bankCode, c.bank_short_name as bankShortName, c.img_id as imgId " +
            "FROM account_bank_receive a " +
            "INNER JOIN terminal_bank_receive b " +
            "ON a.id = b.bank_id " +
            "INNER JOIN bank_type c " +
            "ON a.bank_type_id = c.id " +
            "INNER JOIN terminal d " +
            "ON d.id = b.terminal_id " +
            "INNER JOIN merchant_member e " +
            "ON e.terminal_id = d.id " +
            "WHERE d.user_id != :userId " +
            "AND b.terminal_id IS NOT NULL AND b.terminal_id != '' " +
            "AND e.user_id = :userId " +
            "LIMIT :offset, 20", nativeQuery = true)
    List<IBankShareResponseDTO> getTerminalBankShareByUserId(String userId, int offset);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM terminal_bank_receive WHERE terminal_id = :terminalId", nativeQuery = true)
    void removeTerminalBankReceiveByTerminalId(String terminalId);

    @Query(value = "SELECT b.code FROM terminal_bank_receive a "
            + "INNER JOIN terminal b on b.id = a.terminal_id "
            + "INNER JOIN merchant_member c ON (c.merchant_id = b.merchant_id AND c.terminal_id = b.id) "
            + "WHERE c.user_id = :userId AND a.bank_id = :bankId AND c.terminal_id != '' "
            + "GROUP BY b.code ", nativeQuery = true)
    List<String> getTerminalCodeByUserIdAndBankId(String userId, String bankId);

    @Query(value = "SELECT b.code FROM terminal_bank_receive a "
            + "INNER JOIN terminal b on b.id = a.terminal_id "
            + "INNER JOIN merchant_member c ON c.merchant_id = b.merchant_id "
            + "WHERE c.user_id = :userId AND a.bank_id = :bankId AND c.terminal_id = '' "
            + "GROUP BY b.code ", nativeQuery = true)
    List<String> getTerminalCodeByUserIdAndBankIdNoTerminal(String userId, String bankId);

    @Query(value = "SELECT a.bank_id FROM terminal_bank_receive a "
            + "INNER JOIN terminal b ON a.terminal_id = b.id "
            + "WHERE b.code = :terminalCode LIMIT 1", nativeQuery = true)
    String getBankIdByTerminalCode(String terminalCode);

    @Query(value = "SELECT a.id AS machineId, a.raw_terminal_code AS machineCode, "
            + "a.sub_terminal_address AS machineAddress, a.terminal_code AS terminalCode, "
            + "CASE WHEN a.trace_transfer = '' THEN a.data1 ELSE a.data2 END AS qrCode, "
            + "b.id AS bankId, b.bank_account AS bankAccount, b.bank_account_name AS userBankName, "
            + "c.bank_short_name AS bankShortName, c.bank_code AS bankCode "
            + "FROM terminal_bank_receive a "
            + "INNER JOIN account_bank_receive b ON a.bank_id = b.id "
            + "INNER JOIN bank_type c ON c.id = b.bank_type_id "
            + "WHERE a.raw_terminal_code = :machineCode", nativeQuery = true)
    ITerminalInternalDTO getTerminalInternalByMachineCode(String machineCode);

    @Query(value = "SELECT a.raw_terminal_code FROM terminal_bank_receive a "
            + "WHERE a.raw_terminal_code = :rawTerminalCode LIMIT 1", nativeQuery = true)
    String checkExistedRawTerminalCode(String rawTerminalCode);

    @Query(value = "SELECT * FROM terminal_bank_receive "
            + "WHERE terminal_code = :terminalCode LIMIT 1", nativeQuery = true)
    TerminalBankReceiveEntity getTerminalBankReceiveEntityByTerminalCode(String terminalCode);
}
