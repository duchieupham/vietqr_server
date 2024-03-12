package com.vietqr.org.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "MerchantConnection")
public class MerchantConnectionEntity {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "merchantId")
    private String merchantId;

    @Column(name = "urlGetToken")
    private String urlGetToken;

    @Column(name = "urlCallback")
    private String urlCallback;

    public MerchantConnectionEntity(String id, String merchantId, String urlGetToken, String urlCallback) {
        this.id = id;
        this.merchantId = merchantId;
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

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
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
