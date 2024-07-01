package com.vietqr.org.entity.qrfeed;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
@Entity
@Table(name = "QrInteraction")
public class QrInteractionEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "qrWalletId")
    private String qrWalletId;
    @Column(name = "userId")
    private String userId;
    @Column(name = "interactionType")
    private int interactionType;
    @Column(name = "timeCreated")
    private long timeCreated;

    public QrInteractionEntity(String id, String qrWalletId, String userId, int interactionType, long timeCreated) {
        this.id = id;
        this.qrWalletId = qrWalletId;
        this.userId = userId;
        this.interactionType = interactionType;
        this.timeCreated = timeCreated;
    }

    public QrInteractionEntity() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQrWalletId() {
        return qrWalletId;
    }

    public void setQrWalletId(String qrWalletId) {
        this.qrWalletId = qrWalletId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getInteractionType() {
        return interactionType;
    }

    public void setInteractionType(int interactionType) {
        this.interactionType = interactionType;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }
}
