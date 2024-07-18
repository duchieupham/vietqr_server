package com.vietqr.org.dto;

public class TransactionRelatedDetailDTO {
    private String transactionId;

    private long amount;

    private String bankAccount;

    private String bankName;

    private String bankShortName;

    private String bankCode;

    private String content;

    private long time;

    private long timePaid;

    private int status;

    private int type;

    private String note;

    private String referenceNumber;

    private String orderId;

    private String terminalCode;

    public TransactionRelatedDetailDTO() {
    }

    public TransactionRelatedDetailDTO(String transactionId, long amount, String bankAccount, String bankName, String bankShortName,
                                       String bankCode, String content, long time, long timePaid, int status, int type, String note,
                                       String referenceNumber, String orderId, String terminalCode) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.bankAccount = bankAccount;
        this.bankName = bankName;
        this.bankShortName = bankShortName;
        this.bankCode = bankCode;
        this.content = content;
        this.time = time;
        this.timePaid = timePaid;
        this.status = status;
        this.type = type;
        this.note = note;
        this.referenceNumber = referenceNumber;
        this.orderId = orderId;
        this.terminalCode = terminalCode;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankShortName() {
        return bankShortName;
    }

    public void setBankShortName(String bankShortName) {
        this.bankShortName = bankShortName;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public String getTerminalCode() {
        return terminalCode;
    }

    public void setTerminalCode(String terminalCode) {
        this.terminalCode = terminalCode;
    }
}
