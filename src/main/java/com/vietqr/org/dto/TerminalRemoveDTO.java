package com.vietqr.org.dto;

import javax.validation.constraints.NotBlank;

public class TerminalRemoveDTO {

    @NotBlank
    private String userId;

    @NotBlank
    private String terminalId;

    public TerminalRemoveDTO(String userId, String terminalId) {
        this.userId = userId;
        this.terminalId = terminalId;
    }

    public TerminalRemoveDTO() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }
}
