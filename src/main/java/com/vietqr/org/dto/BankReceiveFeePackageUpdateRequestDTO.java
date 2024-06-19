package com.vietqr.org.dto;

public class BankReceiveFeePackageUpdateRequestDTO {
    private String feePackageId;
    private String title;
    private long activeFee;
    private long annualFee;
    private long fixFee;
    private double percentFee;
    private double vat;
    private int recordType;


    public String getFeePackageId() {
        return feePackageId;
    }

    public void setFeePackageId(String feePackageId) {
        this.feePackageId = feePackageId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getActiveFee() {
        return activeFee;
    }

    public void setActiveFee(long activeFee) {
        this.activeFee = activeFee;
    }

    public long getAnnualFee() {
        return annualFee;
    }

    public void setAnnualFee(long annualFee) {
        this.annualFee = annualFee;
    }

    public long getFixFee() {
        return fixFee;
    }

    public void setFixFee(long fixFee) {
        this.fixFee = fixFee;
    }

    public double getPercentFee() {
        return percentFee;
    }

    public void setPercentFee(double percentFee) {
        this.percentFee = percentFee;
    }

    public double getVat() {
        return vat;
    }

    public void setVat(double vat) {
        this.vat = vat;
    }

    public int getRecordType() {
        return recordType;
    }

    public void setRecordType(int recordType) {
        this.recordType = recordType;
    }
}
