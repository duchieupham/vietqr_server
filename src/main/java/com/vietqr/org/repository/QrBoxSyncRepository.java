package com.vietqr.org.repository;

import com.vietqr.org.entity.QrBoxSyncEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface QrBoxSyncRepository extends JpaRepository<QrBoxSyncEntity, String> {
    @Query(value = "SELECT qr_box_code FROM qr_box_sync "
            + "WHERE qr_box_code = :code LIMIT 1", nativeQuery = true)
    String checkExistQRBoxCode(String code);

    @Query(value = "SELECT qr_box_code FROM qr_box_sync "
            + "WHERE certificate = :qrCertificate LIMIT 1 ", nativeQuery = true)
    String getByQrCertificate(String qrCertificate);
}
