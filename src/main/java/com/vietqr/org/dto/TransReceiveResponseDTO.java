package com.vietqr.org.dto;

public interface TransReceiveResponseDTO {

    // referenceNumber
    // orderId
    // amount
    // transType
    // status
    // type
    // time created
    // time paid
    String getTransactionId();
    String getReferenceNumber();

    String getOrderId();

    Long getAmount();

    String getContent();

    String getTransType();

    Integer getStatus();

    Integer getType();

    Long getTimeCreated();

    Long getTimePaid();

    String getTerminalCode();

    String getNote();

}
