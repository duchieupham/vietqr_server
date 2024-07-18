package com.vietqr.org.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "AccountLogin")
public class AccountLoginEntity implements Serializable {

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

    @Column(name = "cardNfcNumber")
    private String cardNfcNumber;

    @Column(name = "time")
    private long time;

    @Column(name = "syncBitrix")
    private Boolean syncBitrix;
    @Column(name = "isVerify")
    private boolean isVerify = false;

    public AccountLoginEntity() {
        super();
    }

    public AccountLoginEntity(String id, String phoneNo, String password, boolean status, String email,
                              String cardNumber, String cardNfcNumber, Long time) {
        super();
        this.id = id;
        this.phoneNo = phoneNo;
        this.password = password;
        this.status = status;
        this.email = email;
        this.cardNumber = cardNumber;
        this.cardNfcNumber = cardNfcNumber;
        this.time = time;
    }

    public AccountLoginEntity(String id, String phoneNo, String password, boolean status, String email,
                              String cardNumber, String cardNfcNumber, long time, Boolean syncBitrix) {
        this.id = id;
        this.phoneNo = phoneNo;
        this.password = password;
        this.status = status;
        this.email = email;
        this.cardNumber = cardNumber;
        this.cardNfcNumber = cardNfcNumber;
        this.time = time;
        this.syncBitrix = syncBitrix;
    }

    public AccountLoginEntity(String id, String phoneNo, String password, boolean status, String email, String cardNumber, String cardNfcNumber, long time, Boolean syncBitrix, boolean isVerify) {
        this.id = id;
        this.phoneNo = phoneNo;
        this.password = password;
        this.status = status;
        this.email = email;
        this.cardNumber = cardNumber;
        this.cardNfcNumber = cardNfcNumber;
        this.time = time;
        this.syncBitrix = syncBitrix;
        this.isVerify = isVerify;
    }

    public boolean isVerify() {
        return isVerify;
    }

    public void setIsVerify(boolean verify) {
        isVerify = verify;
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

    public String getCardNfcNumber() {
        return cardNfcNumber;
    }

    public void setCardNfcNumber(String cardNfcNumber) {
        this.cardNfcNumber = cardNfcNumber;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Boolean getSyncBitrix() {
        return syncBitrix;
    }

    public void setSyncBitrix(Boolean syncBitrix) {
        this.syncBitrix = syncBitrix;
    }

}
