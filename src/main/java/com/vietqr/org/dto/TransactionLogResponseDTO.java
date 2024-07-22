package com.vietqr.org.dto;

public class TransactionLogResponseDTO {
    private Integer statusCode;
    private long timeRequest;
    private long timeResponse;
    private String message;

    public TransactionLogResponseDTO() {
        statusCode = 0;
        timeRequest = 0;
        timeResponse = 0;
        message = "";
    }

    public TransactionLogResponseDTO(Integer statusCode, long timeRequest, long timeResponse, String message) {
        this.statusCode = statusCode;
        this.timeRequest = timeRequest;
        this.timeResponse = timeResponse;
        this.message = message;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
