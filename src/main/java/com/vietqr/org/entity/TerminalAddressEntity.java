package com.vietqr.org.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TerminalAddress")
public class TerminalAddressEntity implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    // id of table terminal_bank, not terminalId
    @Column(name = "terminalBankId")
    private String terminalBankId;

    @Column(name = "bankId")
    private String bankId;

    @Column(name = "bankAccount")
    private String bankAccount;

    @Column(name = "cusomerSyncId")
    private String customerSyncId;

    public TerminalAddressEntity() {
        super();
    }

    public TerminalAddressEntity(String id, String terminalBankId, String bankId, String bankAccount,
            String customerSyncId) {
        this.id = id;
        this.terminalBankId = terminalBankId;
        this.bankId = bankId;
        this.bankAccount = bankAccount;
        this.customerSyncId = customerSyncId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTerminalBankId() {
        return terminalBankId;
    }

    public void setTerminalBankId(String terminalBankId) {
        this.terminalBankId = terminalBankId;
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

    public String getCustomerSyncId() {
        return customerSyncId;
    }

    public void setCustomerSyncId(String customerSyncId) {
        this.customerSyncId = customerSyncId;
    }

}
