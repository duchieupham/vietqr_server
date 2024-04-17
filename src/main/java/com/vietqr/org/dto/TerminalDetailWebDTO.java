package com.vietqr.org.dto;

public class TerminalDetailWebDTO {
    private String terminalId;
    private String terminalName;
    private String terminalAddress;
    private int totalTrans;
    private long totalAmount;
    private int totalMember;
    private String terminalCode;
    private String bankName;
    private String bankAccount;
    private String bankShortName;
    private String bankAccountName;

    public TerminalDetailWebDTO(String terminalId, String terminalName, String terminalAddress, int totalTrans, long totalAmount, int totalMember, String terminalCode, String bankName, String bankAccount, String bankShortName, String bankAccountName) {
        this.terminalId = terminalId;
        this.terminalName = terminalName;
        this.terminalAddress = terminalAddress;
        this.totalTrans = totalTrans;
        this.totalAmount = totalAmount;
        this.totalMember = totalMember;
        this.terminalCode = terminalCode;
        this.bankName = bankName;
        this.bankAccount = bankAccount;
        this.bankShortName = bankShortName;
        this.bankAccountName = bankAccountName;
    }

    public TerminalDetailWebDTO() {
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
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

    public int getTotalTrans() {
        return totalTrans;
    }

    public void setTotalTrans(int totalTrans) {
        this.totalTrans = totalTrans;
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getTotalMember() {
        return totalMember;
    }

    public void setTotalMember(int totalMember) {
        this.totalMember = totalMember;
    }

    public String getTerminalCode() {
        return terminalCode;
    }

    public void setTerminalCode(String terminalCode) {
        this.terminalCode = terminalCode;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
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

    public String getBankAccountName() {
        return bankAccountName;
    }

    public void setBankAccountName(String bankAccountName) {
        this.bankAccountName = bankAccountName;
    }
}
