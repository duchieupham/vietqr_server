package com.vietqr.org.entity.newsfeed;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PostLike")
public class PostLikeEntity implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "postId")
    private String postId;

    @Column(name = "userId")
    private String userId;

    @Column(name = "timeCreated")
    private Long timeCreated;

    public PostLikeEntity() {
        super();
    }

    public PostLikeEntity(String id, String postId, String userId, Long timeCreated) {
        this.id = id;
        this.postId = postId;
        this.userId = userId;
        this.timeCreated = timeCreated;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Long timeCreated) {
        this.timeCreated = timeCreated;
    }

}
