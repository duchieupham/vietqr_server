package com.vietqr.org.dto;

import java.util.Objects;

public class BankAccountSyncDTO {
    private String bankAccount;
    private String bankCode;

    public BankAccountSyncDTO() {
    }

    public BankAccountSyncDTO(String bankAccount, String bankCode) {
        this.bankAccount = bankAccount;
        this.bankCode = bankCode;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BankAccountSyncDTO that = (BankAccountSyncDTO) o;
        return Objects.equals(bankAccount, that.bankAccount) && Objects.equals(bankCode, that.bankCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bankAccount, bankCode);
    }
}
