package com.vietqr.org.dto;

public interface IInvoiceDetailDTO {
    String getInvoiceId();
    String getBillNumber();
    String getInvoiceNumber();
    String getInvoiceName();
    long getTimeCreated();
    long getTimePaid();
    int getStatus();
    long getVatAmount();
    long getAmount();
    double getVat();
    String getBankId();
    String getData();
    String getContent();
    long getTotalAmount();
    String getBankIdRecharge();
    String getUserId();
    String getMerchantId();
}
