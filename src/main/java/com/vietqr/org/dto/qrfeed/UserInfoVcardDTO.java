package com.vietqr.org.dto.qrfeed;

public class UserInfoVcardDTO {
    private String email;
    private String address;
    private String phoneNo;
    private String website;
    private String fullName;
    private String companyName;

    public UserInfoVcardDTO(String email, String address, String phoneNo, String website, String fullName, String companyName) {
        this.email = email;
        this.address = address;
        this.phoneNo = phoneNo;
        this.website = website;
        this.fullName = fullName;
        this.companyName = companyName;
    }

    public UserInfoVcardDTO() {

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

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}
