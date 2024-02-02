package com.vietqr.org.dto;

import java.io.Serializable;

public class TransactionInputDTO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private int offset;
    private String bankId;

    private String from;

    private String to;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public TransactionInputDTO() {
        super();
    }

    public TransactionInputDTO(int offset, String bankId, String from, String to) {
        this.offset = offset;
        this.bankId = bankId;
        this.from = from;
        this.to = to;
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
