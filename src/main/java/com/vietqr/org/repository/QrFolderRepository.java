package com.vietqr.org.repository;

import com.vietqr.org.dto.qrfeed.*;
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
            "INNER JOIN account_information b ON a.user_id = b.user_id " +
            "INNER JOIN qr_folder_user c ON a.user_id = c.user_id " +
            "WHERE ((a.description LIKE %:value%) OR (a.title LIKE %:value%)) AND (c.user_id = :userId) " +
            "ORDER BY a.time_created DESC " +
            "LIMIT :offset, :size  ", nativeQuery = true)
    List<IListQrFolderDTO> getListFolders(String value, int offset, int size, String userId);

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

    @Query(value = "SELECT a.id AS id, a.description AS description, " +
            "DATE_FORMAT(CONVERT_TZ(FROM_UNIXTIME(a.time_created), '+00:00', '+07:00'), '%d-%m-%Y %H:%i') AS timeCreated, " +
            "a.title AS title, a.user_id AS userId " +
            "FROM qr_folder a " +
            "WHERE a.id = :folderId ", nativeQuery = true)
    IFolderDetailDTO getFolderDetailById(String folderId);

    @Query(value = "SELECT * FROM qr_folder WHERE id = :qrFolderId", nativeQuery = true)
    QrFolderEntity findByQrFolderId(@Param("qrFolderId") String qrFolderId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM qr_folder WHERE id = :qrFolderId", nativeQuery = true)
    void deleteByQrFolderId(@Param("qrFolderId") String qrFolderId);


    @Query(value = "SELECT ai.user_id AS userId, " +
            "IFNULL(TRIM(CONCAT_WS(' ', TRIM(ai.last_name), TRIM(ai.middle_name), TRIM(ai.first_name))), '') AS fullName, " +
            "IFNULL(al.phone_no, '') AS phoneNo, " +
            "IFNULL(ai.img_id, '') AS imageId " +
            "FROM account_information ai " +
            "JOIN account_login al ON ai.user_id = al.id " +
            "WHERE al.phone_no LIKE %:phoneNo%", nativeQuery = true)
    List<UserDTO> findUsersByPhoneNo(@Param("phoneNo") String phoneNo);
}
