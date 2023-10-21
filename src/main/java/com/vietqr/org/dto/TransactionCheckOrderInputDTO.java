package com.vietqr.org.dto;

import java.io.Serializable;

public class TransactionCheckOrderInputDTO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String bankAccount;
    private Integer type;
    private String value;
    private String checkSum;

    public TransactionCheckOrderInputDTO() {
        super();
    }

    public TransactionCheckOrderInputDTO(String bankAccount, Integer type, String value, String checkSum) {
        this.bankAccount = bankAccount;
        this.type = type;
        this.value = value;
        this.checkSum = checkSum;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(String checkSum) {
        this.checkSum = checkSum;
    }

}
