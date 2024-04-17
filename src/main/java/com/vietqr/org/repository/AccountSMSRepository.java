package com.vietqr.org.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.dto.AccountCheckDTO;
import com.vietqr.org.dto.AccountSmsSearchDTO;
import com.vietqr.org.entity.AccountSmsEntity;

@Repository
public interface AccountSMSRepository extends JpaRepository<AccountSmsEntity, Long> {

    @Query(value = "SELECT id FROM account_sms WHERE phone_no = :phoneNo AND password = :password AND status = 1 ", nativeQuery = true)
    String loginSms(@Param(value = "phoneNo") String phoneNo, @Param(value = "password") String password);

    @Query(value = "SELECT * FROM account_sms WHERE id = :id ", nativeQuery = true)
    AccountSmsEntity getAccountSmsById(@Param(value = "id") String id);

    @Query(value = "SELECT id, status FROM account_sms WHERE phone_no = :phoneNo", nativeQuery = true)
    AccountCheckDTO checkExistedPhoneNo(@Param(value = "phoneNo") String phoneNo);

    @Query(value = "SELECT id, phone_no as phoneNo, email, full_name as fullName, img_id as imgId, carrier_type_id as carrierTypeId "
            + "FROM account_sms "
            + "WHERE phone_no = :phoneNo", nativeQuery = true)
    AccountSmsSearchDTO getAccountSmsSearch(@Param(value = "phoneNo") String phoneNo);

    @Transactional
    @Modifying
    @Query(value = "UPDATE account_sms SET password = :password WHERE id = :id", nativeQuery = true)
    void updatePassword(@Param(value = "password") String password, @Param(value = "id") String id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE account_sms SET status = :status WHERE id = :id", nativeQuery = true)
    void updateStatus(@Param(value = "status") int status, @Param(value = "id") String id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE account_sms SET last_login = :lastLogin, access_count = :accessCount WHERE id = :id", nativeQuery = true)
    void updateAccessLoginSms(@Param(value = "lastLogin") long lastLogin,
            @Param(value = "accessCount") long accessCount,
            @Param(value = "id") String id);

    @Query(value = "SELECT account_sms FROM account_setting WHERE id = :id", nativeQuery = true)
    Long getAccessCountById(@Param(value = "id") String id);
}
