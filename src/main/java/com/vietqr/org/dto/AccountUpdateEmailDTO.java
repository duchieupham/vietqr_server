package com.vietqr.org.dto;

import javax.validation.constraints.NotNull;

public class AccountUpdateEmailDTO {
    @NotNull
    private String email;

    public AccountUpdateEmailDTO() {
    }

    public AccountUpdateEmailDTO(String email) {
        this.email = email;
    }

    public @NotNull String getEmail() {
        return email.trim();
    }

    public void setEmail(@NotNull String email) {
        this.email = email;
    }
}
