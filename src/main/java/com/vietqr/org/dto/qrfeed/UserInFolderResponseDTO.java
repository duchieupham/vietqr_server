package com.vietqr.org.dto.qrfeed;

import java.util.List;

public class UserInFolderResponseDTO {
    private String userId;
    private Object userData;
    private String folderId;
    private String titleFolder;
    private String descriptionFolder;

    public UserInFolderResponseDTO() {
    }

    public UserInFolderResponseDTO(String userId, List<Object> userData, String folderId, String titleFolder, String descriptionFolder) {
        this.userId = userId;
        this.userData = userData;
        this.folderId = folderId;
        this.titleFolder = titleFolder;
        this.descriptionFolder = descriptionFolder;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Object getUserData() {
        return userData;
    }

    public void setUserData(Object userData) {
        this.userData = userData;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public String getTitleFolder() {
        return titleFolder;
    }

    public void setTitleFolder(String titleFolder) {
        this.titleFolder = titleFolder;
    }

    public String getDescriptionFolder() {
        return descriptionFolder;
    }

    public void setDescriptionFolder(String descriptionFolder) {
        this.descriptionFolder = descriptionFolder;
    }
}
