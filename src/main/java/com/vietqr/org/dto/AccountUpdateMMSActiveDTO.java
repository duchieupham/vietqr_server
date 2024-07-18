package com.vietqr.org.dto;

import javax.validation.constraints.NotBlank;

public class AccountUpdateMMSActiveDTO {
    @NotBlank
    private String bankTypeId;
    @NotBlank
    private String bankAccount;
    @NotBlank
    private String bankCode;
    @NotBlank
    private String bankAccountName;
    @NotBlank
    private String userId;
    private String bankId;
    private String address;
    private String nationalId;
    private String phoneAuthenticated;

    public AccountUpdateMMSActiveDTO() {
    }

    public AccountUpdateMMSActiveDTO(String bankTypeId, String bankAccount, String bankCode, String bankAccountName, String userId, String nationalId, String phoneAuthenticated) {
        this.bankTypeId = bankTypeId;
        this.bankAccount = bankAccount;
        this.bankCode = bankCode;
        this.bankAccountName = bankAccountName;
        this.userId = userId;
        this.nationalId = nationalId;
        this.phoneAuthenticated = phoneAuthenticated;
    }

    public AccountUpdateMMSActiveDTO(String bankTypeId, String bankAccount, String bankCode, String bankAccountName, String userId, String bankId, String address, String nationalId, String phoneAuthenticated) {
        this.bankTypeId = bankTypeId;
        this.bankAccount = bankAccount;
        this.bankCode = bankCode;
        this.bankAccountName = bankAccountName;
        this.userId = userId;
        this.bankId = bankId;
        this.address = address;
        this.nationalId = nationalId;
        this.phoneAuthenticated = phoneAuthenticated;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankTypeId() {
        return bankTypeId;
    }

    public void setBankTypeId(String bankTypeId) {
        this.bankTypeId = bankTypeId;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getBankAccountName() {
        return bankAccountName;
    }

    public void setBankAccountName(String bankAccountName) {
        this.bankAccountName = bankAccountName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getPhoneAuthenticated() {
        return phoneAuthenticated;
    }

    public void setPhoneAuthenticated(String phoneAuthenticated) {
        this.phoneAuthenticated = phoneAuthenticated;
    }
}
