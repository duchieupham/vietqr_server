package com.vietqr.org.dto;

public class UserUpdateResponseDTO {


    private String email;

    private String firstName;

    private String middleName;

    private String lastName;


    private String address;

    private Integer gender;
    private String nationalId;
    private String oldNationalId;

    private String nationalDate;

    public UserUpdateResponseDTO() {
    }

    public UserUpdateResponseDTO(String email, String firstName, String middleName, String lastName, String address, Integer gender, String nationalId, String oldNationalId, String nationalDate) {
        this.email = email;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.address = address;
        this.gender = gender;
        this.nationalId = nationalId;
        this.oldNationalId = oldNationalId;
        this.nationalDate = nationalDate;
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
}
