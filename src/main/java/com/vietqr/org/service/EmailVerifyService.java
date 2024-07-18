package com.vietqr.org.service;

import com.vietqr.org.entity.AccountLoginEntity;
import com.vietqr.org.entity.EmailVerifyEntity;
import org.springframework.stereotype.Service;

@Service
public interface EmailVerifyService {

    public int insertEmailVerify(EmailVerifyEntity entity);

    public EmailVerifyEntity getEmailVerifyByUserId(String userId);

    void updateEmailVerifiedByUserId(String userId, int otp);
}
