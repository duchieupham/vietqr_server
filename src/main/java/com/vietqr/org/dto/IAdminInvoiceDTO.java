package com.vietqr.org.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface IAdminInvoiceDTO {
    String getInvoiceId();
    Long getTimePaid();
    String getVso();
    String getMidName();
    long getAmount();
    String getData();
    String getQrCode();
    double getVat();
    long getVatAmount();
    long getAmountNoVat();
    String getBillNumber();
    String getInvoiceName();
    String getPhoneNo();
    String getEmail();
    Long getTimeCreated();
    int getStatus();
    String getMerchantId();

}
