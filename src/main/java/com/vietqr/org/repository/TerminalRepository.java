package com.vietqr.org.repository;

import com.vietqr.org.dto.*;
import com.vietqr.org.entity.TerminalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface TerminalRepository extends JpaRepository<TerminalEntity, Long> {

    @Query(value = "SELECT id FROM terminal WHERE code = :code LIMIT 1", nativeQuery = true)
    String checkTerminalExisted(String code);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM terminal WHERE id = :id", nativeQuery = true)
    void removeTerminalById(String id);

    @Query(value = "SELECT COUNT(DISTINCT terminal_id) FROM account_bank_receive_share WHERE user_id = :userId"
            + " AND terminal_id IS NOT NULL AND terminal_id != ''", nativeQuery = true)
    int countNumberOfTerminalByUserId(@Param(value = "userId") String userId);

    @Query(value = "SELECT COUNT(id) FROM terminal WHERE user_id = :userId ", nativeQuery = true)
    int countNumberOfTerminalByUserIdOwner(@Param(value = "userId") String userId);

    @Query(value = "SELECT a.id as id, (COUNT(b.user_id) + c.total) as totalMembers, "
            + "a.name as name, a.address as address, "
            + "a.code as code, a.is_default as isDefault, a.user_id as userId "
            + "FROM terminal a "
            + "INNER JOIN (SELECT merchant_id, COUNT(user_id) AS total "
            + "FROM merchant_member WHERE terminal_id = '' GROUP BY merchant_id) c "
            + "ON a.merchant_id = c.merchant_id "
            + "LEFT JOIN merchant_member b "
            + "ON b.terminal_id = a.id "
            + "WHERE a.user_id = :userId "
            + "GROUP BY a.id "
            + "ORDER BY a.code ASC "
            + "LIMIT :offset, 20", nativeQuery = true)
    List<TerminalResponseInterfaceDTO> getTerminalsByUserId(@Param(value = "userId") String userId,
                                                            @Param(value = "offset") int offset
    );

//    @Query(value = "SELECT DISTINCT a.id as terminalId, a.name as terminalName, c.total as totalMembers, a.code as terminalCode, " +
//            "a.address as terminalAddress, a.is_default as isDefault, b.bank_id as bankId "
//            + "FROM terminal a "
//            + "INNER JOIN ( "
//            + "SELECT terminal_id, COUNT(DISTINCT user_id) as total "
//            + "FROM account_bank_receive_share WHERE terminal_id IS NOT NULL "
//            + "AND terminal_id != '' GROUP BY terminal_id) c ON a.id = c.terminal_id "
//            + "INNER JOIN account_bank_receive_share b "
//            + "ON a.id = b.terminal_id "
//            + "WHERE b.bank_id IN :bankIds " +
//            "AND b.user_id = :userId " +
//            "GROUP BY a.id, b.bank_id ", nativeQuery = true)
//@Query(value = "SELECT a.id as id, (COUNT(b.user_id) + c.total) as totalMembers, "
//        + "a.name as name, a.address as address, "
//        + "a.code as code, a.is_default as isDefault, a.user_id as userId "
//        + "FROM terminal a "
//        + "INNER JOIN (SELECT merchant_id, COUNT(user_id) AS total "
//        + "FROM merchant_member WHERE terminal_id = '' GROUP BY merchant_id) c "
//        + "ON a.merchant_id = c.merchant_id "
//        + "LEFT JOIN merchant_member b "
//        + "ON b.terminal_id = a.id "
//        + "WHERE a.user_id = :userId "
//        + "GROUP BY a.id "
//        + "ORDER BY a.code ASC "
//        + "LIMIT :offset, 20"
    @Query(value = "SELECT DISTINCT a.id as terminalId, a.name as terminalName, "
            + "(c.total + COUNT(DISTINCT d.user_id)) as totalMembers, a.code as terminalCode, "
            + "a.address as terminalAddress, a.is_default as isDefault, b.bank_id as bankId "
            + "FROM terminal a "
            + "INNER JOIN ( "
            + "SELECT merchant_id, COUNT(user_id) AS total "
            + "FROM merchant_member WHERE terminal_id = '' GROUP BY merchant_id) c "
            + "ON a.merchant_id = c.merchant_id "
            + "INNER JOIN terminal_bank_receive b  "
            + "ON a.id = b.terminal_id "
            + "LEFT JOIN merchant_member d ON d.terminal_id = a.id "
            + "WHERE b.bank_id IN :bankIds "
            + "AND a.user_id = :userId "
            + "GROUP BY a.id, b.bank_id ", nativeQuery = true)
    List<ITerminalShareDTO> getTerminalSharesByBankIds(List<String> bankIds, String userId);

    @Query(value = "SELECT DISTINCT a.id as id, a.name as name, " +
            "c.total as totalMembers, a.code as code, " +
            "a.address as address, a.is_default as isDefault, a.user_id as userId "
            + "FROM terminal a "
            + "INNER JOIN (SELECT terminal_id, (COUNT(user_id) + 1) AS total "
            + "FROM merchant_member GROUP BY terminal_id) c "
            + "ON a.id = c.terminal_id "
            + "INNER JOIN merchant_member d ON d.terminal_id = a.id "
            + "WHERE d.user_id = :userId "
            + "GROUP BY a.id "
            + "LIMIT :offset, 20", nativeQuery = true)
    List<TerminalResponseInterfaceDTO> getTerminalsShareByUserId(String userId, int offset);

    @Query(value = "SELECT COUNT(DISTINCT a.id) "
            + "FROM terminal a "
            + "INNER JOIN merchant_member d ON d.terminal_id = a.id "
            + "WHERE d.user_id = :userId ", nativeQuery = true)
    int countNumberOfTerminalShareByUserId(String userId);

    @Query(value = "SELECT a.id as id, a.name as name, a.code as code, a.address as address, a.user_id as userId, " +
            "a.is_default as isDefault, count(DISTINCT b.user_id) as totalMember "
            + "FROM merchant_member b "
            + "INNER JOIN (SELECT * FROM terminal WHERE id = :id) a "
            + "ON a.merchant_id = b.merchant_id "
            + "WHERE b.terminal_id = :id OR b.terminal_id = ''", nativeQuery = true)
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
            "ORDER BY a.code ASC " +
            "LIMIT :offset, 20" , nativeQuery = true)
    List<TerminalResponseInterfaceDTO> getTerminalsByUserIdAndBankIdOffset(String userId, String bankId,int offset);

    @Query(value = "SELECT DISTINCT a.id as terminalId, " +
            "a.name as terminalName, a.address as terminalAddress, " +
            "a.code as terminalCode, a.user_id as userId " +
            "FROM terminal a " +
            "INNER JOIN merchant_member b " +
            "ON (b.merchant_id = a.merchant_id AND b.terminal_id = a.id) " +
            "INNER JOIN terminal_bank_receive c " +
            "ON c.terminal_id = a.id " +
            "WHERE b.user_id = :userId " +
            "AND c.bank_id = :bankId " +
            "ORDER BY a.code ASC", nativeQuery = true)
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
            "AND c.is_authenticated = true LIMIT 1", nativeQuery = true)
    TerminalEntity getTerminalByTerminalCodeAndBankAccount(String terminalCode, String bankAccount);

    @Query(value = "SELECT DISTINCT a.id FROM terminal a " +
            "INNER JOIN account_bank_receive_share b ON a.id = b.terminal_id " +
            "WHERE trace_transfer = :traceTransfer AND is_owner = true ", nativeQuery = true)
    String getTerminalByTraceTransfer(String traceTransfer);

    @Query(value = "SELECT DISTINCT a.* FROM terminal a INNER JOIN account_bank_receive_share b WHERE (b.qr_code IS NULL OR b.qr_code = '') " +
            "AND b.bank_id IS NOT NULL AND b.bank_id != '' ", nativeQuery = true)
    List<TerminalEntity> getAllTerminalNoQRCode();

    @Query(value = "SELECT * FROM terminal WHERE code = :terminalCode LIMIT 1", nativeQuery = true)
    TerminalEntity getTerminalByTerminalCode(String terminalCode);

    @Query(value = "SELECT a.id AS terminalId, a.name AS terminalName, "
            + "a.address AS terminalAddress, "
            + "a.code AS terminalCode, f.bank_name AS bankName, e.bank_account AS bankAccount, "
            + "f.bank_short_name as bankShortName, e.bank_account_name AS bankAccountName "
            + "FROM terminal a "
            + "INNER JOIN account_bank_receive_share c ON c.terminal_id = a.id "
            + "INNER JOIN account_bank_receive e ON e.id = c.bank_id "
            + "INNER JOIN bank_type f ON f.id = e.bank_type_id "
            + "WHERE c.user_id = :userId "
            + "AND a.name LIKE %:value% "
            + "ORDER BY a.code ASC "
            + "LIMIT :offset, 20", nativeQuery = true)
    List<ITerminalDetailWebDTO> getTerminalWebByUserId(@RequestParam(value = "userId") String userId,
                                                       @RequestParam(value = "offset") int offset,
                                                       @RequestParam(value = "value") String value);

    @Query(value = "SELECT a.id AS terminalId, a.name AS terminalName, "
            + "a.address AS terminalAddress, d.total_trans AS totalTrans, "
            + "d.total_amount AS totalAmount, count(distinct c.user_id) AS totalMember, "
            + "a.code AS terminalCode, f.bank_name AS bankName, e.bank_account AS bankAccount, "
            + "f.bank_short_name as bankShortName, e.bank_account_name AS bankAccountName "
            + "FROM terminal a "
            + "INNER JOIN merchant b ON b.id = a.merchant_id "
            + "INNER JOIN account_bank_receive_share c ON c.terminal_id = a.id "
            + "INNER JOIN terminal_statistic d ON d.terminal_id = a.id "
            + "INNER JOIN account_bank_receive e ON e.id = c.bank_id "
            + "INNER JOIN bank_type f ON f.id = e.bank_type_id "
            + "WHERE b.merchant_id = :merchantId "
            + "AND b.user_id = :userId "
            + "AND a.name LIKE %:value% "
            + "AND d.time = :time "
            + "LIMIT :offset, 20", nativeQuery = true)
    List<ITerminalDetailWebDTO> getTerminalWebByUserIdAndMerchantId(@RequestParam(value = "merchantId") String merchantId,
                                                                    @RequestParam(value = "userId") String userId,
                                                                    @RequestParam(value = "offset") int offset,
                                                                    @RequestParam(value = "value") String value,
                                                                    @RequestParam(value = "time") long time);

    @Query(value = "SELECT a.terminal_id AS terminalId, a.bank_id AS bankId, "
            + "c.bank_name AS bankName, c.bank_code AS bankCode, "
            + "b.bank_account AS bankAccount, b.bank_account_name AS userBankName, "
            + "c.bank_short_name AS bankShortName, a.qr_code AS qrCode, c.img_id AS imgId "
            + "FROM account_bank_receive_share a "
            + "INNER JOIN account_bank_receive b ON a.bank_id = b.id "
            + "INNER JOIN bank_type c ON c.id = b.bank_type_id "
            + "WHERE a.terminal_id = :terminalId "
            + "AND a.user_id = :userId ", nativeQuery = true)
    ITerminalBankResponseDTO getTerminalResponseById(String terminalId, String userId);

    @Query(value = "SELECT a.id as Id, a.name AS name, "
            + "a.address AS address, a.code AS code "
            + "FROM terminal a "
            + "WHERE a.id = :terminalId", nativeQuery = true)
    ITerminalWebResponseDTO getTerminalWebById(String terminalId);

    @Query(value = "SELECT a.id FROM terminal a "
            + "INNER JOIN merchant b ON a.merchant_id = b.id "
            + "WHERE a.public_id = :terminalId "
            + "AND a.merchant_id = :merchantId ", nativeQuery = true)
    String checkExistedTerminalIntoMerchant(String terminalId, String merchantId);

    @Query(value = "SELECT * FROM terminal WHERE public_id = :publicId", nativeQuery = true)
    TerminalEntity findTerminalByPublicId(String publicId);

    @Query(value = "SELECT a.public_id AS terminalId, "
            + "a.name AS terminalName, a.code AS terminalCode, "
            + "a.address AS terminalAddress, "
            + "d.bank_code AS bankCode, c.bank_account AS bankAccount, "
            + "c.bank_account_name AS bankAccountName, "
            + "d.bank_name AS bankName, c.mms_active AS isMmsActive, "
            + "b.data1 AS data1, b.data2 AS data2 "
            + "FROM terminal a "
            + "INNER JOIN terminal_bank_receive b ON a.id = b.terminal_id "
            + "INNER JOIN account_bank_receive c ON b.bank_id = c.id "
            + "INNER JOIN bank_type d ON d.id = c.bank_type_id "
            + " WHERE a.merchant_id = :merchantId "
            + "LIMIT :offset, :size ", nativeQuery = true)
    List<ITerminalTidResponseDTO> getTerminalByMerchantId(String merchantId, int offset, int size);

    @Query(value = "SELECT raw_terminal_code FROM terminal WHERE raw_terminal_code = :terminalCode", nativeQuery = true)
    String checkExistedRawTerminalCode(String terminalCode);

    @Query(value = "SELECT a.id AS terminalId, a.name AS terminalName, "
            + "a.address AS terminalAddress, a.code as terminalCode, "
            + "a.user_id AS userId FROM terminal a "
            + "INNER JOIN terminal_bank_receive b "
            + "ON a.id = b.terminal_id "
            + "WHERE a.user_id = :userId AND b.bank_id = :bankId ", nativeQuery = true)
    List<TerminalCodeResponseDTO> getListTerminalResponseByBankIdAndUserId(String userId, String bankId);

    @Query(value = "SELECT code FROM terminal WHERE code = :value LIMIT 1", nativeQuery = true)
    String getTerminalCodeByTerminalCode(String value);

    @Query(value = "SELECT a.* FROM terminal a "
            + "INNER JOIN terminal_bank_receive b ON a.id = b.terminal_id "
            + "WHERE b.terminal_code = :terminalCode", nativeQuery = true)
    TerminalEntity getTerminalByTerminalBankReceiveCode(String terminalCode);

    @Query(value = "SELECT DISTINCT a.code FROM terminal a "
            + "INNER JOIN account_bank_receive_share b ON a.id = b.terminal_id "
            + "WHERE b.user_id = :userId", nativeQuery = true)
    List<String> getAllCodeByUserId(String userId);

    @Query(value = "SELECT a.code FROM terminal a "
            + "INNER JOIN merchant_member b ON b.merchant_id = a.merchant_id "
            + "INNER JOIN merchant c ON c.id = a.merchant_id "
            + "WHERE b.user_id = :userId ", nativeQuery = true)
    List<String> getAllCodeByUserIdOwner(String userId);

    @Query(value = "SELECT a.id AS terminalId, a.name AS terminalName, "
            + "a.address AS terminalAddress, a.code AS terminalCode "
            + "FROM terminal a "
            + "WHERE a.user_id = :userId "
            + "ORDER BY a.code ASC "
            + "LIMIT :offset, 10", nativeQuery = true)
    List<IStatisticTerminalOverViewDTO> getListTerminalByUserId(String userId, int offset);

    @Query(value = "SELECT a.id AS terminalId, a.name AS terminalName, "
            + "a.address AS terminalAddress, a.code AS terminalCode "
            + "FROM terminal a "
            + "INNER JOIN account_bank_receive_share b ON a.id = b.terminal_id "
            + "WHERE b.user_id = :userId "
            + "AND b.is_owner = false "
            + "ORDER BY a.code ASC "
            + "LIMIT :offset, :size", nativeQuery = true)
    List<IStatisticTerminalOverViewDTO> getListTerminalByUserIdNotOwner(String userId, int offset, int size);

    @Query(value = "SELECT a.id AS terminalId, a.name AS terminalName, "
            + "a.code AS terminalCode "
            + "FROM terminal a "
            + "INNER JOIN merchant_member b ON b.terminal_id = a.id "
            + "WHERE b.merchant_id = :merchantId "
            + "AND b.user_id = :userId AND b.terminal_id != '' ", nativeQuery = true)
    List<TerminalMapperDTO> getTerminalsByUserIdAndMerchantId(@Param(value = "userId") String userId,
                                                              @Param(value = "merchantId") String merchantId);

    @Query(value = "SELECT a.code FROM terminal a "
            + "INNER JOIN merchant_member b ON b.terminal_id = a.id "
            + "WHERE b.merchant_id = :merchantId "
            + "AND b.user_id = :userId AND b.terminal_id != ''", nativeQuery = true)
    List<String> getAllCodeByMerchantId(String merchantId, String userId);

    @Query(value = "SELECT a.id AS terminalId, a.name AS terminalName, "
            + "a.code AS terminalCode, a.address AS terminalAddress "
            + "FROM terminal a "
            + "INNER JOIN merchant_member b ON (b.merchant_id = a.merchant_id AND b.terminal_id = a.id) "
            + "WHERE a.merchant_id = :merchantId "
            + "AND b.user_id = :userId "
            + "ORDER BY a.code ASC "
            + "LIMIT :offset, 10", nativeQuery = true)
    List<IStatisticTerminalOverViewDTO> getListTerminalByMerchantId(String merchantId, String userId, int offset);

    @Query(value = "SELECT a.code FROM terminal a "
            + "INNER JOIN merchant_member b ON b.merchant_id = a.merchant_id "
            + "WHERE b.merchant_id = :merchantId "
            + "AND b.user_id = :userId AND b.terminal_id = ''", nativeQuery = true)
    List<String> getAllCodeByMerchantIdOwner(String merchantId, String userId);

    @Query(value = "SELECT DISTINCT a.code FROM terminal a "
            + "INNER JOIN merchant_member b ON b.terminal_id = a.id "
            + "WHERE b.merchant_id = :merchantId "
            + "AND b.user_id = :userId ", nativeQuery = true)
    List<String> getAllCodeByMerchantIdIn(String merchantId, String userId);

    @Query(value = "SELECT DISTINCT a.id as terminalId, a.name as terminalName, "
            + "c.total as totalMembers, a.code as terminalCode, "
            + "a.address as terminalAddress, a.is_default as isDefault, b.bank_id as bankId "
            + "FROM terminal a "
            + "INNER JOIN (SELECT terminal_id, (COUNT(user_id) + 1) AS total "
            + "FROM merchant_member GROUP BY terminal_id) c "
            + "ON a.id = c.terminal_id "
            + "INNER JOIN terminal_bank_receive b  "
            + "ON a.id = b.terminal_id "
            + "LEFT JOIN merchant_member d ON d.terminal_id = a.id "
            + "WHERE b.bank_id IN :bankIds "
            + "AND d.user_id = :userId "
            + "AND a.user_id != :userId "
            + "GROUP BY a.id, b.bank_id ", nativeQuery = true)
    List<ITerminalShareDTO> getTerminalSharesByBankIds2(List<String> bankIds, String userId);

    @Query(value = "SELECT DISTINCT a.id as terminalId, " +
            "a.name as terminalName, a.address as terminalAddress, " +
            "a.code as terminalCode, a.user_id as userId " +
            "FROM terminal a " +
            "INNER JOIN terminal_bank_receive c " +
            "ON c.terminal_id = a.id " +
            "WHERE a.user_id = :userId " +
            "AND c.bank_id = :bankId " +
            "ORDER BY a.code ASC ", nativeQuery = true)
    List<TerminalCodeResponseDTO> getTerminalsByUserIdAndBankIdOwner(String userId, String bankId);

    @Query(value = "SELECT a.id AS terminalId, a.name AS terminalName, "
            + "a.code AS terminalCode, a.address AS terminalAddress "
            + "FROM terminal a "
            + "INNER JOIN merchant b ON b.id = a.merchant_id "
            + "WHERE a.merchant_id = :merchantId "
            + "AND a.user_id = :userId "
            + "ORDER BY a.code ASC "
            + "LIMIT :offset, 10", nativeQuery = true)
    List<IStatisticTerminalOverViewDTO> getListTerminalByMerchantIdOwner(String merchantId, String userId, int offset);

    @Query(value = "SELECT a.name AS terminalName, "
            + "a.code AS terminalCode, a.address AS terminalAddress "
            + "FROM terminal a "
            + "WHERE a.code = :terminalCode ", nativeQuery = true)
    List<ITerminalExportDTO> getTerminalExportByCode(String terminalCode);

    @Query(value = "SELECT DISTINCT a.name AS terminalName, "
            + "a.code AS terminalCode, a.address AS terminalAddress "
            + "FROM terminal a "
            + "INNER JOIN merchant_member b ON b.merchant_id = a.merchant_id "
            + "WHERE b.user_id = :userId AND terminal_id = ''", nativeQuery = true)
    List<ITerminalExportDTO> getTerminalExportByUserId(String userId);

    @Query(value = "SELECT DISTINCT a.name AS terminalName, "
            + "a.code AS terminalCode, a.address AS terminalAddress "
            + "FROM terminal a "
            + "INNER JOIN merchant_member b ON b.terminal_id = a.id "
            + "WHERE b.user_id = :userId AND terminal_id != ''", nativeQuery = true)
    List<ITerminalExportDTO> getTerminalByUserIdHaveRole(String userId);

    @Query(value = "SELECT user_id "
            + "FROM terminal "
            + "WHERE id = :terminalId "
            + "LIMIT 1", nativeQuery = true)
    String getUserIdByTerminalId(String terminalId);
}
