package com.vietqr.org.dto;

import java.io.Serializable;

public class ContactListDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String id;
    private String nickname;
    private int status;
    private int type;
    private String imgId;
    private String description;

    public ContactListDTO() {
        super();
    }

    public ContactListDTO(String id, String nickname, int status, int type, String imgId, String description) {
        this.id = id;
        this.nickname = nickname;
        this.status = status;
        this.type = type;
        this.imgId = imgId;
        this.description = description;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getImgId() {
        return imgId;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
