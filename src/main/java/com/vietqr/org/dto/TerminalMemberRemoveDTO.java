package com.vietqr.org.dto;

import javax.validation.constraints.NotBlank;

public class TerminalMemberRemoveDTO {

    @NotBlank
    private String terminalId;

    @NotBlank
    private String userId;

    public TerminalMemberRemoveDTO() {
    }

    public TerminalMemberRemoveDTO(String terminalId, String userId) {
        this.terminalId = terminalId;
        this.userId = userId;
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
}
