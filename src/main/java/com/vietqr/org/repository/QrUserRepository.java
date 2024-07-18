package com.vietqr.org.repository;

import com.vietqr.org.dto.qrfeed.UserRoleDTO;
import com.vietqr.org.entity.qrfeed.QrUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface QrUserRepository extends JpaRepository<QrUserEntity, String> {
    @Modifying
    @Transactional
    @Query(value="UPDATE qr_user SET role = :role WHERE user_id = :userId AND qr_wallet_id = :qrWalletId",nativeQuery = true)
    void updateUserRole(@Param("userId") String userId, @Param("qrWalletId") String qrWalletId, @Param("role") String role);

    @Modifying
    @Transactional
    @Query(value="UPDATE qr_user qu SET qu.role = :role WHERE qu.user_id = :userId AND qu.user_id IN " +
            "(SELECT qfu.user_id FROM qr_folder_user qfu WHERE qfu.qr_folder_id = :folderId " +
            "UNION " +
            "SELECT qf.user_id FROM qr_folder qf WHERE qf.id = :folderId)", nativeQuery = true)
    void updateSingleUserRole(@Param("userId") String userId, @Param("folderId") String folderId, @Param("role") String role);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM qr_user WHERE user_id = :userId AND qr_wallet_id IN (" +
            "SELECT qf.qr_folder_id FROM qr_folder_user qf WHERE qf.qr_folder_id = :qrFolderId " +
            "UNION " +
            "SELECT qf.id FROM qr_folder qf WHERE qf.id = :qrFolderId)", nativeQuery = true)
    void deleteUserRole(@Param("qrFolderId") String qrFolderId, @Param("userId") String userId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE qr_user SET role = :role "
            + "WHERE user_id = :userId AND qr_folder_id = :folderId ", nativeQuery = true)
    void updateRoleUser(String folderId, String userId, String role);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM qr_user "
            + "WHERE user_id = :userId AND qr_folder_id = :folderId ", nativeQuery = true)
    void deleteUserFromFolder(String folderId, String userId);

    @Query(value = "SELECT role FROM qr_user "
            + "WHERE (user_id = :userID) AND (qr_folder_id = :folderId) ", nativeQuery = true)
    String checkRoleEdit(String userID, String folderId);
}
