package com.vietqr.org.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "Terminal")
public class TerminalEntity implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "name")
    private String name;
    @Column(name = "code")
    private String code;
    @Column(name = "rawTerminalCode")
    private String rawTerminalCode;
    @Column(name = "address")
    private String address;
    @Column(name = "isDefault")
    private boolean isDefault;
    @Column(name = "timeCreated")
    private long timeCreated;
    @Column(name = "merchantId")
    private String merchantId;
    @Column(name = "userId")
    private String userId;
    @Column(name = "publicId")
    private String publicId;

    public TerminalEntity() {
    }

    public TerminalEntity(String id, String name, String code, String address, boolean isDefault,
                          int timeCreated, String merchantId, String userId) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.address = address;
        this.isDefault = isDefault;
        this.timeCreated = timeCreated;
        this.merchantId = merchantId;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPublicId() {
        return publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    public String getRawTerminalCode() {
        return rawTerminalCode;
    }

    public void setRawTerminalCode(String rawTerminalCode) {
        this.rawTerminalCode = rawTerminalCode;
    }
}
