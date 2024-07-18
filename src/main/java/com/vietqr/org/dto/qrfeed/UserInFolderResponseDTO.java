package com.vietqr.org.dto.qrfeed;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
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
