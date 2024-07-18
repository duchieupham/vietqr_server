package com.vietqr.org.dto;

public class DataTransactionDTO {
    private String bankAccount;
    private String content;
    private long amount;
    private long time;
    private long timePaid;
    private int type;
    private int status;
    private String transType;
    private String referenceNumber;
    private String orderId;
    private String terminalCode;
    private String note;

    public DataTransactionDTO() {
    }

    public DataTransactionDTO(String bankAccount, String content, long amount, long time, long timePaid, int type, int status, String transType, String referenceNumber, String orderId, String terminalCode, String note) {
        this.bankAccount = bankAccount;
        this.content = content;
        this.amount = amount;
        this.time = time;
        this.timePaid = timePaid;
        this.type = type;
        this.status = status;
        this.transType = transType;
        this.referenceNumber = referenceNumber;
        this.orderId = orderId;
        this.terminalCode = terminalCode;
        this.note = note;
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

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTimePaid() {
        return timePaid;
    }

    public void setTimePaid(long timePaid) {
        this.timePaid = timePaid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
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
}
