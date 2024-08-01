package com.vietqr.org.repository;

import com.vietqr.org.dto.*;
import com.vietqr.org.entity.MerchantSyncEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface MerchantSyncRepository extends JpaRepository<MerchantSyncEntity, String> {
    @Query(value = "SELECT a.id AS merchantId, a.name AS merchantName, "
            + "a.vso AS vsoCode, a.business_type AS platform, COUNT(c.bank_id) AS numberOfBank "
            + "FROM merchant_sync a "
            + "INNER JOIN merchant_connection b ON a.id = b.mid "
            + "INNER JOIN bank_receive_connection c ON b.id = c.mid_connect_id "
            + "LIMIT :offset, :size", nativeQuery = true)
    List<IMerchantInvoiceDTO> getMerchantSyncs(int offset, int size);

    @Query(value = "SELECT a.id AS merchantId, a.name AS merchantName, "
            + "a.vso AS vsoCode, a.business_type AS platform, COUNT(b.bank_id) AS numberOfBank "
            + "FROM merchant_sync a "
            + "LEFT JOIN bank_receive_fee_package b ON a.id = b.mid "
            + "WHERE a.name LIKE %:value% "
            + "GROUP BY a.id "
            + "LIMIT :offset, :size ", nativeQuery = true)
    List<IMerchantInvoiceDTO> getMerchantSyncsByName(String value, int offset, int size);

    @Query(value = "SELECT COUNT(a.id) "
            + "FROM merchant_sync a "
            + "WHERE a.name LIKE %:value% ", nativeQuery = true)
    int countMerchantSyncsByName(String value);

    @Query(value = "SELECT a.id AS merchantId, a.name AS merchantName "
            + "FROM merchant_sync a "
            + "WHERE a.id = :merchantId ", nativeQuery = true)
    IMerchantEditDetailDTO getMerchantEditDetail(String merchantId);

    @Query(value = "SELECT a.id AS mid, a.name AS midName, "
            + "a.vso AS vso "
            + "FROM merchant_sync a "
            + "WHERE a.id = :merchantId ", nativeQuery = true)
    IMerchantInfoDTO getMerchantSyncInfo(String merchantId);


    @Query(value = "SELECT id, name, vso, business_type AS bussinessType,"
            + " address , national_id AS nationalId, is_active AS isActive, "
            + " user_id as userId , account_customer_id AS accountCustomerId "
            + " FROM merchant_sync "
            + " WHERE name LIKE %:value%  LIMIT :offset, :size", nativeQuery = true)
    List<IMerchantSyncDTO> getAllMerchants(@Param("value") String value, @Param("offset") int offset, @Param("size") int size);

    @Query(value = "SELECT id, name, vso, business_type AS businessType,"
            + " career, address, national_id AS nationalId, "
            + "is_active AS isActive, user_id AS userId, account_customer_id AS accountCustomerId "
            + " FROM merchant_sync WHERE id = :id", nativeQuery = true)
    IMerchantSyncDTO getMerchantById(@Param("id") String id);

    @Query(value = "SELECT b.is_master FROM account_customer a "
            + "LEFT JOIN merchant_sync b ON a.id = b.account_customer_id "
            + "WHERE a.username = :username ", nativeQuery = true)
    boolean getMerchantSyncByUsername(String username);

    @Query(value = "SELECT COUNT(id) FROM merchant_sync WHERE name LIKE %:value%", nativeQuery = true)
    int countMerchantsByName(@Param("value") String value);

    @Query(value = "SELECT id, name, vso, business_type AS businessType,"
            + " career, address, national_id AS nationalId, is_active AS isActive, "
            + " user_id AS userId, account_customer_id AS accountCustomerId"
            + " FROM merchant_sync WHERE name LIKE %:value% LIMIT :offset, :size", nativeQuery = true)
    List<IMerchantSyncDTO> getMerchantsByName(@Param("value") String value, @Param("offset") int offset, @Param("size") int size);


    @Modifying
    @Transactional
    @Query(value = "DELETE FROM merchant_sync WHERE id = :id", nativeQuery = true)
    void deleteMerchantById(@Param("id") String id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE merchant_sync SET name = :midName WHERE id = :mid", nativeQuery = true)
    void updateMerchantName(String midName, String mid);

    @Modifying
    @Transactional
    @Query(value = "UPDATE merchant_sync SET account_customer_id = :accountCustomerId, address = :address, career = :career, " +
            "business_type = :businessType, name = :name, user_id = :userId, vso = :vso, email = :email, " +
            "ref_id = :refId, full_name = :fullName, phone_no = :phoneNo WHERE publish_id = :publishId", nativeQuery = true)
    void updateMerchantV2(@Param("publishId") String publishId,
                          @Param("accountCustomerId") String accountCustomerId,
                          @Param("address") String address,
                          @Param("career") String career,
                          @Param("businessType") String businessType,
                          @Param("name") String name,
                          @Param("userId") String userId,
                          @Param("vso") String vso,
                          @Param("email") String email,
                          @Param("refId") String refId,
                          @Param("fullName") String fullName,
                          @Param("phoneNo") String phoneNo);
    @Query(value = "SELECT * FROM merchant_sync "
            + "WHERE publish_id = :mid LIMIT 1", nativeQuery = true)
    MerchantSyncEntity getMerchantSyncByPublicId(String mid);

    @Query(value = "SELECT * FROM merchant_sync "
            + "WHERE national_id = :merchantIdentity LIMIT 1", nativeQuery = true)
    MerchantSyncEntity getMerchantByMerchantIdentity(String merchantIdentity);

    @Query(value = "SELECT publish_id FROM merchant_sync "
            + "WHERE certificate = :certificate LIMIT 1", nativeQuery = true)
    String getPublishIdSyncByCertificate(String certificate);

    @Query(value = "SELECT * FROM merchant_sync WHERE name = :merchantName LIMIT 1", nativeQuery = true)
    MerchantSyncEntity getMerchantSyncsByMerchantName(String merchantName);

    @Query(value = "SELECT * FROM merchant_sync WHERE id = :mid LIMIT 1", nativeQuery = true)
    MerchantSyncEntity getMerchantSyncById(String mid);

    @Query(value = "SELECT * FROM merchant_sync WHERE publish_id = :mid LIMIT 1", nativeQuery = true)
    MerchantSyncEntity getMerchantSyncByPublishId(String mid);

    @Query(value = "SELECT COUNT(id) FROM merchant_sync WHERE (ref_id = :mid OR id = :mid) LIMIT 1", nativeQuery = true)
    int countMerchantByMidSync(String mid);

    @Query(value = "SELECT publish_id AS mid, full_name AS merchantFullName, "
            + "name AS merchantName, address AS merchantAddress, national_id AS merchantIdentify, "
            + "email AS contactEmail, phone_no AS contactPhone, certificate AS certificate, "
            + "client_id AS clientId, webhook AS webhook, is_master AS isMaster "
            + "FROM merchant_sync WHERE (ref_id = :mid OR id = :mid) "
            + "ORDER BY publish_id DESC "
            + "LIMIT :offset, :size", nativeQuery = true)
    List<IMerchantSyncPublicDTO> getMerchantByMidSync(String mid, int offset, int size);

    @Query(value = "SELECT publish_id AS mid, full_name AS merchantFullName, "
            + "name AS merchantName, address AS merchantAddress, national_id AS merchantIdentify, "
            + "email AS contactEmail, phone_no AS contactPhone, certificate AS certificate, "
            + "client_id AS clientId, webhook AS webhook, is_master AS isMaster "
            + "FROM merchant_sync WHERE (ref_id = :mid OR id = :mid) "
            + "ORDER BY publish_id DESC ", nativeQuery = true)
    List<IMerchantSyncPublicDTO> getMerchantByMidSyncV2(String mid);

    @Query(value = "SELECT id FROM merchant_sync "
            + "WHERE publish_id = :publicId LIMIT 1", nativeQuery = true)
    String getIdByPublicId(String publicId);

    @Query(value = "SELECT id FROM merchant_sync "
            + "WHERE name = :merchantName LIMIT 1", nativeQuery = true)
    String getMerchantIdSyncByName(String merchantName);

    @Query(value = "SELECT id FROM merchant_sync "
            + "WHERE publish_id = :code LIMIT 1", nativeQuery = true)
    String checkExistedPublishId(String code);
}
