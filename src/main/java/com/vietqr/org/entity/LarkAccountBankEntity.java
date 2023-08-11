package com.vietqr.org.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "LarkAccountBank")
public class LarkAccountBankEntity implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "userId")
    private String userId;

    @Column(name = "larkId")
    private String larkId;

    @Column(name = "bankId")
    private String bankId;

    @Column(name = "webhook")
    private String webhook;

    public LarkAccountBankEntity() {
        super();
    }

    public LarkAccountBankEntity(String id, String userId, String larkId, String bankId, String webhook) {
        this.id = id;
        this.userId = userId;
        this.larkId = larkId;
        this.bankId = bankId;
        this.webhook = webhook;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLarkId() {
        return larkId;
    }

    public void setLarkId(String larkId) {
        this.larkId = larkId;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getWebhook() {
        return webhook;
    }

    public void setWebhook(String webhook) {
        this.webhook = webhook;
    }

}
