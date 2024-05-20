package com.vietqr.org.dto;

public class InvoiceRemoveDTO {
    private String invoiceId;

    public InvoiceRemoveDTO() {
    }

    public InvoiceRemoveDTO(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }
}
