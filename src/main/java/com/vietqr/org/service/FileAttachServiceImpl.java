package com.vietqr.org.service;

import com.vietqr.org.entity.qrfeed.FileAttachmentEntity;
import com.vietqr.org.repository.FileAttachRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class FileAttachServiceImpl implements FileAttachService {
    @Autowired
    private FileAttachRepository repo;

    private static final Map<String, String> FILE_TYPE_MAP = new HashMap<>();

    static {
        FILE_TYPE_MAP.put("application/pdf", "PDF");
        FILE_TYPE_MAP.put("application/msword", "Word");
        FILE_TYPE_MAP.put("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "Word");
        FILE_TYPE_MAP.put("application/vnd.ms-excel", "Excel");
        FILE_TYPE_MAP.put("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "Excel");
    }

    private String getDisplayFileType(String contentType) {
        return FILE_TYPE_MAP.getOrDefault(contentType, contentType);
    }

    @Override
    public void saveFile(MultipartFile file) throws IOException {
        FileAttachmentEntity imageInvoice = new FileAttachmentEntity();
        imageInvoice.setId(UUID.randomUUID().toString());
        imageInvoice.setFileName(file.getOriginalFilename());
        imageInvoice.setFileData(file.getBytes());
        imageInvoice.setFileType(file.getContentType());
        imageInvoice.setDisplayFileType(getDisplayFileType(file.getContentType()));
        repo.save(imageInvoice);
    }

    @Override
    public FileAttachmentEntity getFile(String id) {
        return repo.findImageById(id);
    }

    @Override
    public FileAttachmentEntity getFileById(String id) {
        return repo.findImgById(id);
    }
}
