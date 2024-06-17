package com.vietqr.org.repository;

import com.vietqr.org.dto.IMerchantEditDetailDTO;
import com.vietqr.org.dto.IMerchantInfoDTO;
import com.vietqr.org.dto.IMerchantInvoiceDTO;
import com.vietqr.org.dto.IMerchantSyncDTO;
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


    @Query(value="SELECT id, name, vso, business_type AS bussinessType,"
             + " address , national_id AS nationalId, is_active AS isActive, "
             + " user_id as userId , account_customer_id AS accountCustomerId "
             + " FROM merchant_sync "
            + " WHERE name LIKE %:value%  LIMIT :offset, :size" , nativeQuery = true)
    List<IMerchantSyncDTO> getAllMerchants(@Param("value") String value, @Param("offset") int offset, @Param("size") int size);

    @Query(value = "SELECT id, name, vso, business_type AS businessType,"
            + " career, address, national_id AS nationalId, "
            + "is_active AS isActive, user_id AS userId, account_customer_id AS accountCustomerId "
            + " FROM merchant_sync WHERE id = :id", nativeQuery = true)
    IMerchantSyncDTO getMerchantById(@Param("id") String id);

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
}
