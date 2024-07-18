package com.vietqr.org.dto;

public class ListAccountBankDTO {
    private String id;
    private String bankAccount;
    private String bankAccountName;
    private String bankTypeId;
    private boolean isAuthenticated;
    private boolean isSync;
    private boolean isWpSync;
    private boolean mmsActive;
    private String nationalId;
    private String phoneAuthenticated;
    private boolean status;
    private String userId;
    private boolean isRpaSync;
    private int type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getBankTypeId() {
        return bankTypeId;
    }

    public void setBankTypeId(String bankTypeId) {
        this.bankTypeId = bankTypeId;
    }

    public boolean isAuthenticated(boolean isAuthenticated) {
        return this.isAuthenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        isAuthenticated = authenticated;
    }

    public boolean isSync(boolean isSync) {
        return this.isSync;
    }

    public void setSync(boolean sync) {
        isSync = sync;
    }

    public boolean isWpSync(boolean b) {
        return isWpSync;
    }

    public void setWpSync(boolean wpSync) {
        isWpSync = wpSync;
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

    public String getPhoneAuthenticated() {
        return phoneAuthenticated;
    }

    public void setPhoneAuthenticated(String phoneAuthenticated) {
        this.phoneAuthenticated = phoneAuthenticated;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isRpaSync(boolean b) {
        return isRpaSync;
    }

    public void setRpaSync(boolean rpaSync) {
        isRpaSync = rpaSync;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public ListAccountBankDTO() {
    }

    public ListAccountBankDTO(String id, String bankAccount, String bankAccountName, String bankTypeId, boolean isAuthenticated, boolean isSync, boolean isWpSync, boolean mmsActive, String nationalId, String phoneAuthenticated, boolean status, String userId, boolean isRpaSync, int type) {
        this.id = id;
        this.bankAccount = bankAccount;
        this.bankAccountName = bankAccountName;
        this.bankTypeId = bankTypeId;
        this.isAuthenticated = isAuthenticated;
        this.isSync = isSync;
        this.isWpSync = isWpSync;
        this.mmsActive = mmsActive;
        this.nationalId = nationalId;
        this.phoneAuthenticated = phoneAuthenticated;
        this.status = status;
        this.userId = userId;
        this.isRpaSync = isRpaSync;
        this.type = type;
    }
}
