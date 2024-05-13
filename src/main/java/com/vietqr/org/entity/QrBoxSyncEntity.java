package com.vietqr.org.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "QrBoxSync")
public class QrBoxSyncEntity implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "certificate")
    private String certificate;

    @Column(name = "macAddress")
    private String macAddress;

    @Column(name = "qrBoxCode")
    private String qrBoxCode;

    @Column(name = "timeCreated")
    private long timeCreated;

    @Column(name = "isActive")
    private boolean isActive;

    @Column(name = "timeSync")
    private long timeSync;

    public QrBoxSyncEntity(String id, String certificate, long timeCreated, long timeSync) {
        this.id = id;
        this.certificate = certificate;
        this.timeCreated = timeCreated;
        this.timeSync = timeSync;
    }

    public QrBoxSyncEntity() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public long getTimeSync() {
        return timeSync;
    }

    public void setTimeSync(long timeSync) {
        this.timeSync = timeSync;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean active) {
        isActive = active;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getQrBoxCode() {
        return qrBoxCode;
    }

    public void setQrBoxCode(String qrBoxCode) {
        this.qrBoxCode = qrBoxCode;
    }
}
