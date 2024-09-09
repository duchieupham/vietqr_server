package com.vietqr.org.dto;

public class RequestResetPassword {
    private String email;
    private String phoneNo;

    public RequestResetPassword() {
    }

    public RequestResetPassword(String email, String phoneNo) {
        this.email = email;
        this.phoneNo = phoneNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }
}
