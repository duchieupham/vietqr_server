package com.vietqr.org.dto;

public interface TransRequestDTO {
    String getRequestId();
    String getTransactionId();
    String getUserId();
    String getTerminalId();
    String getMerchantId();
    String getRequestValue();
    int getRequestType();
    String getFullName();
    String getPhoneNumber();
    String getTerminalName();
    String getMerchantName();

}
