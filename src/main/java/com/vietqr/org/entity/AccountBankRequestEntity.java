package com.vietqr.org.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "AccountBankRequest")
public class AccountBankRequestEntity implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "bankAccount")
    private String bankAccount;

    @Column(name = "userBankName")
    private String userBankName;

    @Column(name = "bankCode")
    private String bankCode;

    @Column(name = "nationalId")
    private String nationalId;

    @Column(name = "phoneAuthenticated")
    private String phoneAuthenticated;

    // 0: personal
    // 1: business
    @Column(name = "requestType")
    private int requestType;

    @Column(name = "address")
    private String address;

    @Column(name = "timeCreated")
    private Long timeCreated;

    @Column(name = "userId")
    private String userId;

    @Column(name = "isSync")
    private boolean isSync;

    public AccountBankRequestEntity() {
        super();
    }

    public AccountBankRequestEntity(String id, String bankAccount, String userBankName, String bankCode,
            String nationalId, String phoneAuthenticated, int requestType, String address,
            Long timeCreated, String userId, boolean isSync) {
        this.id = id;
        this.bankAccount = bankAccount;
        this.userBankName = userBankName;
        this.bankCode = bankCode;
        this.nationalId = nationalId;
        this.phoneAuthenticated = phoneAuthenticated;
        this.requestType = requestType;
        this.address = address;
        this.timeCreated = timeCreated;
        this.userId = userId;
        this.isSync = isSync;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getUserBankName() {
        return userBankName;
    }

    public void setUserBankName(String userBankName) {
        this.userBankName = userBankName;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getPhoneAuthenticated() {
        return phoneAuthenticated;
    }

    public void setPhoneAuthenticated(String phoneAuthenticated) {
        this.phoneAuthenticated = phoneAuthenticated;
    }

    public int getRequestType() {
        return requestType;
    }

    public void setRequestType(int requestType) {
        this.requestType = requestType;
    }

    public Long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isSync() {
        return isSync;
    }

    public void setSync(boolean isSync) {
        this.isSync = isSync;
    }

}
