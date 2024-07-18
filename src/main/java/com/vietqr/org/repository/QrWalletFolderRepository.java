package com.vietqr.org.repository;

import com.google.auto.value.extension.memoized.Memoized;
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

//    @Transactional
//    @Modifying
//    @Query(value = "INSERT INTO qr_wallet_folder (id, qr_folder_id, qr_wallet_id) " +
//            "VALUES (:id, :qrFolderId, :qrWalletId) " +
//            "INNER JOIN qr_wallet b ON a.qr_wallet_id = b.id " +
//            "WHERE b.user_id = :userId ", nativeQuery = true)

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO qr_wallet_folder (id, qr_folder_id, qr_wallet_id) " +
//            "SELECT :id, :qrFolderId, b.id " +
//            "FROM qr_wallet b " +
//            "WHERE b.id = :qrWalletId AND b.user_id = :userId "
            "VALUES (:id, :qrFolderId, :qrWalletId) ", nativeQuery = true)
    void insertQrWalletFolder(
            @Param("id") String id,
            @Param("qrFolderId") String qrFolderId,
            @Param("qrWalletId") String qrWalletId);

    @Query(value = "SELECT b.id, b.qrFolderId, b.id " +
            "FROM qr_wallet b " +
            "WHERE b.id = :qrFolderId AND b.user_id = :userId ", nativeQuery = true)
    List<String> getToInsert(String qrFolderId, String userId);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO `qr_wallet_folder` (id, qr_folder_id, qr_wallet_id) " +
            "VALUES (:id, :qrFolderId, :qrWalletId) ", nativeQuery = true)
    void addQrWalletFolder(String id, String qrFolderId, String qrWalletId);

    @Query(value = "SELECT qr_wallet_id FROM qr_wallet_folder WHERE qr_folder_id = :qrFolderId", nativeQuery = true)
    List<String> findQrWalletIdsByQrFolderId(@Param("qrFolderId") String qrFolderId);

    @Query(value = "SELECT COUNT(id) FROM qr_wallet_folder WHERE qr_folder_id = :qrFolderId", nativeQuery = true)
    int countQrFolder(String qrFolderId);

    @Query(value = "SELECT a.qr_wallet_id FROM qr_wallet_folder a " +
            "INNER JOIN qr_folder b ON a.qr_folder_id = b.id " +
            "WHERE qr_folder_id = :qrFolderId " +
            "AND b.user_id = :userId ", nativeQuery = true)
    List<String> checkQrExists(String qrFolderId, String userId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM qr_wallet_folder WHERE qr_folder_id = :qrFolderId", nativeQuery = true)
    void deleteByQrFolderId(@Param("qrFolderId") String qrFolderId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM qr_wallet_folder WHERE qr_wallet_id IN :qrWalletIds", nativeQuery = true)
    void deleteQrItemsInAllFolders(@Param("qrWalletIds") List<String> qrWalletIds);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM qr_wallet_folder WHERE qr_wallet_id IN :qrIds", nativeQuery = true)
    void deleteQrsInFolder(List<String> qrIds);

    @Modifying
    @Query(value = "SELECT qwf.qr_wallet_id " +
            "FROM qr_wallet_folder qwf " +
            "INNER JOIN qr_user qu ON qwf.qr_folder_id = qu.qr_folder_id " +
            "WHERE qu.user_id = :userId " +
            "AND qu.role = 'ADMIN' " +
            "AND qwf.qr_folder_id = :folderId " +
            "AND qwf.qr_wallet_id IN (:qrIds)", nativeQuery = true)
    List<String> getListQrsInFolder(String folderId, String userId, List<String> qrIds);
}
