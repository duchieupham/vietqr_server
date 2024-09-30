package com.vietqr.org.dto;

import java.util.List;

public class InvoiceMapTransactionDTO {
    private String invoiceId;
    private List<TransactionInvoiceItemDTO> invoiceItemList;

    private List<TransactionInvoiceDTO> transactionList;

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public List<TransactionInvoiceItemDTO> getInvoiceItemList() {
        return invoiceItemList;
    }

    public void setInvoiceItemList(List<TransactionInvoiceItemDTO> invoiceItemList) {
        this.invoiceItemList = invoiceItemList;
    }

    public List<TransactionInvoiceDTO> getTransactionList() {
        return transactionList;
    }

    public void setTransactionList(List<TransactionInvoiceDTO> transactionList) {
        this.transactionList = transactionList;
    }
}
