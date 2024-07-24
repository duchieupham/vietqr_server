package com.vietqr.org.dto;

public class ConfirmOtpEmailDTO {
    private String userId;
    private String otp;
    private String email;

    public ConfirmOtpEmailDTO() {
    }

    public ConfirmOtpEmailDTO(String userId, String otp, String email) {
        this.userId = userId;
        this.otp = otp;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
