package com.vietqr.org.entity.newsfeed;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PostComment")
public class PostCommentEntity implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "postId")
    private String postId;

    @Column(name = "comment", columnDefinition = "LONGTEXT")
    private String comment;

    @Column(name = "timeCreated")
    private Long timeCreated;

    public PostCommentEntity() {
        super();
    }

    public PostCommentEntity(String id, String postId, String comment, Long timeCreated) {
        this.id = id;
        this.postId = postId;
        this.comment = comment;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Long timeCreated) {
        this.timeCreated = timeCreated;
    }

}
