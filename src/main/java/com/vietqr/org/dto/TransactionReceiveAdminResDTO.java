package com.vietqr.org.dto;

public class TransactionReceiveAdminResDTO {
    private String bankAccount;

    private long fromDate;

    private long toDate;

    public TransactionReceiveAdminResDTO() {
    }

    public TransactionReceiveAdminResDTO(String bankAccount, long fromDate, long toDate) {
        this.bankAccount = bankAccount;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public String getBankAccount() {
        return bankAccount.trim();
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public long getFromDate() {
        return fromDate;
    }

    public void setFromDate(long fromDate) {
        this.fromDate = fromDate;
    }

    public long getToDate() {
        return toDate;
    }

    public void setToDate(long toDate) {
        this.toDate = toDate;
    }
}
