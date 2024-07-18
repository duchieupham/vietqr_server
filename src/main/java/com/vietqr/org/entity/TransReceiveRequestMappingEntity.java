package com.vietqr.org.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TransReceiveRequestMapping")
public class TransReceiveRequestMappingEntity {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

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

    //0 : waiting
    //1: approved
    //2: rejected
    @Column(name = "status")
    private Integer status;

    @Column(name = "timeApproved")
    private long timeApproved;

    public TransReceiveRequestMappingEntity(String id, String merchantId, String terminalId,
                                            String transactionReceiveId, String userId,
                                            int requestType, String requestValue,
                                            long timeCreated, long timeApproved) {
        this.id = id;
        this.merchantId = merchantId;
        this.terminalId = terminalId;
        this.transactionReceiveId = transactionReceiveId;
        this.userId = userId;
        this.requestType = requestType;
        this.requestValue = requestValue;
        this.timeCreated = timeCreated;
        this.timeApproved = timeApproved;
    }

    public TransReceiveRequestMappingEntity() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
