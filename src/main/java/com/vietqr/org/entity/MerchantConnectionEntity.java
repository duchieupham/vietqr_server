package com.vietqr.org.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "MerchantConnection")
public class MerchantConnectionEntity implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "mid")
    private String mid;
    @Column(name = "urlGetToken")
    private String urlGetToken;
    @Column(name = "urlCallback")
    private String urlCallback;
    @Column(name = "username")
    private String username;
    @Column(name = "password")
    private String password;
    @Column(name = "token")
    private String token;
    @Column(name = "type")
    private int type;

    public MerchantConnectionEntity(String id, String mid, String urlGetToken, String urlCallback) {
        this.id = id;
        this.mid = mid;
        this.urlGetToken = urlGetToken;
        this.urlCallback = urlCallback;
    }

    public MerchantConnectionEntity() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String merchantId) {
        this.mid = merchantId;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
