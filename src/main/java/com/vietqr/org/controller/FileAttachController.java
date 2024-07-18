package com.vietqr.org.controller;

import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.entity.qrfeed.FileAttachmentEntity;
import com.vietqr.org.service.FileAttachService;
import com.vietqr.org.service.InvoiceService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class FileAttachController {
    private static final Logger logger = Logger.getLogger(FileAttachController.class);

    @Autowired
    FileAttachService fileAttachService;

    @Autowired
    InvoiceService invoiceService;

    @PostMapping("images-invoice/upload")
    public ResponseEntity<Object> getImageInvoice(
            @RequestParam String invoiceId,
            @RequestPart("file") MultipartFile file) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            UUID id = UUID.randomUUID();
            fileAttachService.saveFile(file, id.toString());

            // update file to invoice
            invoiceService.updateFileInvoiceById(id.toString(), invoiceId);

            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED" + e.getMessage(), "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // api lấy ra file name nếu invoice đó có file
    @GetMapping("images-invoice/get-file")
    public ResponseEntity<Object> getFileInInvoice(@RequestParam String invoiceId) {
        Object result;
        HttpStatus httpStatus;
        try {
            String fileName = fileAttachService.getFileInInvoice(invoiceId);
            if (fileName != null) {
                result = new ResponseMessageDTO("SUCCESS", fileName);
                httpStatus = HttpStatus.OK;
            } else {
                result = new ResponseMessageDTO("FAILED", "E05");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }


    @GetMapping(value = "images-invoice/download-files", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> downloadFileInMachine(@RequestParam String invoiceId) {
        byte[] result = new byte[0];
        HttpStatus httpStatus;
        try {
            FileAttachmentEntity imageInvoice = fileAttachService.getFile(invoiceId);
            if (imageInvoice != null) {
                // get file information
                result = imageInvoice.getFileData();
//                String fileName = imageInvoice.getFileName();

                // download file on disk
//                String downloadDir = System.getProperty("user.home") + "/Downloads/";
//                String filePath = downloadDir + fileName;
//                saveFileToDisk(fileData, filePath);

//                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            } else {
//                result = new ResponseMessageDTO("FAILED", "E05");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
//            result = new ResponseMessageDTO("FAILED", "E05");
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
