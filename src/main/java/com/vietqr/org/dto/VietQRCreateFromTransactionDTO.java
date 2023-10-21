package com.vietqr.org.dto;

import java.io.Serializable;

public class VietQRCreateFromTransactionDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String bankId;
    private String amount;
    private String content;
    private String userId;
    private String terminalCode;
    private String transactionId;

    // newTransaction = true => create new Transaction and generate QR
    // newTransaction = false => generate QR
    private boolean newTransaction;

    public VietQRCreateFromTransactionDTO() {
        super();
    }

    public VietQRCreateFromTransactionDTO(String bankId, String amount, String content, String userId,
            boolean newTransaction) {
        this.bankId = bankId;
        this.amount = amount;
        this.content = content;
        this.userId = userId;
        this.newTransaction = newTransaction;
    }

    public VietQRCreateFromTransactionDTO(String bankId, String amount, String content, String userId,
            boolean newTransaction, String terminalCode, String transactionId) {
        this.bankId = bankId;
        this.amount = amount;
        this.content = content;
        this.userId = userId;
        this.newTransaction = newTransaction;
        this.terminalCode = terminalCode;
        this.transactionId = transactionId;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isNewTransaction() {
        return newTransaction;
    }

    public void setNewTransaction(boolean newTransaction) {
        this.newTransaction = newTransaction;
    }

    public String getTerminalCode() {
        return terminalCode;
    }

    public void setTerminalCode(String terminalCode) {
        this.terminalCode = terminalCode;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

}
