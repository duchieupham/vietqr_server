package com.vietqr.org.dto;

import java.io.Serializable;

public class TransMMSCheckOutDTO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    // - ftCode (referenceNumber)
    // - orderId (referenceLabelCode)
    // - sign
    // - amount
    // - time
    // - transType
    // - status

    private String referenceNumber;
    private String orderId;
    // private String sign;
    private String amount;
    private long time;
    private String transType;
    // thanh cong, that bai, dang cho giao dich, khong khop lenh
    private int status;

    public TransMMSCheckOutDTO() {
        super();
    }

    public TransMMSCheckOutDTO(String referenceNumber, String orderId, String sign, String amount, long time,
            String transType, int status) {
        this.referenceNumber = referenceNumber;
        this.orderId = orderId;
        // this.sign = sign;
        this.amount = amount;
        this.time = time;
        this.transType = transType;
        this.status = status;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    // public String getSign() {
    // return sign;
    // }

    // public void setSign(String sign) {
    // this.sign = sign;
    // }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}
