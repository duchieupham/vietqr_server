package com.vietqr.org.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PaymentFeeAccBank")
public class PaymentFeeAccBankEntity implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "bankId")
    private String bankId;

    @Column(name = "accountBankFeeId")
    private String accountBankFeeId;

    @Column(name = "month")
    private String month;

    @Column(name = "status")
    private Integer status;

    public PaymentFeeAccBankEntity() {
        super();
    }

    public PaymentFeeAccBankEntity(String id, String bankId, String accountBankFeeId, String month, Integer status) {
        this.id = id;
        this.bankId = bankId;
        this.accountBankFeeId = accountBankFeeId;
        this.month = month;
        this.status = status;
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

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getAccountBankFeeId() {
        return accountBankFeeId;
    }

    public void setAccountBankFeeId(String accountBankFeeId) {
        this.accountBankFeeId = accountBankFeeId;
    }

}
