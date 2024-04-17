package com.vietqr.org.dto;

import java.io.Serializable;

public class RefundRequestDTO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String bankAccount;
    private String referenceNumber;
    private String amount;
    private String content;
    private String checkSum;

    public RefundRequestDTO() {
        super();
    }

    public RefundRequestDTO(String bankAccount, String referenceNumber, String amount, String content,
            String checkSum) {
        this.bankAccount = bankAccount;
        this.referenceNumber = referenceNumber;
        this.amount = amount;
        this.content = content;
        this.checkSum = checkSum;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(String checkSum) {
        this.checkSum = checkSum;
    }

}
