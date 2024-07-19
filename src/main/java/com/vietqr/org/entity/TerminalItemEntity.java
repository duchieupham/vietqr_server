package com.vietqr.org.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "TerminalItem")
public class TerminalItemEntity implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    // qr code luong 1
    @Column(name = "data1")
    private String data1 = "";

    // qr code luong 2
    @Column(name = "data2")
    private String data2 = "";

    // ma san pham
    @Column(name = "serviceCode")
    private String serviceCode;

    @Column(name = "amount")
    private long amount = 0;

    @Column(name = "content")
    private String content;

    @Column(name = "rawServiceCode")
    private String rawServiceCode;

    // ma code cua hang
    @Column(name = "terminalCode")
    private String terminalCode;

    @Column(name = "bankId")
    private String bankId;

    @Column(name = "bankAccount")
    private String bankAccount;

    @Column(name = "terminalId")
    private String terminalId = "";

    @Column(name = "traceTransfer")
    private String traceTransfer = "";

    public TerminalItemEntity() {
    }

    public TerminalItemEntity(String id, String data1, String data2, String serviceCode, String terminalCode,
                              String bankId, String bankAccount, String terminalId, String traceTransfer) {
        this.id = id;
        this.data1 = data1;
        this.data2 = data2;
        this.serviceCode = serviceCode;
        this.terminalCode = terminalCode;
        this.bankId = bankId;
        this.bankAccount = bankAccount;
        this.terminalId = terminalId;
        this.traceTransfer = traceTransfer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getTerminalCode() {
        return terminalCode;
    }

    public void setTerminalCode(String terminalCode) {
        this.terminalCode = terminalCode;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getTraceTransfer() {
        return traceTransfer;
    }

    public void setTraceTransfer(String traceTransfer) {
        this.traceTransfer = traceTransfer;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRawServiceCode() {
        return rawServiceCode;
    }

    public void setRawServiceCode(String rawServiceCode) {
        this.rawServiceCode = rawServiceCode;
    }
}
