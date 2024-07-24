package com.vietqr.org.service;

import com.google.auto.value.extension.serializable.SerializableAutoValue;
import com.vietqr.org.dto.EmailDetails;
import com.vietqr.org.entity.EmailVerifyEntity;
import com.vietqr.org.repository.EmailVerifyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

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

}