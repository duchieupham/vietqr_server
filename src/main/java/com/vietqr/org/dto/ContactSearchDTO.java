package com.vietqr.org.dto;

import java.io.Serializable;

public class ContactSearchDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String nickname;
    private String userId;
    private int type;
    private int offset;

    public ContactSearchDTO() {
        super();
    }

    public ContactSearchDTO(String nickname, String userId, int type, int offset) {
        this.nickname = nickname;
        this.userId = userId;
        this.type = type;
        this.offset = offset;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

}
