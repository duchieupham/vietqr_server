package com.vietqr.org.dto;

public class QrCertificateDTO {
    private String webhook;
    private String merchantIdentify;

    public QrCertificateDTO() {
    }

    public QrCertificateDTO(String webhook, String merchantIdentify) {
        this.webhook = webhook;
        this.merchantIdentify = merchantIdentify;
    }

    public String getWebhook() {
        return webhook;
    }

    public void setWebhook(String webhook) {
        this.webhook = webhook;
    }

    public String getMerchantIdentify() {
        return merchantIdentify;
    }

    public void setMerchantIdentify(String merchantIdentify) {
        this.merchantIdentify = merchantIdentify;
    }
}
