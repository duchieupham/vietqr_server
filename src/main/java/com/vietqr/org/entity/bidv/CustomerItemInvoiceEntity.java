package com.vietqr.org.entity.bidv;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CustomerItemInvoice")
public class CustomerItemInvoiceEntity implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "billId")
    private String billId;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "quantity")
    private Long quantity;

    @Column(name = "amount")
    private Long amount;

    @Column(name = "totalAmount")
    private Long totalAmount;

    public CustomerItemInvoiceEntity() {
        super();
    }

    public CustomerItemInvoiceEntity(String id, String billId, String name, String description, Long quantity,
            Long amount, Long totalAmount) {
        this.id = id;
        this.billId = billId;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.amount = amount;
        this.totalAmount = totalAmount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
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

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Long totalAmount) {
        this.totalAmount = totalAmount;
    }

}
