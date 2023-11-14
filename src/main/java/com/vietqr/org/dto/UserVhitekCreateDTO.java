package com.vietqr.org.dto;

import java.io.Serializable;

public class UserVhitekCreateDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String userId;
    private String email;
    private String phoneNo;
    private String password;
    private String name;

    public UserVhitekCreateDTO() {
        super();
    }

    public UserVhitekCreateDTO(String userId, String email, String phoneNo, String password, String name) {
        this.userId = userId;
        this.email = email;
        this.phoneNo = phoneNo;
        this.password = password;
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
