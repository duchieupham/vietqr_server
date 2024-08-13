package com.vietqr.org.repository;

import com.vietqr.org.dto.IMerchantSyncV2DTO;
import com.vietqr.org.entity.MerchantSyncEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MerchantSyncV2Repository extends JpaRepository<MerchantSyncEntity, String> {

    @Query(value = "SELECT count(*) as total " +
            "FROM merchant_sync a " +
            "WHERE name LIKE :name", nativeQuery = true)
    public int countMerchantByName(String name);

    @Query(value = "SELECT count(*) as total " +
            "FROM merchant_sync a " +
            "WHERE publish_id LIKE :publishId", nativeQuery = true)
    public int countMerchantByPublishId(String publishId);

    @Query(value = "SELECT " +
            "a.id AS id, " +
            "a.name AS name, " +
            "a.full_name AS fullName, " +
            "a.vso AS vso, " +
            "a.business_type AS businessType, " +
            "a.career AS career, " +
            "a.address AS address, " +
            "a.national_id AS nationalId, " +
            "a.is_active AS isActive, " +
            "a.user_id AS userId, " +
            "a.account_customer_id AS accountCustomerId, " +
            "a.email AS email, " +
            "a.phone_no AS phoneNo, " +
            "a.publish_id AS publishId, " +
            "a.ref_id AS refId, " +
            "a.is_master AS isMaster, " +
            "a.certificate AS certificate, " +
            "a.webhook AS webhook, " +
            "a.client_id AS clientId " +
            "FROM merchant_sync a " +
            "LIMIT :index, :size",
            nativeQuery = true)
    List<IMerchantSyncV2DTO> getMerchantSyncs(int index, int size);

    @Query(value = "SELECT count(*) as total " +
            "FROM merchant_sync a", nativeQuery = true)
    int countMerchant();
}
