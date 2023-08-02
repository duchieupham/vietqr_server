package com.vietqr.org.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ReportImage")
public class ReportImageEntity implements Serializable {

    /**
    *
    */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "reportId")
    private String reportId;

    @Column(name = "imgId")
    private String imgId;

    public ReportImageEntity() {
        super();
    }

    public ReportImageEntity(String id, String reportId, String imgId) {
        this.id = id;
        this.reportId = reportId;
        this.imgId = imgId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getImgId() {
        return imgId;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }

}
