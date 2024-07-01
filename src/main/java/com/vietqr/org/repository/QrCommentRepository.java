package com.vietqr.org.repository;

import com.vietqr.org.dto.qrfeed.QrCommentDTO;
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

    @Query(value = "SELECT c.id AS id, c.message AS message, " +
            "JSON_UNQUOTE(JSON_EXTRACT(c.user_data, '$.fullName')) AS fullName, " +
            "c.time_created AS timeCreated " +
            "FROM qr_comment c " +
            "INNER JOIN qr_wallet_comment wc ON wc.qr_comment_id = c.id " +
            "WHERE wc.qr_wallet_id = :qrWalletId", nativeQuery = true)
    List<QrCommentDTO> findCommentsByQrWalletId(@Param("qrWalletId") String qrWalletId);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO qr_comment (id, message, user_id, user_data, time_created) VALUES(:id, :message, :userId, :userData, :timeCreated)", nativeQuery = true)
    void insertComment(@Param("id") String id, @Param("message") String message, @Param("userId") String userId, @Param("userData") String userData, @Param("timeCreated") long timeCreated);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO qr_wallet_comment (id, qr_wallet_id, qr_comment_id) VALUES (:id, :qrWalletId, :qrCommentId)", nativeQuery = true)
    void linkCommentToQrWallet(@Param("id") String id, @Param("qrWalletId") String qrWalletId, @Param("qrCommentId") String qrCommentId);

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

    @Query(value = "SELECT DISTINCT JSON_UNQUOTE(JSON_EXTRACT(c.user_data, '$.fullName')) AS fullName " +
            "FROM qr_comment c " +
            "INNER JOIN qr_wallet_comment wc ON wc.qr_comment_id = c.id " +
            "WHERE wc.qr_wallet_id = :qrWalletId", nativeQuery = true)
    List<String> findUserNamesWhoCommented(@Param("qrWalletId") String qrWalletId);

}