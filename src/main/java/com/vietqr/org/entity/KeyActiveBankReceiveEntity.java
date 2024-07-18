package com.vietqr.org.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "KeyActiveBankReceive")
public class KeyActiveBankReceiveEntity implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id")
    private String id;

    //0 : inactive
    //1 : active
    @Column(name = "status")
    private int status;

    @Column(name = "duration")
    private int duration;

    @Column(name = "keyActive")
    private String keyActive;

    @Column(name = "valueActive")
    private String valueActive;

    @Column(name = "secretKey")
    private String secretKey;

    @Column(name = "createAt")
    private long createAt;

    @Column(name = "version")
    private int version;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getKeyActive() {
        return keyActive;
    }

    public void setKeyActive(String keyActive) {
        this.keyActive = keyActive;
    }

    public String getValueActive() {
        return valueActive;
    }

    public void setValueActive(String valueActive) {
        this.valueActive = valueActive;
    }

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
