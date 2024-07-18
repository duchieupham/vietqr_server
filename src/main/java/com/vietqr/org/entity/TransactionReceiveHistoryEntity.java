package com.vietqr.org.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "TransactionReceiveHistory")
public class TransactionReceiveHistoryEntity implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "transactionReceiveId")
    private String transactionReceiveId;

    @Column(name = "userId")
    private String userId;

    @Column(name = "data1")
    private String data1;
    @Column(name = "data2")
    private String data2;
    @Column(name = "data3")
    private String data3;
    // 0: init, 1: request, 2: approve, 3: error, 4: refund, 5, processing
    @Column(name = "type")
    private int type;
    @Column(name = "timeUpdated")
    private long timeUpdated;

    public TransactionReceiveHistoryEntity(String id, String transactionReceiveId,
                                           String userId, String data1, String data2,
                                           String data3, int type, long timeUpdated) {
        this.id = id;
        this.transactionReceiveId = transactionReceiveId;
        this.userId = userId;
        this.data1 = data1;
        this.data2 = data2;
        this.data3 = data3;
        this.type = type;
        this.timeUpdated = timeUpdated;
    }

    public TransactionReceiveHistoryEntity() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTransactionReceiveId() {
        return transactionReceiveId;
    }

    public void setTransactionReceiveId(String transactionReceiveId) {
        this.transactionReceiveId = transactionReceiveId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getData1() {
        return data1;
    }

    public void setData1(String data1) {
        this.data1 = data1;
    }

    public String getData2() {
        return data2;
    }

    public void setData2(String data2) {
        this.data2 = data2;
    }

    public String getData3() {
        return data3;
    }

    public void setData3(String data3) {
        this.data3 = data3;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getTimeUpdated() {
        return timeUpdated;
    }

    public void setTimeUpdated(long timeUpdated) {
        this.timeUpdated = timeUpdated;
    }
}
