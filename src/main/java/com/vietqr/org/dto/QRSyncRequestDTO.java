package com.vietqr.org.dto;

import javax.validation.constraints.NotBlank;

public class QRSyncRequestDTO {
    @NotBlank
    private String terminalId;
    @NotBlank
    private String machineCode;
    @NotBlank
    private String bankId;

    public QRSyncRequestDTO(String terminalId, String machineCode) {
        this.terminalId = terminalId;
        this.machineCode = machineCode;
    }

    public QRSyncRequestDTO() {
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

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }
}
