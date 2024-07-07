package com.vietqr.org.entity.qrfeed;


import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "QrFolderUser")
public class QrFolderUserEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "qrFolderId")
    private String qrFolderId;
    @Column(name = "userId")
    private String userId;

    public QrFolderUserEntity() {
    }

    public QrFolderUserEntity(String id, String qrFolderId, String userId) {
        this.id = id;
        this.qrFolderId = qrFolderId;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQrFolderId() {
        return qrFolderId;
    }

    public void setQrFolderId(String qrFolderId) {
        this.qrFolderId = qrFolderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
