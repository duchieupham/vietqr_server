//package com.vietqr.org.util;
//
//import com.amazonaws.auth.AWSStaticCredentialsProvider;
//import com.amazonaws.auth.BasicAWSCredentials;
//import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
//import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class AwsSesConfig {
//
//    @Value("${aws.access-key}")
//    private String awsAccessKey;
//
//    @Value("${aws.secret-key}")
//    private String awsSecretKey;
//
//    @Bean
//    public AmazonSimpleEmailService amazonSimpleEmailService() {
//        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
//        return AmazonSimpleEmailServiceClientBuilder.standard()
//                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
//                .build();
//    }
//}
