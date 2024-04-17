package com.vietqr.org.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "MobileCarrier")
public class MobileCarrierEntity implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "prefix")
    private String prefix;

    @Column(name = "typeId")
    private String typeId;

    public MobileCarrierEntity() {
        super();
    }

    public MobileCarrierEntity(String id, String prefix, String typeId) {
        this.id = id;
        this.prefix = prefix;
        this.typeId = typeId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setType(String typeId) {
        this.typeId = typeId;
    }

}
