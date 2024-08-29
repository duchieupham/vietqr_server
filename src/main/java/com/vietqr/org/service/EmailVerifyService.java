package com.vietqr.org.service;

import com.vietqr.org.entity.EmailVerifyEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface EmailVerifyService {

    public int insertEmailVerify(EmailVerifyEntity entity);

    public List<EmailVerifyEntity> getEmailVerifyByUserId(String userId);
//    public EmailVerifyEntity getEmailVerifyByUserIdBackUp(String userId);

    void updateEmailVerifiedByUserId(String userId, int otp);

}
