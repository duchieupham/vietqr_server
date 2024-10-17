package com.vietqr.org.constant;

public enum GroupCodeConstant {
    RETRY_GROUP("R"),
    ERROR_GROUP("E"),
    SUCCESS_GROUP("S");

    private final String value;

    private GroupCodeConstant(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
