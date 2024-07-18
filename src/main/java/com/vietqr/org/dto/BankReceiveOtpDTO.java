package com.vietqr.org.dto;

public interface BankReceiveOtpDTO {
    String getId();
    String getUserId();
    String getBankId();
    String getOtpToken();
    String getKeyActive();
    long getExpiredDate();
    long getAmount();
    String getRequestId();
}
