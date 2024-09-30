package com.vietqr.org.dto;

public class TransactionInvoiceItemDTO {
    private String id;

    private long totalAfterVat;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTotalAfterVat() {
        return totalAfterVat;
    }

    public void setTotalAfterVat(long totalAfterVat) {
        this.totalAfterVat = totalAfterVat;
    }
}
