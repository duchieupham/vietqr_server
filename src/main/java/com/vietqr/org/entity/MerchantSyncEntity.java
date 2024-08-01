package com.vietqr.org.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "MerchantSync")
public class MerchantSyncEntity implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "name")
    private String name;
    @Column(name = "fullName")
    private String fullName = "";
    @Column(name = "vso")
    private String vso;
    @Column(name = "businessType")
    private String businessType;
    @Column(name = "career")
    private String career;
    @Column(name = "address")
    private String address;
    @Column(name = "nationalId")
    private String nationalId;
    @Column(name = "isActive")
    private boolean isActive;
    @Column(name = "userId")
    private String userId;
    @Column(name = "accountCustomerId")
    private String accountCustomerId;
    @Column(name = "email")
    private String email;
    @Column(name = "phoneNo")
    private String phoneNo;
    @Column(name = "publishId")
    private String publishId = "";

    @Column(name = "refId")
    private String refId = "";

    @Column(name = "isMaster")
    private boolean isMaster = false;

    @Column(name = "certificate")
    private String certificate = "";

    @Column(name = "webhook")
    private String webhook = "";

    @Column(name = "clientId")
    private String clientId = "";

    public MerchantSyncEntity() {
    }

    public MerchantSyncEntity(String id, String name, String fullName, String vso, String businessType, String career, String address, String nationalId, boolean isActive, String userId, String accountCustomerId, String email, String phoneNo, String publishId, String refId, boolean isMaster, String certificate, String webhook, String clientId) {
        this.id = id;
        this.name = name;
        this.fullName = fullName;
        this.vso = vso;
        this.businessType = businessType;
        this.career = career;
        this.address = address;
        this.nationalId = nationalId;
        this.isActive = isActive;
        this.userId = userId;
        this.accountCustomerId = accountCustomerId;
        this.email = email;
        this.phoneNo = phoneNo;
        this.publishId = publishId;
        this.refId = refId;
        this.isMaster = isMaster;
        this.certificate = certificate;
        this.webhook = webhook;
        this.clientId = clientId;
    }

    public MerchantSyncEntity(String id, String name, String vso, String businessType,
                              String career, String address, String nationalId, boolean isActive, String userId, String accountCustomerId, String email) {
        this.id = id;
        this.name = name;
        this.vso = vso;
        this.businessType = businessType;
        this.career = career;
        this.address = address;
        this.nationalId = nationalId;
        this.isActive = isActive;
        this.userId = userId;
        this.accountCustomerId = accountCustomerId;
        this.email = email;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public String getWebhook() {
        return webhook;
    }

    public void setWebhook(String webhook) {
        this.webhook = webhook;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getVso() {
        return vso;
    }

    public void setVso(String vso) {
        this.vso = vso;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getCareer() {
        return career;
    }

    public void setCareer(String career) {
        this.career = career;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAccountCustomerId() {
        return accountCustomerId;
    }

    public void setAccountCustomerId(String accountCustomerId) {
        this.accountCustomerId = accountCustomerId;
    }

    public String getPublishId() {
        return publishId;
    }

    public void setPublishId(String publishId) {
        this.publishId = publishId;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public boolean getIsMaster() {
        return isMaster;
    }

    public void setIsMaster(boolean master) {
        isMaster = master;
    }
}
