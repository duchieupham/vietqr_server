package com.vietqr.org.dto;

public class ResponseSyncTidDTO {
    private String qrCode;
    private String tid;
    private String terminalCode;
    private String terminalName;
    private String bankCode;
    private String bankAccount;
    private String merchantId;

    public ResponseSyncTidDTO() {
    }

    public ResponseSyncTidDTO(String qrCode, String tid, String terminalCode, String terminalName, String bankCode, String bankAccount, String merchantId) {
        this.qrCode = qrCode;
        this.tid = tid;
        this.terminalCode = terminalCode;
        this.terminalName = terminalName;
        this.bankCode = bankCode;
        this.bankAccount = bankAccount;
        this.merchantId = merchantId;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
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

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }
}
