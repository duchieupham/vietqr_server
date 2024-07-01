package com.vietqr.org.entity.qrfeed;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
@Entity
@Table(name = "QrComment")
public class QrCommentEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "message")
    private String message;
    @Column(name = "userId")
    private String userId;
    @Column(name = "userData", columnDefinition = "JSON")
    private String userData;
    @Column(name = "timeCreated")
    private long timeCreated;

    public QrCommentEntity() {
    }

    public QrCommentEntity(String id, String message, String userId, String userData, long timeCreated) {
        this.id = id;
        this.message = message;
        this.userId = userId;
        this.userData = userData;
        this.timeCreated = timeCreated;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserData() {
        return userData;
    }

    public void setUserData(String userData) {
        this.userData = userData;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }
}
