package com.vietqr.org.dto;

public class TransactionReceiveLogDTO {
    private String id;
    private int type;
    private String transactionId;
    private String status;
    private int statusCode;
    private String message;
    private long timeRequest;
    private long timeResponse;

    public TransactionReceiveLogDTO() {
    }

    public TransactionReceiveLogDTO(String id, int type, String transactionId, String status, int statusCode,
                                    String message, long timeRequest, long timeResponse) {
        this.id = id;
        this.type = type;
        this.transactionId = transactionId;
        this.status = status;
        this.statusCode = statusCode;
        this.message = message;
        this.timeRequest = timeRequest;
        this.timeResponse = timeResponse;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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

    public void setType(int type) {
        this.type = type;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimeRequest() {
        return timeRequest;
    }

    public void setTimeRequest(long timeRequest) {
        this.timeRequest = timeRequest;
    }

    public long getTimeResponse() {
        return timeResponse;
    }

    public void setTimeResponse(long timeResponse) {
        this.timeResponse = timeResponse;
    }
}
