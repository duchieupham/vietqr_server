package com.vietqr.org.dto;

import java.io.Serializable;

public class ContactStatusUpdateDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String id;
    private int status;

    public ContactStatusUpdateDTO() {
        super();
    }

    public ContactStatusUpdateDTO(String id, int status) {
        this.id = id;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}
