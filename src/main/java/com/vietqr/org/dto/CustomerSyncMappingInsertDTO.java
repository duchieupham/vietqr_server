package com.vietqr.org.dto;

import java.io.Serializable;

public class CustomerSyncMappingInsertDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String userId;
    private String customerSyncId;
    private String customerSyncTestId;
    // env = 1: test
    // env = 2: golive
    private Integer environment;

    public CustomerSyncMappingInsertDTO() {
        super();
    }

    public CustomerSyncMappingInsertDTO(String userId, String customerSyncId, String customerSyncTestId,
            Integer environment) {
        this.userId = userId;
        this.customerSyncId = customerSyncId;
        this.customerSyncTestId = customerSyncTestId;
        this.environment = environment;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public Integer getEnvironment() {
        return environment;
    }

    public void setEnvironment(Integer environment) {
        this.environment = environment;
    }

}
