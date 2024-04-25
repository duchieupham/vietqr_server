package com.vietqr.org.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "Invoice")
public class InvoiceEntity implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "invoiceId")
    private String invoiceId;

    @Column(name = "name")
    private String name;

    @Column(name = "timeCreated")
    private long timeCreated;

    @Column(name = "timePaid")
    private long timePaid;

    @Column(name = "status")
    private int status;

    @Column(name = "merchantId")
    private String merchantId;

    @Column(name = "bankId")
    private String bankId;

    @Column(name = "amount")
    private long amount;

    @Column(name = "totalAmount")
    private long totalAmount;

    @Column(name = "vatAmount")
    private long vatAmount;

    @Column(name = "vat")
    private double vat;

    @Column(name = "userId")
    private String userId;

    // transaction wallet id (dùng để đối soát)
    @Column(name = "refId")
    private String refId;

    @Column(name = "data", columnDefinition = "JSON")
    private String data;

    // 0 : Information of bankaccount
    // loại dữ liệu của data
    @Column(name = "dataType")
    private int dataType;

    public InvoiceEntity(String id, String invoiceId, String name, long timeCreated,
                         long timePaid, int status, String merchantId, long amount) {
        this.id = id;
        this.invoiceId = invoiceId;
        this.name = name;
        this.timeCreated = timeCreated;
        this.timePaid = timePaid;
        this.status = status;
        this.merchantId = merchantId;
        this.amount = amount;
    }

    public InvoiceEntity() {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public long getTimePaid() {
        return timePaid;
    }

    public void setTimePaid(long timePaid) {
        this.timePaid = timePaid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public double getVat() {
        return vat;
    }

    public void setVat(double vat) {
        this.vat = vat;
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public long getVatAmount() {
        return vatAmount;
    }

    public void setVatAmount(long vatAmount) {
        this.vatAmount = vatAmount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
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
}
