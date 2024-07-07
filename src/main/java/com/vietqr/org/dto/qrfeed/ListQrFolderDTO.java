package com.vietqr.org.dto.qrfeed;

import java.io.Serializable;

public class ListQrFolderDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private long timeCreated;
    private String title;
    private String description;
    private String userId;
    private int countUsers;

    public ListQrFolderDTO(String id, long timeCreated,String description, String title, String userId) {
        this.id = id;
        this.timeCreated = timeCreated;
        this.title = title;
        this.description = description;
        this.userId = userId;
    }

    public int getCountUsers() {
        return countUsers;
    }

    public void setCountUsers(int countUsers) {
        this.countUsers = countUsers;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ListQrFolderDTO() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
