package com.vietqr.org.dto;

public class BoxQrMachineResponseDTO {
    private String qrCode;
    private String bankAccount;
    private String boxId;
    private String bankCode;
    private String boxCode;
    private String subTerminalCode;
    private String subTerminalAddress;

    public BoxQrMachineResponseDTO() {
    }

    public BoxQrMachineResponseDTO(String qrCode, String bankAccount, String boxId, String bankCode,
                                   String boxCode, String subTerminalCode, String subTerminalAddress) {
        this.qrCode = qrCode;
        this.bankAccount = bankAccount;
        this.boxId = boxId;
        this.bankCode = bankCode;
        this.boxCode = boxCode;
        this.subTerminalCode = subTerminalCode;
        this.subTerminalAddress = subTerminalAddress;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getBoxId() {
        return boxId;
    }

    public void setBoxId(String boxId) {
        this.boxId = boxId;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getSubTerminalCode() {
        return subTerminalCode;
    }

    public void setSubTerminalCode(String subTerminalCode) {
        this.subTerminalCode = subTerminalCode;
    }

    public String getSubTerminalAddress() {
        return subTerminalAddress;
    }

    public void setSubTerminalAddress(String subTerminalAddress) {
        this.subTerminalAddress = subTerminalAddress;
    }
}
