package com.vietqr.org.dto;

import java.io.Serializable;

public class CofirmOTPBankDTO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String requestId;
    private String otpValue;
    private String applicationType;

    public CofirmOTPBankDTO() {
        super();
    }

    public CofirmOTPBankDTO(String requestId, String otpValue, String applicationType) {
        this.requestId = requestId;
        this.otpValue = otpValue;
        this.applicationType = applicationType;
    }

    public String getOtpValue() {
        return otpValue;
    }

    public void setOtpValue(String otpValue) {
        this.otpValue = otpValue;
    }

    public String getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(String applicationType) {
        this.applicationType = applicationType;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    @Override
    public String toString() {
        return "RequestBankDTO [requestId=" + requestId + ", otpValue=" + otpValue
                + ", applicationType=" + applicationType + "]";
    }
}
