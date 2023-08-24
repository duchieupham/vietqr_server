package com.vietqr.org.dto;

public interface TransWalletListDTO {

    String getId();

    String getAmount();

    String getBillNumber();

    Integer getStatus();

    String getTransType();

    Integer getPaymentType();

    Integer getPaymentMethod();

    Long getTimeCreated();

    Long getTimePaid();
}
