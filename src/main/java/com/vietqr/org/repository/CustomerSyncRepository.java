package com.vietqr.org.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.dto.AnnualFeeMerchantDTO;
import com.vietqr.org.dto.CusSyncApiInfoDTO;
import com.vietqr.org.dto.CusSyncEcInfoDTO;
import com.vietqr.org.dto.CustomerSyncListDTO;
import com.vietqr.org.dto.MerchantServiceItemDTO;
import com.vietqr.org.entity.CustomerSyncEntity;

@Repository
public interface CustomerSyncRepository extends JpaRepository<CustomerSyncEntity, Long> {

    @Query(value = "SELECT id as customerSyncId, merchant FROM customer_sync ", nativeQuery = true)
    List<AnnualFeeMerchantDTO> getMerchantForServiceFee();

    @Query(value = "SELECT id as customerSyncId, merchant FROM customer_sync WHERE id = :customerSyncId ", nativeQuery = true)
    List<AnnualFeeMerchantDTO> getMerchantForServiceFeeById(
            @Param(value = "customerSyncId") String customerSyncId);

    @Query(value = "SELECT * FROM customer_sync WHERE active = true", nativeQuery = true)
    List<CustomerSyncEntity> getCustomerSyncEntities();

    @Query(value = "SELECT * FROM customer_sync WHERE active = true AND account_id = :accountId ", nativeQuery = true)
    List<CustomerSyncEntity> getCustomerSyncByAccountId(@Param(value = "accountId") String accountId);

    @Query(value = "SELECT * FROM customer_sync WHERE id = :id", nativeQuery = true)
    CustomerSyncEntity getCustomerSyncById(@Param(value = "id") String id);

    @Query(value = "SELECT user_id FROM customer_sync WHERE user_id = :userId", nativeQuery = true)
    String checkExistedCustomerSync(@Param(value = "userId") String userId);

    // @Query(value = "SELECT id FROM customer_sync WHERE ")

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

    @Query(value = "SELECT a.id, a.merchant, a.information AS url, a.ip_address AS ip, a.port, "
            + "CASE "
            + "WHEN a.active = true THEN 1 "
            + "WHEN a.active = false THEN 0 "
            + "END AS active, "
            + "CASE "
            + "WHEN a.user_id IS NOT NULL AND a.user_id <> '' THEN 'Ecommerce' "
            + "ELSE 'API service' "
            + "END AS platform "
            + "FROM customer_sync a "
            + "WHERE a.user_id IS NULL OR a.user_id = ''", nativeQuery = true)
    List<CustomerSyncListDTO> getCustomerSyncAPIList();

    @Query(value = "SELECT a.id, a.merchant, a.information AS url, a.ip_address AS ip, a.port, "
            + "CASE "
            + "WHEN a.active = true THEN 1 "
            + "WHEN a.active = false THEN 0 "
            + "END AS active, "
            + "CASE "
            + "WHEN a.user_id IS NOT NULL AND a.user_id <> '' THEN 'Ecommerce' "
            + "ELSE 'API service' "
            + "END AS platform "
            + "FROM customer_sync a "
            + "WHERE a.merchant LIKE %:value% "
            + "LIMIT :offset, :size ", nativeQuery = true)
    List<CustomerSyncListDTO> getCustomerSyncListByMerchant(String value, int offset, int size); // All

    @Query(value = "SELECT a.id, a.merchant, a.information AS url, a.ip_address AS ip, a.port, "
            + "CASE "
            + "WHEN a.active = true THEN 1 "
            + "WHEN a.active = false THEN 0 "
            + "END AS active, "
            + "CASE "
            + "WHEN a.user_id IS NOT NULL AND a.user_id <> '' THEN 'Ecommerce' "
            + "ELSE 'API service' "
            + "END AS platform "
            + "FROM customer_sync a "
            + "INNER JOIN account_customer_bank b ON a.id = b.customer_sync_id "
            + "WHERE b.bank_account LIKE %:value% "
            + "LIMIT :offset, :size ", nativeQuery = true)
    List<CustomerSyncListDTO> getCustomerSyncListByMerchantByBankAccount(String value, int offset, int size); // Search by Bank account

    @Query(value = "SELECT COUNT(*) " +
            "FROM ( " +
            "SELECT a.id, a.merchant, a.information AS url, a.ip_address AS ip, a.port, " +
            "CASE " +
            "WHEN a.active = true THEN 1 " +
            "WHEN a.active = false THEN 0 " +
            "END AS active, " +
            "CASE " +
            "WHEN a.user_id IS NOT NULL AND a.user_id <> '' THEN 'Ecommerce' " +
            "ELSE 'API service' " +
            "END AS platform " +
            "FROM customer_sync a " +
            "INNER JOIN account_customer_bank b ON a.id = b.customer_sync_id " +
            "WHERE b.bank_account LIKE %:value% " +
            ") AS subquery;", nativeQuery = true)
    int countCustomerSyncListByMerchantByBankAccount(String value); // Search by Bank account

