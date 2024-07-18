package com.vietqr.org.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
@Entity
@Table(name = "GoogleSheetAccountBank")
public class GoogleSheetAccountBankEntity implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "userId")
    private String userId;

    @Column(name = "googleSheetId")
    private String googleSheetId;

    @Column(name = "bankId")
    private String bankId;

    @Column(name = "webhook")
    private String webhook;

    public GoogleSheetAccountBankEntity() {
        super();
    }


    public GoogleSheetAccountBankEntity(String id, String userId, String googleSheetId, String bankId, String webhook) {
        this.id = id;
        this.userId = userId;
        this.googleSheetId = googleSheetId;
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

    public String getGoogleSheetId() {
        return googleSheetId;
    }

    public void setGoogleSheetId(String googleSheetId) {
        this.googleSheetId = googleSheetId;
    }
}
