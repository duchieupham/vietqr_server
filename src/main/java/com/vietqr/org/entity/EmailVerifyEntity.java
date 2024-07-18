package com.vietqr.org.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "EmailVerify")
public class EmailVerifyEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "email")
    private String email;

    @Column(name = "userId")
    private String userId;

    @Column(name = "otp")
    private int otp;

    @Column(name = "isVerify")
    private boolean isVerify = false;
    @Column(name = "timeCreated")
    private long timeCreated;

    public EmailVerifyEntity() {
    }

    public EmailVerifyEntity(String id, String email, String userId, int otp, boolean isVerify, long timeCreated) {
        this.id = id;
        this.email = email;
        this.userId = userId;
        this.otp = otp;
        this.isVerify = isVerify;
        this.timeCreated = timeCreated;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getOtp() {
        return otp;
    }

    public void setOtp(int otp) {
        this.otp = otp;
    }

    public boolean isVerify() {
        return isVerify;
    }

    public void setVerify(boolean verify) {
        isVerify = verify;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }
}
