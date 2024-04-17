package com.vietqr.org.dto;

public class TransactionDetailResDTO {
    private String id;
    private String amount;
    private String bankId;
    private String bankAccount;
    private String content;
    private String refId;
    private Integer status;
    private Integer time;
    private Integer timePaid;
    private Integer type;
    private String traceId;
    private String transType;
    private String bankAccountName;
    private String bankCode;
    private String bankName;
    private String imgId;
    private String referenceNumber;
    private String terminalCode;
    private String note;
    private String orderId;
    private String bankShortName;

    public TransactionDetailResDTO() {
    }

    public TransactionDetailResDTO(String id, String amount, String bankId,
                                   String bankAccount, String content, String refId,
                                   Integer status, Integer time, Integer timePaid,
                                   Integer type, String traceId, String transType,
                                   String bankAccountName, String bankCode, String bankName,
                                   String imgId, String referenceNumber, String terminalCode,
                                   String note, String orderId, String bankShortName) {
        this.id = id;
        this.amount = amount;
        this.bankId = bankId;
        this.bankAccount = bankAccount;
        this.content = content;
        this.refId = refId;
        this.status = status;
        this.time = time;
        this.timePaid = timePaid;
        this.type = type;
        this.traceId = traceId;
        this.transType = transType;
        this.bankAccountName = bankAccountName;
        this.bankCode = bankCode;
        this.bankName = bankName;
        this.imgId = imgId;
        this.referenceNumber = referenceNumber;
        this.terminalCode = terminalCode;
        this.note = note;
        this.orderId = orderId;
        this.bankShortName = bankShortName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
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

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public String getBankAccountName() {
        return bankAccountName;
    }

    public void setBankAccountName(String bankAccountName) {
        this.bankAccountName = bankAccountName;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getImgId() {
        return imgId;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
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
}
