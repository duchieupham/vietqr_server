package com.vietqr.org.dto;

public class BankAccountResponseDTO {
    private String bankAccount;
    private String bankAccountName;
    private String bankShortName;
    private String phoneAuthenticated;
    private boolean mmsActive;
    private String nationalId;
    private Long validFeeFrom;
    private String phoneNo;
    private String email;
    private boolean status;

    // Constructors, getters and setters

    public BankAccountResponseDTO() {
    }

    public BankAccountResponseDTO(String bankAccount, String bankAccountName, String bankShortName,
                                  String phoneAuthenticated, boolean mmsActive, String nationalId,
                                  Long validFeeFrom, String phoneNo, String email, boolean status) {
        this.bankAccount = bankAccount;
        this.bankAccountName = bankAccountName;
        this.bankShortName = bankShortName;
        this.phoneAuthenticated = phoneAuthenticated;
        this.mmsActive = mmsActive;
        this.nationalId = nationalId;
        this.validFeeFrom = validFeeFrom;
        this.phoneNo = phoneNo;
        this.email = email;
        this.status = status;
    }

    // Getters and Setters

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

    public String getBankShortName() {
        return bankShortName;
    }

    public void setBankShortName(String bankShortName) {
        this.bankShortName = bankShortName;
    }

    public String getPhoneAuthenticated() {
        return phoneAuthenticated;
    }

    public void setPhoneAuthenticated(String phoneAuthenticated) {
        this.phoneAuthenticated = phoneAuthenticated;
    }

    public boolean isMmsActive() {
        return mmsActive;
    }

    public void setMmsActive(boolean mmsActive) {
        this.mmsActive = mmsActive;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public Long getValidFeeFrom() {
        return validFeeFrom;
    }

    public void setValidFeeFrom(Long validFeeFrom) {
        this.validFeeFrom = validFeeFrom;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
