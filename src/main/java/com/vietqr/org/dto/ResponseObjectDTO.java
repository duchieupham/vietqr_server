package com.vietqr.org.dto;

import java.io.Serializable;

public class ResponseObjectDTO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String status;
    private Object data;

    public ResponseObjectDTO() {
        super();
    }

    public ResponseObjectDTO(String status, Object data) {
        this.status = status;
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}
