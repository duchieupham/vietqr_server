package com.vietqr.org.repository;

import com.vietqr.org.entity.qrfeed.FileAttachmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface FileAttachRepository extends JpaRepository<FileAttachmentEntity, String> {

    @Transactional
    @Query(value = "INSERT INTO file_attachment (file_name, file_data, file_type) " +
            "VALUES (:fileName, :fileData, :fileType)", nativeQuery = true)
    void saveFile(@Param("fileName") String fileName, @Param("fileData") byte[] fileData, @Param("fileType") String fileType);

    @Query(value = "SELECT * FROM file_attachment a " +
            "INNER JOIN invoice b ON a.id = b.file_attachment_id " +
            "WHERE b.id = :invoiceId ", nativeQuery = true)
    FileAttachmentEntity findImageById(@Param("invoiceId") String invoiceId);

    @Query(value = "SELECT * FROM file_attachment WHERE id = :id ", nativeQuery = true)
    FileAttachmentEntity findImgById(@Param("id") String id);

    @Query(value = "SELECT a.file_name FROM  file_attachment a " +
            "INNER JOIN invoice b ON a.id = b.file_attachment_id " +
            "WHERE b.id = :invoiceId ", nativeQuery = true)
    String getFileInInvoice(@Param("invoiceId") String invoiceId);

}
