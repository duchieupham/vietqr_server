package com.vietqr.org.dto;

import java.util.List;

public class TransactionRelatedRequestDTO {

    private String id;
    private String bankAccount;
    private long amount;
    private String bankId;
    private String content;
    private String orderId;
    private String referenceNumber;
    private int status;
    private long timeCreated;
    private long timePaid;
    private String transType;
    private int type;
    private String userBankName;
    private String bankShortName;
    private String terminalCode;
    private String note;
    private int totalRequest;
    private List<TransRequestDTO> requests;

    public TransactionRelatedRequestDTO(String id, String bankAccount, long amount, String bankId,
                                        String content, String orderId, String referenceNumber,
                                        int status, long timeCreated, long timePaid, String transType,
                                        int type, String userBankName, String bankShortName, String terminalCode,
                                        String note, List<TransRequestDTO> requests) {
        this.id = id;
        this.bankAccount = bankAccount;
        this.amount = amount;
        this.bankId = bankId;
        this.content = content;
        this.orderId = orderId;
        this.referenceNumber = referenceNumber;
        this.status = status;
        this.timeCreated = timeCreated;
        this.timePaid = timePaid;
        this.transType = transType;
        this.type = type;
        this.userBankName = userBankName;
        this.bankShortName = bankShortName;
        this.terminalCode = terminalCode;
        this.note = note;
        this.requests = requests;
    }

    public TransactionRelatedRequestDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
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

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public long getTimePaid() {
        return timePaid;
    }

    public void setTimePaid(long timePaid) {
        this.timePaid = timePaid;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUserBankName() {
        return userBankName;
    }

    public void setUserBankName(String userBankName) {
        this.userBankName = userBankName;
    }

    public String getBankShortName() {
        return bankShortName;
    }

    public void setBankShortName(String bankShortName) {
        this.bankShortName = bankShortName;
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

    public List<TransRequestDTO> getRequests() {
        return requests;
    }

    public void setRequests(List<TransRequestDTO> requests) {
        this.requests = requests;
    }

    public int getTotalRequest() {
        return totalRequest;
    }

    public void setTotalRequest(int totalRequest) {
        this.totalRequest = totalRequest;
    }
}
