package com.vietqr.org.dto.qrfeed;

public class QrFolderUpdateDTO {
    private String title;
    private String description;

    public QrFolderUpdateDTO(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public QrFolderUpdateDTO() {
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
}
