package com.vietqr.org.repository;

import com.vietqr.org.dto.*;
import com.vietqr.org.entity.TerminalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface TerminalRepository extends JpaRepository<TerminalEntity, Long> {

    @Query(value = "SELECT id FROM terminal WHERE code = :code ", nativeQuery = true)
    String checkTerminalExisted(String code);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM terminal WHERE id = :id", nativeQuery = true)
    void removeTerminalById(String id);

    @Query(value = "SELECT count(id) FROM terminal WHERE user_id = :userId", nativeQuery = true)
    int countNumberOfTerminalByUserId(@Param(value = "userId") String userId);

    @Query(value = "SELECT a.id as id, count(DISTINCT b.user_id) as totalMembers, " +
            "a.name as name, a.address as address, " +
            "a.code as code, a.is_default as isDefault, a.user_id as userId " +
            "FROM terminal a " +
            "INNER JOIN account_bank_receive_share b " +
            "ON b.terminal_id = a.id " +
            "WHERE a.user_id = :userId " +
            "AND b.bank_id IS NOT NULL AND b.bank_id != '' " +
            "GROUP BY b.terminal_id " +
            "LIMIT :offset, 20", nativeQuery = true)
    List<TerminalResponseInterfaceDTO> getTerminalsByUserId(@Param(value = "userId") String userId,
                                                            @Param(value = "offset") int offset);

    @Query(value = "SELECT DISTINCT a.id as terminalId, a.name as terminalName, c.total as totalMembers, a.code as terminalCode, " +
            "a.address as terminalAddress, a.is_default as isDefault, b.bank_id as bankId "
            + "FROM terminal a "
            + "INNER JOIN ( "
            + "SELECT terminal_id, COUNT(DISTINCT user_id) as total "
            + "FROM account_bank_receive_share WHERE terminal_id IS NOT NULL "
            + "AND terminal_id != '' GROUP BY terminal_id) c ON a.id = c.terminal_id "
            + "INNER JOIN account_bank_receive_share b "
            + "ON a.id = b.terminal_id "
            + "WHERE b.bank_id IN :bankIds " +
            "AND b.user_id = :userId " +
            "GROUP BY a.id, b.bank_id ", nativeQuery = true)
    List<ITerminalShareDTO> getTerminalSharesByBankIds(List<String> bankIds, String userId);

    @Query(value = "SELECT a.id as id, a.name as name, " +
            "c.total as totalMembers, a.code as code, " +
            "a.address as address, a.is_default as isDefault, a.user_id as userId "
            + "FROM terminal a "
            + "INNER JOIN ( "
            + "SELECT terminal_id, COUNT(DISTINCT user_id) as total "
            + "FROM account_bank_receive_share WHERE terminal_id IS NOT NULL "
            + "AND terminal_id != '' GROUP BY terminal_id) c ON a.id = c.terminal_id "
            + "INNER JOIN account_bank_receive_share b "
            + "ON a.id = b.terminal_id "
            + "WHERE b.is_owner = false "
            + "AND b.user_id = :userId " +
            "GROUP BY b.terminal_id " +
            "LIMIT :offset, 20", nativeQuery = true)
    List<TerminalResponseInterfaceDTO> getTerminalsShareByUserId(String userId, int offset);

    @Query(value = "SELECT count(DISTINCT CASE WHEN b.user_id = :userId THEN a.id END) FROM terminal a "
            + "INNER JOIN account_bank_receive_share b "
            + "ON a.id = b.terminal_id "
            + "WHERE a.user_id != :userId "
            + "AND b.terminal_id IS NOT NULL AND b.terminal_id != ''", nativeQuery = true)
    int countNumberOfTerminalShareByUserId(String userId);

    @Query(value = "SELECT a.id as id, a.name as name, a.code as code, a.address as address, a.user_id as userId, a.is_default as isDefault, count(DISTINCT b.user_id) as totalMember "
            + "FROM terminal a "
            + "INNER JOIN account_bank_receive_share b "
            + "ON a.id = b.terminal_id "
            + "WHERE a.id = :id", nativeQuery = true)
    ITerminalDetailResponseDTO getTerminalById(String id);

    @Query(value = "SELECT * FROM terminal WHERE id = :id", nativeQuery = true)
    TerminalEntity findTerminalById(String id);

    @Query(value = "SELECT a.id as id, c.total as totalMembers, " +
            "a.name as name, a.address as address, " +
            "a.code as code, a.is_default as isDefault, a.user_id as userId " +
            "FROM terminal a " +
            "INNER JOIN ( "
            + "SELECT terminal_id, COUNT(DISTINCT user_id) as total "
            + "FROM account_bank_receive_share WHERE terminal_id IS NOT NULL "
            + "AND terminal_id != '' GROUP BY terminal_id) c ON a.id = c.terminal_id " +
            "INNER JOIN account_bank_receive_share b " +
            "ON b.terminal_id = a.id " +
            "WHERE b.user_id = :userId " +
            "AND b.bank_id = :bankId " +
            "GROUP BY b.terminal_id " +
            "LIMIT :offset, 20", nativeQuery = true)
    List<TerminalResponseInterfaceDTO> getTerminalsByUserIdAndBankId(String userId, String bankId, int offset);

    @Query(value = "SELECT a.id as terminalId, " +
            "a.name as terminalName, a.address as terminalAddress, " +
            "a.code as terminalCode, a.user_id as userId " +
            "FROM terminal a " +
            "INNER JOIN account_bank_receive_share b " +
            "ON b.terminal_id = a.id " +
            "WHERE b.user_id = :userId " +
            "AND b.bank_id = :bankId " +
            "GROUP BY b.terminal_id ", nativeQuery = true)
    List<TerminalCodeResponseDTO> getTerminalsByUserIdAndBankId(String userId, String bankId);


    @Query(value = "SELECT COUNT(DISTINCT terminal_id) FROM account_bank_receive_share " +
            "WHERE user_id = :userId AND bank_id = :bankId AND terminal_id IS NOT NULL " +
            "AND terminal_id != ''", nativeQuery = true)
    int countNumberOfTerminalByUserIdAndBankId(String userId, String bankId);

    @Query(value = "SELECT DISTINCT a.user_id FROM account_bank_receive_share a " +
            "INNER JOIN terminal b ON a.terminal_id = b.id WHERE code = :terminalCode", nativeQuery = true)
    List<String> getUserIdsByTerminalCode(String terminalCode);

    @Query(value = "SELECT a.* FROM terminal a " +
            "INNER JOIN account_bank_receive_share b ON a.id = b.terminal_id " +
            "INNER JOIN account_bank_receive c ON c.id = b.bank_id " +
            "WHERE a.code = :terminalCode AND c.bank_account = :bankAccount " +
            "AND c.is_authenticated = true ", nativeQuery = true)
    TerminalEntity getTerminalByTerminalCodeAndBankAccount(String terminalCode, String bankAccount);

    @Query(value = "SELECT DISTINCT a.id FROM terminal a " +
            "INNER JOIN account_bank_receive_share b ON a.id = b.terminal_id " +
            "WHERE trace_transfer = :traceTransfer", nativeQuery = true)
    String getTerminalByTraceTransfer(String traceTransfer);

    @Query(value = "SELECT DISTINCT a.* FROM terminal a INNER JOIN account_bank_receive_share b WHERE (b.qr_code IS NULL OR b.qr_code = '') " +
            "AND b.bank_id IS NOT NULL AND b.bank_id != '' ", nativeQuery = true)
    List<TerminalEntity> getAllTerminalNoQRCode();
}
