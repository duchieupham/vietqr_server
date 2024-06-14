package com.vietqr.org.dto;

import com.google.firebase.database.annotations.NotNull;
import org.hibernate.validator.constraints.UniqueElements;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class UserRequestDTO {
    @NotNull
    @Size(min = 10, max = 10, message = "Phone number must be 10 digits")
    private String phoneNo;

    @NotNull
    @Size(min = 6, max = 6, message = "Password must be 6 digits")
    private String password;

    @Email
    private String email;

    @NotNull
    private String firstName;

    private String middleName;

    @NotNull
    private String lastName;



    private String address;

    @NotNull
    private Integer gender;
    @Pattern(regexp = "\\d{12}", message = "CCCD must be 12 digits")
    private String nationalId;
    @Pattern(regexp = "\\d{9}", message = "CMND must be 9 digits")
    private String oldNationalId;

    private String nationalDate;


    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
