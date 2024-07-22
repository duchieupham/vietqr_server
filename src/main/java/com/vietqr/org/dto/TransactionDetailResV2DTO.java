package com.vietqr.org.dto;

public class TransactionDetailResV2DTO {
    private String id;
    private String amount;
    private String bankId;
    private String bankAccount;
    private String content;
    private Integer status;
    private Integer time;
    private Integer timePaid;
    private Integer type;
    private String transType;
    private String userBankName;
    private String imgId;
    private String referenceNumber;
    private String terminalCode;
    private String note;
    private String orderId;
    private String bankShortName;
    private String serviceCode;
    private String hashTag;
    private String qrCode;

    public TransactionDetailResV2DTO() {
    }

    public TransactionDetailResV2DTO(String id, String amount, String bankId, String bankAccount,
                                     String content, Integer status, Integer time, Integer timePaid,
                                     Integer type, String transType, String userBankName, String imgId,
                                     String referenceNumber, String terminalCode, String note, String orderId,
                                     String bankShortName, String serviceCode, String hashTag, String qrCode) {
        this.id = id;
        this.amount = amount;
        this.bankId = bankId;
        this.bankAccount = bankAccount;
        this.content = content;
        this.status = status;
        this.time = time;
        this.timePaid = timePaid;
        this.type = type;
        this.transType = transType;
        this.userBankName = userBankName;
        this.imgId = imgId;
        this.referenceNumber = referenceNumber;
        this.terminalCode = terminalCode;
        this.note = note;
        this.orderId = orderId;
        this.bankShortName = bankShortName;
        this.serviceCode = serviceCode;
        this.hashTag = hashTag;
        this.qrCode = qrCode;
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

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public String getUserBankName() {
        return userBankName;
    }

    public void setUserBankName(String userBankName) {
        this.userBankName = userBankName;
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

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getHashTag() {
        return hashTag;
    }

    public void setHashTag(String hashTag) {
        this.hashTag = hashTag;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }
}
