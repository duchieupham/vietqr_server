package com.vietqr.org.dto;

import javax.validation.constraints.NotBlank;

public class TerminalRemoveBankDTO {

    @NotBlank
    private String terminalId;

    @NotBlank
    private String bankId;

    @NotBlank
    private String userId;

    public TerminalRemoveBankDTO() {
    }

    public TerminalRemoveBankDTO(String terminalId, String bankId, String userId) {
        this.terminalId = terminalId;
        this.bankId = bankId;
        this.userId = userId;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
