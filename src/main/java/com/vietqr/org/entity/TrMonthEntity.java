package com.vietqr.org.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TrMonth")
public class TrMonthEntity {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "trs", columnDefinition = "JSON")
    private String trs;

    @Column(name = "month")
    private String month;

    public TrMonthEntity() {
    }

    public TrMonthEntity(String id, String trs, String month) {
        this.id = id;
        this.trs = trs;
        this.month = month;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTrs() {
        return trs;
    }

    public void setTrs(String trs) {
        this.trs = trs;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }
}
