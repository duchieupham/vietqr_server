package com.vietqr.org.dto;

import java.io.Serializable;

public class NotificationCountDTO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private int count;

    public NotificationCountDTO() {
        super();
    }

    public NotificationCountDTO(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

}
