package com.vietqr.org.dto;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;

public class MerchantSyncUpdateV2DTO {

    @NotBlank
    private String id;

    @Nullable
    private String address;

    @Nullable
    private String businessType;

    @Nullable
    private String career;

    @Nullable
    private String name;

    @Nullable
    private String nationalId;

    @Nullable
    private String vso;

    @Nullable
    private String email;

    @Nullable
    private String fullName;

    @Nullable
    private String phoneNo;

    public MerchantSyncUpdateV2DTO() { }

    public MerchantSyncUpdateV2DTO(String id, @Nullable String address, @Nullable String businessType, @Nullable String career, @Nullable String name, @Nullable String nationalId, @Nullable String vso, @Nullable String email, @Nullable String fullName, @Nullable String phoneNo) {
        this.id = id;
        this.address = address;
        this.businessType = businessType;
        this.career = career;
        this.name = name;
        this.nationalId = nationalId;
        this.vso = vso;
        this.email = email;
        this.fullName = fullName;
        this.phoneNo = phoneNo;
    }

    public @NotBlank String getId() {
        return id;
    }

    public void setId(@NotBlank String id) {
        this.id = id;
    }

    @Nullable
    public String getAddress() {
        return address;
    }

    public void setAddress(@Nullable String address) {
        this.address = address;
    }

    @Nullable
    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(@Nullable String businessType) {
        this.businessType = businessType;
    }

    @Nullable
    public String getCareer() {
        return career;
    }

    public void setCareer(@Nullable String career) {
        this.career = career;
    }

    @Nullable
    public String getName() {
        return name;
    }

    public void setName(@Nullable String name) {
        this.name = name;
    }

    @Nullable
    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(@Nullable String nationalId) {
        this.nationalId = nationalId;
    }

    @Nullable
    public String getVso() {
        return vso;
    }

    public void setVso(@Nullable String vso) {
        this.vso = vso;
    }

    @Nullable
    public String getEmail() {
        return email;
    }

    public void setEmail(@Nullable String email) {
        this.email = email;
    }

    @Nullable
    public String getFullName() {
        return fullName;
    }

    public void setFullName(@Nullable String fullName) {
        this.fullName = fullName;
    }

    @Nullable
    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(@Nullable String phoneNo) {
        this.phoneNo = phoneNo;
    }
}
