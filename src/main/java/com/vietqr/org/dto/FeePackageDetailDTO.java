package com.vietqr.org.dto;

public class FeePackageDetailDTO {
    private String feePackage;
    private long annualFee;
    private int fixFee;
    private double percentFee;
    private int recordType;
    private double vat;

    public FeePackageDetailDTO() {
    }

    public FeePackageDetailDTO(String feePackage, long annualFee, int fixFee, double percentFee, int recordType) {
        this.feePackage = feePackage;
        this.annualFee = annualFee;
        this.fixFee = fixFee;
        this.percentFee = percentFee;
        this.recordType = recordType;
    }

    public String getFeePackage() {
        return feePackage;
    }

    public void setFeePackage(String feePackage) {
        this.feePackage = feePackage;
    }

    public long getAnnualFee() {
        return annualFee;
    }

    public void setAnnualFee(long annualFee) {
        this.annualFee = annualFee;
    }

    public int getFixFee() {
        return fixFee;
    }

    public void setFixFee(int fixFee) {
        this.fixFee = fixFee;
    }

    public double getPercentFee() {
        return percentFee;
    }

    public void setPercentFee(double percentFee) {
        this.percentFee = percentFee;
    }

    public int getRecordType() {
        return recordType;
    }

    public void setRecordType(int recordType) {
        this.recordType = recordType;
    }

    public double getVat() {
        return vat;
    }

    public void setVat(double vat) {
        this.vat = vat;
    }
}
