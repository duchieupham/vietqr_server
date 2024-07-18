package com.vietqr.org.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AmazonS3Config {

    @Value("${aws.access.key}")
    private String awsAccessKey;

    @Value("${aws.secret.key}")
    private String awsSecretKey;

    @Value("${aws.region}")
    private String awsRegion;

    @Bean
    public S3Client s3Client() {
        AwsCredentials credentials = AwsBasicCredentials.create(awsAccessKey, awsSecretKey);
        S3Client s3Client = S3Client.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
        return s3Client;
    }
}

