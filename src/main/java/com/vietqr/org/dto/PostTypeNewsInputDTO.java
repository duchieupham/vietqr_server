package com.vietqr.org.dto;

import java.io.Serializable;

public class PostTypeNewsInputDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String content;

    public PostTypeNewsInputDTO() {
        super();
    }

    public PostTypeNewsInputDTO(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
