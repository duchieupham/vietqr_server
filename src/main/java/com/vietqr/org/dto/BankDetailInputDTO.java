package com.vietqr.org.dto;

import java.io.Serializable;

public class BankDetailInputDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String bankId;

    public BankDetailInputDTO() {
        super();
    }

    public BankDetailInputDTO(String bankId) {
        this.bankId = bankId;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

}
