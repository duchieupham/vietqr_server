package com.vietqr.org.repository;

import com.vietqr.org.dto.MerchantResponseDTO;
import com.vietqr.org.entity.MerchantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MerchantRepository extends JpaRepository<MerchantEntity, Long> {

    @Query(value = "SELECT id AS id, name AS name, "
            + "address AS address, vso_code AS vsoCode"
            + "FROM merchant WHERE user_id = :userId", nativeQuery = true)
    List<MerchantResponseDTO> getMerchantsByUserId(
            @Param(value = "userId") String userId);
}
