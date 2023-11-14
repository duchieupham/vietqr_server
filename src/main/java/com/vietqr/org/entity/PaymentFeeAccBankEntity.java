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

    @Column(name = "totalTrans")
    private Long totalTrans;

    @Column(name = "totalAmount")
    private Long totalAmount;

    @Column(name = "totalFee")
    private Long totalFee;

    @Column(name = "vatFee")
    private Long vatFee;

    @Column(name = "annualFee")
    private Long annualFee;

    @Column(name = "totalFeeAfterVat")
    private Long totalFeeAfterVat;

    public PaymentFeeAccBankEntity() {
        super();
    }

    public PaymentFeeAccBankEntity(String id, String bankId, String accountBankFeeId, String month, Integer status,
            Long totalTrans, Long totalAmount, Long totalFee, Long vatFee, Long annualFee, Long totalFeeAfterVat) {
        this.id = id;
        this.bankId = bankId;
        this.accountBankFeeId = accountBankFeeId;
        this.month = month;
        this.status = status;
        this.totalTrans = totalTrans;
        this.totalAmount = totalAmount;
        this.totalFee = totalFee;
        this.vatFee = vatFee;
        this.annualFee = annualFee;
        this.totalFeeAfterVat = totalFeeAfterVat;
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

    public Long getTotalTrans() {
        return totalTrans;
    }

    public void setTotalTrans(Long totalTrans) {
        this.totalTrans = totalTrans;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Long getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(Long totalFee) {
        this.totalFee = totalFee;
    }

    public Long getVatFee() {
        return vatFee;
    }

    public void setVatFee(Long vatFee) {
        this.vatFee = vatFee;
    }

    public Long getAnnualFee() {
        return annualFee;
    }

    public void setAnnualFee(Long annualFee) {
        this.annualFee = annualFee;
    }

    public Long getTotalFeeAfterVat() {
        return totalFeeAfterVat;
    }

    public void setTotalFeeAfterVat(Long totalFeeAfterVat) {
        this.totalFeeAfterVat = totalFeeAfterVat;
    }

}
