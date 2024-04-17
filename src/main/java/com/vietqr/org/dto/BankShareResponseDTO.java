package com.vietqr.org.dto;

import java.util.List;

public class BankShareResponseDTO {

    private String bankId;

    private String bankName;

    private String bankAccount;

    private String userBankName;

    private String bankCode;

    private String bankShortName;

    private String imgId;

    private List<TerminalShareDTO> terminals;

    public BankShareResponseDTO() {
    }

    public BankShareResponseDTO(String bankId, String bankName, String bankAccount, String userBankName, String bankCode, String bankShortName, String imgId, List<TerminalShareDTO> terminals) {
        this.bankId = bankId;
        this.bankName = bankName;
        this.bankAccount = bankAccount;
        this.userBankName = userBankName;
        this.bankCode = bankCode;
        this.bankShortName = bankShortName;
        this.imgId = imgId;
        this.terminals = terminals;
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

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
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

    public List<TerminalShareDTO> getTerminals() {
        return terminals;
    }

    public void setTerminals(List<TerminalShareDTO> terminals) {
        this.terminals = terminals;
    }
}
