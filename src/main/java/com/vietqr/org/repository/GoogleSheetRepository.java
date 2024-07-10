package com.vietqr.org.repository;

import com.vietqr.org.dto.GoogleSheetInfoDetailDTO;
import com.vietqr.org.entity.GoogleSheetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface GoogleSheetRepository extends JpaRepository<GoogleSheetEntity, String> {

    @Transactional
    @Modifying
    @Query(value = "UPDATE google_sheet SET webhook = :webhook WHERE id = :googleSheetId", nativeQuery = true)
    void updateWebhook(@Param("googleSheetId") String googleSheetId, @Param("webhook") String webhook);

    @Query(value = "SELECT COUNT(id) FROM google_sheet WHERE user_id = :userId", nativeQuery = true)
    int countGoogleSheetsByUserId(@Param("userId") String userId);

    @Query(value = "SELECT * FROM google_sheet WHERE user_id = :userId", nativeQuery = true)
    GoogleSheetEntity getGoogleSheetByUserId(@Param("userId") String userId);

    @Query(value = "SELECT * FROM google_sheet WHERE id = :id", nativeQuery = true)
    GoogleSheetEntity getGoogleSheetById(@Param("id") String id);

    @Query(value = "SELECT gs.id AS googleSheetId, gs.webhook AS webhook, COUNT(gsab.id) AS bankAccountCount " +
            "FROM google_sheet gs " +
            "LEFT JOIN google_sheet_account_bank gsab ON gs.id = gsab.google_sheet_id " +
            "WHERE gs.user_id = :userId " +
            "GROUP BY gs.id, gs.webhook " +
            "LIMIT :size OFFSET :offset", nativeQuery = true)
    List<GoogleSheetInfoDetailDTO> getGoogleSheetsByUserIdWithPagination(@Param("userId") String userId, @Param("offset") int offset, @Param("size") int size);

    @Query(value = "SELECT * FROM google_sheet WHERE webhook = :webhook", nativeQuery = true)
    GoogleSheetEntity findByWebhook(@Param("webhook") String webhook);


}