package com.vietqr.org.entity.qrfeed;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "QrWalletFolder")
public class QrWalletFolderEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "qrWalletId")
    private String qrWalletId;
    @Column(name = "qrFolderId")
    private String qrFolderId;

    public QrWalletFolderEntity() {
    }

    public QrWalletFolderEntity(String id, String qrWalletId, String qrFolderId) {
        this.id = id;
        this.qrWalletId = qrWalletId;
        this.qrFolderId = qrFolderId;
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

    public String getQrFolderId() {
        return qrFolderId;
    }

    public void setQrFolderId(String qrFolderId) {
        this.qrFolderId = qrFolderId;
    }
}
