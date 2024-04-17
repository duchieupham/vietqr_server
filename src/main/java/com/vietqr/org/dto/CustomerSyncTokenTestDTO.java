package com.vietqr.org.dto;

import java.io.Serializable;

public class CustomerSyncTokenTestDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String url;
    private String username;
    private String password;

    public CustomerSyncTokenTestDTO() {
        super();
    }

    public CustomerSyncTokenTestDTO(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

}
