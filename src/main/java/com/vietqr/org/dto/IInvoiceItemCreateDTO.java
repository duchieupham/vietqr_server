package com.vietqr.org.dto;

public interface IInvoiceItemCreateDTO {
    Long getAnnualFee();
    Long getFixFee();
    Double getPercentFee();
    Integer getRecordType();
    Long getActiveFee();
    Double getVat();
}
