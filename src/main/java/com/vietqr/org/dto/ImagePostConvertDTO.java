package com.vietqr.org.dto;

import java.io.Serializable;

public class ImagePostConvertDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String base64Image;
    private String name;
    private String style;

    public ImagePostConvertDTO() {
        super();
    }

    public ImagePostConvertDTO(String base64Image, String name, String style) {
        this.base64Image = base64Image;
        this.name = name;
        this.style = style;
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

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

}
