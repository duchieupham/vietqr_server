package com.vietqr.org.dto;

import java.io.Serializable;

public class TokenPluginRequestDTO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String userId;
    private String phoneNo;
    private String hosting;

    public TokenPluginRequestDTO() {
        super();
    }

    public TokenPluginRequestDTO(String userId, String phoneNo, String hosting) {
        this.userId = userId;
        this.phoneNo = phoneNo;
        this.hosting = hosting;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getHosting() {
        return hosting;
    }

    public void setHosting(String hosting) {
        this.hosting = hosting;
    }

}
