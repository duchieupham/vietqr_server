package com.vietqr.org.dto;

public class TidSynchronizeDTO {
    private String mid;
    private String merchantName;
    private String terminalName;
    private String terminalCode;
    private String terminalAddress;
    private String bankAccount;
    private String bankCode;
    private String checkSum;

    public TidSynchronizeDTO() {
    }

    public TidSynchronizeDTO(String mid, String merchantName, String terminalName, String terminalCode,
                             String terminalAddress, String bankAccount, String bankCode) {
        this.mid = mid;
        this.merchantName = merchantName;
        this.terminalName = terminalName;
        this.terminalCode = terminalCode;
        this.terminalAddress = terminalAddress;
        this.bankAccount = bankAccount;
        this.bankCode = bankCode;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getTerminalName() {
        return terminalName;
    }

    public void setTerminalName(String terminalName) {
        this.terminalName = terminalName;
    }

    public String getTerminalCode() {
        return terminalCode;
    }

    public void setTerminalCode(String terminalCode) {
        this.terminalCode = terminalCode;
    }

    public String getTerminalAddress() {
        return terminalAddress;
    }

    public void setTerminalAddress(String terminalAddress) {
        this.terminalAddress = terminalAddress;
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

    public String getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(String checkSum) {
        this.checkSum = checkSum;
    }

    @Override
    public String toString() {
        return "TidSynchronizeDTO [mid=" + mid + ", merchantName=" + merchantName + ", terminalName=" + terminalName
                + ", terminalCode=" + terminalCode + ", terminalAddress=" + terminalAddress + ", bankAccount=" + bankAccount
                + ", bankCode=" + bankCode + ", checkSum=" + checkSum + "]";
    }
}
