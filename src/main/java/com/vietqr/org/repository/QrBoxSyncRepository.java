package com.vietqr.org.repository;

import com.vietqr.org.dto.ITidInternalDTO;
import com.vietqr.org.entity.QrBoxSyncEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface QrBoxSyncRepository extends JpaRepository<QrBoxSyncEntity, String> {
    @Query(value = "SELECT qr_box_code FROM qr_box_sync "
            + "WHERE qr_box_code = :code LIMIT 1", nativeQuery = true)
    String checkExistQRBoxCode(String code);

    @Query(value = "SELECT qr_box_code FROM qr_box_sync "
            + "WHERE certificate = :qrCertificate LIMIT 1 ", nativeQuery = true)
    String getByQrCertificate(String qrCertificate);

    @Query(value = "SELECT * FROM qr_box_sync "
            + "WHERE mac_address = :macAddr LIMIT 1", nativeQuery = true)
    QrBoxSyncEntity getByMacAddress(String macAddr);

    @Query(value = "SELECT a.id AS boxId, a.mac_address AS macAddr, "
            + "a.qr_box_code AS boxCode, '' AS merchantName, "
            + "b.terminal_id AS terminalId, b.terminal_code AS terminalCode, "
            + "c.bank_account AS bankAccount, d.bank_short_name AS bankShortName, "
            + "c.bank_account_name AS userBankName, COALESCE(c.mms_active, 0) AS mmsActive, "
            + "b.sub_terminal_address AS boxAddress, a.certificate AS certificate, "
            + "a.status AS status, a.last_checked AS lastChecked "
            + "FROM qr_box_sync a "
            + "INNER JOIN terminal_bank_receive b ON a.qr_box_code = b.raw_terminal_code "
            + "INNER JOIN account_bank_receive c ON b.bank_id = c.id "
            + "INNER JOIN bank_type d ON d.id = c.bank_type_id "
            + "WHERE c.bank_account = :value "
            + "LIMIT :offset, :size ", nativeQuery = true)
    List<ITidInternalDTO> getQrBoxSyncByBankAccount(String value, int offset, int size);

    @Query(value = "SELECT COUNT(a.id) "
            + "FROM qr_box_sync a "
            + "INNER JOIN terminal_bank_receive b ON a.qr_box_code = b.raw_terminal_code "
            + "INNER JOIN account_bank_receive c ON b.bank_id = c.id "
            + "INNER JOIN bank_type d ON d.id = c.bank_type_id "
            + "WHERE c.bank_account = :value ", nativeQuery = true)
    int countQrBoxSyncByBankAccount(String value);

    @Query(value = "SELECT a.id AS boxId, a.mac_address AS macAddr, "
            + "a.qr_box_code AS boxCode, '' AS merchantName, "
            + "b.terminal_id AS terminalId, b.terminal_code AS terminalCode, "
            + "c.bank_account AS bankAccount, d.bank_short_name AS bankShortName, "
            + "c.bank_account_name AS userBankName, COALESCE(c.mms_active, 0) AS mmsActive, "
            + "b.sub_terminal_address AS boxAddress, a.certificate AS certificate, "
            + "a.status AS status, a.last_checked AS lastChecked "
            + "FROM qr_box_sync a "
            + "LEFT JOIN terminal_bank_receive b ON a.qr_box_code = b.raw_terminal_code "
            + "LEFT JOIN account_bank_receive c ON b.bank_id = c.id "
            + "LEFT JOIN bank_type d ON d.id = c.bank_type_id "
            + "LIMIT :offset, :size ", nativeQuery = true)
    List<ITidInternalDTO> getQrBoxSync(int offset, int size);

    @Query(value = "SELECT COUNT(a.id) "
            + "FROM qr_box_sync a "
            + "LEFT JOIN terminal_bank_receive b ON a.qr_box_code = b.raw_terminal_code "
            + "LEFT JOIN account_bank_receive c ON b.bank_id = c.id "
            + "LEFT JOIN bank_type d ON d.id = c.bank_type_id ", nativeQuery = true)
    int countQrBoxSync();

    @Transactional
    @Modifying
    @Query(value = "UPDATE qr_box_sync SET time_sync = :time, is_active = :active, "
            + "qr_name = :name "
            + "WHERE certificate = :qrCertificate LIMIT 1", nativeQuery = true)
    void updateQrBoxSync(String qrCertificate, long time, boolean active, String name);

    @Transactional
    @Modifying
    @Query(value = "UPDATE qr_box_sync SET status = :status, last_checked = :lastChecked "
            + "WHERE qr_box_code = :boxCode LIMIT 1", nativeQuery = true)
    void updateStatusBox(String boxCode, int status, long lastChecked);
}
