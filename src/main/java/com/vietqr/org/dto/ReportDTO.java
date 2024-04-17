package com.vietqr.org.dto;

import java.io.Serializable;
import java.util.ArrayList;

import org.springframework.web.multipart.MultipartFile;

public class ReportDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String userId;
    private int type;
    private String description;
    private ArrayList<MultipartFile> images;

    public ReportDTO() {
        super();
    }

    public ReportDTO(String userId, int type, String description, ArrayList<MultipartFile> images) {
        this.userId = userId;
        this.type = type;
        this.description = description;
        this.images = images;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<MultipartFile> getImages() {
        return images;
    }

    public void setImages(ArrayList<MultipartFile> images) {
        this.images = images;
    }

}
