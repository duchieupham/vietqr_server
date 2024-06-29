package com.vietqr.org.dto.qrfeed;

public class UserInFolderDTO {
    private String id;
    private String userId;
    private Object userData;
    private String folderId;
    private String title;
    private String description;

    public UserInFolderDTO(String id, String userId, UserInfoVcardDTO userData, String folderId, String title, String description) {
        this.id = id;
        this.userId = userId;
        this.userData = userData;
        this.folderId = folderId;
        this.title = title;
        this.description = description;
    }

    public Object getUserData() {
        return userData;
    }

    public void setUserData(Object userData) {
        this.userData = userData;
    }

    public UserInFolderDTO() {
        super();
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}
