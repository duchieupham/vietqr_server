package com.vietqr.org.dto;

import java.io.Serializable;

public class ContactRelationUpdateDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String id;

    private Integer relation;

    public ContactRelationUpdateDTO() {
        super();
    }

    public ContactRelationUpdateDTO(String id, Integer relation) {
        this.id = id;
        this.relation = relation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getRelation() {
        return relation;
    }

    public void setRelation(Integer relation) {
        this.relation = relation;
    }

}
