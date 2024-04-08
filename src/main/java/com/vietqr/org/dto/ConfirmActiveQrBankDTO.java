package com.vietqr.org.dto;

public class ConfirmActiveQrBankDTO {
    private String otp;
    private String userId;
    private String bankId;
    private String password;
    private String request;
    private Integer paymentMethod;

    public ConfirmActiveQrBankDTO() {
    }

    public ConfirmActiveQrBankDTO(String otp, String userId, String bankId, String password, String request) {
        this.otp = otp;
        this.userId = userId;
        this.bankId = bankId;
        this.password = password;
        this.request = request;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
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

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public Integer getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(Integer paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
