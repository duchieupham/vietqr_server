package com.vietqr.org.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "MerchantMember")
public class MerchantMemberEntity {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "merchantId")
    private String merchantId;

    @Column(name = "terminalId")
    private String terminalId;

    @Column(name = "userId")
    private String userId;

    @Column(name = "timeAdded")
    private long timeAdded;

    @Column(name = "isActive")
    private boolean isActive;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public MerchantMemberEntity(String id, String merchantId, String userId, long timeAdded, boolean isActive) {
        this.id = id;
        this.merchantId = merchantId;
        this.userId = userId;
        this.timeAdded = timeAdded;
        this.isActive = isActive;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public MerchantMemberEntity() {
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getTimeAdded() {
        return timeAdded;
    }

    public void setTimeAdded(long timeAdded) {
        this.timeAdded = timeAdded;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
