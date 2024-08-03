package com.vietqr.org.dto;

import java.io.Serializable;

public class ConfirmRequestFailedBankDTO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String error;
    private String soaErrorCode;
    private String soaErrorDesc;
    private String clientMessageId;
    private String path;

    public ConfirmRequestFailedBankDTO() {
        super();
    }

    public ConfirmRequestFailedBankDTO(String error, String soaErrorCode, String soaErrorDesc, String clientMessageId,
            String path) {
        this.error = error;
        this.soaErrorCode = soaErrorCode;
        this.soaErrorDesc = soaErrorDesc;
        this.clientMessageId = clientMessageId;
        this.path = path;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getSoaErrorCode() {
        return soaErrorCode;
    }

    public void setSoaErrorCode(String soaErrorCode) {
        this.soaErrorCode = soaErrorCode;
    }

    public String getSoaErrorDesc() {
        return soaErrorDesc;
    }

    public void setSoaErrorDesc(String soaErrorDesc) {
        this.soaErrorDesc = soaErrorDesc;
    }

    public String getClientMessageId() {
        return clientMessageId;
    }

    public void setClientMessageId(String clientMessageId) {
        this.clientMessageId = clientMessageId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "ConfirmRequestFailedBankDTO{" +
                "error='" + error + '\'' +
                ", soaErrorCode='" + soaErrorCode + '\'' +
                ", soaErrorDesc='" + soaErrorDesc + '\'' +
                ", clientMessageId='" + clientMessageId + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
