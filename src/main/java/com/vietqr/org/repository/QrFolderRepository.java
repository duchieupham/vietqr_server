package com.vietqr.org.repository;

import com.vietqr.org.dto.qrfeed.FolderInformationDTO;
import com.vietqr.org.dto.qrfeed.IFolderInformationDTO;
import com.vietqr.org.dto.qrfeed.IListQrFolderDTO;
import com.vietqr.org.dto.qrfeed.IListQrWalletDTO;
import com.vietqr.org.entity.qrfeed.QrFolderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface QrFolderRepository extends JpaRepository<QrFolderEntity, String> {

    @Query(value = "SELECT a.id AS id, a.description AS description, a.time_created AS timeCreate, " +
            "a.title AS title, a.user_id AS userId " +
            "FROM qr_folder a " +
            "WHERE a.user_id = :userId ", nativeQuery = true)
    List<IListQrFolderDTO> getListFolders(String userId);

    @Query(value = "SELECT COUNT(a.id) " +
            "FROM qr_folder a " +
            "WHERE ((a.description LIKE %:value%) OR (a.title LIKE %:value%)) AND (a.user_id = :userId) ", nativeQuery = true)
    int countQrFolder(String value,String userId );

    @Transactional
    @Modifying
    @Query(value = "UPDATE qr_folder SET description = :description, title = :title WHERE id = :id ", nativeQuery = true)
    void updateQrFolder(String id, String description, String title);

    @Query(value = "SELECT a.* FROM viet_qr.qr_folder a WHERE a.id = :id ", nativeQuery = true)
    QrFolderEntity getFolderById(String id);

    @Query(value = "SELECT user_id as userId, id AS folderId, title AS titleFolder, description AS descriptionFolder " +
            "FROM viet_qr.qr_folder " +
            "WHERE id = :folderId ", nativeQuery = true)
    IFolderInformationDTO getFolderInfo(String folderId);


    @Query(value = "SELECT user_id as userId, id AS folderId, title AS titleFolder, description AS descriptionFolder " +
            "FROM viet_qr.qr_folder " +
            "WHERE id = :folderId ", nativeQuery = true)
    IFolderInformationDTO getQrInFolder(String folderId);

    @Query(value = "SELECT * FROM qr_folder WHERE id = :qrFolderId", nativeQuery = true)
    QrFolderEntity findByQrFolderId(@Param("qrFolderId") String qrFolderId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM qr_folder WHERE id = :qrFolderId", nativeQuery = true)
    void deleteByQrFolderId(@Param("qrFolderId") String qrFolderId);
}
