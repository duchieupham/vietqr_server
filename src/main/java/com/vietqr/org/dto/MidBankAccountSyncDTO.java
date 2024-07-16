package com.vietqr.org.dto;

import java.util.Objects;

public class MidBankAccountSyncDTO {
    private String mid;
    private String bankAccount;
    private String bankCode;
    private String bankId;

    public MidBankAccountSyncDTO() {
    }

    public MidBankAccountSyncDTO(String mid, String bankAccount, String bankCode, String bankId) {
        this.mid = mid;
        this.bankAccount = bankAccount;
        this.bankCode = bankCode;
        this.bankId = bankId;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
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

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MidBankAccountSyncDTO that = (MidBankAccountSyncDTO) o;
        return Objects.equals(bankAccount, that.bankAccount) && Objects.equals(bankCode, that.bankCode)
                && Objects.equals(mid, that.mid);
    }
}
