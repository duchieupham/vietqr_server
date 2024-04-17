package com.vietqr.org.dto;

import java.io.Serializable;

public class UserSettingUpdateCheckDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private boolean value;
    private String userId;

    public UserSettingUpdateCheckDTO() {
        super();
    }

    public UserSettingUpdateCheckDTO(boolean value, String userId) {
        this.value = value;
        this.userId = userId;
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
