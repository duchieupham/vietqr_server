package com.vietqr.org.dto;

import java.io.Serializable;

public class NotificationInputDTO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private int offset;
    private String userId;

    public NotificationInputDTO() {
        super();
    }

    public NotificationInputDTO(int offset, String userId) {
        this.offset = offset;
        this.userId = userId;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}