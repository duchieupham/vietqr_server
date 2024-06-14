package com.vietqr.org.dto;

public class PasswordResetDTO {
    private String phoneNo;
    private String newPassword;


    public PasswordResetDTO() {
    }

    public PasswordResetDTO(String phoneNo, String newPassword) {
        this.phoneNo = phoneNo;
        this.newPassword = newPassword;

    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

}
