package com.vietqr.org.dto;

public interface BankIdProcessDateResponseDTO {
    String getBankId();
    String getProcessDate();
    String getData();
    long getVatAmount();
    long getTotalAmount();
    double getVat();
    long getTotalAfterVat();
    int getFixFee();
    double getPercentFee();
    String getTitle();
}
