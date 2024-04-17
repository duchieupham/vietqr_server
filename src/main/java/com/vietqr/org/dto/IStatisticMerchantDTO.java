package com.vietqr.org.dto;

public interface IStatisticMerchantDTO {
    String getMerchantId();
    String getMerchantName();
    String getVsoCode();
    int getTotalTrans();
    long getTotalAmount();
    String getDate();
}
