package com.vietqr.org.service;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import com.vietqr.org.dto.EmailDetails;
import com.vietqr.org.dto.SendMailRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender emailSender;

//    @Autowired
//    private EmailConfiguration emailConfiguration;

//    @Autowired
//    private AmazonSimpleEmailService amazonSimpleEmailService;

    @Autowired
    private MailSender mailSender;

    @Value("${spring.mail.username}")
    private String sender;

    @Autowired
    private JavaMailSender javaMailSender;

    private static final String FROM_ADDRESS = "linh.npn@vietqr.vn";

//    @Override
//    public void sendEmail(String from, String to, String subject, String body) {
//        SendEmailRequest request = new SendEmailRequest()
//                .withDestination(new Destination()
//                        .withToAddresses(to))
//                .withMessage(new Message()
//                        .withBody(new Body().withHtml(new Content().withCharset("UTF-8").withData(body)))
//                        .withSubject(new Content().withCharset("UTF-8").withData(subject)))
//                .withSource(from);
//        try {
//            amazonSimpleEmailService.sendEmail(request);
//            System.out.println("Email sent successfully to " + to);
//        } catch (Exception ex) {
//            System.err.println("The email was not sent. Error message: " + ex.getMessage());
//        }
//    }

    @Override
    public void sendMessage(SimpleMailMessage simpleMailMessage) {
        this.mailSender.send(simpleMailMessage);
    }

    @Override
    public void sendMail(String toMail, SendMailRequestDTO sendMailRequest) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(sender);
        simpleMailMessage.setTo(toMail);
        simpleMailMessage.setSubject(sendMailRequest.getSubject());
        simpleMailMessage.setText(sendMailRequest.getMessage());

        mailSender.send(simpleMailMessage);
    }

    @Override
    public String sendMailWithAttachment(EmailDetails emailDetails) {
        // Creating a mime message
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;
        try {
            // Setting multipart as true for attachments to
            // be send
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(sender);
            mimeMessageHelper.setTo(emailDetails.getRecipient());
            mimeMessageHelper.setText(emailDetails.getMsgBody());
            mimeMessageHelper.setSubject(
                    emailDetails.getSubject());

            // Adding the attachment
            FileSystemResource file = new FileSystemResource(new File(emailDetails.getAttachment()));
            mimeMessageHelper.addAttachment(file.getFilename(), file);

            // Sending the mail
            javaMailSender.send(mimeMessage);
            return "Mail sent Successfully";
        }
        // Catch block to handle MessagingException
        catch (MessagingException e) {
            return "Error while sending mail!!!";
        }
    }

    @Override
    @Async
    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sender);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }

}
