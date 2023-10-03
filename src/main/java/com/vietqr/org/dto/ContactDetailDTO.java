package com.vietqr.org.dto;

import java.io.Serializable;

public class ContactDetailDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String id;
    private String nickname;
    private String value;
    private String additionalData;
    private int type;
    private int status;
    private String bankShortName;
    private String bankName;
    private String imgId;
    private String bankAccount;
    private int colorType;
    private Integer relation;
    // for vcard
    private String email;
    private String address;
    private String company;
    private String website;
    private String phoneNo;

    public ContactDetailDTO() {
        super();
    }

    public ContactDetailDTO(String id, String nickname, String value, String additionalData, int type, int status,
            String bankShortName, String bankName, String imgId, String bankAccount, int colorType, Integer relation) {
        this.id = id;
        this.nickname = nickname;
        this.value = value;
        this.additionalData = additionalData;
        this.type = type;
        this.status = status;
        this.bankShortName = bankShortName;
        this.bankName = bankName;
        this.imgId = imgId;
        this.bankAccount = bankAccount;
        this.colorType = colorType;
        this.relation = relation;
    }

    public ContactDetailDTO(String id, String nickname, String value, String additionalData, int type, int status,
            String bankShortName, String bankName, String imgId, String bankAccount, int colorType, Integer relation,
            String email, String address, String company, String website, String phoneNo) {
        this.id = id;
        this.nickname = nickname;
        this.value = value;
        this.additionalData = additionalData;
        this.type = type;
        this.status = status;
        this.bankShortName = bankShortName;
        this.bankName = bankName;
        this.imgId = imgId;
        this.bankAccount = bankAccount;
        this.colorType = colorType;
        this.relation = relation;
        this.email = email;
        this.address = address;
        this.company = company;
        this.website = website;
        this.phoneNo = phoneNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getBankShortName() {
        return bankShortName;
    }

    public void setBankShortName(String bankShortName) {
        this.bankShortName = bankShortName;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getImgId() {
        return imgId;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public int getColorType() {
        return colorType;
    }

    public void setColorType(int colorType) {
        this.colorType = colorType;
    }

    public Integer getRelation() {
        return relation;
    }

    public void setRelation(Integer relation) {
        this.relation = relation;
    }

}
