package com.vietqr.org.dto;

public class ConfirmOtpEmailDTO {
    private String userId;
    private String otp;

    public ConfirmOtpEmailDTO() {
    }

    public ConfirmOtpEmailDTO(String userId, String otp) {
        this.userId = userId;
        this.otp = otp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
