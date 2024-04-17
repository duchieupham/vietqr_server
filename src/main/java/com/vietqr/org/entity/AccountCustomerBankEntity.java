package com.vietqr.org.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "AccountCustomerBank")
public class AccountCustomerBankEntity implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "accountCustomerId")
    private String accountCustomerId;

    @Column(name = "bankId")
    private String bankId;

    @Column(name = "bankAccount")
    private String bankAccount;

    @Column(name = "customerSyncId")
    private String customerSyncId;

    public AccountCustomerBankEntity() {
        super();
    }

    public AccountCustomerBankEntity(String id, String accountCustomerId, String bankId, String bankAccount,
            String customerSyncId) {
        this.id = id;
        this.accountCustomerId = accountCustomerId;
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

    public String getAccountCustomerId() {
        return accountCustomerId;
    }

    public void setAccountCustomerId(String accountCustomerId) {
        this.accountCustomerId = accountCustomerId;
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
