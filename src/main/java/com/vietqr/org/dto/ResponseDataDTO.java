package com.vietqr.org.dto;

public class ResponseDataDTO {
    private String data;

    public ResponseDataDTO() {
    }

    public ResponseDataDTO(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
