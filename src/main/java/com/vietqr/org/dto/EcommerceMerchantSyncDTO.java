package com.vietqr.org.dto;

public class EcommerceMerchantSyncDTO {
    private String ecommerceSite;
    private String checkSum;
    private String webhook;

    public EcommerceMerchantSyncDTO() {
    }

    public EcommerceMerchantSyncDTO(String ecommerceSite, String checkSum) {
        this.ecommerceSite = ecommerceSite;
        this.checkSum = checkSum;
    }

    public String getEcommerceSite() {
        return ecommerceSite;
    }

    public void setEcommerceSite(String ecommerceSite) {
        this.ecommerceSite = ecommerceSite;
    }

    public String getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(String checkSum) {
        this.checkSum = checkSum;
    }

    public String getWebhook() {
        return webhook;
    }

    public void setWebhook(String webhook) {
        this.webhook = webhook;
    }
}
