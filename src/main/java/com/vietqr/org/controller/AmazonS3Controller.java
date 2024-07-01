package com.vietqr.org.controller;

import com.vietqr.org.service.AmazonS3Service;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class AmazonS3Controller {

    private static final Logger logger = Logger.getLogger(AmazonS3Controller.class);

    @Autowired
    private AmazonS3Service amazonS3Service;

    @GetMapping("/amazon-s3/{id}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String id) {
        try {
//            id = "logo-vietqr-official-transparent.png";
            ResponseInputStream<?> responseInputStream = amazonS3Service.downloadFile(id);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = responseInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            byte[] bytes = outputStream.toByteArray();
            return ResponseEntity.ok().body(bytes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/amazon-s3/review/{id}")
    public ResponseEntity<byte[]> downloadFileReview(@PathVariable String id) {
        try {
            ResponseInputStream<?> responseInputStream = amazonS3Service.downloadFile(id);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = responseInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            byte[] bytes = outputStream.toByteArray();
            return ResponseEntity.ok().body(bytes);
        } catch (IOException e) {
            e.printStackTrace(); // Xử lý lỗi, ví dụ log lỗi vào hệ thống
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping(value = "/upload")
    public ResponseEntity<String> uploadFile(@RequestPart("file") MultipartFile file) {
        try {
                String url = amazonS3Service.uploadFile(file.getOriginalFilename(), file);
                return ResponseEntity.ok(url);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("");
        }
    }
}