    @Query(value = "SELECT a.id, a.merchant, a.information AS url, a.ip_address AS ip, a.port, "
            + "CASE "
            + "WHEN a.active = true THEN 1 "
            + "WHEN a.active = false THEN 0 "
            + "END AS active, "
            + "CASE "
            + "WHEN a.user_id IS NOT NULL AND a.user_id <> '' THEN 'Ecommerce' "
            + "ELSE 'API service' "
            + "END AS platform "
            + "FROM customer_sync a "
            + "WHERE (a.user_id IS NULL OR a.user_id = '') AND (a.merchant LIKE %:value%) "
            + "LIMIT :offset, :size ", nativeQuery = true)
    List<CustomerSyncListDTO> getCustomerSyncAPIListByMerchant(String value, int offset, int size); // API

    @Query(value = "SELECT a.id, a.merchant, a.information AS url, a.ip_address AS ip, a.port, "
            + "CASE "
            + "WHEN a.active = true THEN 1 "
            + "WHEN a.active = false THEN 0 "
            + "END AS active, "
            + "CASE "
            + "WHEN a.user_id IS NOT NULL AND a.user_id <> '' THEN 'Ecommerce' "
            + "ELSE 'API service' "
            + "END AS platform "
            + "FROM customer_sync a "
            + "INNER JOIN account_customer_bank b ON a.id = b.customer_sync_id "
            + "WHERE (a.user_id IS NULL OR a.user_id = '') AND (b.bank_account LIKE %:value%) "
            + "LIMIT :offset, :size ", nativeQuery = true)
    List<CustomerSyncListDTO> getCustomerSyncAPIListByMerchantByBankAccount(String value, int offset, int size); // API

    @Query(value = "SELECT COUNT(*) "
            + "FROM ( "
            + "SELECT a.id, a.merchant, a.information AS url, a.ip_address AS ip, a.port,  "
            + "CASE "
            + "WHEN a.active = true THEN 1 "
            + "WHEN a.active = false THEN 0 "
            + "END AS active, "
            + "CASE "
            + "WHEN a.user_id IS NOT NULL AND a.user_id <> '' THEN 'Ecommerce' "
            + "ELSE 'API service' "
            + "END AS platform "
            + "FROM customer_sync a "
            + "WHERE (a.user_id IS NULL OR a.user_id = '') AND (a.merchant LIKE %:value%) "
            + ") AS subquery ", nativeQuery = true)
    int countCustomerSyncAPIListByMerchant(String value); // count API

    @Query(value = "SELECT COUNT(*) "
            + "FROM ( "
            + "SELECT a.id, a.merchant, a.information AS url, a.ip_address AS ip, a.port,  "
            + "CASE "
            + "WHEN a.active = true THEN 1 "
            + "WHEN a.active = false THEN 0 "
            + "END AS active, "
            + "CASE "
            + "WHEN a.user_id IS NOT NULL AND a.user_id <> '' THEN 'Ecommerce' "
            + "ELSE 'API service' "
            + "END AS platform "
            + "FROM customer_sync a "
            + "INNER JOIN account_customer_bank b ON a.id = b.customer_sync_id "
            + "WHERE (a.user_id IS NULL OR a.user_id = '') AND (b.bank_account LIKE %:value%) "
            + ") AS subquery ", nativeQuery = true)
    int countCustomerSyncAPIListByMerchantByBankAccount(String value); // count API by Bank Account

    @Query(value = "SELECT COUNT(*) " +
            "FROM ( " +
            "SELECT a.id, COALESCE(a.merchant, ''), a.information AS url, a.ip_address AS ip, a.port, " +
            "CASE " +
            "WHEN a.active = true THEN 1 " +
            "WHEN a.active = false THEN 0 " +
            "END AS active, " +
            "CASE " +
            "WHEN a.user_id IS NOT NULL AND a.user_id <> '' THEN 'Ecommerce' " +
            "ELSE 'API service' " +
            "END AS platform " +
            "FROM customer_sync a " +
            "INNER JOIN account_customer_bank b ON a.id = b.customer_sync_id " +
            "WHERE (a.user_id IS NOT NULL AND a.user_id <> '') AND (a.merchant IS NULL OR a.merchant LIKE %:value%) " +
            ") as subquery ", nativeQuery = true)
    int countCustomerSyncEcListByMerchant(String value); // count Economy By Bank Account

