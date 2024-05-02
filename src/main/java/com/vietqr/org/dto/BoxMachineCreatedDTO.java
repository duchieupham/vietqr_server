package com.vietqr.org.dto;

public class BoxMachineCreatedDTO {
    private String qrCode;
    private String bankAccount;
    private String boxId;
    private String bankCode;
    private String boxCode;

    public BoxMachineCreatedDTO() {
    }

    public BoxMachineCreatedDTO(String qrCode, String bankAccount, String boxId, String bankCode, String boxCode) {
        this.qrCode = qrCode;
        this.bankAccount = bankAccount;
        this.boxId = boxId;
        this.bankCode = bankCode;
        this.boxCode = boxCode;
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
}
