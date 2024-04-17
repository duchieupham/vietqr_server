package com.vietqr.org.dto;

import java.io.Serializable;

public class ContactInsertDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String additionalData;
    private String nickName;
    private int type;
    private String userId;
    private String value;
    private String bankTypeId;
    private String bankAccount;

    public ContactInsertDTO() {
        super();
    }

    public ContactInsertDTO(String additionalData, String nickName, int type, String userId, String value,
            String bankTypeId, String bankAccount) {
        this.additionalData = additionalData;
        this.nickName = nickName;
        this.type = type;
        this.userId = userId;
        this.value = value;
        this.bankTypeId = bankTypeId;
        this.bankAccount = bankAccount;
    }

    public String getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(String additionalData) {
        this.additionalData = additionalData;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
