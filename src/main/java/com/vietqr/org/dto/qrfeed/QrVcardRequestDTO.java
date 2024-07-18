package com.vietqr.org.dto.qrfeed;

import java.io.Serializable;

public class QrVcardRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String qr;
    private String qrName;
    private String qrDescription;
    private String fullname;
    private String phoneNo;
    private String email;
    private String companyName;
    private String website;
    private String address;
    private int isPublic;
    private int style;
    private int theme;

    public QrVcardRequestDTO(String qr, String qrName, String qrDescription, String fullname, String phoneNo, String email, String companyName, String website, String address, int isPublic, int style, int theme) {
        this.qr = qr;
        this.qrName = qrName;
        this.qrDescription = qrDescription;
        this.fullname = fullname;
        this.phoneNo = phoneNo;
        this.email = email;
        this.companyName = companyName;
        this.website = website;
        this.address = address;
        this.isPublic = isPublic;
        this.style = style;
        this.theme = theme;
    }

    public String getQrName() {
        return qrName;
    }

    public void setQrName(String qrName) {
        this.qrName = qrName;
    }

    public String getQrDescription() {
        return qrDescription;
    }

    public void setQrDescription(String qrDescription) {
        this.qrDescription = qrDescription;
    }

    public QrVcardRequestDTO() {
        super();
    }

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public int getTheme() {
        return theme;
    }

    public void setTheme(int theme) {
        this.theme = theme;
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
