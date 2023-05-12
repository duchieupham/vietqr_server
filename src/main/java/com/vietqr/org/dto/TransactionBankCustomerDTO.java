package com.vietqr.org.dto;

import java.io.Serializable;

public class TransactionBankCustomerDTO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String transactionid;
    private long transactiontime;
    private String referencenumber;
    private int amount;
    private String content;
    private String bankaccount;
    private String transType;
    private String reciprocalAccount;
    private String reciprocalBankCode;
    private String va;
    private long valueDate;
    private String sign;
    private String orderId;

    public TransactionBankCustomerDTO() {
        super();
    }

    public TransactionBankCustomerDTO(String transactionid, int transactiontime, String referencenumber, int amount,
            String content, String bankaccount, String transType, String reciprocalAccount, String reciprocalBankCode,
            String va, int valueDate, String sign, String orderId) {
        super();
        this.transactionid = transactionid;
        this.transactiontime = transactiontime;
        this.referencenumber = referencenumber;
        this.amount = amount;
        this.content = content;
        this.bankaccount = bankaccount;
        this.transType = transType;
        this.reciprocalAccount = reciprocalAccount;
        this.reciprocalBankCode = reciprocalBankCode;
        this.va = va;
        this.valueDate = valueDate;
        this.sign = sign;
        this.orderId = orderId;
    }

    public String getTransactionid() {
        return transactionid;
    }

    public void setTransactionid(String transactionid) {
        this.transactionid = transactionid;
    }

    public long getTransactiontime() {
        return transactiontime;
    }

    public void setTransactiontime(long transactiontime) {
        this.transactiontime = transactiontime;
    }

    public String getReferencenumber() {
        return referencenumber;
    }

    public void setReferencenumber(String referencenumber) {
        this.referencenumber = referencenumber;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getBankaccount() {
        return bankaccount;
    }

    public void setBankaccount(String bankaccount) {
        this.bankaccount = bankaccount;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public String getReciprocalAccount() {
        return reciprocalAccount;
    }

    public void setReciprocalAccount(String reciprocalAccount) {
        this.reciprocalAccount = reciprocalAccount;
    }

    public String getReciprocalBankCode() {
        return reciprocalBankCode;
    }

    public void setReciprocalBankCode(String reciprocalBankCode) {
        this.reciprocalBankCode = reciprocalBankCode;
    }

    public String getVa() {
        return va;
    }

    public void setVa(String va) {
        this.va = va;
    }

    public long getValueDate() {
        return valueDate;
    }

    public void setValueDate(long valueDate) {
        this.valueDate = valueDate;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

}
