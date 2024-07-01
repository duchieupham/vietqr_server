package com.vietqr.org.entity.qrfeed;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
@Entity
@Table(name = "QrUser")
public class QrUserEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "qrWalletId")
    private String qrWalletId;
    @Column(name = "userId")
    private String userId;
    @Column(name = "role")
    private String role;

    public QrUserEntity() {
    }

    public QrUserEntity(String id, String qrWalletId, String userId, String role) {
        this.id = id;
        this.qrWalletId = qrWalletId;
        this.userId = userId;
        this.role = role;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
