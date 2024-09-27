package com.vietqr.org.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "InvoiceTransaction")
public class InvoiceTransactionEntity implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id ;

    @Column(name = "invoiceId")
    private String invoiceId;

    @Column(name = "invoiceItemIds", columnDefinition = "JSON")
    private String invoiceItemIds;

    @Column(name = "bankIdRecharge")
    private String bankIdRecharge;

    @Column(name = "mid")
    private String mid;

    @Column(name = "bankId")
    private String bankId;

    @Column(name = "userId")
    private String userId;

    @Column(name = "qrCode")
    private String qrCode;

    @Column(name = "invoiceNumber")
    private String invoiceNumber;

    @Column(name = "totalAmount")
    private long totalAmount;

    @Column(name = "amount")
    private long amount;

    @Column(name = "vatAmount")
    private long vatAmount;

    @Column(name = "vat")
    private double vat;

    // id của transaction_receive dùng để đối soát
    @Column(name = "refId")
    private String refId;

    @Column(name = "status")
    private int status;

    // list id transaction map with invoice
    @Column(name = "transactionIds", columnDefinition = "JSON")
    private String transactionIds;

    public InvoiceTransactionEntity() {
    }

    public InvoiceTransactionEntity(String id, String invoiceId, String invoiceItemIds, String transactionIds) {
        this.id = id;
        this.invoiceId = invoiceId;
        this.invoiceItemIds = invoiceItemIds;
        this.transactionIds = transactionIds;
    }

    public InvoiceTransactionEntity(String id, String invoiceId, String invoiceItemIds, String bankIdRecharge,
                                    String bankId, String userId, String qrCode, String invoiceNumber, long totalAmount, long amount, long vatAmount, String refId) {
        this.id = id;
        this.invoiceId = invoiceId;
        this.invoiceItemIds = invoiceItemIds;
        this.bankIdRecharge = bankIdRecharge;
        this.bankId = bankId;
        this.userId = userId;
        this.qrCode = qrCode;
        this.invoiceNumber = invoiceNumber;
        this.totalAmount = totalAmount;
        this.amount = amount;
        this.vatAmount = vatAmount;
        this.refId = refId;
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

    public String getInvoiceItemIds() {
        return invoiceItemIds;
    }

    public void setInvoiceItemIds(String invoiceItemId) {
        this.invoiceItemIds = invoiceItemId;
    }

    public String getBankIdRecharge() {
        return bankIdRecharge;
    }

    public void setBankIdRecharge(String bankIdRecharge) {
        this.bankIdRecharge = bankIdRecharge;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public long getVatAmount() {
        return vatAmount;
    }

    public void setVatAmount(long vatAmount) {
        this.vatAmount = vatAmount;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public double getVat() {
        return vat;
    }

    public void setVat(double vat) {
        this.vat = vat;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getTransactionIds() {
        return transactionIds;
    }

    public void setTransactionIds(String transactionIds) {
        this.transactionIds = transactionIds;
    }
}
