package com.vietqr.org.dto;

import javax.validation.constraints.NotBlank;

public class TerminalBankInsertDTO {
    @NotBlank
    private String terminalId;

    @NotBlank
    private String userId;

    @NotBlank
    private String bankId;

    public TerminalBankInsertDTO() {
    }

    public TerminalBankInsertDTO(String terminalId, String userId, String bankId) {
        this.terminalId = terminalId;
        this.userId = userId;
        this.bankId = bankId;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }
}
