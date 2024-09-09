package com.vietqr.org.dto;

import javax.validation.constraints.NotNull;

public class AccountUpdateEmailDTO {
    @NotNull
    private String email;

    /*
    * Type:
    * 0: save
    * 1: verify
    * */
    @NotNull
    private int type;

    /*
    * OTP
    * type = 0 -> otp = 0
    * type = 1 -> otp
    * */
    @NotNull
    private int otp;

    public AccountUpdateEmailDTO() {
    }

    public AccountUpdateEmailDTO(String email, int type) {
        this.email = email;
        this.type = type;
    }

    public @NotNull String getEmail() {
        return email.trim();
    }

    public void setEmail(@NotNull String email) {
        this.email = email;
    }

    @NotNull
    public int getType() {
        return type;
    }

    public void setType(@NotNull int type) {
        this.type = type;
    }

    @NotNull
    public int getOtp() {
        return otp;
    }

    public void setOtp(@NotNull int otp) {
        this.otp = otp;
    }
}
