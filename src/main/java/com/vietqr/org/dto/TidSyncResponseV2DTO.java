package com.vietqr.org.dto;

public class TidSyncResponseV2DTO {
    private String tid;
    private String terminalName;
    private String terminalCode;
    private String bankAccount;
    private String bankCode;

    public TidSyncResponseV2DTO() {
    }

    public TidSyncResponseV2DTO(String tid, String terminalName, String terminalCode, String bankAccount, String bankCode) {
        this.tid = tid;
        this.terminalName = terminalName;
        this.terminalCode = terminalCode;
        this.bankAccount = bankAccount;
        this.bankCode = bankCode;
    }


    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
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

}
