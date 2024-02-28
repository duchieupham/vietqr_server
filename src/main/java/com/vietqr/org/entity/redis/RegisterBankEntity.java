package com.vietqr.org.entity.redis;


import org.springframework.data.redis.core.RedisHash;

@RedisHash("RegisterBank")
public class RegisterBankEntity {
    private String id;
    private String bankName;
    private String bankAccount;
    private String bankAccountName;
    private String bankShortName;
    private String bankCode;
    private boolean isAuthenticated;
    private String nationId;
    private String phoneAuthenticated;
    private String userId;
    private String phoneAccount;
    private String date;
    private String timeZone;
    private long timeValue;

    public RegisterBankEntity(String id, String bankName, String bankAccount,
                              String bankAccountName, String bankShortName,
                              String bankCode, boolean isAuthenticated, String nationId,
                              String phoneAuthenticated, String userId, String phoneAccount,
                              String date, String timeZone, long timeValue) {
        this.id = id;
        this.bankName = bankName;
        this.bankAccount = bankAccount;
        this.bankAccountName = bankAccountName;
        this.bankShortName = bankShortName;
        this.bankCode = bankCode;
        this.isAuthenticated = isAuthenticated;
        this.nationId = nationId;
        this.phoneAuthenticated = phoneAuthenticated;
        this.userId = userId;
        this.phoneAccount = phoneAccount;
        this.date = date;
        this.timeZone = timeZone;
        this.timeValue = timeValue;
    }

    public RegisterBankEntity() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public boolean getIsAuthenticated() {
        return isAuthenticated;
    }

    public void setIsAuthenticated(boolean isAuthenticated) {
        this.isAuthenticated = isAuthenticated;
    }

    public String getNationId() {
        return nationId;
    }

    public void setNationId(String nationId) {
        this.nationId = nationId;
    }

    public String getPhoneAuthenticated() {
        return phoneAuthenticated;
    }

    public void setPhoneAuthenticated(String phoneAuthenticated) {
        this.phoneAuthenticated = phoneAuthenticated;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhoneAccount() {
        return phoneAccount;
    }

    public void setPhoneAccount(String phoneAccount) {
        this.phoneAccount = phoneAccount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public long getTimeValue() {
        return timeValue;
    }

    public void setTimeValue(long timeValue) {
        this.timeValue = timeValue;
    }
}
