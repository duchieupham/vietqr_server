package com.vietqr.org.dto;

public class EcommerceChangeBankAccountDTO {
    private String bankAccount;
    private String bankCode;
    private String ecommerceSite;

    public EcommerceChangeBankAccountDTO() {
    }

    public EcommerceChangeBankAccountDTO(String bankAccount, String bankCode, String ecommerceSite) {
        this.bankAccount = bankAccount;
        this.bankCode = bankCode;
        this.ecommerceSite = ecommerceSite;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getEcommerceSite() {
        return ecommerceSite;
    }

    public void setEcommerceSite(String ecommerceSite) {
        this.ecommerceSite = ecommerceSite;
    }
}
