package com.vietqr.org.dto;

import java.io.Serializable;

public class VietQRCreateCustomerDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String bankAccount;
    private Long amount;
    private String content;
    private String branchCode;
    private String bankCode;
    private String userBankName;

    public VietQRCreateCustomerDTO() {
        super();
    }

    public VietQRCreateCustomerDTO(String bankAccount, Long amount, String content, String branchCode,
            String bankCode, String userBankName, int existing) {
        this.bankAccount = bankAccount;
        this.amount = amount;
        this.content = content;
        this.branchCode = branchCode;
        this.bankCode = bankCode;
        this.userBankName = userBankName;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getUserBankName() {
        return userBankName;
    }

    public void setUserBankName(String userBankName) {
        this.userBankName = userBankName;
    }

}
