package com.vietqr.org.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "BankReceiveOtp")
public class BankReceiveOtpEntity implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "expiredDate")
    private long expiredDate;

    @Column(name = "otpToken")
    private String otpToken;

    @Column(name = "userId")
    private String userId;

    @Column(name = "bankId")
    private String bankId;

    // 0: pending, 1: success, 2: expired
    @Column(name = "status")
    private int status;

    @Column(name = "keyActive")
    private String keyActive;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public long getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(long expiredDate) {
        this.expiredDate = expiredDate;
    }

    public String getOtpToken() {
        return otpToken;
    }

    public void setOtpToken(String otpToken) {
        this.otpToken = otpToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getKeyActive() {
        return keyActive;
    }

    public void setKeyActive(String keyActive) {
        this.keyActive = keyActive;
    }
}
