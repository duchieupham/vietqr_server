package com.vietqr.org.repository;

import com.vietqr.org.dto.qrfeed.IListQrWalletDTO;
import com.vietqr.org.dto.qrfeed.IQrWalletDTO;
import com.vietqr.org.dto.qrfeed.QrCommentDTO;
import com.vietqr.org.entity.qrfeed.QrWalletEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface QrWalletRepository extends JpaRepository<QrWalletEntity, String> {

    @Query(value = "SELECT a.id AS id, a.description AS description, a.is_public AS isPublic, a.qr_type as QrType, " +
            "a.time_created as timeCreate, " +
            "a.title AS title, a.value as content, b.role AS role " +
            "FROM viet_qr.qr_wallet a " +
            "INNER JOIN qr_user b ON a.user_id =  b.user_id " +
            "WHERE (a.description LIKE %:value%) OR (a.title LIKE %:value%) " +
            "GROUP BY a.id, b.role " +
            "ORDER BY a.time_created DESC " +
            "LIMIT :offset, :size  ", nativeQuery = true)
    List<IListQrWalletDTO> getQrWallets(String value, int offset, int size);

    @Query(value = "SELECT COUNT(a.id) " +
            "FROM viet_qr.qr_wallet a " +
            "WHERE (a.description LIKE %:value%) OR (a.title LIKE %:value%) " +
            "ORDER BY a.time_created DESC ", nativeQuery = true)
    int countQrWallet(String value);

    @Query(value = "SELECT a.id AS id, a.description AS description, a.is_public AS isPublic, a.qr_type as QrType, " +
            "a.time_created as timeCreate, a.title AS title, a.value as content, b.role AS role " +
            "FROM viet_qr.qr_wallet a " +
            "WHERE a.user_id = :value AND (a.qr_type = 1) OR (a.qr_type = 0) " +
            "ORDER BY time_created DESC ", nativeQuery = true)
    IListQrWalletDTO getQrLinkOrQrTextByUserId(String value);

    @Query(value = "SELECT a.* FROM viet_qr.qr_wallet a WHERE a.id = :qrId ", nativeQuery = true)
    QrWalletEntity getQrLinkOrQrTextById(String qrId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE qr_wallet SET description = :description, is_public = :isPublic, " +
            "qr_type = :qrType, title = :title, value = :content, style = :style, theme = :theme " +
            "WHERE id = :id ", nativeQuery = true)
    void updateQrWallet(String id, String description, int isPublic, int qrType, String title, String content, int style, int theme);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM qr_wallet WHERE id IN :ids", nativeQuery = true)
    void deleteByIds(@Param("ids") List<String> ids);

    @Query(value = "SELECT id FROM qr_wallet WHERE id IN :ids", nativeQuery = true)
    List<String> findExistingIds(@Param("ids") List<String> ids);

    @Query(value = "SELECT a.user_data AS userData " +
            "FROM qr_wallet a " +
            "INNER JOIN qr_wallet_folder b ON a.id = b.qr_wallet_id " +
            "WHERE b.qr_folder_id = :folderId AND a.qr_type = :type ", nativeQuery = true)
    List<String> getUserDataWithType(@Param("folderId") String folderId, @Param("type") int type);

    @Query(value = "SELECT a.user_data AS userData " +
            "FROM qr_wallet a " +
            "INNER JOIN qr_wallet_folder b ON a.id = b.qr_wallet_id " +
            "WHERE b.qr_folder_id = :folderId AND a.qr_type LIKE %:type% ", nativeQuery = true)
    List<String> getUserDataWithoutType(@Param("folderId") String folderId, @Param("type") String type);

    @Query(value = "SELECT a.user_data AS userData " +
            "FROM qr_wallet a " +
            "INNER JOIN qr_wallet_folder b ON a.id = b.qr_wallet_id " +
            "WHERE b.qr_folder_id = :folderId AND a.qr_type = :type ", nativeQuery = true)
    List<String> getQrDataWithType(@Param("folderId") String folderId, @Param("type") int type);

    @Query(value = "SELECT a.qr_data AS qrData " +
            "FROM qr_wallet a " +
            "INNER JOIN qr_wallet_folder b ON a.id = b.qr_wallet_id " +
            "WHERE b.qr_folder_id = :folderId AND a.qr_type LIKE %:type% ", nativeQuery = true)
    List<String> getQrDataWithoutType(@Param("folderId") String folderId, @Param("type") String type);

    @Query(value = "SELECT a.user_data AS userData " +
            "FROM qr_wallet a " +
            "INNER JOIN qr_wallet_folder b ON a.id = b.qr_wallet_id " +
            "WHERE b.qr_folder_id = :folderId AND a.qr_type LIKE %:type% ", nativeQuery = true)
    List<String> getUserVCardData(@Param("folderId") String folderId, @Param("type") int type);

    @Query(value = "SELECT a.user_data AS userData " +
            "FROM qr_wallet a " +
            "INNER JOIN qr_wallet_folder b ON a.id = b.qr_wallet_id " +
            "WHERE b.qr_folder_id = :folderId AND a.qr_type LIKE %:type% ", nativeQuery = true)
    List<String> getUserVietQrData(@Param("folderId") String folderId, @Param("type") int type);

    @Query(value = "SELECT COUNT(a.id) " +
            "FROM qr_wallet a " +
            "INNER JOIN qr_wallet_folder b ON a.id = b.qr_wallet_id " +
            "WHERE b.qr_folder_id = :folderId AND a.qr_type = :type ", nativeQuery = true)
    int countUserLinkOrTextInfo(@Param("folderId") String folderId, @Param("type") int type);

    @Query(value = "SELECT COUNT(a.id) " +
            "FROM qr_wallet a " +
            "INNER JOIN qr_wallet_folder b ON a.id = b.qr_wallet_id " +
            "WHERE b.qr_folder_id = :folderId AND a.qr_type = :type ", nativeQuery = true)
    int countUserVCardInfo(@Param("folderId") String folderId, @Param("type") int type);

    @Query(value = "SELECT COUNT(a.id) " +
            "FROM qr_wallet a " +
            "INNER JOIN qr_wallet_folder b ON a.id = b.qr_wallet_id " +
            "WHERE b.qr_folder_id = :folderId AND a.qr_type = :type ", nativeQuery = true)
    int countUserVietQrInfo(@Param("folderId") String folderId, @Param("type") int type);

    //    @Query(value = "SELECT COUNT(a.id) " +
//            "FROM viet_qr.qr_wallet a " +
//            "INNER JOIN qr_user b ON a.user_id =  b.user_id " +
//            "WHERE (a.description LIKE %:value%) OR (a.title LIKE %:value%) " +
//            "GROUP BY a.id, b.role " +
//            "ORDER BY time_created DESC ", nativeQuery = true)
//    int countQrWallet(String value);
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM qr_wallet WHERE id IN :qrWalletIds", nativeQuery = true)
    void deleteByQrWalletIds(@Param("qrWalletIds") List<String> qrWalletIds);



    @Query(value = "SELECT w.id AS id, w.title AS title, w.description AS description, " +
            "w.value AS value, w.qr_type AS qrType, w.time_created AS timeCreated, w.user_id AS userId, " +
            "(SELECT COUNT(i.id) FROM qr_interaction i WHERE i.qr_wallet_id = w.id AND i.interaction_type = 1) AS likeCount, " +
            "(SELECT COUNT(wc.id) FROM qr_wallet_comment wc WHERE wc.qr_wallet_id = w.id) AS commentCount, " +
            "CASE WHEN (SELECT COUNT(i.id) FROM qr_interaction i WHERE i.qr_wallet_id = w.id AND i.user_id = :userId AND i.interaction_type = 1) > 0 THEN 1 ELSE 0 END AS hasLiked, " +
            "CASE " +
            "WHEN w.qr_type = '0' THEN w.public_id " +
            "WHEN w.qr_type = '1' THEN w.value " +
            "WHEN w.qr_type = '2' THEN CONCAT(JSON_UNQUOTE(JSON_EXTRACT(w.qr_data, '$.fullName')), ' - ', JSON_UNQUOTE(JSON_EXTRACT(w.qr_data, '$.phoneNo'))) " +
            "WHEN w.qr_type = '3' THEN CONCAT(JSON_UNQUOTE(JSON_EXTRACT(w.user_data, '$.bankCode')), ' - ', JSON_UNQUOTE(JSON_EXTRACT(w.user_data, '$.userBankName'))) " +
            "ELSE NULL " +
            "END AS data, " +
            "IFNULL(TRIM(CONCAT_WS(' ', TRIM(ai.last_name), TRIM(ai.middle_name), TRIM(ai.first_name))), 'Undefined') AS fullName, " +
            "IFNULL(ai.img_id, '') AS imageId, " +
            "IFNULL(w.style, '') AS style, " +
            "IFNULL(w.theme, '') AS theme " +
            "FROM qr_wallet w " +
            "LEFT JOIN account_information ai ON ai.user_id = w.user_id " +
            "WHERE w.is_public = 1 " +
            "ORDER BY w.time_created DESC " +
            "LIMIT :offset, :size", nativeQuery = true)
    List<IQrWalletDTO> findAllPublicQrWallets(@Param("userId") String userId, @Param("offset") int offset, @Param("size") int size);

    @Query(value = "SELECT COUNT(id) FROM qr_wallet WHERE is_public = 1", nativeQuery = true)
    int countPublicQrWallets();


    @Query(value = "SELECT w.id AS id, w.title AS title, w.description AS description, w.value AS value, " +
            "w.qr_type AS qrType, w.time_created AS timeCreated, w.user_id AS userId, " +
            "(SELECT COUNT(id) FROM qr_interaction i WHERE i.qr_wallet_id = w.id AND i.interaction_type =1) AS likeCount, " +
            "(SELECT COUNT(id) FROM qr_wallet_comment wc WHERE wc.qr_wallet_id = w.id) AS commentCount " +
            "FROM qr_wallet w WHERE w.id = :qrWalletId", nativeQuery = true)
    IQrWalletDTO findQRWalletDetailsById(@Param("qrWalletId") String qrWalletId);


//    @Query(value = "SELECT w.id AS id, w.title AS title, w.description AS description, w.value AS value, " +
//            "w.qr_type AS qrType, w.time_created AS timeCreated, w.user_id AS userId, " +
//            "(SELECT COUNT(id) FROM qr_interaction i WHERE i.qr_wallet_id = w.id AND i.interaction_type = 1) AS likeCount, " +
//            "(SELECT COUNT(id) FROM qr_comment wc WHERE wc.qr_wallet_id = w.id) AS commentCount, " +
//            "CASE WHEN (SELECT COUNT(id) FROM qr_interaction i WHERE i.qr_wallet_id = w.id AND i.user_id = :userId AND i.interaction_type = 1) > 0 THEN 1 ELSE 0 END AS hasLiked, " +
//            "CASE " +
//            "WHEN w.qr_type = '0' THEN w.public_id " +
//            "WHEN w.qr_type = '1' THEN w.value " +
//            "WHEN w.qr_type = '2' THEN CONCAT(JSON_UNQUOTE(JSON_EXTRACT(w.qr_data, '$.fullName')), ' - ', JSON_UNQUOTE(JSON_EXTRACT(w.qr_data, '$.phoneNo'))) " +
//            "WHEN w.qr_type = '3' THEN CONCAT(JSON_UNQUOTE(JSON_EXTRACT(w.user_data, '$.bankCode')), ' - ', JSON_UNQUOTE(JSON_EXTRACT(w.user_data, '$.userBankName'))) " +
//            "ELSE NULL " +
//            "END AS data " +
//            "FROM qr_wallet w WHERE w.id = :qrWalletId", nativeQuery = true)
//    IQrWalletDTO findQRWalletDetailsById(@Param("qrWalletId") String qrWalletId, @Param("userId") String userId);

    @Query(value = "SELECT c.id AS id, c.message AS message, " +
            "JSON_UNQUOTE(JSON_EXTRACT(c.user_data, '$.userBankName')) AS userBankName, " +
            "c.time_created AS timeCreated " +
            "FROM qr_comment c " +
            "INNER JOIN qr_wallet_comment wc ON wc.qr_comment_id = c.id " +
            "WHERE wc.qr_wallet_id = :qrWalletId", nativeQuery = true)
    List<QrCommentDTO> findCommentsByQrWalletId(@Param("qrWalletId") String qrWalletId);

    @Query(value = "SELECT COUNT(id) FROM qr_comment WHERE qr_wallet_id = :qrWalletId", nativeQuery = true)
    int countCommentsByQrWalletId(@Param("qrWalletId") String qrWalletId);
}
