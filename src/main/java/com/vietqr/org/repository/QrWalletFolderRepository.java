package com.vietqr.org.repository;

import com.vietqr.org.entity.qrfeed.QrWalletFolderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface QrWalletFolderRepository extends JpaRepository<QrWalletFolderEntity, String> {

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO qr_wallet_folder (id, qr_folder_id, qr_wallet_id) " +
            "VALUES (:id, :qrFolderId, :qrWalletId)", nativeQuery = true)
    void insertQrWalletFolder(
            @Param("id") String id,
            @Param("qrFolderId") String qrFolderId,
            @Param("qrWalletId") String qrWalletId);


}
