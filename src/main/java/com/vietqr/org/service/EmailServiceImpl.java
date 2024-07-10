package com.vietqr.org.service;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import com.vietqr.org.util.EmailConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private EmailConfiguration emailConfiguration;

    @Autowired
    private AmazonSimpleEmailService amazonSimpleEmailService;

    private static final String FROM_ADDRESS = "linh.npn@vietqr.vn";

    @Override
    public void sendEmail(String to, String subject, String body) {
        SendEmailRequest request = new SendEmailRequest()
                .withDestination(new Destination()
                .withToAddresses(to))
                .withMessage(new Message()
                        .withBody(new Body().withHtml(new Content().withCharset("UTF-8").withData(body)))
                        .withSubject(new Content().withCharset("UTF-8").withData(subject)))
                .withSource(FROM_ADDRESS);
        try {
            amazonSimpleEmailService.sendEmail(request);
            System.out.println("Email sent successfully to " + to);
        } catch (Exception ex) {
            System.err.println("The email was not sent. Error message: " + ex.getMessage());
        }
    }

    @Override
    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailConfiguration.getRandomVerifiedEmail());
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }
}
