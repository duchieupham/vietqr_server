package com.vietqr.org.dto;

public class AdminListUserAccountResponseDTO {
    private String id;
    private String address;
    private String birthDate;
    private String email;
    private boolean status;
    private int gender;
    private String firstName;
    private String middleName;
    private String lastName;
    private String fullName;
    private String userIp;
    private String phoneNo;
    private String registerPlatform;
    private String nationalDate;
    private String nationalId;
    private String oldNationalId;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
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

    public String getUserIp() {
        return userIp;
    }

    public void setUserIp(String userIp) {
        this.userIp = userIp;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getRegisterPlatform() {
        return registerPlatform;
    }

    public void setRegisterPlatform(String registerPlatform) {
        this.registerPlatform = registerPlatform;
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

    public AdminListUserAccountResponseDTO() {
    }

    public AdminListUserAccountResponseDTO(String id, String address, String birthDate, String email, boolean status, int gender, String firstName, String middleName, String lastName, String userIp, String phoneNo, String registerPlatform, String nationalDate, String nationalId, String oldNationalId) {
        this.id = id;
        this.address = address;
        this.birthDate = birthDate;
        this.email = email;
        this.status = status;
        this.gender = gender;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.userIp = userIp;
        this.phoneNo = phoneNo;
        this.registerPlatform = registerPlatform;
        this.nationalDate = nationalDate;
        this.nationalId = nationalId;
        this.oldNationalId = oldNationalId;
    }
}
