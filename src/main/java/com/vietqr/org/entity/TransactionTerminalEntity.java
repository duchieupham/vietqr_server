package com.vietqr.org.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TransactionTerminal")
public class TransactionTerminalEntity {
    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "terminalCode")
    private String terminalCode;
    @Column(name = "time")
    private long time;
    @Column(name = "amount")
    private long amount;

    public TransactionTerminalEntity() {
    }

    public TransactionTerminalEntity(String id, String terminalCode, long time, long amount) {
        this.id = id;
        this.terminalCode = terminalCode;
        this.time = time;
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTerminalCode() {
        return terminalCode;
    }

    public void setTerminalCode(String terminalCode) {
        this.terminalCode = terminalCode;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }
}
