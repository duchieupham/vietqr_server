package com.vietqr.org.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "MerchantTransReceiveRequest")
public class MerchantTransReceiveRequestEntity {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "merchantId")
    private String merchantId;

    @Column(name = "terminalId")
    private String terminalId;

    @Column(name = "transactionReceiveId")
    private String transactionReceiveId;

    @Column(name = "userId")
    private String userId;

    @Column(name = "requestType")
    private int requestType;

    @Column(name = "requestValue")
    private String requestValue;

    @Column(name = "timeCreated")
    private long timeCreated;

    @Column(name = "timeApproved")
    private long timeApproved;


    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getTransactionReceiveId() {
        return transactionReceiveId;
    }

    public void setTransactionReceiveId(String transactionReceiveId) {
        this.transactionReceiveId = transactionReceiveId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getRequestType() {
        return requestType;
    }

    public void setRequestType(int requestType) {
        this.requestType = requestType;
    }

    public String getRequestValue() {
        return requestValue;
    }

    public void setRequestValue(String requestValue) {
        this.requestValue = requestValue;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public long getTimeApproved() {
        return timeApproved;
    }

    public void setTimeApproved(long timeApproved) {
        this.timeApproved = timeApproved;
    }
}
