package com.vietqr.org.dto;

public class EcommerceActiveDTO {
    private String fullName;
    private String website;
    private String name;
    private String bankCode;
    private String bankAccount;
    private String certificate;
    private String nationalId;
    private String email;
    private String phoneNo;
    private String address;
    private String webhook;
    private String career;
    private int businessType;

    public EcommerceActiveDTO() {
        this.fullName = "";
        this.website = "";
        this.name = "";
        this.bankCode = "";
        this.bankAccount = "";
        this.certificate = "";
        this.nationalId = "";
        this.email = "";
        this.phoneNo = "";
        this.address = "";
        this.webhook = "";
        this.career = "";
        this.businessType = 1;
    }

    public EcommerceActiveDTO(String fullName, String bankCode, String bankAccount, String certificate, String nationalId,
                              String email, String phoneNo, String address, String webhook, String career) {
        this.fullName = fullName;
        this.bankCode = bankCode;
        this.bankAccount = bankAccount;
        this.certificate = certificate;
        this.nationalId = nationalId;
        this.email = email;
        this.phoneNo = phoneNo;
        this.address = address;
        this.webhook = webhook;
        this.career = career;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWebhook() {
        return webhook;
    }

    public void setWebhook(String webhook) {
        this.webhook = webhook;
    }

    public String getCareer() {
        return career;
    }

    public void setCareer(String career) {
        this.career = career;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBusinessType() {
        return businessType;
    }

    public void setBusinessType(int businessType) {
        this.businessType = businessType;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
