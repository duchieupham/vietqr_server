package com.vietqr.org.dto;

public class PasswordResetDTO {
    private String newPassword;


    public PasswordResetDTO() {
    }

    public PasswordResetDTO( String newPassword) {
        this.newPassword = newPassword;

    }
    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

}
