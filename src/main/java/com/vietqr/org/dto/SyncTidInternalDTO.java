package com.vietqr.org.dto;

public class SyncTidInternalDTO {
    private String notificationType;
    private String bankAccount;
    private String bankShortName;
    private String userBankName;
    private String terminalCode;
    private String terminalName;
    private String qrCode;
    private String bankCode;
    private String imgBank;
    private String homePage;

    public SyncTidInternalDTO() {
    }

    public SyncTidInternalDTO(String notificationType, String bankAccount, String bankShortName, String userBankName,
                              String terminalCode, String terminalName, String qrCode, String bankCode, String imgBank) {
        this.notificationType = notificationType;
        this.bankAccount = bankAccount;
        this.bankShortName = bankShortName;
        this.userBankName = userBankName;
        this.terminalCode = terminalCode;
        this.terminalName = terminalName;
        this.qrCode = qrCode;
        this.bankCode = bankCode;
        this.imgBank = imgBank;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
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

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getImgBank() {
        return imgBank;
    }

    public void setImgBank(String imgBank) {
        this.imgBank = imgBank;
    }

    public String getHomePage() {
        return homePage;
    }

    public void setHomePage(String homePage) {
        this.homePage = homePage;
    }
}
