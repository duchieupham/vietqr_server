package com.vietqr.org.dto;

import java.io.Serializable;

public class TokenPluginDTO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String accessToken;

    public TokenPluginDTO() {
        super();
    }

    public TokenPluginDTO(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

}
