package com.vietqr.org.repository;

import com.vietqr.org.dto.BankReceiveOtpDTO;
import com.vietqr.org.entity.BankReceiveOtpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface BankReceiveOtpRepository extends JpaRepository<BankReceiveOtpEntity, String> {
    @Query(value = "SELECT id AS id, user_id AS userId, bank_id AS bankId, "
            + "otp_token AS otpToken, expired_date AS expiredDate, key_active AS keyActive "
            + "FROM bank_receive_otp "
            + "WHERE bank_id = :bankId "
            + "AND user_id = :userId AND otp_token = :otpToken "
            + "AND status = 0 AND key_active = :keyActive LIMIT 1", nativeQuery = true)
    BankReceiveOtpDTO checkBankReceiveOtp(@Param(value = "userId") String userId,
                                          @Param(value = "bankId") String bankId,
                                          @Param(value = "otpToken") String otpToken,
                                          @Param(value = "keyActive") String keyActive);

    @Query(value = "SELECT * FROM bank_receive_otp "
            + "WHERE user_id = :userId AND bank_id = :bankId AND "
            + "key_active = :keyActive AND status = 0 LIMIT 1", nativeQuery = true)
    BankReceiveOtpEntity getBankReceiveOtpByKey(String keyActive, String bankId, String userId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE bank_receive_otp SET status = :status, otp_token = '' WHERE id = :id", nativeQuery = true)
    void updateStatusBankReceiveOtp(String id, int status);
}
