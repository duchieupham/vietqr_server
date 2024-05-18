package com.vietqr.org.repository;

import com.vietqr.org.dto.IMerchantInvoiceDTO;
import com.vietqr.org.entity.MerchantSyncEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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
            + "a.vso AS vsoCode, a.business_type AS platform, COUNT(c.bank_id) AS numberOfBank "
            + "FROM merchant_sync a "
            + "LEFT JOIN merchant_connection b ON a.id = b.mid "
            + "LEFT JOIN bank_receive_connection c ON b.id = c.mid_connect_id "
            + "WHERE a.name LIKE %:value% "
            + "GROUP BY a.id "
            + "LIMIT :offset, :size ", nativeQuery = true)
    List<IMerchantInvoiceDTO> getMerchantSyncsByName(String value, int offset, int size);

    @Query(value = "SELECT COUNT(a.id) "
            + "FROM merchant_sync a "
            + "LEFT JOIN merchant_connection b ON a.id = b.mid "
            + "LEFT JOIN bank_receive_connection c ON b.id = c.mid_connect_id "
            + "WHERE a.name LIKE %:value% ", nativeQuery = true)
    int countMerchantSyncsByName(String value);
}
