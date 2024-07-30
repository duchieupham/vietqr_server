package com.vietqr.org.dto;

public interface IMerchantSyncPublicDTO {
    String getMid();
    String getMerchantFullName();
    String getMerchantName();
    String getMerchantAddress();
    String getMerchantIdentify();
    String getContactEmail();
    String getContactPhone();
    String getCertificate();
    String getWebSocket();
    String getWebhook();
    Boolean getIsMaster();
}
