package com.vietqr.org.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.dto.CustomerSyncInformationDTO;
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
        @Query(value = "SELECT a.id, a.information AS url, a.ip_address AS ip, a.port, "
                        + "CASE "
                        + "WHEN c.is_sync = 1 OR (c.is_sync = 0 AND c.is_wp_sync = 1) THEN 1 "
                        + "WHEN c.is_sync = 0 AND c.is_wp_sync = 0 THEN 0 "
                        + "ELSE NULL "
                        + "END AS active, "
                        + "CASE "
                        + "WHEN a.user_id IS NOT NULL AND a.user_id <> '' THEN 'Ecommerce' "
                        + "ELSE 'API service' "
                        + "END AS platform "
                        + "FROM customer_sync a "
                        + "INNER JOIN account_customer_bank b ON a.id = b.customer_sync_id "
                        + "INNER JOIN account_bank_receive c ON b.bank_id = c.id ", nativeQuery = true)
        List<CustomerSyncListDTO> getCustomerSyncList();

        @Query(value = "SELECT a.id, a.information AS url, a.ip_address AS ip, a.port, "
                        + "CASE "
                        + "WHEN c.is_sync = 1 OR (c.is_sync = 0 AND c.is_wp_sync = 1) THEN 1 "
                        + "WHEN c.is_sync = 0 AND c.is_wp_sync = 0 THEN 0 "
                        + "ELSE NULL "
                        + "END AS active, "
                        + "CASE "
                        + "WHEN a.user_id IS NOT NULL AND a.user_id <> '' THEN 'Ecommerce' "
                        + "ELSE 'API service' "
                        + "END AS platform, "
                        + "c.bank_account as bankAccount, c.id as bankId, d.bank_short_name as bankShortName, c.bank_account_name as userBankName, "
                        + "c.phone_authenticated as phoneAuthenticated, c.national_id as nationalId, e.phone_no as phoneNo, f.last_name as lastName, f.middle_name as middleName, f.first_name as firstName, f.email, f.address "
                        + "FROM customer_sync a "
                        + "INNER JOIN account_customer_bank b ON a.id = b.customer_sync_id "
                        + "INNER JOIN account_bank_receive c ON b.bank_id = c.id "
                        + "INNER JOIN bank_type d ON c.bank_type_id = d.id "
                        + "INNER JOIN account_login e ON c.user_id = e.id "
                        + "INNER JOIN account_information f ON c.user_id = f.user_id "
                        + "WHERE a.id = :id", nativeQuery = true)
        CustomerSyncInformationDTO getCustomerSyncInformationById(@Param(value = "id") String id);
}