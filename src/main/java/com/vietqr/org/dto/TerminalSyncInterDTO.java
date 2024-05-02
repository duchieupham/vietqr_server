package com.vietqr.org.dto;

public class TerminalSyncInterDTO {
    private String boxAddress;
    private String boxCode;
    private String bankCode;
    private String bankAccount;
    private String checkSum;

    public TerminalSyncInterDTO() {
    }

    public TerminalSyncInterDTO(String boxAddress, String boxCode, String bankCode,
                                String bankAccount, String checkSum) {
        this.boxAddress = boxAddress;
        this.boxCode = boxCode;
        this.bankCode = bankCode;
        this.bankAccount = bankAccount;
        this.checkSum = checkSum;
    }

    public String getBoxAddress() {
        return boxAddress;
    }

    public void setBoxAddress(String boxAddress) {
        this.boxAddress = boxAddress;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
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

    public String getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(String checkSum) {
        this.checkSum = checkSum;
    }
}
