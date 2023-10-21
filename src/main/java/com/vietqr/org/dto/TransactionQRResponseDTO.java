package com.vietqr.org.dto;

import java.io.Serializable;

public class TransactionQRResponseDTO implements Serializable {

    /**
    *
    */
    private static final long serialVersionUID = 1L;

    private String transactionId;
    private String qr;
    private Long amount;
    private String content;
    private String transType;
    private String terminalCode;
    private String orderId;
    private String sign;
    private int type;
    private int status;
    private Long timeCreated;
    // bank account info
    private String bankTypeId;
    private String bankAccount;
    private String bankCode;
    private String bankName;
    private String bankShortName;
    private String imgId;
    private String userBankName;

    public TransactionQRResponseDTO() {
        super();
    }

    public TransactionQRResponseDTO(String transactionId, String qr, Long amount, String content, String transType,
            String terminalCode, String orderId, String sign, int type, int status, Long timeCreated, String bankTypeId,
            String bankAccount, String bankCode, String bankName, String bankShortName, String imgId,
            String userBankName) {
        this.transactionId = transactionId;
        this.qr = qr;
        this.amount = amount;
        this.content = content;
        this.transType = transType;
        this.terminalCode = terminalCode;
        this.orderId = orderId;
        this.sign = sign;
        this.type = type;
        this.status = status;
        this.timeCreated = timeCreated;
        this.bankTypeId = bankTypeId;
        this.bankAccount = bankAccount;
        this.bankCode = bankCode;
        this.bankName = bankName;
        this.bankShortName = bankShortName;
        this.imgId = imgId;
        this.userBankName = userBankName;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getQr() {
        return qr;
    }

    public void setQr(String qr) {
        this.qr = qr;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
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

    public Long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String getBankTypeId() {
        return bankTypeId;
    }

    public void setBankTypeId(String bankTypeId) {
        this.bankTypeId = bankTypeId;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
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

    public String getBankShortName() {
        return bankShortName;
    }

    public void setBankShortName(String bankShortName) {
        this.bankShortName = bankShortName;
    }

    public String getImgId() {
        return imgId;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }

    public String getUserBankName() {
        return userBankName;
    }

    public void setUserBankName(String userBankName) {
        this.userBankName = userBankName;
    }

}
