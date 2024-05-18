package com.vietqr.org.dto;

public interface IAdminInvoiceDTO {
    String getInvoiceId();
    long getTimePaid();
    String getVso();
    String getMidName();
    long getAmount();
    String getData();
    String getQrCode();
    double getVat();
    long vatAmount();
    long getAmountNoVat();
    String getBillNumber();
    String getInvoiceName();
    String getPhoneNo();
    String getEmail();
    long getTimeCreated();
    int getStatus();
}
