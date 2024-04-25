package com.vietqr.org.dto;

public class InvoiceItemResDTO {
    private String invoiceItemId;
    private String invoiceItemName;
    private int quantity;
    private long itemAmount;
    private long totalItemAmount;

    public InvoiceItemResDTO() {
    }

    public InvoiceItemResDTO(String invoiceItemId, String invoiceItemName, int quantity,
                             long itemAmount, long totalItemAmount) {
        this.invoiceItemId = invoiceItemId;
        this.invoiceItemName = invoiceItemName;
        this.quantity = quantity;
        this.itemAmount = itemAmount;
        this.totalItemAmount = totalItemAmount;
    }

    public String getInvoiceItemId() {
        return invoiceItemId;
    }

    public void setInvoiceItemId(String invoiceItemId) {
        this.invoiceItemId = invoiceItemId;
    }

    public String getInvoiceItemName() {
        return invoiceItemName;
    }

    public void setInvoiceItemName(String invoiceItemName) {
        this.invoiceItemName = invoiceItemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public long getItemAmount() {
        return itemAmount;
    }

    public void setItemAmount(long itemAmount) {
        this.itemAmount = itemAmount;
    }

    public long getTotalItemAmount() {
        return totalItemAmount;
    }

    public void setTotalItemAmount(long totalItemAmount) {
        this.totalItemAmount = totalItemAmount;
    }
}
