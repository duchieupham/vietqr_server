package com.vietqr.org.dto;

import java.io.Serializable;

public class AccountSettingVoiceDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String userId;
    private int value;
    // 0: mobile
    // 1: kiot
    // 2: web
    private int type;

    public AccountSettingVoiceDTO() {
        super();
    }

    public AccountSettingVoiceDTO(String userId, int value, int type) {
        this.userId = userId;
        this.value = value;
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
