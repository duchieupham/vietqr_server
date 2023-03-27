package com.vietqr.org.dto;

import java.io.Serializable;

public class FCMTokenUpdateDTO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String userId;
    private String oldToken;
    private String newToken;

    public FCMTokenUpdateDTO() {
        super();
    }

    public FCMTokenUpdateDTO(String userId, String oldToken, String newToken) {
        this.userId = userId;
        this.oldToken = oldToken;
        this.newToken = newToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOldToken() {
        return oldToken;
    }

    public void setOldToken(String oldToken) {
        this.oldToken = oldToken;
    }

    public String getNewToken() {
        return newToken;
    }

    public void setNewToken(String newToken) {
        this.newToken = newToken;
    }

}
