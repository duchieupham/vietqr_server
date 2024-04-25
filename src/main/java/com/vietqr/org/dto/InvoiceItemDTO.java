package com.vietqr.org.dto;

public class InvoiceItemDTO {
    // 0: Phí giao dịch
    private int type;
    private String typeName;
    private String name;
    private String description;
    private long amount;
    private int quantity;
    public InvoiceItemDTO() {
    }

    public InvoiceItemDTO(int type, String typeName, String name, String description) {
        this.type = type;
        this.typeName = typeName;
        this.name = name;
        this.description = description;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
