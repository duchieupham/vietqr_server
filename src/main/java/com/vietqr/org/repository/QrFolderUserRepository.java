package com.vietqr.org.repository;

import com.vietqr.org.entity.qrfeed.QrFolderUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

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
}
