package com.vietqr.org.dto;

public class AccountTerminalMemberDTO {
    private String id;
    private String phoneNo;
    private String fullName;
    private String imgId;

    private String birthDate;

    private String email;

    private String nationalId;

    private int gender;
    private String role;

    public AccountTerminalMemberDTO() {
    }

    public AccountTerminalMemberDTO(String id, String phoneNo, String fullName, String imgId, String birthDate, String email, String nationalId, int gender, String role) {
        this.id = id;
        this.phoneNo = phoneNo;
        this.fullName = fullName;
        this.imgId = imgId;
        this.birthDate = birthDate;
        this.email = email;
        this.nationalId = nationalId;
        this.gender = gender;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getImgId() {
        return imgId;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }
}
