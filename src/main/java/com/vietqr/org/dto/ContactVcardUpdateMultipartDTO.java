package com.vietqr.org.dto;

import java.io.Serializable;

import org.springframework.web.multipart.MultipartFile;

public class ContactVcardUpdateMultipartDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String id;
    private String nickname;
    private String note;
    private int colorType;
    private String address;
    private String company;
    private String email;
    private String phoneNo;
    private String website;
    private String imgId;
    private MultipartFile image;

    public ContactVcardUpdateMultipartDTO() {
        super();
    }

    public ContactVcardUpdateMultipartDTO(String id, String nickname, String note, int colorType, String address,
            String company, String email, String phoneNo, String website, String imgId, MultipartFile image) {
        this.id = id;
        this.nickname = nickname;
        this.note = note;
        this.colorType = colorType;
        this.address = address;
        this.company = company;
        this.email = email;
        this.phoneNo = phoneNo;
        this.website = website;
        this.imgId = imgId;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getColorType() {
        return colorType;
    }

    public void setColorType(int colorType) {
        this.colorType = colorType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getImgId() {
        return imgId;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }

}
