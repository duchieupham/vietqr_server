package com.vietqr.org.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class AccountBankReceiveSoundNotiDTO {
    @NotNull
    @NotEmpty
    private List<String> bankAccounts;

    public AccountBankReceiveSoundNotiDTO() {
    }

    public AccountBankReceiveSoundNotiDTO(List<String> bankAccounts) {
        this.bankAccounts = bankAccounts;
    }

    public @NotNull @NotEmpty List<String> getBankAccounts() {
        return bankAccounts;
    }

    public void setBankAccounts(@NotNull @NotEmpty List<String> bankAccounts) {
        this.bankAccounts = bankAccounts;
    }
}
