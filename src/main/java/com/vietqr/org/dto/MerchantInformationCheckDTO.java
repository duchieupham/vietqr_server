package com.vietqr.org.dto;

import java.io.Serializable;

public class MerchantInformationCheckDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String customerSyncId;
    private String merchantName;
    private String ip;
    private String port;
    private String suffix;
    private Boolean isActive;
    private String url;
    private Boolean isMasterMerchant;
    private String accountId;
    private String refId;
    private String platform;

    public MerchantInformationCheckDTO() {
        super();
    }

    public MerchantInformationCheckDTO(String customerSyncId, String merchantName, String ip, String port,
            String suffix, Boolean isActive, String url, Boolean isMasterMerchant, String accountId, String refId,
            String platform) {
        this.customerSyncId = customerSyncId;
        this.merchantName = merchantName;
        this.ip = ip;
        this.port = port;
        this.suffix = suffix;
        this.isActive = isActive;
        this.url = url;
        this.isMasterMerchant = isMasterMerchant;
        this.accountId = accountId;
        this.refId = refId;
        this.platform = platform;
    }

    public String getCustomerSyncId() {
        return customerSyncId;
    }

    public void setCustomerSyncId(String customerSyncId) {
        this.customerSyncId = customerSyncId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getIsMasterMerchant() {
        return isMasterMerchant;
    }

    public void setIsMasterMerchant(Boolean isMasterMerchant) {
        this.isMasterMerchant = isMasterMerchant;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

}
