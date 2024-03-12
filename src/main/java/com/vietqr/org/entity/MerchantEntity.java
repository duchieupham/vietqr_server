package com.vietqr.org.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Merchant")
public class MerchantEntity {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "name")
    private String name;
    @Column(name = "address")
    private String address;
    @Column(name = "vsoCode")
    private String vsoCode;

    //0 : api service
    //1: ecommerce
    //2: base
    @Column(name = "type")
    private int type;
    @Column(name = "timeCreated")
    private long timeCreated;

    @Column(name = "timePublish")
    private long timePublish;
    @Column(name = "accountCustomerMerchantId")
    private String accountCustomerMerchantId;

    @Column(name = "publicId")
    private String publicId;

    @Column(name = "refId")
    private String refId;

    @Column(name = "isMaster")
    private boolean isMaster;

    @Column(name = "isActive")
    private boolean isActive;

    public MerchantEntity() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getVsoCode() {
        return vsoCode;
    }

    public void setVsoCode(String vsoCode) {
        this.vsoCode = vsoCode;
    }

    public long getTimePublish() {
        return timePublish;
    }

    public void setTimePublish(long timePublish) {
        this.timePublish = timePublish;
    }

    public String getAccountCustomerMerchantId() {
        return accountCustomerMerchantId;
    }

    public void setAccountCustomerMerchantId(String accountCustomerMerchantId) {
        this.accountCustomerMerchantId = accountCustomerMerchantId;
    }

    public String getPublicId() {
        return publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
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

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
