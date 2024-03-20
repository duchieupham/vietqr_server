package com.vietqr.org.dto;

import javax.validation.constraints.NotBlank;
import java.util.List;

public class MerchantMemberCreateDTO {
    private int role;
    private List<String> functionIds;

    @NotBlank
    private String userId;

    private List<String> terminalIds;

    public MerchantMemberCreateDTO() {
    }

    public MerchantMemberCreateDTO(int role, List<String> functionIds,
                                   String userId, List<String> terminalIds) {
        this.role = role;
        this.functionIds = functionIds;
        this.userId = userId;
        this.terminalIds = terminalIds;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public List<String> getFunctionIds() {
        return functionIds;
    }

    public void setFunctionIds(List<String> functionIds) {
        this.functionIds = functionIds;
    }

    public List<String> getTerminalIds() {
        return terminalIds;
    }

    public void setTerminalIds(List<String> terminalIds) {
        this.terminalIds = terminalIds;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
