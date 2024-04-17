package com.vietqr.org.dto;

import javax.validation.constraints.NotBlank;

public class RequestActiveBankReceiveDTO {
    @NotBlank
    private String key;
    @NotBlank
    private String bankId;
    @NotBlank
    private String userId;
    @NotBlank
    private String password;

    private int type;

    public RequestActiveBankReceiveDTO() {
    }

    public RequestActiveBankReceiveDTO(String key, String bankId, String userId, String password) {
        this.key = key;
        this.bankId = bankId;
        this.userId = userId;
        this.password = password;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "RequestActiveBankReceiveDTO [key=" + key + ", bankId=" + bankId + ", userId=" + userId
                + ", password=" + password + "]";
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
