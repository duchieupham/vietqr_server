package com.vietqr.org.dto;

public interface IInvoiceQrDetailDTO {
    String getContent();
    String getDescription();
    String getInvoiceId();
    String getInvoiceNumber();
    String getInvoiceName();
    String getBankAccount();
    String getUserBankName();
    String getBankShortName();
    long getTotalAmount();
    double getVat();
    long getVatAmount();
    long getTotalAmountAfterVat();
    String getBankId();
    String getMerchantId();

}
