package com.vietqr.org.dto;

public class InvoiceRemoveDTO {
    private String invoiceId;
    private String itemId;

    public InvoiceRemoveDTO() {
    }

    public InvoiceRemoveDTO(String itemId) {
        this.itemId = itemId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public InvoiceRemoveDTO(String invoiceId, String itemId) {
        this.invoiceId = invoiceId;
        this.itemId = itemId;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }
}
