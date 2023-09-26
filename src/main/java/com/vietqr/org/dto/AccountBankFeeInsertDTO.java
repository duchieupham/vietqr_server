package com.vietqr.org.dto;

import java.io.Serializable;

public class AccountBankFeeInsertDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private int insertType;
    private String bankId;
    private String customerSyncId;
    private String serviceFeeId;

    public AccountBankFeeInsertDTO() {
        super();
    }

    public AccountBankFeeInsertDTO(int insertType, String bankId, String customerSyncId, String serviceFeeId) {
        this.insertType = insertType;
        this.bankId = bankId;
        this.customerSyncId = customerSyncId;
        this.serviceFeeId = serviceFeeId;
    }

    public int getInsertType() {
        return insertType;
    }

    public void setInsertType(int insertType) {
        this.insertType = insertType;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getCustomerSyncId() {
        return customerSyncId;
    }

    public void setCustomerSyncId(String customerSyncId) {
        this.customerSyncId = customerSyncId;
    }

    public String getServiceFeeId() {
        return serviceFeeId;
    }

    public void setServiceFeeId(String serviceFeeId) {
        this.serviceFeeId = serviceFeeId;
    }

}
