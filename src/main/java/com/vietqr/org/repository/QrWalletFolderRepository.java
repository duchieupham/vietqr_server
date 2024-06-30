package com.vietqr.org.repository;

import com.vietqr.org.entity.qrfeed.QrWalletFolderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

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



    @Query(value = "SELECT qr_wallet_id FROM qr_wallet_folder WHERE qr_folder_id = :qrFolderId", nativeQuery = true)
    List<String> findQrWalletIdsByQrFolderId(@Param("qrFolderId") String qrFolderId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM qr_wallet_folder WHERE qr_folder_id = :qrFolderId", nativeQuery = true)
    void deleteByQrFolderId(@Param("qrFolderId") String qrFolderId);

}
