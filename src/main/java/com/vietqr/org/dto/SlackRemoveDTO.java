package com.vietqr.org.dto;


import java.io.Serializable;

public class SlackRemoveDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;

    public SlackRemoveDTO() {
        super();
    }

    public SlackRemoveDTO(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}