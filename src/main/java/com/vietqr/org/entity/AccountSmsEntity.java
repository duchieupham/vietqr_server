package com.vietqr.org.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "AccountSms")
public class AccountSmsEntity implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "phoneNo")
    private String phoneNo;

    @Column(name = "password")
    private String password;

    @Column(name = "status")
    private boolean status;

    @Column(name = "email")
    private String email;

    @Column(name = "cardNumber")
    private String cardNumber;

    @Column(name = "fullName")
    private String fullName;

    @Column(name = "imgId")
    private String imgId;

    @Column(name = "carrierTypeId")
    private String carrierTypeId;

    @Column(name = "userIp")
    private String userIp;

    @Column(name = "accessCount")
    private long accessCount;

    @Column(name = "lastLogin")
    private long lastLogin;

    @Column(name = "time")
    private long time;

    @Column(name = "voiceSms")
    private boolean voiceSms;

    public AccountSmsEntity() {
        super();
    }

    public AccountSmsEntity(String id, String phoneNo, String password, boolean status, String email, String cardNumber,
            String fullName, String imgId, String carrierTypeId, String userIp, long accessCount, long lastLogin,
            long time, boolean voiceSms) {
        this.id = id;
        this.phoneNo = phoneNo;
        this.password = password;
        this.status = status;
        this.email = email;
        this.cardNumber = cardNumber;
        this.fullName = fullName;
        this.imgId = imgId;
        this.carrierTypeId = carrierTypeId;
        this.userIp = userIp;
        this.accessCount = accessCount;
        this.lastLogin = lastLogin;
        this.time = time;
        this.voiceSms = voiceSms;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getImgId() {
        return imgId;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }

    public String getCarrierTypeId() {
        return carrierTypeId;
    }

    public void setCarrierTypeId(String carrierTypeId) {
        this.carrierTypeId = carrierTypeId;
    }

    public String getUserIp() {
        return userIp;
    }

    public void setUserIp(String userIp) {
        this.userIp = userIp;
    }

    public long getAccessCount() {
        return accessCount;
    }

    public void setAccessCount(long accessCount) {
        this.accessCount = accessCount;
    }

    public long getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(long lastLogin) {
        this.lastLogin = lastLogin;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isVoiceSms() {
        return voiceSms;
    }

    public void setVoiceSms(boolean voiceSms) {
        this.voiceSms = voiceSms;
    }

}
