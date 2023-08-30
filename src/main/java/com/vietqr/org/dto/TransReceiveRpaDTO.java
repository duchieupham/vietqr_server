package com.vietqr.org.dto;

import java.io.Serializable;
import java.util.List;

public class TransReceiveRpaDTO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String bankAccount;
    private String bankCode;
    private String time;
    private List<TransSyncRpaDTO> transactions;

    public TransReceiveRpaDTO() {
        super();
    }

    public TransReceiveRpaDTO(String bankAccount, String bankCode, String time, List<TransSyncRpaDTO> transactions) {
        this.bankAccount = bankAccount;
        this.bankCode = bankCode;
        this.time = time;
        this.transactions = transactions;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<TransSyncRpaDTO> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransSyncRpaDTO> transactions) {
        this.transactions = transactions;
    }

}
