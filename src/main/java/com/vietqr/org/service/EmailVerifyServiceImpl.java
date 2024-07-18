package com.vietqr.org.service;

import com.google.auto.value.extension.serializable.SerializableAutoValue;
import com.vietqr.org.entity.EmailVerifyEntity;
import com.vietqr.org.repository.EmailVerifyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailVerifyServiceImpl implements EmailVerifyService {
    @Autowired
    EmailVerifyRepository repository;


    @Override
    public int insertEmailVerify(EmailVerifyEntity entity) {
        return repository.save(entity) == null ? 0 : 1;
    }

    @Override
    public EmailVerifyEntity getEmailVerifyByUserId(String userId) {
        return repository.getEmailVerifyByUserId(userId);
    }

    @Override
    public void updateEmailVerifiedByUserId(String userId, int otp) {
        repository.updateEmailVerifiedByUserId(userId, otp);
    }
}
