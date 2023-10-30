package com.vietqr.org.entity.newsfeed;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PostHastag")
public class PostHastagEntity implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "postId")
    private String postId;

    @Column(name = "hastagId")
    private String hastagId;

    public PostHastagEntity() {
        super();
    }

    public PostHastagEntity(String id, String postId, String hastagId) {
        this.id = id;
        this.postId = postId;
        this.hastagId = hastagId;
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

    public String getHastagId() {
        return hastagId;
    }

    public void setHastagId(String hastagId) {
        this.hastagId = hastagId;
    }

}
