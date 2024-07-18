package com.vietqr.org.entity.peripheral;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Peripheral")
public class PeripheralEntity implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "bankAccount")
    private String bankAccount;

    @Column(name = "bankCode")
    private String bankCode;

    @Column(name = "terminalCode")
    private String terminalCode;

    @Column(name = "terminalCodeEncrypted")
    private String terminalCodeEncrypted;

    @Column(name = "data1")
    private String data1;

    @Column(name = "data2")
    private String data2;

    public PeripheralEntity() {
        super();
    }

    public PeripheralEntity(String id, String bankAccount, String bankCode, String terminalCode,
            String terminalCodeEncrypted, String data1, String data2) {
        this.id = id;
        this.bankAccount = bankAccount;
        this.bankCode = bankCode;
        this.terminalCode = terminalCode;
        this.terminalCodeEncrypted = terminalCodeEncrypted;
        this.data1 = data1;
        this.data2 = data2;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getTerminalCode() {
        return terminalCode;
    }

    public void setTerminalCode(String terminalCode) {
        this.terminalCode = terminalCode;
    }

    public String getTerminalCodeEncrypted() {
        return terminalCodeEncrypted;
    }

    public void setTerminalCodeEncrypted(String terminalCodeEncrypted) {
        this.terminalCodeEncrypted = terminalCodeEncrypted;
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

}