    @Query(value = "SELECT COUNT(*) " +
            "FROM ( " +
            "SELECT a.id, COALESCE(a.merchant, ''), a.information AS url, a.ip_address AS ip, a.port, " +
            "CASE " +
            "WHEN a.active = true THEN 1 " +
            "WHEN a.active = false THEN 0 " +
            "END AS active, " +
            "CASE " +
            "WHEN a.user_id IS NOT NULL AND a.user_id <> '' THEN 'Ecommerce' " +
            "ELSE 'API service' " +
            "END AS platform " +
            "FROM customer_sync a " +
            "INNER JOIN account_customer_bank b ON a.id = b.customer_sync_id " +
            "WHERE (a.user_id IS NOT NULL AND a.user_id <> '') AND (a.merchant IS NULL OR b.bank_account LIKE %:value%) " +
            ") as subquery ", nativeQuery = true)
    int countCustomerSyncEcListByMerchantByBankAccount(String value); // count Economy

    @Query(value = "SELECT COUNT(*) AS totalElement " +
            "FROM (" +
            "  SELECT a.id, a.merchant, a.information AS url, a.ip_address AS ip, a.port, " +
            "  CASE " +
            "  WHEN a.active = true THEN 1 " +
            "  WHEN a.active = false THEN 0 " +
            "  END AS active, " +
            "  CASE " +
            "  WHEN a.user_id IS NOT NULL AND a.user_id <> '' THEN 'Ecommerce' " +
            "  ELSE 'API service' " +
            "  END AS platform " +
            "  FROM customer_sync a " +
            "  WHERE a.merchant LIKE %:value% " +
            ") as total ", nativeQuery = true)
    int countCustomerSyncListByMerchant(String value);

    @Query(value = "SELECT a.id, COALESCE(a.merchant, ''), a.information AS url, a.ip_address AS ip, a.port, "
            + "CASE "
            + "WHEN a.active = true THEN 1 "
            + "WHEN a.active = false THEN 0 "
            + "END AS active, "
            + "CASE "
            + "WHEN a.user_id IS NOT NULL AND a.user_id <> '' THEN 'Ecommerce' "
            + "ELSE 'API service' "
            + "END AS platform "
            + "FROM customer_sync a "
            + "WHERE (a.user_id IS NOT NULL AND a.user_id <> '') ", nativeQuery = true)
    List<CustomerSyncListDTO> getCustomerSyncEcList();

    @Query(value = "SELECT a.id, COALESCE(a.merchant, ''), a.information AS url, a.ip_address AS ip, a.port, "
            + "CASE "
            + "WHEN a.active = true THEN 1 "
            + "WHEN a.active = false THEN 0 "
            + "END AS active, "
            + "CASE "
            + "WHEN a.user_id IS NOT NULL AND a.user_id <> '' THEN 'Ecommerce' "
            + "ELSE 'API service' "
            + "END AS platform "
            + "FROM customer_sync a "
            + "WHERE (a.user_id IS NOT NULL AND a.user_id <> '') AND (a.merchant IS NULL OR a.merchant LIKE %:value%) "
            + "LIMIT :offset, :size ", nativeQuery = true)
    List<CustomerSyncListDTO> getCustomerSyncEcListByMerchant(String value, int offset, int size);

    @Query(value = "SELECT a.id, COALESCE(a.merchant, ''), a.information AS url, a.ip_address AS ip, a.port, "
            + "CASE "
            + "WHEN a.active = true THEN 1 "
            + "WHEN a.active = false THEN 0 "
            + "END AS active, "
            + "CASE "
            + "WHEN a.user_id IS NOT NULL AND a.user_id <> '' THEN 'Ecommerce' "
            + "ELSE 'API service' "
            + "END AS platform "
            + "FROM customer_sync a "
            + "INNER JOIN account_customer_bank b ON a.id = b.customer_sync_id "
            + "WHERE (a.user_id IS NOT NULL AND a.user_id <> '') AND (a.merchant IS NULL OR b.bank_account LIKE %:value%) "
            + "LIMIT :offset, :size ", nativeQuery = true)
    List<CustomerSyncListDTO> getCustomerSyncEcListByMerchantByBankAccount(String value, int offset, int size);

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

    @Query(value = "SELECT id AS customerSyncId, merchant FROM customer_sync ", nativeQuery = true)
    List<MerchantServiceItemDTO> getMerchantsMappingService();

    @Query(value = "SELECT merchant FROM customer_sync WHERE id = :id ", nativeQuery = true)
    String getMerchantNameById(@Param(value = "id") String id);

    // count customer
    @Query(value = "SELECT COUNT(id) as counter FROM customer_sync ", nativeQuery = true)
    Integer getCountingCustomerSync();

    @Query(value = "SELECT id FROM customer_sync WHERE merchant = :merchantName", nativeQuery = true)
    String checkExistedMerchantName(@Param(value = "merchantName") String merchantName);

}