package com.vietqr.org.dto;

import java.io.Serializable;

public class AccountPushLoginDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String loginId;
    private String userId;
    private String randomKey;

    public AccountPushLoginDTO() {
        super();
    }

    public AccountPushLoginDTO(String loginId, String userId, String randomKey) {
        this.loginId = loginId;
        this.userId = userId;
        this.randomKey = randomKey;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRandomKey() {
        return randomKey;
    }

    public void setRandomKey(String randomKey) {
        this.randomKey = randomKey;
    }

    @Override
    public String toString() {
        return "AccountPushLoginDTO [loginId=" + loginId + ", userId=" + userId + ", randomKey=" + randomKey + "]";
    }

}
