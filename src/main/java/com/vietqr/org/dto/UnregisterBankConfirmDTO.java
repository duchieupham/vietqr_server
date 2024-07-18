package com.vietqr.org.dto;

import java.io.Serializable;

public class UnregisterBankConfirmDTO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String bankAccount;
    private String requestId;
    private String otpValue;
    private String applicationType;

    public UnregisterBankConfirmDTO() {
        super();
    }

    public UnregisterBankConfirmDTO(String bankAccount, String requestId, String otpValue, String applicationType) {
        this.bankAccount = bankAccount;
        this.requestId = requestId;
        this.otpValue = otpValue;
        this.applicationType = applicationType;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
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

}
