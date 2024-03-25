package com.vietqr.org.dto;

public class AccountMemberResponseDTO {
    private String id;
    private String phoneNo;
    private String firstName;
    private String middleName;
    private String lastName;
    private String imgId;
    private boolean isOwner;

    public AccountMemberResponseDTO() {
    }

    public AccountMemberResponseDTO(String id, String phoneNo, String firstName, String middleName, String lastName, String imgId, boolean isOwner) {
        this.id = id;
        this.phoneNo = phoneNo;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.imgId = imgId;
        this.isOwner = isOwner;
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

    public String getImgId() {
        return imgId;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }

    public boolean isOwner() {
        return isOwner;
    }

    public void setOwner(boolean owner) {
        isOwner = owner;
    }
}
