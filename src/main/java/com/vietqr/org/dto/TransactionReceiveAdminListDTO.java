package com.vietqr.org.dto;

public interface TransactionReceiveAdminListDTO {

    String getId();

    String getBankAccount();

    Integer getAmount();

    String getBankId();

    String getContent();

    String getOrderId();

    String getReferenceNumber();

    Integer getStatus();

    Long getTimeCreated();

    Long getTimePaid();

    String getTransType();

}
