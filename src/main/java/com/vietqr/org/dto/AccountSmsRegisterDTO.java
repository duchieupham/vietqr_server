package com.vietqr.org.dto;

import java.io.Serializable;

public class AccountSmsRegisterDTO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String phoneNo;
    private String password;
    private String email;
    private String fullName;
    private String userIp;

    public AccountSmsRegisterDTO() {
        super();
    }

    public AccountSmsRegisterDTO(String phoneNo, String password, String email, String fullName, String userIp) {
        this.phoneNo = phoneNo;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.userIp = userIp;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserIp() {
        return userIp;
    }

    public void setUserIp(String userIp) {
        this.userIp = userIp;
    }

}
