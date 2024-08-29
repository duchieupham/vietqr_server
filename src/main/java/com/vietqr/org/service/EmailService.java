package com.vietqr.org.service;

import com.vietqr.org.dto.EmailDetailDTO;
import com.vietqr.org.dto.SendMailRequestDTO;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;

@Service
public interface EmailService {

    public void sendMessage(SimpleMailMessage simpleMailMessage);

    public void sendMail(String toMail, SendMailRequestDTO sendMailRequest);

    public String sendMailWithAttachment(EmailDetailDTO emailDetailDTO) throws MessagingException;

}
