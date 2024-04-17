package com.vietqr.org.dto;

import java.io.Serializable;

public class AccountLoginPasswordResetDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String phoneNo;
    private String password;

    public AccountLoginPasswordResetDTO() {
        super();
    }

    public AccountLoginPasswordResetDTO(String phoneNo, String password) {
        this.phoneNo = phoneNo;
        this.password = password;
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

}
