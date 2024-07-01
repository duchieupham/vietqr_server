package com.vietqr.org.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

@Service
public class AmazonS3ServiceImpl implements AmazonS3Service {

    private Logger logger = Logger.getLogger(AmazonS3ServiceImpl.class.getName());

    @Autowired
    private S3Client s3Client;

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    @Value("${aws.s3.endpoint.url}")
    private String endpointUrl;

    public String uploadFile(String key, MultipartFile file) {
        String result = "";
        try {
            String fileName = file.getOriginalFilename();
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .metadata(Collections.singletonMap("name", fileName))
                    .build();

            File filePut = convertMultiPartToFile(file);
            s3Client.putObject(request, RequestBody.fromFile(filePut));
            result = endpointUrl + "/" + key;
            System.out.println("File uploaded successfully at: " + System.currentTimeMillis());
        } catch (Exception ignored) {
            System.out.println("Error at uploadFile: " + ignored.toString());
            logger.info("Error at uploadFile: " + ignored.toString());
        }
        return result;
    }

    @Override
    public String getFileLinkById(String key) {
        String result = "";
        try {
            result = endpointUrl + "/" + key;
            System.out.println("File uploaded successfully at: " + System.currentTimeMillis());
        } catch (Exception ignored) {
            System.out.println("Error at uploadFile: " + ignored.toString());
            logger.info("Error at uploadFile: " + ignored.toString());
        }
        return result;
    }

    @Override
    public byte[] downloadImage(String key) throws IOException {
        try {
            // Tạo yêu cầu để tải xuống tệp từ Amazon S3
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket("your-bucket-name")
                    .key(key)
                    .build();

            // Tải xuống tệp và ghi vào ByteArrayOutputStream
            ResponseInputStream<?> responseInputStream = s3Client.getObject(getObjectRequest);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = responseInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            // Lấy dữ liệu từ ByteArrayOutputStream
            byte[] fileBytes = outputStream.toByteArray();

            // Đóng ResponseInputStream và ByteArrayOutputStream sau khi đã sử dụng
            responseInputStream.close();
            outputStream.close();

            return fileBytes;
        } catch (Exception e) {
            // Xử lý exception nếu có lỗi xảy ra
            e.printStackTrace();
            return new byte[0]; // Trả về mảng byte rỗng nếu không tải được tệp
        }
    }

    public ResponseInputStream<?> downloadFile(String id) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(id)
                .build();

        return s3Client.getObject(request, ResponseTransformer.toInputStream());
    }

    private File convertMultiPartToFile(MultipartFile file) {
        File convertedFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            logger.info("Error converting multipartFile to file");
        }
        return convertedFile;
    }
}
