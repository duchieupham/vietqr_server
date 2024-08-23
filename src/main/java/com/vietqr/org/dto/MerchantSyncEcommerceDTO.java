package com.vietqr.org.dto;

public class MerchantSyncEcommerceDTO {
    private String webhook;
    private String clientId;
    private String certificate;

    public MerchantSyncEcommerceDTO(String webhook, String clientId, String certificate) {
        this.webhook = webhook;
        this.clientId = clientId;
        this.certificate = certificate;
    }

    public MerchantSyncEcommerceDTO() {
    }

    public String getWebhook() {
        return webhook;
    }

    public void setWebhook(String webhook) {
        this.webhook = webhook;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }
}
