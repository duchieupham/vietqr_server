package com.vietqr.org.dto;

import java.io.Serializable;

public class TransactionDateDTO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String bankAccount;
    private String fromDate;
    private String toDate;

    public TransactionDateDTO() {
        super();
    }

    public TransactionDateDTO(String bankAccount, String fromDate, String toDate) {
        this.bankAccount = bankAccount;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    @Override
    public String toString() {
        return "TransactionDateDTO [bankAccount=" + bankAccount + ", fromDate=" + fromDate + ", toDate=" + toDate + "]";
    }

}
