package com.vietqr.org.repository;

import com.vietqr.org.dto.MerchantResponseDTO;
import com.vietqr.org.dto.MerchantWebResponseDTO;
import com.vietqr.org.entity.MerchantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}
