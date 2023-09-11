package com.vietqr.org.dto;

import java.io.Serializable;

public class CustomerSyncUpdateDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String url;
    private String ip;
    private String password;
    private String port;
    private String suffix;
    private String username;
    private String customerSyncId;

    public CustomerSyncUpdateDTO() {
        super();
    }

    public CustomerSyncUpdateDTO(String url, String ip, String password, String port, String suffix, String username,
            String customerSyncId) {
        this.url = url;
        this.ip = ip;
        this.password = password;
        this.port = port;
        this.suffix = suffix;
        this.username = username;
        this.customerSyncId = customerSyncId;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCustomerSyncId() {
        return customerSyncId;
    }

    public void setCustomerSyncId(String customerSyncId) {
        this.customerSyncId = customerSyncId;
    }

}
