package com.vietqr.org.dto;

import java.io.Serializable;

public class MerchantInformationCheckDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String id;
    private String customerSyncId;
    private String customerSyncTestId;
    private String userId;
    // private String merchantName;
    // private String ip;
    // private String port;
    // private String suffix;
    // private Boolean isActive;
    // private String url;
    // private Boolean isMasterMerchant;
    // private String accountId;
    // private String refId;
    // private String platform;

    public MerchantInformationCheckDTO() {
        super();
    }

    public MerchantInformationCheckDTO(String id, String customerSyncId, String customerSyncTestId, String userId) {
        this.id = id;
        this.customerSyncId = customerSyncId;
        this.customerSyncTestId = customerSyncTestId;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerSyncId() {
        return customerSyncId;
    }

    public void setCustomerSyncId(String customerSyncId) {
        this.customerSyncId = customerSyncId;
    }

    public String getCustomerSyncTestId() {
        return customerSyncTestId;
    }

    public void setCustomerSyncTestId(String customerSyncTestId) {
        this.customerSyncTestId = customerSyncTestId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
