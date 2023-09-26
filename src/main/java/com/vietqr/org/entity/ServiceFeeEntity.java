package com.vietqr.org.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ServiceFee")
public class ServiceFeeEntity implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "shortName")
    private String shortName;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

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

    // (available display or not)
    @Column(name = "active")
    private boolean active;

    // (is sub item or not)
    @Column(name = "sub")
    private boolean sub;

    @Column(name = "refId")
    private String refId;

    @Column(name = "countingTransType")
    private Integer countingTransType;

    @Column(name = "vat")
    private Double vat;

    public ServiceFeeEntity() {
        super();
    }

    public ServiceFeeEntity(String id, String shortName, String name, String description, Long activeFee,
            Long annualFee, Integer monthlyCycle, Long transFee, Double percentFee, boolean active, boolean sub,
            String refId, Integer countingTransType, Double vat) {
        this.id = id;
        this.shortName = shortName;
        this.name = name;
        this.description = description;
        this.activeFee = activeFee;
        this.annualFee = annualFee;
        this.monthlyCycle = monthlyCycle;
        this.transFee = transFee;
        this.percentFee = percentFee;
        this.active = active;
        this.sub = sub;
        this.refId = refId;
        this.countingTransType = countingTransType;
        this.vat = vat;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isSub() {
        return sub;
    }

    public void setSub(boolean sub) {
        this.sub = sub;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
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
