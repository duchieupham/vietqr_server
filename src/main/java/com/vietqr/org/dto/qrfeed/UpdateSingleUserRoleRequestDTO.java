package com.vietqr.org.dto.qrfeed;

import javax.validation.constraints.NotNull;

public class UpdateSingleUserRoleRequestDTO {
    @NotNull
    private String folderId;
    @NotNull
    private String userId;
    @NotNull
    private String role;

    public UpdateSingleUserRoleRequestDTO() {
    }

    public UpdateSingleUserRoleRequestDTO(String folderId, String userId, String role) {
        this.folderId = folderId;
        this.userId = userId;
        this.role = role;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
