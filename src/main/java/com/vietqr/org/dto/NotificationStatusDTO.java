package com.vietqr.org.dto;

import java.io.Serializable;

public class NotificationStatusDTO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String userId;

    public NotificationStatusDTO() {
        super();
    }

    public NotificationStatusDTO(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
