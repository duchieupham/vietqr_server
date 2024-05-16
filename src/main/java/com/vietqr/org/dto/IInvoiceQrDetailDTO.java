package com.vietqr.org.dto;

public interface IInvoiceQrDetailDTO {
    String getContent();
    String getInvoiceId();
    String getInvoiceNumber();
    String getInvoiceName();
    String getData();
    long getTotalAmount();
    double getVat();
    long getVatAmount();
    long getTotalAmountAfterVat();

}
