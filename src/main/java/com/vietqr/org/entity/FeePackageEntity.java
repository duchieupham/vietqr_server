package com.vietqr.org.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "FeePackage")
public class FeePackageEntity implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "serviceType")
    private int serviceType;

    @Column(name = "title")
    private String title;

    @Column(name = "shortName")
    private String shortName;

    @Column(name = "description")
    private String description;

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

    @Column(name = "refId")
    private String refId;

    public FeePackageEntity() {
    }

    public FeePackageEntity(String id, String title, String shortName, String description,
                            long activeFee, long annualFee, long fixFee, double percentFee,
                            double vat, int recordType, String refId) {
        this.id = id;
        this.title = title;
        this.shortName = shortName;
        this.description = description;
        this.activeFee = activeFee;
        this.annualFee = annualFee;
        this.fixFee = fixFee;
        this.percentFee = percentFee;
        this.vat = vat;
        this.recordType = recordType;
        this.refId = refId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getServiceType() {
        return serviceType;
    }

    public void setServiceType(int serviceType) {
        this.serviceType = serviceType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }
}
