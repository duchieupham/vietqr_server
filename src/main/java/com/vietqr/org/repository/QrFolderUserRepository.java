package com.vietqr.org.repository;

import com.vietqr.org.dto.qrfeed.IUserInFolderDTO;
import com.vietqr.org.dto.qrfeed.UserRoleDTO;
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
    void insertQrWalletFolder(
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

    @Query(value = "SELECT DISTINCT qfu.user_id AS userId, qu.role AS role " +
            "FROM qr_folder_user qfu " +
            "INNER JOIN qr_user qu ON qfu.user_id = qu.user_id " +
            "WHERE qfu.qr_folder_id = :folderId " +
            "UNION " +
            "SELECT DISTINCT qf.user_id AS userId, 'ADMIN' AS role " +
            "FROM qr_folder qf " +
            "WHERE qf.id = :folderId ", nativeQuery = true)
    List<UserRoleDTO> findUserRolesByFolderId(@Param("folderId") String folderId);

}
