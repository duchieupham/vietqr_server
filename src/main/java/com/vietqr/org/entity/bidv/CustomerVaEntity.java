package com.vietqr.org.entity.bidv;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CustomerVa")
public class CustomerVaEntity implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "merchantId")
    private String merchantId;

    @Column(name = "merchantName")
    private String merchantName;

    @Column(name = "bankId")
    private String bankId;

    @Column(name = "bankAccount")
    private String bankAccount;

    @Column(name = "userBankName")
    private String userBankName;

    // identity
    @Column(name = "nationalId")
    private String nationalId;

    // mobile
    @Column(name = "phoneAuthenticated")
    private String phoneAuthenticated;

    // merchantType: Default = 1
    @Column(name = "merchantType")
    private String merchantType;

    public CustomerVaEntity() {
        super();
    }

    public CustomerVaEntity(String id, String merchantId, String merchantName, String bankId, String bankAccount,
            String userBankName, String nationalId, String phoneAuthenticated, String merchantType) {
        this.id = id;
        this.merchantId = merchantId;
        this.merchantName = merchantName;
        this.bankId = bankId;
        this.bankAccount = bankAccount;
        this.userBankName = userBankName;
        this.nationalId = nationalId;
        this.phoneAuthenticated = phoneAuthenticated;
        this.merchantType = merchantType;
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

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
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

    public String getMerchantType() {
        return merchantType;
    }

    public void setMerchantType(String merchantType) {
        this.merchantType = merchantType;
    }

}
