package com.vietqr.org.dto;

import java.util.List;

public class TerminalDetailResponseDTO {
    private String id;
    private String name;
    private String address;
    private String code;
    private String userId;
    private boolean isDefault;
    private int totalMember;
//    private String qrCode;
    private List<TerminalBankResponseDTO> banks;

    private List<AccountMemberDTO> members;

    public TerminalDetailResponseDTO() {
    }

    public TerminalDetailResponseDTO(String id, String name, String address, String code, String userId, boolean isDefault, List<TerminalBankResponseDTO> banks, List<AccountMemberDTO> members) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.code = code;
        this.userId = userId;
        this.isDefault = isDefault;
        this.banks = banks;
        this.members = members;
    }

    public int getTotalMember() {
        return totalMember;
    }

    public void setTotalMember(int totalMember) {
        this.totalMember = totalMember;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public boolean getIsDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
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

    public List<AccountMemberDTO> getMembers() {
        return members;
    }

    public void setMembers(List<AccountMemberDTO> members) {
        this.members = members;
    }
}
