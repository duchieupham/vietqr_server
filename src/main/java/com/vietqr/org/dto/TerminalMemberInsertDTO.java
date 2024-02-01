package com.vietqr.org.dto;

import javax.validation.constraints.NotBlank;

public class TerminalMemberInsertDTO {
    @NotBlank
    private String terminalId;

    @NotBlank
    private String userId;

    public TerminalMemberInsertDTO() {
    }

    public TerminalMemberInsertDTO(String terminalId, String userId) {
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
