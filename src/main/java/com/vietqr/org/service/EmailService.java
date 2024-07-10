package com.vietqr.org.service;

import org.springframework.stereotype.Service;

@Service
public interface EmailService {
    public void sendSimpleMessage(String to, String subject, String text);
    public void sendEmail(String to, String subject, String body);
}
