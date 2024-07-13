package com.vietqr.org.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "TransactionRefund")
public class TransactionRefundEntity implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "bankAccount")
    private String bankAccount;

    @Column(name = "bankId")
    private String bankId;

    @Column(name = "content")
    private String content;

    @Column(name = "amount")
    private long amount;

    @Column(name = "time")
    private long time;

    @Column(name = "timePaid")
    private long timePaid;

    // ref reference_number from transaction_receive
    @Column(name = "refNumber")
    private String refNumber;

    @Column(name = "transactionId")
    private String transactionId;

    // transaction status: SUCCESS == 1/FAILED == 0
    @Column(name = "status")
    private int status;

    // default = D
    @Column(name = "transType")
    private String transType;

    @Column(name = "referenceNumber")
    private String referenceNumber;

    @Column(name = "userId")
    private String userId;

    @Column(name = "note")
    private String note;

    @Column(name = "multiTimes")
    private boolean multiTimes;

    public TransactionRefundEntity() {
    }

    public TransactionRefundEntity(String id, String bankAccount, String bankId, String content, long amount, long time,
                                   long timePaid, String refNumber, int status, String transType,
                                   String referenceNumber, String userId, String note) {
        this.id = id;
        this.bankAccount = bankAccount;
        this.bankId = bankId;
        this.content = content;
        this.amount = amount;
        this.time = time;
        this.timePaid = timePaid;
        this.refNumber = refNumber;
        this.status = status;
        this.transType = transType;
        this.referenceNumber = referenceNumber;
        this.userId = userId;
        this.note = note;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTimePaid() {
        return timePaid;
    }

    public void setTimePaid(long timePaid) {
        this.timePaid = timePaid;
    }

    public String getRefNumber() {
        return refNumber;
    }

    public void setRefNumber(String refNumber) {
        this.refNumber = refNumber;
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

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isMultiTimes() {
        return multiTimes;
    }

    public void setMultiTimes(boolean multiTimes) {
        this.multiTimes = multiTimes;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}
