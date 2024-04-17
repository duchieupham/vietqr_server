package com.vietqr.org.dto;

import java.io.Serializable;

public class AccountBankFeeDateUpdateDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    // type == 0 => update start date
    // type == 1 => update end date
    private int type;
    private String date;
    private String accountBankFeeId;

    public AccountBankFeeDateUpdateDTO() {
        super();
    }

    public AccountBankFeeDateUpdateDTO(int type, String date, String accountBankFeeId) {
        this.type = type;
        this.date = date;
        this.accountBankFeeId = accountBankFeeId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAccountBankFeeId() {
        return accountBankFeeId;
    }

    public void setAccountBankFeeId(String accountBankFeeId) {
        this.accountBankFeeId = accountBankFeeId;
    }

}
