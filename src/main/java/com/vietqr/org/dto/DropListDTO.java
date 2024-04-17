package com.vietqr.org.dto;

public class DropListDTO {
    private int title;
    private String value;

    public DropListDTO(int title, String value) {
        this.title = title;
        this.value = value;
    }

    public DropListDTO() {
    }

    public int getTitle() {
        return title;
    }

    public void setTitle(int title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
