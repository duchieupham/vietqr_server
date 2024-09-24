package com.vietqr.org.dto;

import java.util.List;

public class InvoiceMapTransactionDTO {
    private String invoiceId;
    private List<ITransactionInvoiceItemDTO> invoiceItemList;

    private List<ITransactionInvoiceDTO> transactionList;

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public List<ITransactionInvoiceItemDTO> getInvoiceItemList() {
        return invoiceItemList;
    }

    public void setInvoiceItemList(List<ITransactionInvoiceItemDTO> invoiceItemList) {
        this.invoiceItemList = invoiceItemList;
    }

    public List<ITransactionInvoiceDTO> getTransactionList() {
        return transactionList;
    }

    public void setTransactionList(List<ITransactionInvoiceDTO> transactionList) {
        this.transactionList = transactionList;
    }
}
