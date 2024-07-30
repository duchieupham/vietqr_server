package com.vietqr.org.dto;

import java.util.ArrayList;
import java.util.List;

public class InvoiceOverviewDTO {
    private int countInvoice;
    private long amountUnpaid;

    private List<IInvoiceLatestDTO> invoices;

    public InvoiceOverviewDTO() {
        invoices = new ArrayList<>();
    }

    public InvoiceOverviewDTO(int countInvoice, long amountUnpaid, List<IInvoiceLatestDTO> invoices) {
        this.countInvoice = countInvoice;
        this.amountUnpaid = amountUnpaid;
        this.invoices = invoices;
    }

    public int getCountInvoice() {
        return countInvoice;
    }

    public void setCountInvoice(int countInvoice) {
        this.countInvoice = countInvoice;
    }

    public long getAmountUnpaid() {
        return amountUnpaid;
    }

    public void setAmountUnpaid(long amountUnpaid) {
        this.amountUnpaid = amountUnpaid;
    }

    public List<IInvoiceLatestDTO> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<IInvoiceLatestDTO> invoices) {
        this.invoices = invoices;
    }
}
