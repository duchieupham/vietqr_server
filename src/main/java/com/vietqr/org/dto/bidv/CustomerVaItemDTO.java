package com.vietqr.org.dto.bidv;

public interface CustomerVaItemDTO {
    // id
    // merchant name
    // merchant id
    // customer id
    // bank account
    // unpaidInvoiceAmount

    String getId();

    String getMerchantName();

    String getMerchantId();

    String getCustomerId();

    String getBankAccount();

    Integer getUnpaidInvoiceAmount();

    Integer getUnpaidInvoiceCount();
}
