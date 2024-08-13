package com.vietqr.org.dto;

import org.apache.poi.hpsf.GUID;

import javax.validation.constraints.NotBlank;

public class MerchantSyncCreateV2DTO {

    @NotBlank
    private String address;

    @NotBlank
    private String businessType;

    @NotBlank
    private String career;

    @NotBlank
    private String name;

    @NotBlank
    private String nationalId;

    @NotBlank
    private String userId;

    @NotBlank
    private String vso;

    @NotBlank
    private String email;

    @NotBlank
    private String fullName;

    @NotBlank
    private String phoneNo;

    public MerchantSyncCreateV2DTO() {}

    public MerchantSyncCreateV2DTO(String address, String businessType, String career, String name, String nationalId, String userId, String vso, String email, String fullName, String phoneNo) {
        this.address = address;
        this.businessType = businessType;
        this.career = career;
        this.name = name;
        this.nationalId = nationalId;
        this.userId = userId;
        this.vso = vso;
        this.email = email;
        this.fullName = fullName;
        this.phoneNo = phoneNo;
    }

    public @NotBlank String getAddress() {
        return address;
    }

    public void setAddress(@NotBlank String address) {
        this.address = address;
    }

    public @NotBlank String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(@NotBlank String businessType) {
        this.businessType = businessType;
    }

    public @NotBlank String getCareer() {
        return career;
    }

    public void setCareer(@NotBlank String career) {
        this.career = career;
    }

    public @NotBlank String getName() {
        return name;
    }

    public void setName(@NotBlank String name) {
        this.name = name;
    }

    public @NotBlank String getNationalId() {
        return nationalId;
    }

    public void setNationalId(@NotBlank String nationalId) {
        this.nationalId = nationalId;
    }

    public @NotBlank String getUserId() {
        return userId;
    }

    public void setUserId(@NotBlank String userId) {
        this.userId = userId;
    }

    public @NotBlank String getVso() {
        return vso;
    }

    public void setVso(@NotBlank String vso) {
        this.vso = vso;
    }

    public @NotBlank String getEmail() {
        return email;
    }

    public void setEmail(@NotBlank String email) {
        this.email = email;
    }

    public @NotBlank String getFullName() {
        return fullName;
    }

    public void setFullName(@NotBlank String fullName) {
        this.fullName = fullName;
    }

    public @NotBlank String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(@NotBlank String phoneNo) {
        this.phoneNo = phoneNo;
    }
}
