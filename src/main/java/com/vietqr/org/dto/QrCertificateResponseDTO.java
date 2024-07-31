package com.vietqr.org.dto;

public class QrCertificateResponseDTO {
    private String merchantId;
    private String certificate;
    private String webhook;
    private String clientId;

    public QrCertificateResponseDTO(String merchantId, String certificate, String webhook, String clientId) {
        this.merchantId = merchantId;
        this.certificate = certificate;
        this.webhook = webhook;
        this.clientId = clientId;
    }

    public QrCertificateResponseDTO() {
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
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
}
