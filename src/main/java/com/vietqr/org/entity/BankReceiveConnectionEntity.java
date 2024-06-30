package com.vietqr.org.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "BankReceiveConnection")
public class BankReceiveConnectionEntity implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "bankId")
    private String bankId;

    @Column(name = "midConnectId")
    private String midConnectId;

    @Column(name = "data", columnDefinition = "JSON")
    private String data;

    @Column(name = "terminalBankId")
    private String terminalBankId;

    @Column(name = "mid")
    private String mid;

    @Column(name = "isActive")
    private boolean isActive = false;

    public BankReceiveConnectionEntity() {
    }

    public BankReceiveConnectionEntity(String id, String bankId, String midConnectId,
                                       String data, String terminalBankId) {
        this.id = id;
        this.bankId = bankId;
        this.midConnectId = midConnectId;
        this.data = data;
        this.terminalBankId = terminalBankId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getMidConnectId() {
        return midConnectId;
    }

    public void setMidConnectId(String midConnectId) {
        this.midConnectId = midConnectId;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTerminalBankId() {
        return terminalBankId;
    }

    public void setTerminalBankId(String terminalBankId) {
        this.terminalBankId = terminalBankId;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
