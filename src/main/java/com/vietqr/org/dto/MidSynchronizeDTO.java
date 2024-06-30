package com.vietqr.org.dto;

public class MidSynchronizeDTO {
    private String merchantFullName;
    private String merchantName;
    private String merchantAddress;
    private String merchantIdentity;
    private String contactEmail;
    private String contactPhone;

    public MidSynchronizeDTO(String merchantFullName, String merchantName, String merchantAddress,
                             String merchantIdentity, String contactEmail, String contactPhone) {
        this.merchantFullName = merchantFullName;
        this.merchantName = merchantName;
        this.merchantAddress = merchantAddress;
        this.merchantIdentity = merchantIdentity;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
    }

    public MidSynchronizeDTO() {
    }

    public String getMerchantFullName() {
        return merchantFullName;
    }

    public void setMerchantFullName(String merchantFullName) {
        this.merchantFullName = merchantFullName;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getMerchantAddress() {
        return merchantAddress;
    }

    public void setMerchantAddress(String merchantAddress) {
        this.merchantAddress = merchantAddress;
    }

    public String getMerchantIdentity() {
        return merchantIdentity;
    }

    public void setMerchantIdentity(String merchantIdentity) {
        this.merchantIdentity = merchantIdentity;
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
}
