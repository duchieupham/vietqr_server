package com.vietqr.org.dto;

public interface ITransactionReceiveLogDTO {
    String getId();
    Integer getType();
    String getTransactionId();
    String getStatus();
    Integer getStatusCode();
    String getMessage();
    Long getTimeRequest();
    Long getTimeResponse();

}
