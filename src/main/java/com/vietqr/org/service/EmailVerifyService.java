package com.vietqr.org.service;

import com.vietqr.org.dto.EmailDetails;
import com.vietqr.org.entity.AccountLoginEntity;
import com.vietqr.org.entity.EmailVerifyEntity;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.List;

@Service
public interface EmailVerifyService {

    public int insertEmailVerify(EmailVerifyEntity entity);

    public List<EmailVerifyEntity> getEmailVerifyByUserId(String userId);

    void updateEmailVerifiedByUserId(String userId, int otp);

}
