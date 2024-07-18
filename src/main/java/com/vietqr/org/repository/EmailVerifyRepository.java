package com.vietqr.org.repository;

import com.vietqr.org.entity.EmailVerifyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface EmailVerifyRepository extends JpaRepository<EmailVerifyEntity, String> {

    @Query(value = "SELECT * FROM email_verify WHERE user_id = :userId ", nativeQuery = true)
    EmailVerifyEntity getEmailVerifyByUserId(String userId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE email_verify SET is_verify = TRUE WHERE (user_id = :userId) AND (otp = :otp) ", nativeQuery = true)
    void updateEmailVerifiedByUserId(String userId, int otp);

}
