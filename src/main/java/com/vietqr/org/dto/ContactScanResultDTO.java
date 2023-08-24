package com.vietqr.org.dto;

import java.io.Serializable;

public class ContactScanResultDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String nickname;
    private String imgId;

    public ContactScanResultDTO() {
        super();
    }

    public ContactScanResultDTO(String nickname, String imgId) {
        this.nickname = nickname;
        this.imgId = imgId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getImgId() {
        return imgId;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }

}
