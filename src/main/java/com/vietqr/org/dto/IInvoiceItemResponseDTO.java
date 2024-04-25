package com.vietqr.org.dto;

public interface IInvoiceItemResponseDTO {
    String getInvoiceItemId();
    String getInvoiceItemName();
    int getQuantity();
    long getItemAmount();
    long getTotalItemAmount();
}
