package com.vietqr.org.repository;

import com.vietqr.org.dto.qrfeed.UserLikeDTO;
import com.vietqr.org.entity.qrfeed.QrInteractionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface QrInteractionRepository extends JpaRepository<QrInteractionEntity, String> {
    @Query(value = "SELECT COUNT(*) FROM qr_interaction WHERE qr_wallet_id = :qrWalletId AND user_id = :userId AND interaction_type = 1", nativeQuery = true)
    int countLike(@Param("qrWalletId") String qrWalletId, @Param("userId") String userId);

    @Query(value = "SELECT COUNT(*) FROM qr_interaction WHERE qr_wallet_id = :qrWalletId AND user_id = :userId", nativeQuery = true)
    int countInteraction(@Param("qrWalletId") String qrWalletId, @Param("userId") String userId);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO qr_interaction (id, qr_wallet_id, user_id, interaction_type, time_created) VALUES (:id, :qrWalletId, :userId, :interactionType, :timeCreated)", nativeQuery = true)
    void insertInteraction(@Param("id") String id, @Param("qrWalletId") String qrWalletId, @Param("userId") String userId, @Param("interactionType") int interactionType, @Param("timeCreated") long timeCreated);

    @Modifying
    @Transactional
    @Query(value = "UPDATE qr_interaction SET interaction_type = :interactionType, time_created = :timeCreated WHERE qr_wallet_id = :qrWalletId AND user_id = :userId", nativeQuery = true)
    void updateInteraction(@Param("qrWalletId") String qrWalletId, @Param("userId") String userId, @Param("interactionType") int interactionType, @Param("timeCreated") long timeCreated);

    @Query(value = "SELECT  JSON_UNQUOTE(JSON_EXTRACT(u.user_data, '$.fullName')) AS fullName " +
            "FROM qr_interaction i " +
            "INNER JOIN qr_wallet u ON i.qr_wallet_id = u.id " +
            "WHERE i.qr_wallet_id = :qrWalletId AND i.interaction_type = 1", nativeQuery = true)
    List<String> findUserNamesWhoLiked(@Param("qrWalletId") String qrWalletId);

    @Query(value = "SELECT DISTINCT i.user_id AS userId, " +
            "IFNULL(TRIM(CONCAT_WS(' ', TRIM(ai.last_name), TRIM(ai.middle_name), TRIM(ai.first_name))), 'Undefined') AS fullName " +
            "FROM qr_interaction i " +
            "LEFT JOIN account_information ai ON ai.user_id = i.user_id " +
            "WHERE i.qr_wallet_id = :qrWalletId AND i.interaction_type = 1 " +
            "GROUP BY i.user_id, ai.last_name, ai.middle_name, ai.first_name " +
            "ORDER BY i.user_id " +
            "LIMIT :offset, :size", nativeQuery = true)
    List<UserLikeDTO> findLikersByQrWalletId(@Param("qrWalletId") String qrWalletId, @Param("offset") int offset, @Param("size") int size);

    @Query(value = "SELECT COUNT(DISTINCT i.user_id) " +
            "FROM qr_interaction i " +
            "WHERE i.qr_wallet_id = :qrWalletId AND i.interaction_type = 1", nativeQuery = true)
    int countLikersByQrWalletId(@Param("qrWalletId") String qrWalletId);
}
