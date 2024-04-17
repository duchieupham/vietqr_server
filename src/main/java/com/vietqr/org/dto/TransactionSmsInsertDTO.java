package com.vietqr.org.dto;

import java.io.Serializable;

public class TransactionSmsInsertDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String bankAccount;
    private String bankId;
    private String content;
    private String address;
    private Long amount;
    private Long accountBalance;
    private Long time;
    private String transType;
    private String referenceNumber;
    private String smsId;

    public TransactionSmsInsertDTO() {
        super();
    }

    public TransactionSmsInsertDTO(String bankAccount, String bankId, String content, String address, Long amount,
            Long accountBalance, Long time, String transType, String referenceNumber, String smsId) {
        this.bankAccount = bankAccount;
        this.bankId = bankId;
        this.content = content;
        this.address = address;
        this.amount = amount;
        this.accountBalance = accountBalance;
        this.time = time;
        this.transType = transType;
        this.referenceNumber = referenceNumber;
        this.smsId = smsId;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Long getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(Long accountBalance) {
        this.accountBalance = accountBalance;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getSmsId() {
        return smsId;
    }

    public void setSmsId(String smsId) {
        this.smsId = smsId;
    }

}
