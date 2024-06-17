package com.vietqr.org.dto;

public class MerchantConnectionResponseDTO {
    private String id;
    private String mid;
    private String urlGetToken;
    private String urlCallback;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

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
}
