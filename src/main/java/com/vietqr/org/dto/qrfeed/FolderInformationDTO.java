package com.vietqr.org.dto.qrfeed;

public class FolderInformationDTO {
    private String userId;
    private String folderId;
    private String titleFolder;
    private String descriptionFolder;

    public FolderInformationDTO() {
    }

    public FolderInformationDTO(String userId, String folderId, String titleFolder, String descriptionFolder) {
        this.userId = userId;
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
