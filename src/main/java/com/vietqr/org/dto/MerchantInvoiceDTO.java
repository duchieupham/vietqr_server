package com.vietqr.org.dto;

public class MerchantInvoiceDTO {
    private String merchantId;
    private String merchantName;
    private String vsoCode;
    private Integer numberOfBank;
    private String platform;

    public MerchantInvoiceDTO() {
    }

    public MerchantInvoiceDTO(String merchantId, String merchantName,
                              String vsoCode, int numberOfBank, String platform) {
        this.merchantId = merchantId;
        this.merchantName = merchantName;
        this.vsoCode = vsoCode;
        this.numberOfBank = numberOfBank;
        this.platform = platform;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getVsoCode() {
        return vsoCode;
    }

    public void setVsoCode(String vsoCode) {
        this.vsoCode = vsoCode;
    }

    public Integer getNumberOfBank() {
        return numberOfBank;
    }

    public void setNumberOfBank(Integer numberOfBank) {
        this.numberOfBank = numberOfBank;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }
}
