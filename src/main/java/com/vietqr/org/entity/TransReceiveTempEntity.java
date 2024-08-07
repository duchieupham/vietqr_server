package com.vietqr.org.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TransReceiveTemp")
public class TransReceiveTempEntity {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "bankId")
    private String bankId;

    @Column(name = "transIds")
    private String transIds;

    @Column(name = "lastTimes")
    private long lastTimes;

    @Column(name = "nums")
    private int nums;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getTransIds() {
        return transIds;
    }

    public void setTransIds(String transIds) {
        this.transIds = transIds;
    }

    public long getLastTimes() {
        return lastTimes;
    }

    public void setLastTimes(long lastTimes) {
        this.lastTimes = lastTimes;
    }

    public int getNums() {
        return nums;
    }

    public void setNums(int nums) {
        this.nums = nums;
    }
}
