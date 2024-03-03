package com.vietqr.org.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TerminalStatistic")
public class TerminalStatisticEntity {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "terminalId")
    private String terminalId;

    @Column(name = "time")
    private long time;

    @Column(name = "totalTrans")
    private int totalTrans;

    @Column(name = "totalAmount")
    private long totalAmount;

    @Column(name = "version")
    private int version;

    public TerminalStatisticEntity() {
    }

    public TerminalStatisticEntity(String id, String terminalId, long time, int totalTrans, long totalAmount, int version) {
        this.id = id;
        this.terminalId = terminalId;
        this.time = time;
        this.totalTrans = totalTrans;
        this.totalAmount = totalAmount;
        this.version = version;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getTotalTrans() {
        return totalTrans;
    }

    public void setTotalTrans(int totalTrans) {
        this.totalTrans = totalTrans;
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
