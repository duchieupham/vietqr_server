package com.vietqr.org.dto;

import java.util.List;

public class ListPaymentRequestDTO {
    private List<PaymentRequestDTO> invoices;

    public ListPaymentRequestDTO() {
    }

    public ListPaymentRequestDTO(List<PaymentRequestDTO> invoices) {
        this.invoices = invoices;
    }

    public List<PaymentRequestDTO> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<PaymentRequestDTO> invoices) {
        this.invoices = invoices;
    }
}
