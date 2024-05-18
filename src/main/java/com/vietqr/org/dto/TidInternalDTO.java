package com.vietqr.org.dto;

public class TidInternalDTO {
    private String boxId;
    private String macAddr;
    private String boxCode;
    private String merchantName;
    private String terminalId;
    private String terminalName;
    private String terminalCode;
    private String bankAccount;
    private String bankShortName;
    private String userBankName;
    private String feePackage;
    private String boxAddress;
    private String certificate;
    private int status;

    public TidInternalDTO() {
    }

    public TidInternalDTO(String boxId, String macAddr, String boxCode, String merchantName,
                          String terminalName, String terminalCode, String bankAccount, String bankShortName,
                          String userBankName, String feePackage, String boxAddress, String certificate, int status) {
        this.boxId = boxId;
        this.macAddr = macAddr;
        this.boxCode = boxCode;
        this.merchantName = merchantName;
        this.terminalName = terminalName;
        this.terminalCode = terminalCode;
        this.bankAccount = bankAccount;
        this.bankShortName = bankShortName;
        this.userBankName = userBankName;
        this.feePackage = feePackage;
        this.boxAddress = boxAddress;
        this.certificate = certificate;
        this.status = status;
    }

    public String getBoxId() {
        return boxId;
    }

    public void setBoxId(String boxId) {
        this.boxId = boxId;
    }

    public String getMacAddr() {
        return macAddr;
    }

    public void setMacAddr(String macAddr) {
        this.macAddr = macAddr;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
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

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getBankShortName() {
        return bankShortName;
    }

    public void setBankShortName(String bankShortName) {
        this.bankShortName = bankShortName;
    }

    public String getUserBankName() {
        return userBankName;
    }

    public void setUserBankName(String userBankName) {
        this.userBankName = userBankName;
    }

    public String getFeePackage() {
        return feePackage;
    }

    public void setFeePackage(String feePackage) {
        this.feePackage = feePackage;
    }

    public String getBoxAddress() {
        return boxAddress;
    }

    public void setBoxAddress(String boxAddress) {
        this.boxAddress = boxAddress;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }
}
