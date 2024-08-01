package com.vietqr.org.dto;

public class DynamicQRBoxDTO {
    private String notificationType;
    private String transactionReceiveId;
    private String bankAccount;
    private String bankShortName;
    private String userBankName;
    private String content;
    private String amount;
    private String qrCode;
    private String qrType;

    public DynamicQRBoxDTO() {
    }

    public DynamicQRBoxDTO(String notificationType, String transactionReceiveId, String bankAccount, String bankShortName,
                           String userBankName, String content, String amount, String qrCode, String qrType) {
        this.notificationType = notificationType;
        this.transactionReceiveId = transactionReceiveId;
        this.bankAccount = bankAccount;
        this.bankShortName = bankShortName;
        this.userBankName = userBankName;
        this.content = content;
        this.amount = amount;
        this.qrCode = qrCode;
        this.qrType = qrType;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getTransactionReceiveId() {
        return transactionReceiveId;
    }

    public void setTransactionReceiveId(String transactionReceiveId) {
        this.transactionReceiveId = transactionReceiveId;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getQrType() {
        return qrType;
    }

    public void setQrType(String qrType) {
        this.qrType = qrType;
    }
}
