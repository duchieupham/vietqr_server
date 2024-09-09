package com.vietqr.org.service;

import com.vietqr.org.entity.EmailVerifyEntity;
import com.vietqr.org.repository.EmailVerifyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmailVerifyServiceImpl implements EmailVerifyService {
    @Autowired
    EmailVerifyRepository repository;

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public int insertEmailVerify(EmailVerifyEntity entity) {
        return repository.save(entity) == null ? 0 : 1;
    }

    @Override
    public List<EmailVerifyEntity> getEmailVerifyByUserId(String userId) {
        return repository.getEmailVerifyByUserId(userId);
    }

    @Override
    public void updateEmailVerifiedByUserId(String userId, int otp) {
        repository.updateEmailVerifiedByUserId(userId, otp);
    }

    @Override
    public Optional<EmailVerifyEntity> getEmailVerifyByUserEmail(String userId, String email) {
        return repository.getEmailVerifyByUserEmail(userId, email);
    }

    @Override
    public boolean existsOTP(String userId, String email, int otp) {
        return repository.existsOTP(userId, email, otp) == 1;
    }
}
