package com.vietqr.org.dto;

public interface InvoiceUpdateItemDTO {
    String getInvoiceId();
    Long getTotalAmount();
    Long getVatAmount();
    Long getTotalAmountAfterVat();
}
