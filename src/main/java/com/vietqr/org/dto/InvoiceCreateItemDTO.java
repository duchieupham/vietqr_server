package com.vietqr.org.dto;

public class InvoiceCreateItemDTO {
    private String itemId;
    private String content;
    private String unit;
    private String time;
    private int type;
    private int quantity;
    private long amount;
    private long totalAmount;
    private double vat;
    private long vatAmount;
    private long amountAfterVat;

    public InvoiceCreateItemDTO() {
        this.itemId = "";
        this.content = "";
        this.unit = "";
        this.time = "";
        this.type = 9;
        this.quantity = 1;
        this.amount = 0;
        this.totalAmount = 0;
        this.vat = 0;
        this.vatAmount = 0;
        this.amountAfterVat = 0;
    }

    public InvoiceCreateItemDTO(String itemId, String content, String unit, String time, int type,
                                int quantity, long amount, long totalAmount, double vat, long vatAmount,
                                long amountAfterVat) {
        this.itemId = itemId;
        this.content = content;
        this.unit = unit;
        this.time = time;
        this.type = type;
        this.quantity = quantity;
        this.amount = amount;
        this.totalAmount = totalAmount;
        this.vat = vat;
        this.vatAmount = vatAmount;
        this.amountAfterVat = amountAfterVat;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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
