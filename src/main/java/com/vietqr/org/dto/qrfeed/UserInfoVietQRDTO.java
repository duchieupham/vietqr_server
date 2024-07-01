package com.vietqr.org.dto.qrfeed;

public class UserInfoVietQRDTO {
    private String bankCode;
    private String bankAccount;
    private String userBankName;

    public UserInfoVietQRDTO(String bankCode, String bankAccount, String userBankName) {
        this.bankCode = bankCode;
        this.bankAccount = bankAccount;
        this.userBankName = userBankName;
    }

    public UserInfoVietQRDTO() {
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
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
}
