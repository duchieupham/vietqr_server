package com.vietqr.org.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TransactionMobileRecharge")
public class TransactionMobileRechargeEntity implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "transWalletId")
    private String transWalletId;

    @Column(name = "phoneNo")
    private String phoneNo;

    @Column(name = "fullName")
    private String fullName;

    @Column(name = "carrierTypeId")
    private String carrierTypeId;

    public TransactionMobileRechargeEntity() {
        super();
    }

    public TransactionMobileRechargeEntity(String id, String transWalletId, String phoneNo, String fullName,
            String carrierTypeId) {
        this.id = id;
        this.transWalletId = transWalletId;
        this.phoneNo = phoneNo;
        this.fullName = fullName;
        this.carrierTypeId = carrierTypeId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTransWalletId() {
        return transWalletId;
    }

    public void setTransWalletId(String transWalletId) {
        this.transWalletId = transWalletId;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCarrierTypeId() {
        return carrierTypeId;
    }

    public void setCarrierTypeId(String carrierTypeId) {
        this.carrierTypeId = carrierTypeId;
    }

}
