package com.vietqr.org.dto;

import javax.validation.constraints.NotBlank;

public class QRBoxRequestDto {
    @NotBlank
    private String terminalId;
    @NotBlank
    private String machineCode;

    public QRBoxRequestDto(String terminalId, String machineCode) {
        this.terminalId = terminalId;
        this.machineCode = machineCode;
    }

    public QRBoxRequestDto() {
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }
}
