package com.vietqr.org.repository;

import com.vietqr.org.entity.EmailVerifyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Repository
public interface EmailVerifyRepository extends JpaRepository<EmailVerifyEntity, String> {

    @Query(value = "SELECT * FROM email_verify WHERE user_id = :userId ORDER BY time_created DESC ", nativeQuery = true)
    List<EmailVerifyEntity> getEmailVerifyByUserId(String userId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE email_verify SET is_verify = TRUE WHERE (user_id = :userId) AND (otp = :otp) ", nativeQuery = true)
    void updateEmailVerifiedByUserId(String userId, String otp);

    @Query(value = "SELECT * FROM email_verify WHERE user_id = :userId AND email = :email ORDER BY time_created DESC LIMIT 1", nativeQuery = true)
    Optional<EmailVerifyEntity> getEmailVerifyByUserEmail(@Param(value = "userId") String userId,@Param(value = "email") String email);

    @Query(value = "SELECT EXISTS(SELECT 1 FROM email_verify WHERE user_id = :userId AND email = :email AND otp = :otp)", nativeQuery = true)
    int existsOTP(@Param(value = "userId") String userId,@Param(value = "email") String email,@Param(value = "otp") String otp);
}
