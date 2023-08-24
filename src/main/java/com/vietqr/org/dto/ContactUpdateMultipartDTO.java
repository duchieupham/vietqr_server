package com.vietqr.org.dto;

import java.io.Serializable;

import org.springframework.web.multipart.MultipartFile;

public class ContactUpdateMultipartDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String id;
    private String nickName;
    private int colorType;
    private String additionalData;
    private MultipartFile image;
    private String imgId;

    public ContactUpdateMultipartDTO() {
        super();
    }

    public ContactUpdateMultipartDTO(String id, String nickName, int colorType, String additionalData,
            MultipartFile image, String imgId) {
        this.id = id;
        this.nickName = nickName;
        this.colorType = colorType;
        this.additionalData = additionalData;
        this.image = image;
        this.imgId = imgId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getColorType() {
        return colorType;
    }

    public void setColorType(int colorType) {
        this.colorType = colorType;
    }

    public String getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(String additionalData) {
        this.additionalData = additionalData;
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }

    public String getImgId() {
        return imgId;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }

}
