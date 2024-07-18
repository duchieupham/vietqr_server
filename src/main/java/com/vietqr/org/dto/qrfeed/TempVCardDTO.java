package com.vietqr.org.dto.qrfeed;

import com.google.gson.Gson;

import java.io.Serializable;

public class TempVCardDTO implements Serializable {
    private String fullName;
    private String phoneNo;
    private String email;
    private String companyName;
    private String website;
    private String value;
    private String address;

    public TempVCardDTO() {
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
