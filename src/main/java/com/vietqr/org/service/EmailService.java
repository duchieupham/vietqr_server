package com.vietqr.org.service;

import com.vietqr.org.dto.EmailDetails;
import com.vietqr.org.dto.SendMailRequestDTO;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;

@Service
public interface EmailService {
    public void sendSimpleMessage(String to, String subject, String text);

//    public void sendEmail(String from, String to, String subject, String body);

    public void sendMessage(SimpleMailMessage simpleMailMessage);

    public void sendMail(String toMail, SendMailRequestDTO sendMailRequest);

    public String sendMailWithAttachment(EmailDetails emailDetails);

}
