package com.vietqr.org.dto;

public class BankReceiveFeePackageDTOv2 {
    private Long activeFee;
    private Long annualFee;
    private Long fixFee;
    private Double percentFee;
    private Integer recordType;

    public BankReceiveFeePackageDTOv2() {}

    public BankReceiveFeePackageDTOv2(Long activeFee, Long annualFee, Long fixFee, Double percentFee, Integer recordType) {
        this.activeFee = activeFee;
        this.annualFee = annualFee;
        this.fixFee = fixFee;
        this.percentFee = percentFee;
        this.recordType = recordType;
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

    public Long getFixFee() {
        return fixFee;
    }

    public void setFixFee(Long fixFee) {
        this.fixFee = fixFee;
    }

    public Double getPercentFee() {
        return percentFee;
    }

    public void setPercentFee(Double percentFee) {
        this.percentFee = percentFee;
    }

    public Integer getRecordType() {
        return recordType;
    }

    public void setRecordType(Integer recordType) {
        this.recordType = recordType;
    }
}
