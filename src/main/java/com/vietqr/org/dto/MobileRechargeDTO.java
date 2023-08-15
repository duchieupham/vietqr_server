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
    // 0: VQR Unit
    // 1: VietQR Scan
    private Integer paymentMethod;

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

    public MobileRechargeDTO(String phoneNo, String userId, int rechargeType, String carrierTypeId, String otp,
            Integer paymentMethod) {
        this.phoneNo = phoneNo;
        this.userId = userId;
        this.rechargeType = rechargeType;
        this.carrierTypeId = carrierTypeId;
        this.otp = otp;
        this.paymentMethod = paymentMethod;
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

    public Integer getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(Integer paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

}
