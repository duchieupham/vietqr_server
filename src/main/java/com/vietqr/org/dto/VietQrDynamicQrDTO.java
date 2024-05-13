package com.vietqr.org.dto;

public class VietQrDynamicQrDTO {
    private String bankAccount;
    private String bankName;
    private String bankShortName;
    private String userBankName;
    private String note;
    private long amount;
    private String content;
    private String imgId;
    private String qrCode;
    private String boxCode;
    private String transType;
    private String bankCode;
    private String orderId;
    private String terminalCode;
    private String terminalName;

    public VietQrDynamicQrDTO() {
    }

    public VietQrDynamicQrDTO(String bankAccount, String bankName, String bankShortName, String userBankName,
                              String note, String content, String imgId, String qrCode, String boxCode) {
        this.bankAccount = bankAccount;
        this.bankName = bankName;
        this.bankShortName = bankShortName;
        this.userBankName = userBankName;
        this.note = note;
        this.content = content;
        this.imgId = imgId;
        this.qrCode = qrCode;
        this.boxCode = boxCode;
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

    public String getUserBankName() {
        return userBankName;
    }

    public void setUserBankName(String userBankName) {
        this.userBankName = userBankName;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImgId() {
        return imgId;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
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
}
