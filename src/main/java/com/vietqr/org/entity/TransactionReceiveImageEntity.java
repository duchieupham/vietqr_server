package com.vietqr.org.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TransactionReceiveImage")
public class TransactionReceiveImageEntity implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "transactionReceiveId")
    private String transactionReceiveId;

    @Column(name = "imgId")
    private String imgId;

    public TransactionReceiveImageEntity() {
        super();
    }

    public TransactionReceiveImageEntity(String id, String transactionReceiveId, String imgId) {
        this.id = id;
        this.transactionReceiveId = transactionReceiveId;
        this.imgId = imgId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTransactionReceiveId() {
        return transactionReceiveId;
    }

    public void setTransactionReceiveId(String transactionReceiveId) {
        this.transactionReceiveId = transactionReceiveId;
    }

    public String getImgId() {
        return imgId;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }

}
