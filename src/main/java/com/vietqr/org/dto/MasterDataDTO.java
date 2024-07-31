package com.vietqr.org.dto;

public class MasterDataDTO {
    private String mid;
    private String merchantName;
    private String merchantFullName;
    private String merchantAddress;
    private String contactEmail;
    private String contactPhone;
    private String merchantIdentify;
    private String certificate;
    private String webhook;
    private String clientId;

    public MasterDataDTO() {
    }

    public MasterDataDTO(String mid, String merchantName, String merchantFullName, String merchantAddress,
                         String contactEmail, String contactPhone, String merchantIdentify, String certificate, String webhook, String clientId) {
        this.mid = mid;
        this.merchantName = merchantName;
        this.merchantFullName = merchantFullName;
        this.merchantAddress = merchantAddress;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
        this.merchantIdentify = merchantIdentify;
        this.certificate = certificate;
        this.webhook = webhook;
        this.clientId = clientId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getMerchantFullName() {
        return merchantFullName;
    }

    public void setMerchantFullName(String merchantFullName) {
        this.merchantFullName = merchantFullName;
    }

    public String getMerchantAddress() {
        return merchantAddress;
    }

    public void setMerchantAddress(String merchantAddress) {
        this.merchantAddress = merchantAddress;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getMerchantIdentify() {
        return merchantIdentify;
    }

    public void setMerchantIdentify(String merchantIdentify) {
        this.merchantIdentify = merchantIdentify;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
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
