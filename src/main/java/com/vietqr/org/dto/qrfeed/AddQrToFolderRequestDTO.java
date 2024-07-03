package com.vietqr.org.dto.qrfeed;

import java.io.Serializable;
import java.util.ArrayList;

public class AddQrToFolderRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    public String folderId;
    public String userId;
    public ArrayList<String> qrIds;

    public AddQrToFolderRequestDTO(String folderId, String userId, ArrayList<String> qrIds) {
        this.folderId = folderId;
        this.userId = userId;
        this.qrIds = qrIds;
    }

    public AddQrToFolderRequestDTO() {
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

    public ArrayList<String> getQrIds() {
        return qrIds;
    }

    public void setQrIds(ArrayList<String> qrIds) {
        this.qrIds = qrIds;
    }
}
