package com.vietqr.org.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TransactionReceiveLog")
public class TransactionReceiveLogEntity implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "transactionId")
    private String transactionId;

    @Column(name = "status")
    private String status;

    @Column(name = "message")
    private String message;

    @Column(name = "urlCallback")
    private String urlCallback;

    @Column(name = "time")
    private long time;

    @Column(name = "timeResponse")
    private long timeResponse;

    @Column(name = "statusCode")
    private Integer statusCode;

    // 0: GET TOKEN
    // 1: TRANS SYNC
    @Column(name = "type")
    private Integer type;

    public TransactionReceiveLogEntity() {
        super();
    }

    public TransactionReceiveLogEntity(String id, String transactionId, String status, String message, long time,
            String urlCallback) {
        this.id = id;
        this.transactionId = transactionId;
        this.status = status;
        this.message = message;
        this.time = time;
        this.urlCallback = urlCallback;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getUrlCallback() {
        return urlCallback;
    }

    public void setUrlCallback(String urlCallback) {
        this.urlCallback = urlCallback;
    }

    public long getTimeResponse() {
        return timeResponse;
    }

    public void setTimeResponse(long timeResponse) {
        this.timeResponse = timeResponse;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
