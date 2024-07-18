package com.vietqr.org.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;

import java.io.IOException;

@Service
public interface AmazonS3Service {
    public String uploadFile(String key, MultipartFile file);
    public ResponseInputStream<?> downloadFile(String key);
    public String getFileLinkById(String key);
    public byte[] downloadImage(String key) throws IOException;
}
