package com.vietqr.org.dto;

import java.io.Serializable;

public class AccountCustomerInputDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String merchantName;

    public AccountCustomerInputDTO() {
        super();
    }

    public AccountCustomerInputDTO(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

}
