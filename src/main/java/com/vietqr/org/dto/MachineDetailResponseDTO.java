package com.vietqr.org.dto;

public class MachineDetailResponseDTO {
    private String boxCode;
    private String terminalSubCode;
    private String boxId;
    private String machineId;
    private String bankAccount;
    private String userBankName;
    private String bankCode;
    private String bankShortName;
    private String boxAddress;
    private String qrCode;

    public MachineDetailResponseDTO() {
    }

    public MachineDetailResponseDTO(String boxCode, String terminalSubCode, String boxId, String bankAccount,
                                    String userBankName, String bankCode, String bankShortName, String boxAddress) {
        this.boxCode = boxCode;
        this.terminalSubCode = terminalSubCode;
        this.boxId = boxId;
        this.bankAccount = bankAccount;
        this.userBankName = userBankName;
        this.bankCode = bankCode;
        this.bankShortName = bankShortName;
        this.boxAddress = boxAddress;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getTerminalSubCode() {
        return terminalSubCode;
    }

    public void setTerminalSubCode(String terminalSubCode) {
        this.terminalSubCode = terminalSubCode;
    }

    public String getBoxId() {
        return boxId;
    }

    public void setBoxId(String boxId) {
        this.boxId = boxId;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getUserBankName() {
        return userBankName;
    }

    public void setUserBankName(String userBankName) {
        this.userBankName = userBankName;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankShortName() {
        return bankShortName;
    }

    public void setBankShortName(String bankShortName) {
        this.bankShortName = bankShortName;
    }

    public String getBoxAddress() {
        return boxAddress;
    }

    public void setBoxAddress(String boxAddress) {
        this.boxAddress = boxAddress;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }
}
