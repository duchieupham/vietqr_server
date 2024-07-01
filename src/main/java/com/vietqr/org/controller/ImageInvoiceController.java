package com.vietqr.org.controller;

import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.entity.qrfeed.FileAttachmentEntity;
import com.vietqr.org.service.FileAttachService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class ImageInvoiceController {
    private static final Logger logger = Logger.getLogger(ImageInvoiceController.class);

    @Autowired
    FileAttachService imageInvoiceService;

    @PostMapping("images-invoice")
    public ResponseEntity<Object> getImageInvoice(@RequestPart("file") MultipartFile file) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            imageInvoiceService.saveFile(file);

            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED" + e.getMessage(), "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("images-invoice/{id}")
    public ResponseEntity<Object> downloadFileInMachine(@PathVariable String id) {
        Object result;
        HttpStatus httpStatus;
        try {
            FileAttachmentEntity imageInvoice = imageInvoiceService.getFile(id);
            if (imageInvoice != null) {
                // get file information
                byte[] fileData = imageInvoice.getFileData();
                String fileName = imageInvoice.getFileName();

                // download file on disk
                String downloadDir = System.getProperty("user.home") + "/Downloads/";
                String filePath = downloadDir + fileName;
                saveFileToDisk(fileData, filePath);

                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            } else {
                result = new ResponseMessageDTO("FAILED", "E05");
                httpStatus = HttpStatus.NOT_FOUND;
            }
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    private void saveFileToDisk(byte[] fileData, String filePath) throws IOException {
        File file = new File(filePath);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(fileData);
        fos.close();
    }

}
