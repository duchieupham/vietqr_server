package com.vietqr.org.entity.bidv;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CustomerInvoice")
public class CustomerInvoiceEntity implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "customerId")
    private String customerId;

    // BIDV quy định:
    // 0: Gạch nợ không khớp số tiền
    // 1: Gạch nợ khớp số tiền
    @Column(name = "type")
    private int type;

    @Column(name = "amount")
    private Long amount;

    @Column(name = "billId")
    private String billId;

    // 0: Chưa thanh toán
    // 1: Đã thanh toán
    @Column(name = "status")
    private int status;

    public CustomerInvoiceEntity() {
        super();
    }

    public CustomerInvoiceEntity(String id, String customerId, int type, Long amount, String billId, int status) {
        this.id = id;
        this.customerId = customerId;
        this.type = type;
        this.amount = amount;
        this.billId = billId;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}
