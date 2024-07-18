package com.vietqr.org.dto;

import javax.validation.constraints.NotNull;

public class AccountInfoRequestDTO {
    @NotNull
    private String bankCode;
    @NotNull
    private String accountNumber;
    @NotNull
    private String accountType;
    @NotNull
    private String transferType;
    @NotNull
    private String checkSum;

    public AccountInfoRequestDTO() {
    }

    public AccountInfoRequestDTO(String bankCode, String accountNumber, String accountType,
                                 String transferType, String checkSum) {
        this.bankCode = bankCode;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.transferType = transferType;
        this.checkSum = checkSum;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getTransferType() {
        return transferType;
    }

    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }

    public String getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(String checkSum) {
        this.checkSum = checkSum;
    }
}
