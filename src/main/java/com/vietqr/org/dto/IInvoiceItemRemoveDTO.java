package com.vietqr.org.dto;

public interface IInvoiceItemRemoveDTO {
    String getItemId();
    Long getTotalAmount();
    Long getTotalAmountAfterVat();
    Long getVatAmount();
}
