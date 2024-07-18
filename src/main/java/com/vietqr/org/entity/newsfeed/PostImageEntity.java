package com.vietqr.org.entity.newsfeed;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PostImage")
public class PostImageEntity implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "postId")
    private String postId;

    @Column(name = "index")
    private Integer index;

    @Column(name = "imgPostId")
    private String imgPostId;

    public PostImageEntity() {
        super();
    }

    public PostImageEntity(String id, String postId, Integer index, String imgPostId) {
        this.id = id;
        this.postId = postId;
        this.index = index;
        this.imgPostId = imgPostId;
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

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getImgPostId() {
        return imgPostId;
    }

    public void setImgPostId(String imgPostId) {
        this.imgPostId = imgPostId;
    }

}
