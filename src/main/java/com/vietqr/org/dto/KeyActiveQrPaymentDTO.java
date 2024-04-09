package com.vietqr.org.dto;

import javax.validation.constraints.NotBlank;

public class KeyActiveQrPaymentDTO {

    private int type;
    @NotBlank
    private String userId;
    @NotBlank
    private String bankId;
    @NotBlank
    private String password;

    @NotBlank
    private String feeId;

    public KeyActiveQrPaymentDTO() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFeeId() {
        return feeId;
    }

    public void setFeeId(String feeId) {
        this.feeId = feeId;
    }
}
