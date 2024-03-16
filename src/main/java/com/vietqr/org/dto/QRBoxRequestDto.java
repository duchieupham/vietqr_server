package com.vietqr.org.dto;

import javax.validation.constraints.NotBlank;

public class QRBoxRequestDto {
    @NotBlank
    private String terminalId;
    private String terminalCode;
    @NotBlank
    private String terminalName;

    public QRBoxRequestDto(String terminalId, String terminalCode, String terminalName) {
        this.terminalId = terminalId;
        this.terminalCode = terminalCode;
        this.terminalName = terminalName;
    }

    public QRBoxRequestDto() {
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getTerminalCode() {
        return terminalCode;
    }

    public void setTerminalCode(String terminalCode) {
        this.terminalCode = terminalCode;
    }

    public String getTerminalName() {
        return terminalName;
    }

    public void setTerminalName(String terminalName) {
        this.terminalName = terminalName;
    }
}
