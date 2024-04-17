package com.vietqr.org.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "AccountWallet")
public class AccountWalletEntity implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "userId")
    private String userId;

    // public ID
    @Column(name = "walletId")
    private String walletId;

    @Column(name = "amount")
    private String amount;

    @Column(name = "enableService")
    private boolean enableService;

    @Column(name = "isActive")
    private boolean isActive;

    @Column(name = "point")
    private long point;

    @Column(name = "sharingCode")
    private String sharingCode;

    public AccountWalletEntity() {
        super();
    }

    public AccountWalletEntity(String id, String userId, String walletId, String amount, boolean enableService,
            boolean isActive, long point, String sharingCode) {
        this.id = id;
        this.userId = userId;
        this.walletId = walletId;
        this.amount = amount;
        this.enableService = enableService;
        this.isActive = isActive;
        this.point = point;
        this.sharingCode = sharingCode;
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

    public String getWalletId() {
        return walletId;
    }

    public void setWalletId(String walletId) {
        this.walletId = walletId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public boolean isEnableService() {
        return enableService;
    }

    public void setEnableService(boolean enableService) {
        this.enableService = enableService;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public long getPoint() {
        return point;
    }

    public void setPoint(long point) {
        this.point = point;
    }

    public String getSharingCode() {
        return sharingCode;
    }

    public void setSharingCode(String sharingCode) {
        this.sharingCode = sharingCode;
    }

}
