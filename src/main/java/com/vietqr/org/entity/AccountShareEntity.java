package com.vietqr.org.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "AccountShare")
public class AccountShareEntity implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "sharingCode")
    private String sharingCode;

    // new user ID
    @Column(name = "userId")
    private String userId;

    public AccountShareEntity() {
        super();
    }

    public AccountShareEntity(String id, String sharingCode, String userId) {
        this.id = id;
        this.sharingCode = sharingCode;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSharingCode() {
        return sharingCode;
    }

    public void setSharingCode(String sharingCode) {
        this.sharingCode = sharingCode;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
