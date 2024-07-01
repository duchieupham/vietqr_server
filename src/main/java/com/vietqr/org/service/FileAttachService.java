package com.vietqr.org.service;

import com.vietqr.org.entity.InvoiceEntity;
import com.vietqr.org.entity.qrfeed.FileAttachmentEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public interface FileAttachService {
    public void saveFile(MultipartFile file, String id) throws IOException;
    public FileAttachmentEntity getFile(String id);
    public FileAttachmentEntity getFileById(String id);
}
