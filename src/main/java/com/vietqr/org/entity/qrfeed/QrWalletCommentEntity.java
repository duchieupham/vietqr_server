package com.vietqr.org.entity.qrfeed;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "QrWalletComment")
public class QrWalletCommentEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "qrWalletId")
    private String qrWalletId;
    @Column(name = "qrCommentId")
    private String qrCommentId;

    public QrWalletCommentEntity(String id, String qrWalletId, String qrCommentId) {
        this.id = id;
        this.qrWalletId = qrWalletId;
        this.qrCommentId = qrCommentId;
    }

    public QrWalletCommentEntity() {
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

    public String getQrCommentId() {
        return qrCommentId;
    }

    public void setQrCommentId(String qrCommentId) {
        this.qrCommentId = qrCommentId;
    }
}
