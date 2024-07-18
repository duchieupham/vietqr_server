package com.vietqr.org.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.FcmTokenEntity;
import com.vietqr.org.entity.FcmTokenSmsEntity;

@Repository
public interface FcmTokenSmsRepository extends JpaRepository<FcmTokenSmsEntity, Long> {

    @Query(value = "SELECT * FROM fcm_token_sms WHERE sms_id = :smsId", nativeQuery = true)
    List<FcmTokenEntity> getFcmTokenSmsBySmsId(@Param(value = "smsId") String smsId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM fcm_token_sms WHERE token = :token", nativeQuery = true)
    void deleteFcmTokenSms(@Param(value = "token") String token);

    @Transactional
    @Modifying
    @Query(value = "UPDATE fcm_token_sms SET token = :newToken WHERE sms_id = :smsId AND token = :oldToken", nativeQuery = true)
    void updateTokenSms(@Param(value = "newToken") String newToken, @Param(value = "smsId") String smsId,
            @Param(value = "oldToken") String oldToken);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM fcm_token_sms WHERE sms_id = :smsId", nativeQuery = true)
    void deleteTokensBySmsId(@Param(value = "smsId") String smsId);
}
