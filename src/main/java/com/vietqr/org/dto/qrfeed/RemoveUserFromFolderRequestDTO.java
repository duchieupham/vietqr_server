package com.vietqr.org.dto.qrfeed;

import javax.validation.constraints.NotNull;

public class RemoveUserFromFolderRequestDTO {
    @NotNull
    private String folderId;
    @NotNull
    private String userId;

    public RemoveUserFromFolderRequestDTO() {
    }

    public RemoveUserFromFolderRequestDTO(String folderId, String userId) {
        this.folderId = folderId;
        this.userId = userId;
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
}
