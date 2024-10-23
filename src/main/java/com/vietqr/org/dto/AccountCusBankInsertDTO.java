package com.vietqr.org.dto;

import java.io.Serializable;

public class AccountCusBankInsertDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String bankAccount;
    private String userBankName;
    private String bankCode;
    private String customerSyncId;
    private String accountCustomerId;

    public AccountCusBankInsertDTO() {
        super();
    }

    public AccountCusBankInsertDTO(String bankAccount, String userBankName, String customerSyncId,
            String accountCustomerId) {
        this.bankAccount = bankAccount;
        this.userBankName = userBankName;
        this.customerSyncId = customerSyncId;
        this.accountCustomerId = accountCustomerId;
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

    public String getCustomerSyncId() {
        return customerSyncId;
    }

    public void setCustomerSyncId(String customerSyncId) {
        this.customerSyncId = customerSyncId;
    }

    public String getAccountCustomerId() {
        return accountCustomerId;
    }

    public void setAccountCustomerId(String accountCustomerId) {
        this.accountCustomerId = accountCustomerId;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }
}
