package com.vietqr.org.entity.qrfeed;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "QrWallet")
public class QrWalletEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "title")
    private String title;
    @Column(name = "description")
    private String description;
    @Column(name = "value", columnDefinition = "LONGTEXT")
    private String value;
    @Column(name = "qrType")
    private String qrType;
    @Column(name = "qrData", columnDefinition = "JSON")
    private String qrData;
    @Column(name = "userData", columnDefinition = "JSON")
    private String userData;
    @Column(name = "isPublic")
    private int isPublic;
    @Column(name = "timeCreated")
    private long timeCreated;
    @Column(name = "userId")
    private String userId;
    @Column(name = "pin")
    private String pin;
    @Column(name = "publicId")
    private String publicId;

    public QrWalletEntity() {
    }

    public QrWalletEntity(String id, String title, String description, String value, String qrType, String qrData, String userData, int isPublic, long timeCreated, String userId, String pin, String publicId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.value = value;
        this.qrType = qrType;
        this.qrData = qrData;
        this.userData = userData;
        this.isPublic = isPublic;
        this.timeCreated = timeCreated;
        this.userId = userId;
        this.pin = pin;
        this.publicId = publicId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getQrType() {
        return qrType;
    }

    public void setQrType(String qrType) {
        this.qrType = qrType;
    }

    public String getQrData() {
        return qrData;
    }

    public void setQrData(String qrData) {
        this.qrData = qrData;
    }

    public String getUserData() {
        return userData;
    }

    public void setUserData(String userData) {
        this.userData = userData;
    }

    public int getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(int isPublic) {
        this.isPublic = isPublic;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getPublicId() {
        return publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }
}
