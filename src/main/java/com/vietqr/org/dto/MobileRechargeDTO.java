package com.vietqr.org.dto;

import java.io.Serializable;

public class MobileRechargeDTO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String phoneNo;
    private String userId;
    // 1: 10k
    // 2: 20k
    // 3: 50k
    // 4: 100k
    // 5: 200k
    // 6: 500k
    private int rechargeType;
    private String carrierTypeId;
    private String otp;

    public MobileRechargeDTO() {
        super();
    }

    public MobileRechargeDTO(String phoneNo, String userId, int rechargeType, String carrierTypeId, String otp) {
        this.phoneNo = phoneNo;
        this.userId = userId;
        this.rechargeType = rechargeType;
        this.carrierTypeId = carrierTypeId;
        this.otp = otp;
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

    public String getCarrierTypeId() {
        return carrierTypeId;
    }

    public void setCarrierTypeId(String carrierTypeId) {
        this.carrierTypeId = carrierTypeId;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

}
