package com.vietqr.org.dto;

public class AccountTerminalMemberDTO {
    private String id;
    private String phoneNo;
    private String fullName;
    private String imgId;
    private String role;

    public AccountTerminalMemberDTO() {
    }

    public AccountTerminalMemberDTO(String id, String phoneNo, String fullName, String imgId, String role) {
        this.id = id;
        this.phoneNo = phoneNo;
        this.fullName = fullName;
        this.imgId = imgId;
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
}
