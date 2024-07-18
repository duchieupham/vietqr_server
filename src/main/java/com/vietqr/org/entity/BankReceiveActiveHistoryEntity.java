package com.vietqr.org.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "BankReceiveActiveHistory")
public class BankReceiveActiveHistoryEntity {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "keyId")
    private String keyId;

    @Column(name = "type")
    private int type;

    @Column(name = "keyActive")
    private String keyActive;

    @Column(name = "bankId")
    private String bankId;

    @Column(name = "userId")
    private String userId;

    @Column(name = "createAt")
    private long createAt;

    @Column(name = "validFeeFrom")
    private long validFeeFrom;

    @Column(name = "validFeeTo")
    private long validFeeTo;

    @Column(name = "data")
    private String data;

    @Column(name = "refId")
    private String refId;

    public BankReceiveActiveHistoryEntity() {
    }

    public BankReceiveActiveHistoryEntity(String id, String keyId, String bankId, String userId,
                                          long createAt, long validFeeFrom, long validFeeTo, String data) {
        this.id = id;
        this.keyId = keyId;
        this.bankId = bankId;
        this.userId = userId;
        this.createAt = createAt;
        this.validFeeFrom = validFeeFrom;
        this.validFeeTo = validFeeTo;
        this.data = data;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }

    public long getValidFeeFrom() {
        return validFeeFrom;
    }

    public void setValidFeeFrom(long validFeeFrom) {
        this.validFeeFrom = validFeeFrom;
    }

    public long getValidFeeTo() {
        return validFeeTo;
    }

    public void setValidFeeTo(long validFeeTo) {
        this.validFeeTo = validFeeTo;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getKeyActive() {
        return keyActive;
    }

    public void setKeyActive(String keyActive) {
        this.keyActive = keyActive;
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
}
