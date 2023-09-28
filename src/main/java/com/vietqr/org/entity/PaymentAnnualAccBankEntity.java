package com.vietqr.org.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PaymentAnnualAccBank")
public class PaymentAnnualAccBankEntity implements Serializable {
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

    @Column(name = "totalPayment")
    private Long totalPayment;

    @Column(name = "fromDate")
    private String fromDate;

    @Column(name = "toDate")
    private String toDate;

    // 0: Unpaid
    // 1: paid
    @Column(name = "status")
    private Integer status;

    public PaymentAnnualAccBankEntity() {
        super();
    }

    public PaymentAnnualAccBankEntity(String id, String bankId, String accountBankFeeId, Long totalPayment,
            String fromDate, String toDate,
            Integer status) {
        this.id = id;
        this.bankId = bankId;
        this.accountBankFeeId = accountBankFeeId;
        this.totalPayment = totalPayment;
        this.fromDate = fromDate;
        this.toDate = toDate;
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

    public String getAccountBankFeeId() {
        return accountBankFeeId;
    }

    public void setAccountBankFeeId(String accountBankFeeId) {
        this.accountBankFeeId = accountBankFeeId;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getTotalPayment() {
        return totalPayment;
    }

    public void setTotalPayment(Long totalPayment) {
        this.totalPayment = totalPayment;
    }

}
