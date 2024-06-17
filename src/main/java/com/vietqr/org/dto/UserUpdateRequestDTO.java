package com.vietqr.org.dto;

import com.google.firebase.database.annotations.NotNull;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class UserUpdateRequestDTO {

    @Email
    private String email;

    @NotNull
    @NotBlank
    private String firstName;
    @NotBlank
    @NotNull
    private String middleName;

    @NotNull
    @NotBlank
    private String lastName;


    @NotBlank
    @NotNull
    private String address;

    @NotNull
    private Integer gender;
    @NotNull
    @NotBlank
    private String nationalId;
    @NotNull
    @NotBlank
    private String oldNationalId;
    @NotNull
    @NotBlank
    private String nationalDate;

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
