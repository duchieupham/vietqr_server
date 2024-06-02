package com.vietqr.org.dto;

public interface IInvoiceDTO {
    String getInvoiceId();
    String getInvoiceName();
    String getInvoiceDescription();
    double getVat();
    long getVatAmount();
    long getTotalAmount();
    long getTotalAmountAfterVat();
    int getStatus();
    String getBankId();
    String getData();
    String getMerchantId();
    String getUserId();
    String getBankIdRecharge();
    String getInvoiceNumber();
}
