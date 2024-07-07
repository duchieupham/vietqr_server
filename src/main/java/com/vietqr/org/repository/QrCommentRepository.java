package com.vietqr.org.repository;

import com.vietqr.org.dto.qrfeed.UserCommentDTO;
import com.vietqr.org.entity.qrfeed.QrCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface QrCommentRepository extends JpaRepository<QrCommentEntity, String> {

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO qr_comment (id, message, user_id, user_data, time_created) " +
            "VALUES(:id, :message, :userId, :userData, :timeCreated)", nativeQuery = true)
    void insertComment(@Param("id") String id,
                       @Param("message") String message,
                       @Param("userId") String userId,
                       @Param("userData") String userData,
                       @Param("timeCreated") long timeCreated);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO qr_wallet_comment (id, qr_wallet_id, qr_comment_id) " +
            "VALUES (:id, :qrWalletId, :qrCommentId)", nativeQuery = true)
    void linkCommentToQrWallet(@Param("id") String id,
                               @Param("qrWalletId") String qrWalletId,
                               @Param("qrCommentId") String qrCommentId);

    @Query(value = "SELECT user_data FROM qr_wallet WHERE user_id = :userId LIMIT 1", nativeQuery = true)
    String findUserDataByUserId(@Param("userId") String userId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM qr_comment WHERE id = :commentId",nativeQuery=true)
    void deleteComment(@Param("commentId") String commentId);


    @Transactional
    @Modifying
    @Query(value="DELETE FROM qr_wallet_comment WHERE qr_comment_id = :commentId",nativeQuery=true)
    void unlinkCommentFromQrWallet(@Param("commentId")String commentId);


    @Query(value = "SELECT c.user_id AS userId, " +
            "IFNULL(TRIM(CONCAT_WS(' ', TRIM(ai.last_name), TRIM(ai.middle_name), TRIM(ai.first_name))), 'Undefined') AS fullName, " +
            "IFNULL(ai.img_id, '') AS imageId " +
            "FROM qr_comment c " +
            "LEFT JOIN account_information ai ON ai.user_id = c.user_id " +
            "INNER JOIN qr_wallet_comment wc ON wc.qr_comment_id = c.id " +
            "WHERE wc.qr_wallet_id = :qrWalletId " +
            "ORDER BY c.user_id " +
            "LIMIT :offset, :size", nativeQuery = true)
    List<UserCommentDTO> findCommentersByQrWalletId(@Param("qrWalletId") String qrWalletId, @Param("offset") int offset, @Param("size") int size);

    @Query(value = "SELECT COUNT(DISTINCT c.user_id) " +
            "FROM qr_comment c " +
            "INNER JOIN qr_wallet_comment wc ON wc.qr_comment_id = c.id " +
            "WHERE wc.qr_wallet_id = :qrWalletId", nativeQuery = true)
    int countCommentersByQrWalletId(@Param("qrWalletId") String qrWalletId);


    @Query(value = "SELECT wc.qr_wallet_id FROM qr_wallet_comment wc WHERE wc.qr_comment_id = :commentId", nativeQuery = true)
    String findQrWalletIdByCommentId(@Param("commentId") String commentId);
}