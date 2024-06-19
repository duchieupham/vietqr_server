package com.vietqr.org.dto;

public interface IFeePackageDTO {
    String getId();

    long getActiveFee();

    long getAnnualFee();

    String getDescription();

    long getFixFee();

    double getPercentFee();

    int getRecordType();

    String getRefId();

    int getServiceType();

    String getShortName();

    String getTitle();

    double getVat();

}
