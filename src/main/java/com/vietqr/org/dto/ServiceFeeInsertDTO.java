package com.vietqr.org.dto;

import java.io.Serializable;

public class ServiceFeeInsertDTO implements Serializable {

    /**
    *
    */
    private static final long serialVersionUID = 1L;

    private String shortName;
    private String name;
    private String description;
    private Long activeFee;
    private Long annualFee;
    private Integer monthlyCycle;
    private Long transFee;
    private Double percentFee;
    private boolean sub;
    private String refId;
    private Integer countingTransType;
    private Double vat;

    public ServiceFeeInsertDTO() {
        super();
    }

    public ServiceFeeInsertDTO(String shortName, String name, String description, Long activeFee, Long annualFee,
            Integer monthlyCycle, Long transFee, Double percentFee, boolean sub, String refId,
            Integer countingTransType, Double vat) {
        this.shortName = shortName;
        this.name = name;
        this.description = description;
        this.activeFee = activeFee;
        this.annualFee = annualFee;
        this.monthlyCycle = monthlyCycle;
        this.transFee = transFee;
        this.percentFee = percentFee;
        this.sub = sub;
        this.refId = refId;
        this.countingTransType = countingTransType;
        this.vat = vat;
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