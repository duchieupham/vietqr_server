package com.vietqr.org.dto;

public class PasswordResetDTO {
    private String phoneNo;
    private String newPassword;
    private String confirmPassword;

    public PasswordResetDTO() {
    }

    public PasswordResetDTO(String phoneNo, String newPassword, String confirmPassword) {
        this.phoneNo = phoneNo;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
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

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
