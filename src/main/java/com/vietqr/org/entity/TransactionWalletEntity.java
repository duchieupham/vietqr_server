package com.vietqr.org.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "TransactionWallet")
public class TransactionWalletEntity implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "userId")
    private String userId;

    @Column(name = "amount")
    private String amount;

    @Column(name = "content")
    private String content;

    @Column(name = "status")
    private int status;

    @Column(name = "transType")
    private String transType;

    @Column(name = "billNumber")
    private String billNumber;

    @Column(name = "timeCreated")
    private long timeCreated;

    @Column(name = "timePaid")
    private long timePaid;

    @Column(name = "otp")
    private String otp;

    @Column(name = "paymentType")
    private int paymentType;

    // 0: VQR
    // 1: VietQR
    @Column(name = "paymentMethod")
    private int paymentMethod;

    @Column(name = "referenceNumber")
    private String referenceNumber;

    @Column(name = "phoneNoRC")
    private String phoneNoRC;

    @Column(name = "data")
    private String data;

    public TransactionWalletEntity() {
        super();
        this.data = "";
    }

    public TransactionWalletEntity(String id, String userId, String amount, String content, int status,
                                   String transType, String billNumber, long timeCreated, long timePaid, String otp, int paymentType) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.content = content;
        this.status = status;
        this.transType = transType;
        this.billNumber = billNumber;
        this.timeCreated = timeCreated;
        this.timePaid = timePaid;
        this.otp = otp;
        this.paymentType = paymentType;
    }

    public TransactionWalletEntity(String id, String userId, String amount, String content, int status,
                                   String transType, String billNumber, long timeCreated, long timePaid, String otp, int paymentType,
                                   int paymentMethod, String referenceNumber) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.content = content;
        this.status = status;
        this.transType = transType;
        this.billNumber = billNumber;
        this.timeCreated = timeCreated;
        this.timePaid = timePaid;
        this.otp = otp;
        this.paymentType = paymentType;
        this.paymentMethod = paymentMethod;
        this.referenceNumber = referenceNumber;
    }

    public TransactionWalletEntity(String id, String userId, String amount, String content, int status,
                                   String transType, String billNumber, long timeCreated, long timePaid, String otp, int paymentType,
                                   int paymentMethod, String referenceNumber, String phoneNoRC) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.content = content;
        this.status = status;
        this.transType = transType;
        this.billNumber = billNumber;
        this.timeCreated = timeCreated;
        this.timePaid = timePaid;
        this.otp = otp;
        this.paymentType = paymentType;
        this.paymentMethod = paymentMethod;
        this.referenceNumber = referenceNumber;
        this.phoneNoRC = phoneNoRC;
    }

    public String getPhoneNoRC() {
        return phoneNoRC;
    }

    public void setPhoneNoRC(String phoneNoRC) {
        this.phoneNoRC = phoneNoRC;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
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

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public int getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(int paymentType) {
        this.paymentType = paymentType;
    }

    public int getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(int paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
