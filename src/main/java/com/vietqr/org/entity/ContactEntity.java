package com.vietqr.org.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Contact")
public class ContactEntity implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "userId")
    private String userId;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "value")
    private String value;

    @Column(name = "additionalData")
    private String additionalData;

    @Column(name = "type")
    private int type;

    @Column(name = "status")
    private int status;

    @Column(name = "time")
    private long time;

    // if type == 2 - bank => add swiftCode and bankAccount
    @Column(name = "bankTypeId")
    private String bankTypeId;

    @Column(name = "bankAccount")
    private String bankAccount;

    @Column(name = "imgId")
    private String imgId;

    @Column(name = "colorType")
    private int colorType;

    public ContactEntity() {
        super();
    }

    public ContactEntity(String id, String userId, String nickname, String value, String additionalData, int type,
            int status, long time, String bankTypeId, String bankAccount) {
        this.id = id;
        this.userId = userId;
        this.nickname = nickname;
        this.value = value;
        this.additionalData = additionalData;
        this.type = type;
        this.status = status;
        this.time = time;
        this.bankTypeId = bankTypeId;
        this.bankAccount = bankAccount;
    }

    public ContactEntity(String id, String userId, String nickname, String value, String additionalData, int type,
            int status, long time, String bankTypeId, String bankAccount, String imgId, int colorType) {
        this.id = id;
        this.userId = userId;
        this.nickname = nickname;
        this.value = value;
        this.additionalData = additionalData;
        this.type = type;
        this.status = status;
        this.time = time;
        this.bankTypeId = bankTypeId;
        this.bankAccount = bankAccount;
        this.imgId = imgId;
        this.colorType = colorType;
    }

    public String getImgId() {
        return imgId;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }

    public int getColorType() {
        return colorType;
    }

    public void setColorType(int colorType) {
        this.colorType = colorType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(String additionalData) {
        this.additionalData = additionalData;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getBankTypeId() {
        return bankTypeId;
    }

    public void setBankTypeId(String bankTypeId) {
        this.bankTypeId = bankTypeId;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

}
