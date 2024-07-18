package com.vietqr.org.dto;

public class UserResponseDTO {
    private String id;
    private String phoneNo;
    private String email;
    private String firstName;
    private String middleName;
    private String lastName;

    private String address;
    private Integer gender;
    private String nationalId;
    private String oldNationalId;
    private String nationalDate;
    private boolean status;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
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

    public String getNationalDate() {
        return nationalDate;
    }

    public void setNationalDate(String nationalDate) {
        this.nationalDate = nationalDate;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
