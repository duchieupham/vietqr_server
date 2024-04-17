package com.vietqr.org.dto;

import java.io.Serializable;

public class ImagePostConvertDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String base64Image;
    private String name;

    public ImagePostConvertDTO() {
        super();
    }

    public ImagePostConvertDTO(String base64Image, String name) {
        this.base64Image = base64Image;
        this.name = name;
    }

    public String getBase64Image() {
        return base64Image;
    }

    public void setBase64Image(String base64Image) {
        this.base64Image = base64Image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
