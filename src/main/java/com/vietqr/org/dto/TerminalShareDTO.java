package com.vietqr.org.dto;

public class TerminalShareDTO {
    private String id;

    private String terminalName;

    private int totalMembers;

    private String terminalCode;

    private String terminalAddress;

    private boolean isDefault;

    private String bankId;

    public TerminalShareDTO() {
    }

    public TerminalShareDTO(String id, String terminalName, int totalMembers, String terminalCode, String terminalAddress, boolean isDefault, String bankId) {
        this.id = id;
        this.terminalName = terminalName;
        this.totalMembers = totalMembers;
        this.terminalCode = terminalCode;
        this.terminalAddress = terminalAddress;
        this.isDefault = isDefault;
        this.bankId = bankId;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTerminalName() {
        return terminalName;
    }

    public void setTerminalName(String terminalName) {
        this.terminalName = terminalName;
    }

    public int getTotalMembers() {
        return totalMembers;
    }

    public void setTotalMembers(int totalMembers) {
        this.totalMembers = totalMembers;
    }

    public String getTerminalCode() {
        return terminalCode;
    }

    public void setTerminalCode(String terminalCode) {
        this.terminalCode = terminalCode;
    }

    public String getTerminalAddress() {
        return terminalAddress;
    }

    public void setTerminalAddress(String terminalAddress) {
        this.terminalAddress = terminalAddress;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }
}
