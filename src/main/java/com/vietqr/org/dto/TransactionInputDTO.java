package com.vietqr.org.dto;

import java.io.Serializable;

public class TransactionInputDTO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private int offset;
    private String bankId;

    public TransactionInputDTO() {
        super();
    }

    public TransactionInputDTO(int offset, String bankId) {
        this.offset = offset;
        this.bankId = bankId;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

}
