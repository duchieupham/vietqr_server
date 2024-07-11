package com.vietqr.org.dto;

import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public class DeleteQrFolder {

    public String folderId;
    public String userId;
    public List<String> qrIds;

    public DeleteQrFolder() {
    }

    public DeleteQrFolder(String folderId, String userId, List<String> qrIds) {
        this.folderId = folderId;
        this.userId = userId;
        this.qrIds = qrIds;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getQrIds() {
        return qrIds;
    }

    public void setQrIds(List<String> qrIds) {
        this.qrIds = qrIds;
    }
}
