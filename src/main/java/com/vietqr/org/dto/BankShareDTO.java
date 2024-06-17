package com.vietqr.org.dto;

public class BankShareDTO {
    private String bankAccount;
    private String bankAccountName;
    private boolean status;
    private boolean mmsActive;
    private String phoneAuthenticated;
    private String nationalId;
    private String bankShortName;
    private int fromDate;
    private int toDate;
    private int activeService;

    public BankShareDTO() {
    }

    public BankShareDTO(String bankAccount, String bankAccountName, boolean status, boolean mmsActive, String phoneAuthenticated, String nationalId, String bankShortName, int fromDate, int toDate, int activeService) {
        this.bankAccount = bankAccount;
        this.bankAccountName = bankAccountName;
        this.status = status;
        this.mmsActive = mmsActive;
        this.phoneAuthenticated = phoneAuthenticated;
        this.nationalId = nationalId;
        this.bankShortName = bankShortName;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.activeService = activeService;
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

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean isMmsActive() {
        return mmsActive;
    }

    public void setMmsActive(boolean mmsActive) {
        this.mmsActive = mmsActive;
    }

    public String getPhoneAuthenticated() {
        return phoneAuthenticated;
    }

    public void setPhoneAuthenticated(String phoneAuthenticated) {
        this.phoneAuthenticated = phoneAuthenticated;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getBankShortName() {
        return bankShortName;
    }

    public void setBankShortName(String bankShortName) {
        this.bankShortName = bankShortName;
    }

    public int getFromDate() {
        return fromDate;
    }

    public void setFromDate(int fromDate) {
        this.fromDate = fromDate;
    }

    public int getToDate() {
        return toDate;
    }

    public void setToDate(int toDate) {
        this.toDate = toDate;
    }

    public int getActiveService() {
        return activeService;
    }

    public void setActiveService(int activeService) {
        this.activeService = activeService;
    }
}
