package com.vietqr.org.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "ProcessedInvoice")
public class ProcessedInvoiceEntity implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id ;

    @Column(name = "bankId")
    private String bankId;

    // 0: Phí thường niên
    // 1: Phí giao dịch
    @Column(name = "type")
    private int type;

    // id của item trong invoice item
    @Column(name = "refId")
    private String refId;

    @Column(name = "mid")
    private String mid;

    @Column(name = "userId")
    private String userId;

    @Column(name = "timeCreated")
    private long timeCreated;

    // Thời gian của item (format yyyyMM) 202405
    @Column(name = "processDate")
    private String processDate;

    public ProcessedInvoiceEntity() {
    }

    public ProcessedInvoiceEntity(String id, String bankId, int type, String refId,
                                  String mid, String userId, long timeCreated, String processDate) {
        this.id = id;
        this.bankId = bankId;
        this.type = type;
        this.refId = refId;
        this.mid = mid;
        this.userId = userId;
        this.timeCreated = timeCreated;
        this.processDate = processDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String getProcessDate() {
        return processDate;
    }

    public void setProcessDate(String processDate) {
        this.processDate = processDate;
    }
}
