package com.vietqr.org.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "BankReceiveFeePackage")
public class BankReceiveFeePackageEntity implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "bankId")
    private String bankId;

    @Column(name = "feePackageId")
    private String feePackageId;

    @Column(name = "title")
    private String title;

    @Column(name = "activeFee")
    private long activeFee;

    @Column(name = "annualFee")
    private long annualFee;

    @Column(name = "fixFee")
    private long fixFee;

    @Column(name = "percentFee")
    private double percentFee;

    @Column(name = "vat")
    private double vat;

    @Column(name = "recordType")
    private int recordType;

    @Column(name = "data", columnDefinition = "JSON")
    private String data;

    public BankReceiveFeePackageEntity() {
    }

    public BankReceiveFeePackageEntity(String id, String bankId, String feePackageId, String title, long activeFee,
                                       long annualFee, long fixFee, double percentFee, double vat, int recordType,
                                       String data) {
        this.id = id;
        this.bankId = bankId;
        this.feePackageId = feePackageId;
        this.title = title;
        this.activeFee = activeFee;
        this.annualFee = annualFee;
        this.fixFee = fixFee;
        this.percentFee = percentFee;
        this.vat = vat;
        this.recordType = recordType;
        this.data = data;
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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
