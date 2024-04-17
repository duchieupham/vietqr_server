package com.vietqr.org.dto;

import java.io.Serializable;

public class UserSettingUpdateDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private int value;
    private String userId;

    public UserSettingUpdateDTO() {
        super();
    }

    public UserSettingUpdateDTO(int value, String userId) {
        this.value = value;
        this.userId = userId;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
