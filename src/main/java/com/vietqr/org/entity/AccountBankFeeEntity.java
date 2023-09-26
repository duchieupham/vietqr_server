package com.vietqr.org.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "AccountBankFee")
public class AccountBankFeeEntity implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "bankId")
    private String bankId;

    @Column(name = "serviceFeeId")
    private String serviceFeeId;

    @Column(name = "startDate")
    private String startDate;

    @Column(name = "endDate")
    private String endDate;

    @Column(name = "shortName")
    private String shortName;

    // fee for active service
    @Column(name = "activeFee")
    private Long activeFee;

    // (monthly fee, but collect money by 3/6/9/12 months)
    @Column(name = "annualFee")
    private Long annualFee;

    // 3/6/9/12 months
    @Column(name = "monthlyCycle")
    private Integer monthlyCycle;

    // (fee per transaction)
    @Column(name = "transFee")
    private Long transFee;

    // (fee based on total successful amount)
    @Column(name = "percentFee")
    private Double percentFee;

    @Column(name = "countingTransType")
    private Integer countingTransType;

    @Column(name = "vat")
    private Double vat;

    public AccountBankFeeEntity() {
        super();
    }

    public AccountBankFeeEntity(String id, String bankId, String serviceFeeId, String startDate, String endDate,
            String shortName, Long activeFee, Long annualFee, Integer monthlyCycle, Long transFee, Double percentFee,
            Integer countingTransType, Double vat) {
        this.id = id;
        this.bankId = bankId;
        this.serviceFeeId = serviceFeeId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.shortName = shortName;
        this.activeFee = activeFee;
        this.annualFee = annualFee;
        this.monthlyCycle = monthlyCycle;
        this.transFee = transFee;
        this.percentFee = percentFee;
        this.countingTransType = countingTransType;
        this.vat = vat;
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

    public String getServiceFeeId() {
        return serviceFeeId;
    }

    public void setServiceFeeId(String serviceFeeId) {
        this.serviceFeeId = serviceFeeId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Long getActiveFee() {
        return activeFee;
    }

    public void setActiveFee(Long activeFee) {
        this.activeFee = activeFee;
    }

    public Long getAnnualFee() {
        return annualFee;
    }

    public void setAnnualFee(Long annualFee) {
        this.annualFee = annualFee;
    }

    public Integer getMonthlyCycle() {
        return monthlyCycle;
    }

    public void setMonthlyCycle(Integer monthlyCycle) {
        this.monthlyCycle = monthlyCycle;
    }

    public Long getTransFee() {
        return transFee;
    }

    public void setTransFee(Long transFee) {
        this.transFee = transFee;
    }

    public Double getPercentFee() {
        return percentFee;
    }

    public void setPercentFee(Double percentFee) {
        this.percentFee = percentFee;
    }

    public Integer getCountingTransType() {
        return countingTransType;
    }

    public void setCountingTransType(Integer countingTransType) {
        this.countingTransType = countingTransType;
    }

    public Double getVat() {
        return vat;
    }

    public void setVat(Double vat) {
        this.vat = vat;
    }

}
