package com.vietqr.org.repository;

import com.vietqr.org.dto.BankReceiveOtpDTO;
import com.vietqr.org.entity.BankReceiveOTPEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BankReceiveOTPRepository extends JpaRepository<BankReceiveOTPEntity, String> {
    @Query(value = "SELECT  FROM bank_receive_otp "
            + "WHERE bank_id = :bankId "
            + "AND user_id = :userId AND otp = :otp", nativeQuery = true)
    BankReceiveOtpDTO checkBankReceiveOtp(@Param(value = "userId") String userId,
                                          @Param(value = "bankId") String bankId,
                                          @Param(value = "otp") String otp);
}
