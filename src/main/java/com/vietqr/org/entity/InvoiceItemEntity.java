package com.vietqr.org.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "InvoiceItem")
public class InvoiceItemEntity implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "invoiceId")
    private String invoiceId;

    // Tiền trên item
    @Column(name = "amount")
    private long amount;

    @Column(name = "quantity")
    private int quantity;

    // Tổng tiền
    @Column(name = "totalAmount")
    private long totalAmount;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    //0: Phần mềm ViệtQR
    @Column(name = "type")
    private int type;

    @Column(name = "typeName")
    private String typeName;

    @Column(name = "data")
    private String data;

    //0 : Phần mềm VietQR <Annual Fee>
    @Column(name = "dataType")
    private int dataType;

    public InvoiceItemEntity() {
    }

    public InvoiceItemEntity(String id, String invoiceId, long amount, int quantity, String name,
                             String description, int type, String typeName, String data, int dataType) {
        this.id = id;
        this.invoiceId = invoiceId;
        this.amount = amount;
        this.quantity = quantity;
        this.name = name;
        this.description = description;
        this.type = type;
        this.typeName = typeName;
        this.data = data;
        this.dataType = dataType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(long totalAmount) {
        this.totalAmount = totalAmount;
    }
}
