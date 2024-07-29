package com.vietqr.org.dto;

public class MidSynchronizeV2DTO extends MidSynchronizeDTO{
    private String career;
    private String webhook;
    private String certificate;

    public MidSynchronizeV2DTO(String merchantFullName, String merchantName, String merchantAddress, String merchantIdentity, String contactEmail, String contactPhone) {
        super(merchantFullName, merchantName, merchantAddress, merchantIdentity, contactEmail, contactPhone);
    }

    public MidSynchronizeV2DTO() {
    }

    public MidSynchronizeV2DTO(String merchantFullName, String merchantName, String merchantAddress, String merchantIdentity, String contactEmail, String contactPhone, String career, String webhook, String certificate) {
        super(merchantFullName, merchantName, merchantAddress, merchantIdentity, contactEmail, contactPhone);
        this.career = career;
        this.webhook = webhook;
        this.certificate = certificate;
    }

    public MidSynchronizeV2DTO(String career, String webhook, String certificate) {
        this.career = career;
        this.webhook = webhook;
        this.certificate = certificate;
    }

    public String getCareer() {
        return career;
    }

    public void setCareer(String career) {
        this.career = career;
    }

    public String getWebhook() {
        return webhook;
    }

    public void setWebhook(String webhook) {
        this.webhook = webhook;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }
}
