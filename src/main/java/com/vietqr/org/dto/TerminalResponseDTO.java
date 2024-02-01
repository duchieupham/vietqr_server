package com.vietqr.org.dto;

import java.io.Serializable;
import java.util.List;

public class TerminalResponseDTO implements TerminalResponseInterfaceDTO {
    private String id;
    private int totalMembers;
    private String name;
    private String address;
    private String code;
    private boolean isDefault;
    private String userId;
    private List<TerminalBankResponseDTO> banks;

    public TerminalResponseDTO() {
    }

    public TerminalResponseDTO(String id, int totalMembers, String name, String address, String code, boolean isDefault, String userId) {
        this.id = id;
        this.totalMembers = totalMembers;
        this.name = name;
        this.address = address;
        this.code = code;
        this.isDefault = isDefault;
        this.userId = userId;
    }

    public void setTotalMembers(int totalMembers) {
        this.totalMembers = totalMembers;
    }

    public boolean getIsDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTotalMembers() {
        return totalMembers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<TerminalBankResponseDTO> getBanks() {
        return banks;
    }

    public void setBanks(List<TerminalBankResponseDTO> banks) {
        this.banks = banks;
    }
}
