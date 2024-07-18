package com.vietqr.org.dto;

import javax.validation.constraints.NotBlank;

public class InvoiceRemoveDTO {
    @NotBlank
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
