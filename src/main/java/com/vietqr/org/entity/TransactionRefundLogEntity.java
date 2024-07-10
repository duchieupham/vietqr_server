package com.vietqr.org.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "TransactionRefundLog")
public class TransactionRefundLogEntity implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "bankAccount")
    private String bankAccount;

    @Column(name = "referenceNumber")
    private String referenceNumber;

    @Column(name = "content")
    private String content;

    @Column(name = "amount")
    private long amount;

    @Column(name = "timeCreated")
    private long timeCreated;

    @Column(name = "timeResponse")
    private long timeResponse;

    // ref reference_number from transaction_receive
    @Column(name = "refNumber")
    private String refNumber;

    // transaction status: SUCCESS == 1/FAILED == 0
    @Column(name = "status")
    private int status;

    @Column(name = "message")
    private String message;

    @Column(name = "checkSum")
    private String checkSum;

    public TransactionRefundLogEntity() {
    }

    public TransactionRefundLogEntity(String id, String bankAccount, String referenceNumber, String content, long amount,
                                      long timeCreated, long timeResponse, String refNumber, int status,
                                      String message, String checkSum) {
        this.id = id;
        this.bankAccount = bankAccount;
        this.referenceNumber = referenceNumber;
        this.content = content;
        this.amount = amount;
        this.timeCreated = timeCreated;
        this.timeResponse = timeResponse;
        this.refNumber = refNumber;
        this.status = status;
        this.message = message;
        this.checkSum = checkSum;
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

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
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

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long requestTime) {
        this.timeCreated = requestTime;
    }

    public long getTimeResponse() {
        return timeResponse;
    }

    public void setTimeResponse(long responseTime) {
        this.timeResponse = responseTime;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(String checkSum) {
        this.checkSum = checkSum;
    }
}
