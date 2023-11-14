package com.vietqr.org.dto;

import java.io.Serializable;

public class TerminalActiveVhitekDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String userId;
    private String mid;
    private String address;
    private String userIdVhitek;

    public TerminalActiveVhitekDTO() {
        super();
    }

    public TerminalActiveVhitekDTO(String userId, String mid, String address, String userIdVhitek) {
        this.userId = userId;
        this.mid = mid;
        this.address = address;
        this.userIdVhitek = userIdVhitek;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUserIdVhitek() {
        return userIdVhitek;
    }

    public void setUserIdVhitek(String userIdVhitek) {
        this.userIdVhitek = userIdVhitek;
    }

}
