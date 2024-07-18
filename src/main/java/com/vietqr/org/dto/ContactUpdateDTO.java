package com.vietqr.org.dto;

import java.io.Serializable;

public class ContactUpdateDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String id;
    private String nickName;
    private int type;
    private String additionalData;

    public ContactUpdateDTO() {
        super();
    }

    public ContactUpdateDTO(String id, String nickName, int type, String additionalData) {
        this.id = id;
        this.nickName = nickName;
        this.type = type;
        this.additionalData = additionalData;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(String additionalData) {
        this.additionalData = additionalData;
    }

}
