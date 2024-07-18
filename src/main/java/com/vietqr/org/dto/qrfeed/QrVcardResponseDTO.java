package com.vietqr.org.dto.qrfeed;

import java.io.Serializable;

public class QrVcardResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String fullname;
    private String phoneNo;
    private String email;
    private String companyName;
    private String website;
    private String address;

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public QrVcardResponseDTO(String fullname, String phoneNo, String email, String companyName, String website, String address) {
        this.fullname = fullname;
        this.phoneNo = phoneNo;
        this.email = email;
        this.companyName = companyName;
        this.website = website;
        this.address = address;
    }

    public QrVcardResponseDTO() {
        super();
    }
}
