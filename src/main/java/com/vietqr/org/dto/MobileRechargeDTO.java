package com.vietqr.org.dto;

import java.io.Serializable;

public class MobileRechargeDTO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String phoneNo;
    private String userId;
    private int rechargeType;
    private int carrierTypeId;

    public MobileRechargeDTO() {
        super();
    }

    public MobileRechargeDTO(String phoneNo, String userId, int rechargeType, int carrierTypeId) {
        this.phoneNo = phoneNo;
        this.userId = userId;
        this.rechargeType = rechargeType;
        this.carrierTypeId = carrierTypeId;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getRechargeType() {
        return rechargeType;
    }

    public void setRechargeType(int rechargeType) {
        this.rechargeType = rechargeType;
    }

    public int getCarrierTypeId() {
        return carrierTypeId;
    }

    public void setCarrierTypeId(int carrierTypeId) {
        this.carrierTypeId = carrierTypeId;
    }

}
