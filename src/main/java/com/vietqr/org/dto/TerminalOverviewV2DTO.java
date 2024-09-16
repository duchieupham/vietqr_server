package com.vietqr.org.dto;

public class TerminalOverviewV2DTO {
    private String terminalId;
    private String terminalName;
    private String terminalCode;

    public TerminalOverviewV2DTO() {
    }

    public TerminalOverviewV2DTO(String terminalId, String terminalName, String terminalCode) {
        this.terminalId = terminalId;
        this.terminalName = terminalName;
        this.terminalCode = terminalCode;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getTerminalName() {
        return terminalName;
    }

    public void setTerminalName(String terminalName) {
        this.terminalName = terminalName;
    }

    public String getTerminalCode() {
        return terminalCode;
    }

    public void setTerminalCode(String terminalCode) {
        this.terminalCode = terminalCode;
    }
}
