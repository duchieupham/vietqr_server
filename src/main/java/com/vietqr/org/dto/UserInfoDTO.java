package com.vietqr.org.dto;

public class UserInfoDTO {
    private String id;
    private String phoneNo;
    private String firstName;
    private String middleName;
    private String lastName;
    private String fullName;
    private String email;
    private int gender;
    private boolean status;
    private String nationalDate;
    private String nationalId;
    private String oldNationalId;
    private String address;

    public UserInfoDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserInfoDTO(String id, String phoneNo, String firstName, String middleName, String lastName, String fullName, String email, int gender, boolean status, String nationalDate, String nationalId, String oldNationalId, String address) {
        this.id = id;
        this.phoneNo = phoneNo;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.fullName = fullName;
        this.email = email;
        this.gender = gender;
        this.status = status;
        this.nationalDate = nationalDate;
        this.nationalId = nationalId;
        this.oldNationalId = oldNationalId;
        this.address = address;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getNationalDate() {
        return nationalDate;
    }

    public void setNationalDate(String nationalDate) {
        this.nationalDate = nationalDate;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getOldNationalId() {
        return oldNationalId;
    }

    public void setOldNationalId(String oldNationalId) {
        this.oldNationalId = oldNationalId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
