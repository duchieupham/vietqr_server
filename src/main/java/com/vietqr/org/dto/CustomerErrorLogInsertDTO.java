package com.vietqr.org.dto;

public class CustomerErrorLogInsertDTO {
    private String groupCode;
    private String errorCode;
    private String errorDescription;

    public CustomerErrorLogInsertDTO() {
    }

    public CustomerErrorLogInsertDTO(String groupCode, String errorCode, String errorDescription) {
        this.groupCode = groupCode;
        this.errorCode = errorCode;
        this.errorDescription = errorDescription;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }
}
