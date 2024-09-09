package com.vietqr.org.dto;

import com.google.firebase.database.annotations.NotNull;

import java.util.List;

public class PaymentRequestDTOv2 {
    @NotNull
    private List<String> invoiceIds;
    private String bankIdRecharge;

    public PaymentRequestDTOv2() {
    }

    public PaymentRequestDTOv2(List<String> invoiceIds, String bankIdRecharge) {
        this.invoiceIds = invoiceIds;
        this.bankIdRecharge = bankIdRecharge;
    }

    public List<String> getInvoiceIds() {
        return invoiceIds;
    }

    public void setInvoiceIds(List<String> invoiceIds) {
        this.invoiceIds = invoiceIds;
    }

    public String getBankIdRecharge() {
        return bankIdRecharge;
    }

    public void setBankIdRecharge(String bankIdRecharge) {
        this.bankIdRecharge = bankIdRecharge;
    }
}
