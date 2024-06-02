package com.vietqr.org.dto;

public class InvoiceItemDetailDTO {
    private String invoiceItemId;
    private String invoiceItemName;
    private String unit;
    private int quantity;
    private long amount;
    private long totalAmount;
    private double vat;
    private int type;
    private long vatAmount;
    private long totalAmountAfterVat;
    private int status;
    private long timePaid;

    public InvoiceItemDetailDTO() {
    }

    public InvoiceItemDetailDTO(String invoiceItemId, String invoiceItemName, String unit, int quantity,
                                long amount, long totalAmount, double vat, long vatAmount, long totalAmountAfterVat) {
        this.invoiceItemId = invoiceItemId;
        this.invoiceItemName = invoiceItemName;
        this.unit = unit;
        this.quantity = quantity;
        this.amount = amount;
        this.totalAmount = totalAmount;
        this.vat = vat;
        this.vatAmount = vatAmount;
        this.totalAmountAfterVat = totalAmountAfterVat;
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

    public long getTotalAmountAfterVat() {
        return totalAmountAfterVat;
    }

    public void setTotalAmountAfterVat(long totalAmountAfterVat) {
        this.totalAmountAfterVat = totalAmountAfterVat;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getTimePaid() {
        return timePaid;
    }

    public void setTimePaid(long timePaid) {
        this.timePaid = timePaid;
    }
}
