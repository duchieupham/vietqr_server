package com.vietqr.org.dto.qrfeed;

import java.io.Serializable;

public class QrVcardRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String qr;
    private String fullname;
    private String phoneNo;
    private String email;
    private String companyName;
    private String website;
    private String address;
    private int isPublic;

    public QrVcardRequestDTO(String qr, String fullname, String phoneNo, String email, String companyName, String website, String address, int isPublic) {
        this.qr = qr;
        this.fullname = fullname;
        this.phoneNo = phoneNo;
        this.email = email;
        this.companyName = companyName;
        this.website = website;
        this.address = address;
        this.isPublic = isPublic;
    }

    public QrVcardRequestDTO() {
        super();
    }

    public int getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(int isPublic) {
        this.isPublic = isPublic;
    }

    public String getQr() {
        return qr;
    }

    public void setQr(String qr) {
        this.qr = qr;
    }

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
}