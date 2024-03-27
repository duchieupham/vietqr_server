package com.vietqr.org.dto;

public class ApproveTransRequestDTO {
    private String requestId;
    private String transactionId;
    private String userId;

    public ApproveTransRequestDTO() {
    }

    public ApproveTransRequestDTO(String requestId, String transactionId, String userId) {
        this.requestId = requestId;
        this.transactionId = transactionId;
        this.userId = userId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
