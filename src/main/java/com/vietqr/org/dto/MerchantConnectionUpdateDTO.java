package com.vietqr.org.dto;

public class MerchantConnectionUpdateDTO {
    private String id;
    private String urlCallback;
    private String urlGetToken;
    private String mid;
    private String password;
    private String token;
    private String username;

    public MerchantConnectionUpdateDTO() {}

    public MerchantConnectionUpdateDTO(String id, String urlCallback, String urlGetToken, String mid, String password, String token, String username) {
        this.id = id;
        this.urlCallback = urlCallback;
        this.urlGetToken = urlGetToken;
        this.mid = mid;
        this.password = password;
        this.token = token;
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrlCallback() {
        return urlCallback;
    }

    public void setUrlCallback(String urlCallback) {
        this.urlCallback = urlCallback;
    }

    public String getUrlGetToken() {
        return urlGetToken;
    }

    public void setUrlGetToken(String urlGetToken) {
        this.urlGetToken = urlGetToken;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
