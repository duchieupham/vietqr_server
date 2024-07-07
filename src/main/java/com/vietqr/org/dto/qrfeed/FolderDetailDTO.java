package com.vietqr.org.dto.qrfeed;

public class FolderDetailDTO {

    private String id;

    private String title;

    private String description;

    private String timeCreated;

    private int countUser;

    private int countQr;

    public FolderDetailDTO(String id, String title, String description, String timeCreated, int countUser, int countQr) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.timeCreated = timeCreated;
        this.countUser = countUser;
        this.countQr = countQr;
    }

    public FolderDetailDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(String timeCreated) {
        this.timeCreated = timeCreated;
    }

    public int getCountUser() {
        return countUser;
    }

    public void setCountUser(int countUser) {
        this.countUser = countUser;
    }

    public int getCountQr() {
        return countQr;
    }

    public void setCountQr(int countQr) {
        this.countQr = countQr;
    }
}
