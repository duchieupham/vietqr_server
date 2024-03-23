package com.vietqr.org.dto;

import javax.validation.constraints.NotBlank;

public class MapRequestDTO {
    @NotBlank
    private String transactionId;
    private int requestType;
    @NotBlank
    private String requestValue;
    @NotBlank
    private String userId;

    public MapRequestDTO() {
    }

    public MapRequestDTO(String transactionId, int requestType, String requestValue, String userId) {
        this.transactionId = transactionId;
        this.requestType = requestType;
        this.requestValue = requestValue;
        this.userId = userId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
