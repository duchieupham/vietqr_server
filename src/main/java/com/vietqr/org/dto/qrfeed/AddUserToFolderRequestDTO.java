package com.vietqr.org.dto.qrfeed;

import java.util.List;

public class AddUserToFolderRequestDTO {
    public String folderId;
    public String userId;
    public List<String> userIds;

    public AddUserToFolderRequestDTO(String folderId, String userId, List<String> userIds) {
        this.folderId = folderId;
        this.userId = userId;
        this.userIds = userIds;
    }

    public AddUserToFolderRequestDTO() {
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

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }
}
