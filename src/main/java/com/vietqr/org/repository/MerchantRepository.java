package com.vietqr.org.repository;

import com.vietqr.org.dto.*;
import com.vietqr.org.entity.MerchantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface MerchantRepository extends JpaRepository<MerchantEntity, Long> {

    @Query(value = "SELECT a.id AS id, a.name AS name, "
            + "a.address AS address, a.vso_code AS vsoCode, "
            + "COUNT(b.id) AS totalTerminals "
            + "FROM merchant a "
            + "LEFT JOIN terminal b ON a.id = b.merchant_id "
            + "WHERE a.user_id = :userId "
            + "GROUP BY a.id "
            + "LIMIT :offset, 20", nativeQuery = true)
    List<MerchantResponseDTO> getMerchantsByUserId(
            @Param(value = "userId") String userId,
            @Param(value = "offset") int offset);

    @Query(value = "SELECT a.id AS id, a.name AS name, "
            + "a.address AS address, count(b.id) AS totalTerminals "
            + "FROM merchant a INNER JOIN terminal b ON a.id = b.merchant_id "
            + "WHERE id = :merchantId ", nativeQuery = true)
    MerchantWebResponseDTO getMerchantWebResponseDTO(String merchantId);

    @Query(value = "SELECT a.id AS id, a.name AS name, "
            + "a.address AS address, COUNT(DISTINCT b.id) AS totalTerminals "
            + "FROM merchant a INNER JOIN terminal b ON a.id = b.merchant_id "
            + "INNER JOIN account_bank_receive_share c ON b.id = c.terminal_id "
            + "WHERE c.user_id = :userId "
            + "GROUP BY a.id "
            + "LIMIT 1 ", nativeQuery = true)
    MerchantWebResponseDTO getMerchantByUserIdLimit(String userId);

    @Query(value = "SELECT a.id AS merchantId, a.name AS merchantName, a.vso AS vsoCode, "
            + "COALESCE(COUNT(c.id), 0) AS totalTrans, COALESCE(SUM(c.amount), 0) AS totalAmount "
            + "FROM merchant a "
            + "INNER JOIN terminal b ON a.id = b.merchant_id "
            + "INNER JOIN transaction_terminal_temp c ON c.terminal_code = b.code "
            + "INNER JOIN account_bank_receive_share d ON d.terminal_id = b.id "
            + "WHERE a.user_id = :userId AND a.id = :merchantId "
            + "AND c.time >= :fromDate AND c.time <= :toDate LIMIT 1", nativeQuery = true)
    IStatisticMerchantDTO getStatisticMerchantByMerchantAndUserId(String merchantId, String userId,
                                                                  String fromDate, String toDate);

    @Transactional
    @Modifying
    @Query(value = "UPDATE merchant SET is_active = 0 WHERE id = :merchantId AND user_id = :userId", nativeQuery = true)
    int inactiveMerchantByMerchantId(String merchantId, String userId);

    @Query(value = "SELECT DISTINCT a.id AS id, a.name AS name, "
            + "a.address AS address, a.vso_code AS vsoCode "
            + "FROM merchant a "
            + "INNER JOIN merchant_member c ON a.id = c.merchant_id "
            + "WHERE c.user_id = :userId ", nativeQuery = true)
    List<MerchantResponseListDTO> getMerchantsByUserId(String userId);

    @Query(value = "SELECT a.id AS id, a.name AS name, "
            + "a.address AS address, COALESCE(a.vso_code, '') AS vsoCode, a.user_id AS userId, "
            + "COUNT(b.id) AS totalTerminals, COALESCE(a.business_type, '') AS businessType, "
            + "COALESCE(a.business_sector, '') AS businessSector, "
            + "COALESCE(a.tax_id, '') AS taxId "
            + "FROM merchant a "
            + "LEFT JOIN terminal b ON a.id = b.merchant_id "
            + "LEFT JOIN terminal_bank_receive c ON c.terminal_id = b.id "
            + "WHERE a.user_id = :userId AND c.bank_id = :bankId "
            + "GROUP BY a.id ", nativeQuery = true)
    List<MerchantResponseDTO> getMerchantsByUserIdNoPaging(String userId, String bankId);
}
