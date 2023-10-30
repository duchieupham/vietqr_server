package com.vietqr.org.dto;

import java.io.Serializable;
import java.util.ArrayList;

import org.springframework.web.multipart.MultipartFile;

public class PostTypeNewsfeedInputDTO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String userId;
    private String content;
    private ArrayList<MultipartFile> images;

    public PostTypeNewsfeedInputDTO() {
        super();
    }

    public PostTypeNewsfeedInputDTO(String userId, String content,
            ArrayList<MultipartFile> images) {
        this.userId = userId;
        this.content = content;
        this.images = images;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ArrayList<MultipartFile> getImages() {
        return images;
    }

    public void setImages(ArrayList<MultipartFile> images) {
        this.images = images;
    }

}
