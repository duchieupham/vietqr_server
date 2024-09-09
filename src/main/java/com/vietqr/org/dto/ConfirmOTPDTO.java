package com.vietqr.org.dto;

public class ConfirmOTPDTO {
    private String phoneNo;
    private String otp;

    public ConfirmOTPDTO() {
    }

    public ConfirmOTPDTO(String phoneNo, String otp) {
        this.phoneNo = phoneNo;
        this.otp = otp;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
