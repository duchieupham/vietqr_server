package com.vietqr.org.dto;

import java.util.ArrayList;
import java.util.List;

public class TerminalDetailDTO {
    private String terminalId;
    private String terminalCode;
    private String terminalName;
    private String terminalAddress;
    private String userId;
    private String userBankName;
    private String bankShortName;
    private String bankAccount;
    private int totalSubTerminal;
    private String qrCode;
    private int ratePrevDate;
    private int totalTrans;
    private long totalAmount;
    private List<SubTerminalDTO> subTerminals;

    public TerminalDetailDTO() {
        terminalId = "";
        terminalCode = "";
        terminalName = "";
        terminalAddress = "";
        userId = "";
        userBankName = "";
        bankShortName = "";
        bankAccount = "";
        totalSubTerminal = 0;
        qrCode = "";
        ratePrevDate = 0;
        totalTrans = 0;
        totalAmount = 0;
        subTerminals = new ArrayList<>();
    }

    public int getRatePrevDate() {
        return ratePrevDate;
    }

    public void setRatePrevDate(int ratePrevDate) {
        this.ratePrevDate = ratePrevDate;
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

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public int getTotalSubTerminal() {
        return totalSubTerminal;
    }

    public void setTotalSubTerminal(int totalSubTerminal) {
        this.totalSubTerminal = totalSubTerminal;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public List<SubTerminalDTO> getSubTerminals() {
        return subTerminals;
    }

    public void setSubTerminals(List<SubTerminalDTO> subTerminals) {
        this.subTerminals = subTerminals;
    }
}
