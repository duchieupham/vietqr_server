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

    @Column(name = "name")
    private String name;

    // BIDV quy định:
    // 0: Gạch nợ không khớp số tiền
    // 1: Gạch nợ khớp số tiền
    @Column(name = "type")
    private int type;

    @Column(name = "amount")
    private Long amount;

    @Column(name = "billId")
    private String billId;

    @Column(name = "timeCreated")
    private Long timeCreated;

    @Column(name = "timePaid")
    private Long timePaid;

    // 0: Chưa thanh toán
    // 1: Đã thanh toán
    @Column(name = "status")
    private int status;

    // 0: Chưa được vấn tin từ BIDV
    // 1: Đã vấn tin từ BIDV
    @Column(name = "inquire")
    private int inquire;

    // 0: Default là hóa đơn BIDV
    // 1: Transaction_receive
    // 2: Static qr
    @Column(name = "qrType")
    private int qrType = 0;

    public CustomerInvoiceEntity() {
        super();
    }

    public CustomerInvoiceEntity(String id, String customerId,
            String name,
            int type,
            Long amount, String billId,
            Long timeCreated, Long timePaid,
            int status, int inquire) {
        this.id = id;
        this.customerId = customerId;
        this.name = name;
        this.type = type;
        this.amount = amount;
        this.billId = billId;
        this.timeCreated = timeCreated;
        this.timePaid = timePaid;
        this.status = status;
        this.inquire = inquire;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public Long getTimePaid() {
        return timePaid;
    }

    public void setTimePaid(Long timePaid) {
        this.timePaid = timePaid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getInquire() {
        return inquire;
    }

    public void setInquire(int inquire) {
        this.inquire = inquire;
    }

    public int getQrType() {
        return qrType;
    }

    public void setQrType(int qrType) {
        this.qrType = qrType;
    }
}
