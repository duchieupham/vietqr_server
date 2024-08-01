package com.vietqr.org.dto;

public class TerminalSyncInterV2DTO {
    private String merchantId;
    private String merchantName;
    private String qrCertificate;
    private String checkSum;
    private String bankCode;
    private String bankAccount;
    private String terminalCode;
    private String terminalName;
    private String terminalAddress;

    public TerminalSyncInterV2DTO() {
    }

    public TerminalSyncInterV2DTO(String merchantId, String merchantName, String qrCertificate, String checkSum, String bankCode, String bankAccount, String terminalCode, String terminalName, String terminalAddress) {
        this.merchantId = merchantId;
        this.merchantName = merchantName;
        this.qrCertificate = qrCertificate;
        this.checkSum = checkSum;
        this.bankCode = bankCode;
        this.bankAccount = bankAccount;
        this.terminalCode = terminalCode;
        this.terminalName = terminalName;
        this.terminalAddress = terminalAddress;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getQrCertificate() {
        return qrCertificate;
    }

    public void setQrCertificate(String qrCertificate) {
        this.qrCertificate = qrCertificate;
    }

    public String getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(String checkSum) {
        this.checkSum = checkSum;
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
}
