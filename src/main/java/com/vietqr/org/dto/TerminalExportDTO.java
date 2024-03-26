package com.vietqr.org.dto;

public class TerminalExportDTO {
    private String terminalAddress;
    private String terminalName;

    public TerminalExportDTO(String terminalName, String terminalAddress) {
        this.terminalAddress = terminalAddress;
        this.terminalName = terminalName;
    }

    public String getTerminalAddress() {
        return terminalAddress;
    }

    public void setTerminalAddress(String terminalAddress) {
        this.terminalAddress = terminalAddress;
    }

    public String getTerminalName() {
        return terminalName;
    }

    public void setTerminalName(String terminalName) {
        this.terminalName = terminalName;
    }
}
