package com.vietqr.org.dto;

public class InvoiceUpdateDTO {
    private String invoiceId;
    private String itemId;
    private int type;
    private String content;
    private String unit;
    private int quantity;
    private long amount;
    private long totalAmount;
    private double vat;
    private long vatAmount;
    private long amountAfterVat;

    public InvoiceUpdateDTO() {
    }

    public InvoiceUpdateDTO(String invoiceId, String itemId, int type, String content, String unit,
                            int quantity, long amount, long totalAmount, double vat, long vatAmount,
                            long amountAfterVat) {
        this.invoiceId = invoiceId;
        this.itemId = itemId;
        this.type = type;
        this.content = content;
        this.unit = unit;
        this.quantity = quantity;
        this.amount = amount;
        this.totalAmount = totalAmount;
        this.vat = vat;
        this.vatAmount = vatAmount;
        this.amountAfterVat = amountAfterVat;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getVat() {
        return vat;
    }

    public void setVat(double vat) {
        this.vat = vat;
    }

    public long getVatAmount() {
        return vatAmount;
    }

    public void setVatAmount(long vatAmount) {
        this.vatAmount = vatAmount;
    }

    public long getAmountAfterVat() {
        return amountAfterVat;
    }

    public void setAmountAfterVat(long amountAfterVat) {
        this.amountAfterVat = amountAfterVat;
    }
}
