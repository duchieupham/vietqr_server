package com.vietqr.org.entity.newsfeed;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CategoryPost")
public class CategoryPostEntity implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "title")
    private String title;

    @Column(name = "imgPostId")
    private String imgPostId;

    @Column(name = "description", columnDefinition = "LONGTEXT")
    private String description;

    @Column(name = "sub")
    private Boolean sub;

    @Column(name = "refId")
    private String refId;

    public CategoryPostEntity() {
        super();
    }

    public CategoryPostEntity(String id, String title, String imgPostId, String description, Boolean sub,
            String refId) {
        this.id = id;
        this.title = title;
        this.imgPostId = imgPostId;
        this.description = description;
        this.sub = sub;
        this.refId = refId;
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

    public String getImgPostId() {
        return imgPostId;
    }

    public void setImgPostId(String imgPostId) {
        this.imgPostId = imgPostId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getSub() {
        return sub;
    }

    public void setSub(Boolean sub) {
        this.sub = sub;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

}
