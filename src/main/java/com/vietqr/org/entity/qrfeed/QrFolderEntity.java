package com.vietqr.org.entity.qrfeed;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "QrFolder")
public class QrFolderEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "title")
    private String title;
    @Column(name = "description")
    private String description;
    @Column(name = "timeCreated")
    private long timeCreated;
    @Column(name = "userData", columnDefinition = "JSON")
    private String userData;
    @Column(name = "userId")
    private String userId;

    public QrFolderEntity() {
    }

    public QrFolderEntity(String id, String title, String description, long timeCreated, String userData, String userId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.timeCreated = timeCreated;
        this.userData = userData;
        this.userId = userId;
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

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String getUserData() {
        return userData;
    }

    public void setUserData(String userData) {
        this.userData = userData;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
