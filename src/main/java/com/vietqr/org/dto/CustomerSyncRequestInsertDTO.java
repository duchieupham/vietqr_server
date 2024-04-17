package com.vietqr.org.dto;

import java.io.Serializable;

public class CustomerSyncRequestInsertDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String userId;
    private String merchantName;
    private String url;
    private String ip;
    private String port;
    private String suffixUrl;
    private String address;
    private String bankAccount;
    private String userBankName;
    private String customerUsername;
    private String customerPassword;
    private String systemUsername;
    private int customerSyncActive;

    public CustomerSyncRequestInsertDTO() {
        super();
    }

    public CustomerSyncRequestInsertDTO(String userId, String merchantName, String url, String ip, String port,
            String suffixUrl, String address, String bankAccount, String userBankName, String customerUsername,
            String customerPassword, String systemUsername, int customerSyncActive) {
        this.userId = userId;
        this.merchantName = merchantName;
        this.url = url;
        this.ip = ip;
        this.port = port;
        this.suffixUrl = suffixUrl;
        this.address = address;
        this.bankAccount = bankAccount;
        this.userBankName = userBankName;
        this.customerUsername = customerUsername;
        this.customerPassword = customerPassword;
        this.systemUsername = systemUsername;
        this.customerSyncActive = customerSyncActive;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public String getSuffixUrl() {
        return suffixUrl;
    }

    public void setSuffixUrl(String suffixUrl) {
        this.suffixUrl = suffixUrl;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getUserBankName() {
        return userBankName;
    }

    public void setUserBankName(String userBankName) {
        this.userBankName = userBankName;
    }

    public String getCustomerUsername() {
        return customerUsername;
    }

    public void setCustomerUsername(String customerUsername) {
        this.customerUsername = customerUsername;
    }

    public String getCustomerPassword() {
        return customerPassword;
    }

    public void setCustomerPassword(String customerPassword) {
        this.customerPassword = customerPassword;
    }

    public String getSystemUsername() {
        return systemUsername;
    }

    public void setSystemUsername(String systemUsername) {
        this.systemUsername = systemUsername;
    }

    public int getCustomerSyncActive() {
        return customerSyncActive;
    }

    public void setCustomerSyncActive(int customerSyncActive) {
        this.customerSyncActive = customerSyncActive;
    }

}
