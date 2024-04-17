package com.vietqr.org.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "AccountCustomerMerchant")
public class AccountCustomerMerchantEntity implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "account_customer_id")
    private String accountCustomerId;

    @Column(name = "merchant_id")
    private String merchantId;

    public AccountCustomerMerchantEntity() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public AccountCustomerMerchantEntity(String id, String accountCustomerId, String merchantId) {
        this.id = id;
        this.accountCustomerId = accountCustomerId;
        this.merchantId = merchantId;
    }

    public String getAccountCustomerId() {
        return accountCustomerId;
    }

    public void setAccountCustomerId(String accountCustomerId) {
        this.accountCustomerId = accountCustomerId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }
}
