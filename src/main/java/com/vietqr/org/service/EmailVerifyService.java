package com.vietqr.org.service;

import com.vietqr.org.entity.EmailVerifyEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface EmailVerifyService {

    public int insertEmailVerify(EmailVerifyEntity entity);

    public List<EmailVerifyEntity> getEmailVerifyByUserId(String userId);
//    public EmailVerifyEntity getEmailVerifyByUserIdBackUp(String userId);

    void updateEmailVerifiedByUserId(String userId, String otp);

    Optional<EmailVerifyEntity> getEmailVerifyByUserEmail(String userId, String email);

    boolean existsOTP(String userId, String email, String otp);
}
