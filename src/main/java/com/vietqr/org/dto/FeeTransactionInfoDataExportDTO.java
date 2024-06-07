package com.vietqr.org.dto;

public class FeeTransactionInfoDataExportDTO {
    private String transactionId;
    private long amount;
    private String bankAccount;
    private String bankName;
    private String bankShortName;
    private String bankCode;
    private String content;
    private String time;
    private String timePaid;
    private String transType;
    private String status;
    private String type;
    private String note;
    private String referenceNumber;
    private String orderId;
    private String terminalCode;
    private String terminalName;
    private String terminalAddress;
    private boolean hiddenAmount;

    public FeeTransactionInfoDataExportDTO() {
    }

    public FeeTransactionInfoDataExportDTO(String transactionId, long amount, String bankAccount, String bankName,
                                           String bankShortName, String bankCode, String content, String time, String timePaid,
                                           String transType, String status, String type, String note, String referenceNumber, String orderId, String terminalCode, String terminalName, String terminalAddress, boolean hiddenAmount) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.bankAccount = bankAccount;
        this.bankName = bankName;
        this.bankShortName = bankShortName;
        this.bankCode = bankCode;
        this.content = content;
        this.time = time;
        this.timePaid = timePaid;
        this.transType = transType;
        this.status = status;
        this.type = type;
        this.note = note;
        this.referenceNumber = referenceNumber;
        this.orderId = orderId;
        this.terminalCode = terminalCode;
        this.terminalName = terminalName;
        this.terminalAddress = terminalAddress;
        this.hiddenAmount = hiddenAmount;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTimePaid() {
        return timePaid;
    }

    public void setTimePaid(String timePaid) {
        this.timePaid = timePaid;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
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

    public String getTerminalName() {
        return terminalName;
    }

    public void setTerminalName(String terminalName) {
        this.terminalName = terminalName;
    }

    public String getTerminalAddress() {
        return terminalAddress;
    }

    public void setTerminalAddress(String terminalAddress) {
        this.terminalAddress = terminalAddress;
    }

    public boolean isHiddenAmount() {
        return hiddenAmount;
    }

    public void setHiddenAmount(boolean hiddenAmount) {
        this.hiddenAmount = hiddenAmount;
    }
}
