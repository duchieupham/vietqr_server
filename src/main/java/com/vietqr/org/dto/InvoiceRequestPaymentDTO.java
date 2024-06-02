package com.vietqr.org.dto;

public interface InvoiceRequestPaymentDTO {
    String getQrCode();
    String getId();
    long getTotalAMount();
    String getInvoiceName();
    String getData();
    String getInvoiceNumber();
    long getAmount();
    double getVat();
    long getVatAmount();
    String getInvoiceId();
}
