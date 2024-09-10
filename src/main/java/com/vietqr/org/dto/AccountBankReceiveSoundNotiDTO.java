package com.vietqr.org.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class AccountBankReceiveSoundNotiDTO {
    @NotNull
    @NotEmpty
    private List<String> bankIds;

    @NotBlank
    private String userId;

    public AccountBankReceiveSoundNotiDTO() {
    }

    public AccountBankReceiveSoundNotiDTO(List<String> bankIds) {
        this.bankIds = bankIds;
    }

    public List<String> getBankIds() {
        return bankIds;
    }

    public void setBankIds(List<String> bankIds) {
        this.bankIds = bankIds;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
