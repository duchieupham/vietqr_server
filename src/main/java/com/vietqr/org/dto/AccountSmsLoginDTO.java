package com.vietqr.org.dto;

import java.io.Serializable;

public class AccountSmsLoginDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String phoneNo;
    private String password;
    private String fcmToken;
    private String device;

    public AccountSmsLoginDTO() {
        super();
    }

    public AccountSmsLoginDTO(String phoneNo, String password, String fcmToken, String device) {
        this.phoneNo = phoneNo;
        this.password = password;
        this.fcmToken = fcmToken;
        this.device = device;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

}
