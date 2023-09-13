package com.vietqr.org.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.dto.CusSyncApiInfoDTO;
import com.vietqr.org.dto.CusSyncEcInfoDTO;
import com.vietqr.org.dto.CustomerSyncListDTO;
import com.vietqr.org.entity.CustomerSyncEntity;

@Repository
public interface CustomerSyncRepository extends JpaRepository<CustomerSyncEntity, Long> {

        @Query(value = "SELECT * FROM customer_sync WHERE active = true", nativeQuery = true)
        List<CustomerSyncEntity> getCustomerSyncEntities();

        @Query(value = "SELECT * FROM customer_sync WHERE id = :id", nativeQuery = true)
        CustomerSyncEntity getCustomerSyncById(@Param(value = "id") String id);

        @Query(value = "SELECT user_id FROM customer_sync WHERE user_id = :userId", nativeQuery = true)
        String checkExistedCustomerSync(@Param(value = "userId") String userId);

        // @Query(value = "SELECT id FROM customer_sync WHERE username = :username AND
        // active = true", nativeQuery = true)
        // List<String> checkExistedCustomerSyncByUsername(@Param(value = "username")
        // String username);

        @Transactional
        @Modifying
        @Query(value = "UPDATE customer_sync SET information = :information WHERE user_id = :userId", nativeQuery = true)
        void updateCustomerSyncInformation(@Param(value = "information") String information,
                        @Param(value = "userId") String userId);

        @Query(value = "SELECT id FROM customer_sync WHERE information = :information", nativeQuery = true)
        String checkExistedCustomerSyncByInformation(@Param(value = "information") String information);

        // web admin
        @Query(value = "SELECT a.id, a.merchant, a.information AS url, a.ip_address AS ip, a.port, "
                        + "CASE "
                        + "WHEN a.active = true THEN 1 "
                        + "WHEN a.active = false THEN 0 "
                        + "END AS active, "
                        + "CASE "
                        + "WHEN a.user_id IS NOT NULL AND a.user_id <> '' THEN 'Ecommerce' "
                        + "ELSE 'API service' "
                        + "END AS platform "
                        + "FROM customer_sync a ", nativeQuery = true)
        List<CustomerSyncListDTO> getCustomerSyncList();

        // 0 => API Service
        // 1 => E-Commerce
        // get user type by id
        @Query(value = "SELECT CASE WHEN user_id IS NOT NULL AND user_id <> '' THEN 1 ELSE 0 END as platform "
                        + "FROM customer_sync "
                        + "WHERE id = :id ", nativeQuery = true)
        Integer checkCustomerSyncTypeById(@Param(value = "id") String id);

        // get user info type API service
        @Query(value = "SELECT a.id, a.merchant, "
                        + "CASE  "
                        + "WHEN a.active = true THEN 1  "
                        + "WHEN a.active = false THEN 0 "
                        + "END as active,  "
                        + "CASE  "
                        + "WHEN a.user_id IS NOT NULL AND a.user_id <> '' THEN 'Ecommerce' ELSE 'API service'  "
                        + "END as platform,  "
                        + "a.information as url, a.ip_address as ip, a.port, a.suffix_url as suffix, a.address, a.username as customerUsername, a.password as customerPassword,  "
                        + "c.id as accountCustomerId, c.username as systemUsername, c.password as systemPassword "
                        + "FROM customer_sync a "
                        + "INNER JOIN account_customer_bank b  "
                        + "ON a.id = b.customer_sync_id "
                        + "INNER JOIN account_customer c  "
                        + "ON c.id = b.account_customer_id  "
                        + "WHERE a.id = :id LIMIT 0, 1 ", nativeQuery = true)
        CusSyncApiInfoDTO getCustomerSyncApiInfo(@Param(value = "id") String id);

        // get user info type E-Commerce
        @Query(value = "SELECT a.id, "
                        + "CASE  "
                        + "WHEN a.active = true THEN 1  "
                        + "WHEN a.active = false THEN 0 "
                        + "END as active,  "
                        + "CASE  "
                        + "WHEN a.user_id IS NOT NULL AND a.user_id <> '' THEN 'Ecommerce' ELSE 'API service'  "
                        + "END as platform,  "
                        + "a.information as url, b.phone_no as phoneNo, c.email, c.first_name as firstName, c.middle_name as middleName, c.last_name as lastName "
                        + "FROM customer_sync a  "
                        + "INNER JOIN account_login b "
                        + "ON a.user_id = b.id "
                        + "INNER JOIN account_information c "
                        + "ON a.user_id = c.user_id "
                        + "WHERE a.id = :id LIMIT 0, 1", nativeQuery = true)
        CusSyncEcInfoDTO getCustomerSyncEcInfo(@Param(value = "id") String id);

        @Transactional
        @Modifying
        @Query(value = "UPDATE customer_sync SET active = :active WHERE id = :customerSyncId ", nativeQuery = true)
        void updateCustomerSyncStatus(@Param(value = "active") boolean active,
                        @Param(value = "customerSyncId") String customerSyncId);

        @Query(value = "SELECT merchant FROM customer_sync WHERE merchant = :merchant", nativeQuery = true)
        List<String> checkExistedMerchant(@Param(value = "merchant") String merchant);

        @Transactional
        @Modifying
        @Query(value = "UPDATE customer_sync SET information = :url, ip_address = :ip, password = :password, port = :port, "
                        + "suffix_url = :suffix, username = :username WHERE id = :customerSyncId ", nativeQuery = true)
        void updateCustomerSync(
                        @Param(value = "url") String url,
                        @Param(value = "ip") String ip,
                        @Param(value = "password") String password,
                        @Param(value = "port") String port,
                        @Param(value = "suffix") String suffix,
                        @Param(value = "username") String username,
                        @Param(value = "customerSyncId") String customerSyncId);
}