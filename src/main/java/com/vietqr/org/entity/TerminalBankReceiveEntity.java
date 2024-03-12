package com.vietqr.org.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TerminalBankReceive")
public class TerminalBankReceiveEntity {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "terminalId")
    private String terminalId;

    @Column(name = "bankId")
    private String bankId;

    // luong thuong
    @Column(name = "data1")
    private String data1;

    //luong uu tien
    @Column(name = "data2")
    private String data2;

    @Column(name = "traceTransfer")
    private String traceTransfer;


    public TerminalBankReceiveEntity(String id, String terminalId, String bankId, String data1, String data2) {
        this.id = id;
        this.terminalId = terminalId;
        this.bankId = bankId;
        this.data1 = data1;
        this.data2 = data2;
    }

    public String getTraceTransfer() {
        return traceTransfer;
    }

    public void setTraceTransfer(String traceTransfer) {
        this.traceTransfer = traceTransfer;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getData1() {
        return data1;
    }

    public void setData1(String data1) {
        this.data1 = data1;
    }

    public String getData2() {
        return data2;
    }

    public void setData2(String data2) {
        this.data2 = data2;
    }

    public TerminalBankReceiveEntity() {
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
