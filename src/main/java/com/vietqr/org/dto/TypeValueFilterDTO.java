package com.vietqr.org.dto;

public class TypeValueFilterDTO {
    private int type;
    private String value;

    public TypeValueFilterDTO() {
        type = 9;
        value = "";
    }

    public TypeValueFilterDTO(int type, String value) {
        this.type = type;
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
