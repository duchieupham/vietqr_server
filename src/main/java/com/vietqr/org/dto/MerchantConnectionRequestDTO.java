package com.vietqr.org.dto;

import javax.validation.constraints.NotBlank;

public class MerchantConnectionRequestDTO {
    @NotBlank(message = "URL Get Token is mandatory")
    private String urlGetToken;

    @NotBlank(message = "URL Callback is mandatory")
    private String urlCallback;

    @NotBlank(message = "Username is mandatory")
    private String username;

    @NotBlank(message = "Password is mandatory")
    private String password;

    @NotBlank(message = "Bank ID is mandatory")
    private String bankId;

    // Getters and Setters
    public String getUrlGetToken() {
        return urlGetToken;
    }

    public void setUrlGetToken(String urlGetToken) {
        this.urlGetToken = urlGetToken;
    }

    public String getUrlCallback() {
        return urlCallback;
    }

    public void setUrlCallback(String urlCallback) {
        this.urlCallback = urlCallback;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }
}
