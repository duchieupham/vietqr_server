package com.vietqr.org.dto;

import java.io.Serializable;

public class TransImgIdDTO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String imgId;

    public TransImgIdDTO() {
        super();
    }

    public TransImgIdDTO(String imgId) {
        this.imgId = imgId;
    }

    public String getImgId() {
        return imgId;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }
}
