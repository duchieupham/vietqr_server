package com.vietqr.org.dto;

import java.io.Serializable;

public class UserSettingUpdateDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private Object value;
    private String userId;

    public UserSettingUpdateDTO(Object value, String userId) {
        this.value = value;
        this.userId = userId;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
