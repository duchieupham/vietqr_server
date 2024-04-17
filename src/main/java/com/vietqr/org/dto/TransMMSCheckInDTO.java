package com.vietqr.org.dto;

import java.io.Serializable;

public class TransMMSCheckInDTO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    // - orderId (referenceLabelCode)
    // - referenceNumber (ftCode)
    // - checkSum (2 value + accessKey + BLC_SAB)
    private String bankAccount;
    private String orderId;
    private String referenceNumber;
    private String checkSum;

    public TransMMSCheckInDTO() {
        super();
    }

    public TransMMSCheckInDTO(String bankAccount, String orderId, String referenceNumber, String checkSum) {
        this.bankAccount = bankAccount;
        this.orderId = orderId;
        this.referenceNumber = referenceNumber;
        this.checkSum = checkSum;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(String checkSum) {
        this.checkSum = checkSum;
    }

}
