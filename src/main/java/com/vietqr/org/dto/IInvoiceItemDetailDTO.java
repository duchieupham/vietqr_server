package com.vietqr.org.dto;

public interface IInvoiceItemDetailDTO {
    String getInvoiceItemId();
    String getInvoiceItemName();
    String getUnit();
    Integer getQuantity();
    Long getAmount();
    Long getTotalAmount();
    Double getVat();
    Long getVatAmount();
    Long getAmountAfterVat();

}
