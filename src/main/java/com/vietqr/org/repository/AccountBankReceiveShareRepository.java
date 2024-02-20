package com.vietqr.org.repository;

import com.vietqr.org.dto.*;
import com.vietqr.org.entity.AccountBankReceiveShareEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface AccountBankReceiveShareRepository
        extends JpaRepository<AccountBankReceiveShareEntity, Long> {
    @Query(value = "SELECT DISTINCT a.bank_id as bankId, b.bank_account as bankAccount, b.bank_account_name as userBankName, " +
            "c.bank_name as bankName, c.bank_short_name as bankShortName, b.phone_authenticated as phoneAuthenticated, " +
            " c.bank_code as bankCode, c.img_id as imgId, b.type as bankType, b.is_authenticated as authenticated, " +
            "b.user_id as userId, a.is_owner as isOwner , c.status as bankTypeStatus, c.id as bankTypeId, " +
            "c.unlinked_type as unlinkedType, b.national_id as nationalId "
            + "FROM account_bank_receive_share a "
            + "INNER JOIN account_bank_receive b "
            + "ON a.bank_id = b.id "
            + "INNER JOIN bank_type c "
            + "ON b.bank_type_id = c.id "
            + "WHERE a.user_id = :userId", nativeQuery = true)
    List<AccountBankReceiveShareDTO> getAccountBankReceiveShare(@Param(value = "userId") String userId);

    @Query(value = "SELECT a.id, a.phone_no as phoneNo, b.first_name as firstName, b.middle_name as middleName, b.last_name as lastName, b.img_id as imgId, c.is_owner as isOwner "
            + "FROM account_login a "
            + "INNER JOIN account_information b "
            + "ON a.id = b.user_id "
            + "INNER JOIN account_bank_receive_share c "
            + "ON c.user_id = a.id "
            + "WHERE a.status = 1 AND c.bank_id = :bankId", nativeQuery = true)
    List<AccountMemberDTO> getMembersFromBank(@Param(value = "bankId") String bankId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM account_bank_receive_share WHERE user_id = :userId AND bank_id = :bankId", nativeQuery = true)
    void removeMemberFromBank(@Param(value = "userId") String userId,
                              @Param(value = "bankId") String bankId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM account_bank_receive_share WHERE bank_id = :bankId AND is_owner = false", nativeQuery = true)
    void removeAllMemberFromBank(@Param(value = "bankId") String bankId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM account_bank_receive_share WHERE bank_id = :bankId", nativeQuery = true)
    void deleteAccountBankReceiveShareByBankId(String bankId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM account_bank_receive_share WHERE terminal_id = :terminalId", nativeQuery = true)
    void removeTerminalGroupByTerminalId(String terminalId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM account_bank_receive_share "
            + "WHERE terminal_id = :terminalId AND user_id = :userId", nativeQuery = true)
    void removeMemberFromTerminal(String terminalId, String userId);

    // check member existed in account bank receive share
    @Query(value = "SELECT user_id as userId "
            + "FROM account_bank_receive_share "
            + "WHERE bank_id = :bankId "
            + "AND user_id = :userId ", nativeQuery = true)
    String checkUserExistedFromBank(String bankId, String userId);

    @Query(value = "SELECT user_id as userId "
            + "FROM account_bank_receive_share "
            + "WHERE terminal_id = :terminalId "
            + "AND user_id = :userId "
            + "LIMIT 1", nativeQuery = true)
    String checkUserExistedFromTerminal(String terminalId, String userId);

    @Query(value = "SELECT DISTINCT a.id, a.phone_no as phoneNo, b.first_name as firstName, "
            + "b.middle_name as middleName, b.last_name as lastName, b.img_id as imgId, c.is_owner as isOwner "
            + "FROM account_login a "
            + "INNER JOIN account_information b "
            + "ON a.id = b.user_id "
            + "INNER JOIN account_bank_receive_share c "
            + "ON c.user_id = a.id "
            + "WHERE a.status = 1 AND c.terminal_id = :terminalId", nativeQuery = true)
    List<AccountMemberDTO> getMembersFromTerminal(String terminalId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM account_bank_receive_share " +
            "WHERE terminal_id = :terminalId AND bank_id = :bankId", nativeQuery = true)
    void removeBankAccountFromTerminal(String terminalId, String bankId);

    @Query(value = "SELECT DISTINCT b.terminal_id as terminalId, c.id as bankId, d.bank_name as bankName, d.bank_code as bankCode, c.bank_account as bankAccount, c.bank_account_name as userBankName, " +
            "d.bank_short_name as bankShortName, d.img_id as imgId, b.qr_code as qrCode "
            + "FROM terminal a "
            + "INNER JOIN account_bank_receive_share b "
            + "ON a.id = b.terminal_id "
            + "INNER JOIN account_bank_receive c "
            + "ON c.id = b.bank_id " +
            "INNER JOIN bank_type d " +
            "ON d.id = c.bank_type_id "
            + "WHERE a.id IN :terminalIds", nativeQuery = true)
    List<ITerminalBankResponseDTO> findByTerminalIdIn(@Param("terminalIds") List<String> terminalIds);

    @Query(value = "SELECT DISTINCT a.id as bankId, c.bank_name as bankName, " +
            "a.bank_account as bankAccount, a.bank_account_name as userBankName, " +
            "c.bank_code as bankCode, c.bank_short_name as bankShortName, c.img_id as imgId " +
            "FROM account_bank_receive a " +
            "INNER JOIN account_bank_receive_share b " +
            "ON a.id = b.bank_id " +
            "INNER JOIN bank_type c " +
            "ON a.bank_type_id = c.id " +
            "WHERE b.user_id = :userId AND b.is_owner = true " +
            "AND b.terminal_id IS NOT NULL AND b.terminal_id != '' " +
            "LIMIT :offset, 20", nativeQuery = true)
    List<IBankShareResponseDTO> findBankShareByUserId(String userId, int offset);

    @Query(value = "SELECT count(distinct bank_id) FROM account_bank_receive_share " +
            "WHERE user_id = :userId AND is_owner = true " +
            "AND terminal_id IS NOT NULL AND terminal_id != '' ", nativeQuery = true)
    int countNumberOfBankShareByUserId(String userId);

    @Query(value = "SELECT DISTINCT bank_id as bankId, qr_code as qrCode, trace_transfer as traceTransfer FROM account_bank_receive_share " +
            "WHERE terminal_id = :terminalId", nativeQuery = true)
    List<BankQRTerminalDTO> getBankIdsFromTerminalId(@Param("terminalId") String terminalId);

    @Query(value = "SELECT count(DISTINCT CASE WHEN a.user_id = :userId AND a.bank_id != '' THEN a.bank_id END) FROM account_bank_receive_share a " +
            "INNER JOIN terminal b " +
            "ON a.terminal_id = b.id " +
            "WHERE b.user_id != :userId " +
            "AND a.terminal_id IS NOT NULL AND a.terminal_id != ''", nativeQuery = true)
    int countNumberOfTerminalBankShareByUserId(String userId);

    @Query(value = "SELECT DISTINCT a.id as bankId, c.bank_name as bankName, " +
            "a.bank_account as bankAccount, a.bank_account_name as userBankName, " +
            "c.bank_code as bankCode, c.bank_short_name as bankShortName, c.img_id as imgId " +
            "FROM account_bank_receive a " +
            "INNER JOIN account_bank_receive_share b " +
            "ON a.id = b.bank_id " +
            "INNER JOIN bank_type c " +
            "ON a.bank_type_id = c.id " +
            "INNER JOIN terminal d " +
            "ON d.id = b.terminal_id " +
            "WHERE d.user_id != :userId " +
            "AND b.terminal_id IS NOT NULL AND b.terminal_id != '' " +
            "AND b.user_id = :userId " +
            "LIMIT :offset, 20", nativeQuery = true)
    List<IBankShareResponseDTO> getTerminalBankShareByUserId(String userId, int offset);

    @Query(value = "SELECT DISTINCT user_id FROM account_bank_receive_share " +
            "WHERE terminal_id = :terminalId AND user_id != :userId", nativeQuery = true)
    List<String> getUserIdsFromTerminalId(String terminalId, String userId);

    @Query(value = "SELECT DISTINCT b.id as bankId, b.bank_account as bankAccount, b.bank_account_name as userBankName, "
            + "c.bank_name as bankName, c.bank_short_name as bankShortName, "
            + " c.bank_code as bankCode, c.img_id as imgId, "
            + "b.user_id as userId "
            + "FROM account_bank_receive b "
            + "INNER JOIN bank_type c "
            + "ON b.bank_type_id = c.id "
            + "WHERE b.user_id = :userId AND b.id NOT IN "
            + "(SELECT a.bank_id FROM account_bank_receive_share a WHERE a.terminal_id = :terminalId AND a.terminal_id IS NOT NULL AND a.terminal_id != '') "
            + "AND b.is_authenticated = true ", nativeQuery = true)
    List<TerminalBankReceiveDTO> getAccountBankReceiveShareByTerminalId(String userId, String terminalId);

    @Query(value = "SELECT * FROM account_bank_receive_share WHERE terminal_id = :terminalId " +
            "AND bank_id IS NOT NULL AND bank_id != ''", nativeQuery = true)
    List<AccountBankReceiveShareEntity> getAccountBankReceiveShareByTerminalId(String terminalId);
}
