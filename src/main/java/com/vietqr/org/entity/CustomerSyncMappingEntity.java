package com.vietqr.org.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CustomerSyncMapping")
public class CustomerSyncMappingEntity implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "userId")
    private String userId;

    @Column(name = "cusSyncTestId")
    private String cusSyncTestId;

    @Column(name = "cusSyncId")
    private String cusSyncId;

    public CustomerSyncMappingEntity() {
        super();
    }

    public CustomerSyncMappingEntity(String id, String userId, String cusSyncTestId, String cusSyncId) {
        this.id = id;
        this.userId = userId;
        this.cusSyncTestId = cusSyncTestId;
        this.cusSyncId = cusSyncId;
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

    public String getCusSyncTestId() {
        return cusSyncTestId;
    }

    public void setCusSyncTestId(String cusSyncTestId) {
        this.cusSyncTestId = cusSyncTestId;
    }

    public String getCusSyncId() {
        return cusSyncId;
    }

    public void setCusSyncId(String cusSyncId) {
        this.cusSyncId = cusSyncId;
    }

}
