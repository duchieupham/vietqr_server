package com.vietqr.org.dto;

import java.io.Serializable;

public class TransWalletInsertDTO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String phoneNo;
    private String amount;
    // private String content;
    private String transType;

    public TransWalletInsertDTO() {
        super();
    }

    public TransWalletInsertDTO(String phoneNo, String amount, String transType,
            String billNumber) {
        this.phoneNo = phoneNo;
        this.amount = amount;
        // this.content = content;
        this.transType = transType;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    // public String getContent() {
    // return content;
    // }

    // public void setContent(String content) {
    // this.content = content;
    // }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

}
