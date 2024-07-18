package com.vietqr.org.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "TransactionBidv")
public class TransactionBidvEntity implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "transId")
    private String transId;
    @Column(name = "transDate")
    private String transDate;
    @Column(name = "customerId")
    private String customerId;
    @Column(name = "serviceId")
    private String serviceId;
    @Column(name = "billId")
    private String billId;
    @Column(name = "amount")
    private String amount;
    @Column(name = "checkSum")
    private String checkSum;

    public TransactionBidvEntity() {
    }

    public TransactionBidvEntity(String id, String transId, String transDate,
                                 String customerId, String serviceId, String billId, String amount, String checkSum) {
        this.id = id;
        this.transId = transId;
        this.transDate = transDate;
        this.customerId = customerId;
        this.serviceId = serviceId;
        this.billId = billId;
        this.amount = amount;
        this.checkSum = checkSum;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public String getTransDate() {
        return transDate;
    }

    public void setTransDate(String transDate) {
        this.transDate = transDate;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(String checkSum) {
        this.checkSum = checkSum;
    }
}
