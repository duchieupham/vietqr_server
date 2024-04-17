package com.vietqr.org.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "MerchantBankReceive")
public class MerchantBankReceiveEntity {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "merchantId")
    private String merchantId;

    @Column(name = "bankId")
    private String bankId;

    public MerchantBankReceiveEntity(String id, String merchantId, String bankId) {
        this.id = id;
        this.merchantId = merchantId;
        this.bankId = bankId;
    }

    public MerchantBankReceiveEntity() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }
}
