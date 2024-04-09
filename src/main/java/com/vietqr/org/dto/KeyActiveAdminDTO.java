package com.vietqr.org.dto;

public class KeyActiveAdminDTO {
    private String key;
    private int status;
    private int duration;
    private long createAt;
    private String userId;
    private String fullName;
    private String email;
    private String phoneNo;
    private String bankId;
    private String bankAccount;
    private String bankName;
    private String userBankName;
    private String bankShortName;
    private long validFeeFrom;
    private long validFeeTo;
    private boolean isValidKey;

    public KeyActiveAdminDTO() {
    }

    public KeyActiveAdminDTO(String key, int status, int duration, long createAt,
                             String bankId, String bankAccount, String bankName,
                             String userBankName, String bankShortName,
                             long validFeeFrom, long validFeeTo) {
        this.key = key;
        this.status = status;
        this.duration = duration;
        this.createAt = createAt;
        this.bankId = bankId;
        this.bankAccount = bankAccount;
        this.bankName = bankName;
        this.userBankName = userBankName;
        this.bankShortName = bankShortName;
        this.validFeeFrom = validFeeFrom;
        this.validFeeTo = validFeeTo;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
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

    public long getValidFeeFrom() {
        return validFeeFrom;
    }

    public void setValidFeeFrom(long validFeeFrom) {
        this.validFeeFrom = validFeeFrom;
    }

    public long getValidFeeTo() {
        return validFeeTo;
    }

    public void setValidFeeTo(long validFeeTo) {
        this.validFeeTo = validFeeTo;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public boolean isValidKey() {
        return isValidKey;
    }

    public void setValidKey(boolean validKey) {
        isValidKey = validKey;
    }
}
