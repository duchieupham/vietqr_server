package com.vietqr.org.repository;

import com.vietqr.org.dto.qrfeed.IUserInFolderDTO;
import com.vietqr.org.dto.qrfeed.IUserRoleDTO;

import com.vietqr.org.entity.qrfeed.QrFolderUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface QrFolderUserRepository extends JpaRepository<QrFolderUserEntity, String> {

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO qr_folder_user (id, qr_folder_id, user_id) " +
            "VALUES (:id, :qrFolderId, :userId)", nativeQuery = true)
    void insertQrWalletFolder(String id, String qrFolderId, String userId);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO qr_folder_user (id, qr_folder_id, user_id) " +
            "VALUES (:id, :qrFolderId, :userId)", nativeQuery = true)
    void addUserAdmin(
            @Param("id") String id,
            @Param("qrFolderId") String qrFolderId,
            @Param("userId") String userId);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO qr_folder_user (id, qr_folder_id, user_id) " +
            "SELECT :id, :qrFolderId, :userIds " +
            "FROM DUAL " +
            "WHERE EXISTS (SELECT 1 FROM qr_wallet u WHERE u.id = :userId)", nativeQuery = true)
    void insertUserToFolder(@Param("id") String id,
                            @Param("qrFolderId") String qrFolderId,
                            @Param("userIds") String userIds,
                            @Param("userId") String userId);


    @Query(value = "SELECT a.id AS id, a.user_id AS userId, a.user_data AS userData, b.qr_folder_id " +
            "FROM qr_wallet a " +
            "INNER JOIN qr_folder_user b ON a.user_id = b.user_id " +
            "WHERE a.user_id = :qrFolderId ", nativeQuery = true)
    List<IUserInFolderDTO> getUserInFolder(String qrFolderId);

//    @Query(value = "SELECT DISTINCT qfu.user_id AS userId, qu.role AS role " +
//            "FROM qr_folder_user qfu " +
//            "INNER JOIN qr_user qu ON qfu.user_id = qu.user_id " +
//            "WHERE qfu.qr_folder_id = :folderId " +
//            "UNION " +
//            "SELECT DISTINCT qf.user_id AS userId, 'ADMIN' AS role " +
//            "FROM qr_folder qf " +
//            "WHERE qf.id = :folderId", nativeQuery = true)
//    List<IUserRoleDTO> findUserRolesByFolderId(@Param("folderId") String folderId);

    @Query(value = "SELECT DISTINCT qfu.user_id AS userId, qu.role AS role, " +
            "IFNULL(TRIM(CONCAT_WS(' ', TRIM(ai.last_name), TRIM(ai.middle_name), TRIM(ai.first_name))), 'Undefined') AS fullName, " +
            "IFNULL(ai.img_id, '') AS imageId " +
            "FROM qr_folder_user qfu " +
            "INNER JOIN qr_user qu ON (qfu.qr_folder_id = qu.qr_folder_id AND qfu.user_id = qu.user_id) " +
            "LEFT JOIN account_information ai ON ai.user_id = qfu.user_id " +
            "WHERE qfu.qr_folder_id = :folderId " +
            "UNION " +
            "SELECT DISTINCT qf.user_id AS userId, 'ADMIN' AS role, " +
            "IFNULL(TRIM(CONCAT_WS(' ', TRIM(ai.last_name), TRIM(ai.middle_name), TRIM(ai.first_name))), 'Undefined') AS fullName, " +
            "IFNULL(ai.img_id, '') AS imageId " +
            "FROM qr_folder qf " +
            "LEFT JOIN account_information ai ON ai.user_id = qf.user_id " +
            "WHERE qf.id = :folderId", nativeQuery = true)
    List<IUserRoleDTO> findUserRolesByFolderId(@Param("folderId") String folderId);

    @Query(value = "SELECT DISTINCT qfu.user_id AS userId, qu.role AS role, " +
            "IFNULL(TRIM(CONCAT_WS(' ', TRIM(ai.last_name), TRIM(ai.middle_name), " +
            "TRIM(ai.first_name))), 'Undefined') AS fullName, " +
            "IFNULL(ai.img_id, '') AS imageId, " +
            "IFNULL(al.phone_no, '') AS phoneNo " +
            "FROM qr_folder_user qfu " +
            "INNER JOIN qr_user qu ON (qfu.qr_folder_id = qu.qr_folder_id AND qfu.user_id = qu.user_id) " +
            "LEFT JOIN account_information ai ON ai.user_id = qfu.user_id " +
            "LEFT JOIN account_login al ON al.id = qfu.user_id " +
            "WHERE qfu.qr_folder_id = :folderId " +
            "AND (TRIM(CONCAT_WS(' ', TRIM(ai.last_name), TRIM(ai.middle_name), " +
            "TRIM(ai.first_name))) LIKE %:value%) " +
            "ORDER BY " +
            "CASE " +
            "WHEN role = 'ADMIN' THEN 1 " +
            "WHEN role IN ('EDITOR', 'MANAGER') THEN 2 " +
            "WHEN role = 'VIEWER' THEN 3 " +
            "ELSE 4 " +
            "END, role " +
            "LIMIT :offset, :size ", nativeQuery = true)
    List<IUserRoleDTO> findUserRolesByFolderId(@Param("folderId") String folderId,
                                               @Param("value") String value,
                                               @Param("offset") int offset,
                                               @Param("size") int size);

    @Query(value = "SELECT COUNT(*) FROM (" +
            "SELECT qfu.user_id " +
            "FROM qr_folder_user qfu " +
            "INNER JOIN qr_user qu ON (qfu.qr_folder_id = qu.qr_folder_id AND qfu.user_id = qu.user_id) " +
            "LEFT JOIN account_information ai ON ai.user_id = qfu.user_id " +
            "WHERE qfu.qr_folder_id = :folderId " +
            "AND (TRIM(CONCAT_WS(' ', TRIM(ai.last_name), TRIM(ai.middle_name), TRIM(ai.first_name))) LIKE %:value%) " +
            "UNION " +
            "SELECT qf.user_id " +
            "FROM qr_folder qf " +
            "LEFT JOIN account_information ai ON ai.user_id = qf.user_id " +
            "WHERE qf.id = :folderId " +
            "AND (TRIM(CONCAT_WS(' ', TRIM(ai.last_name), TRIM(ai.middle_name), TRIM(ai.first_name))) LIKE %:value%)" +
            ") AS subquery", nativeQuery = true)
    int countUserRolesByFolderId(@Param("folderId") String folderId, @Param("value") String value);

    @Query(value = "SELECT COUNT(id) FROM qr_folder_user WHERE qr_folder_id = :folderId ", nativeQuery = true)
    int countUsersFolder(String folderId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM qr_folder_user WHERE qr_folder_id = :qrFolderId AND user_id = :userId", nativeQuery = true)
    void deleteUserFromFolder(@Param("qrFolderId") String qrFolderId, @Param("userId") String userId);

    @Query(value = "SELECT COUNT(id) FROM qr_folder_user WHERE qr_folder_id = :qrFolderId AND user_id = :userId", nativeQuery = true)
    int countUserInFolder(@Param("qrFolderId") String qrFolderId, @Param("userId") String userId);
//    @Query(nativeQuery = true, name = "QrFolderUser.findUserRolesByFolderId")
//    List<IUserRoleDTO> findUserRolesByFolderId(@Param("folderId") String folderId);
}
