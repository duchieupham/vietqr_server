package com.vietqr.org.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "FcmTokenSms")
public class FcmTokenSmsEntity implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "token")
    private String token;

    @Column(name = "smsId")
    private String smsId;

    @Column(name = "device")
    private String device;

    public FcmTokenSmsEntity() {
        super();
    }

    public FcmTokenSmsEntity(String id, String token, String smsId, String device) {
        this.id = id;
        this.token = token;
        this.smsId = smsId;
        this.device = device;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSmsId() {
        return smsId;
    }

    public void setSmsId(String smsId) {
        this.smsId = smsId;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

}
