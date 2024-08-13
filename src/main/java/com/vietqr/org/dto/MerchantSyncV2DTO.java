package com.vietqr.org.dto;

import javax.persistence.Column;
import javax.persistence.Id;

public class MerchantSyncV2DTO {

    public String id;

    public String name;

    public String fullName;

    public String vso;

    public String businessType;

    public String career;

    public String address;

    public String nationalId;

    public boolean isActive;

    public String userId;

    public String accountCustomerId;

    public String email;

    public String phoneNo;

    public String publishId;

    public String refId;

    public boolean isMaster;

    public String certificate;

    public String webhook;

    public String clientId;

    public MerchantSyncV2DTO(String id, String name, String fullName, String vso, String businessType, String career, String address, String nationalId, boolean isActive, String userId, String accountCustomerId, String email, String phoneNo, String publishId, String refId, boolean isMaster, String certificate, String webhook, String clientId) {
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
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

    public boolean isMaster() {
        return isMaster;
    }

    public void setMaster(boolean master) {
        isMaster = master;
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
}
