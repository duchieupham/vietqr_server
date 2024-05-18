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

    public MerchantSyncEntity() {
    }

    public MerchantSyncEntity(String id, String name, String vso, String businessType,
                              String career, String address, String nationalId, boolean isActive, String userId, String accountCustomerId) {
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
}
