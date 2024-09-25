package com.vietqr.org.dto;

public class TransactionSyncDTO {
    private String transactionId;
    private String transactionTime;
    private String referenceNumber;
    private String amount;
    private String content;
    private String bankAccount;
    private String transType;
    private String sign;
    private String terminalCode;
    private String urlLink;
    private String subTerminalCode;
    private String serviceCode;

    public TransactionSyncDTO() {
    }

    public TransactionSyncDTO(String transactionId, String transactionTime, String referenceNumber, String amount, String content, String bankAccount, String transType, String sign, String terminalCode, String urlLink, String subTerminalCode, String serviceCode) {
        this.transactionId = transactionId;
        this.transactionTime = transactionTime;
        this.referenceNumber = referenceNumber;
        this.amount = amount;
        this.content = content;
        this.bankAccount = bankAccount;
        this.transType = transType;
        this.sign = sign;
        this.terminalCode = terminalCode;
        this.urlLink = urlLink;
        this.subTerminalCode = subTerminalCode;
        this.serviceCode = serviceCode;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(String transactionTime) {
        this.transactionTime = transactionTime;
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

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getTerminalCode() {
        return terminalCode;
    }

    public void setTerminalCode(String terminalCode) {
        this.terminalCode = terminalCode;
    }

    public String getUrlLink() {
        return urlLink;
    }

    public void setUrlLink(String urlLink) {
        this.urlLink = urlLink;
    }

    public String getSubTerminalCode() {
        return subTerminalCode;
    }

    public void setSubTerminalCode(String subTerminalCode) {
        this.subTerminalCode = subTerminalCode;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }
}
