package com.vietqr.org.dto;

import java.io.Serializable;

public class RequestPaymentDTO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String userId;
    private String password;
    // 0: recharge VQR
    // 1: recharge mobile
    // 2: exchange point
    private int paymentType;
    // // 0: VQR
    // // 1: VietQR
    // private Integer paymentMethod;

    public RequestPaymentDTO() {
        super();
    }

    public RequestPaymentDTO(String userId, String password, int paymentType) {
        this.userId = userId;
        this.password = password;
        this.paymentType = paymentType;
    }

    // public RequestPaymentDTO(String userId, String password, int paymentType,
    // Integer paymentMethod) {
    // this.userId = userId;
    // this.password = password;
    // this.paymentType = paymentType;
    // this.paymentMethod = paymentMethod;
    // }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(int paymentType) {
        this.paymentType = paymentType;
    }

    // public Integer getPaymentMethod() {
    // return paymentMethod;
    // }

    // public void setPaymentMethod(Integer paymentMethod) {
    // this.paymentMethod = paymentMethod;
    // }

}
