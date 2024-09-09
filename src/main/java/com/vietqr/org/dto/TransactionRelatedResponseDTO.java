package com.vietqr.org.dto;

public class TransactionRelatedResponseDTO {
    private String transactionId;
    private String amount;
    private String bankAccount;
    private String content;
    private Integer time;
    private Integer timePaid;
    private Integer status;
    private Integer type;
    private String transType;
    private String terminalCode;
    private String note;
    private String referenceNumber;
    private String orderId;
    private String bankShortName;
    private String subCode;

    public TransactionRelatedResponseDTO() {
    }

    public TransactionRelatedResponseDTO(String transactionId, String amount, String bankAccount, String content, Integer time, Integer timePaid, Integer status, Integer type, String transType, String terminalCode, String note, String referenceNumber, String orderId, String bankShortName) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.bankAccount = bankAccount;
        this.content = content;
        this.time = time;
        this.timePaid = timePaid;
        this.status = status;
        this.type = type;
        this.transType = transType;
        this.terminalCode = terminalCode;
        this.note = note;
        this.referenceNumber = referenceNumber;
        this.orderId = orderId;
        this.bankShortName = bankShortName;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public Integer getTimePaid() {
        return timePaid;
    }

    public void setTimePaid(Integer timePaid) {
        this.timePaid = timePaid;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public String getTerminalCode() {
        return terminalCode;
    }

    public void setTerminalCode(String terminalCode) {
        this.terminalCode = terminalCode;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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

    public String getBankShortName() {
        return bankShortName;
    }

    public void setBankShortName(String bankShortName) {
        this.bankShortName = bankShortName;
    }

    public String getSubCode() {
        return subCode;
    }

    public void setSubCode(String subCode) {
        this.subCode = subCode;
    }
}
