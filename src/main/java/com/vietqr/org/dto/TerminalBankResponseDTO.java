package com.vietqr.org.dto;

public class TerminalBankResponseDTO {

    private String terminalId;
    private String bankId;
    private String bankName;
    private String bankCode;
    private String bankAccount;
    private String userBankName;
    private String bankShortName;
    private String imgId;
    private String qrCode;

    public TerminalBankResponseDTO() {
    }

    public TerminalBankResponseDTO(String terminalId, String bankId, String bankName, String bankCode,
                                   String bankAccount, String userBankName, String bankShortName, String imgId,
                                   String qrCode) {
        this.terminalId = terminalId;
        this.bankId = bankId;
        this.bankName = bankName;
        this.bankCode = bankCode;
        this.bankAccount = bankAccount;
        this.userBankName = userBankName;
        this.bankShortName = bankShortName;
        this.imgId = imgId;
        this.qrCode = qrCode;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
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

    public String getImgId() {
        return imgId;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }
}
