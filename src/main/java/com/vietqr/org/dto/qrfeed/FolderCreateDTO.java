package com.vietqr.org.dto.qrfeed;

import java.io.Serializable;

public class FolderCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private String title;
    private String description;
    private String userId;

    public FolderCreateDTO() {
        super();
    }

    public FolderCreateDTO(String title, String description, String userId) {
        this.title = title;
        this.description = description;
        this.userId = userId;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
