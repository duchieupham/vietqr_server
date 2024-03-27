package com.vietqr.org.dto;

import javax.validation.constraints.NotBlank;
import java.util.List;

public class MerchantMemberCreateDTO {
    private String merchantId;
    private List<String> roleIds;
    @NotBlank
    private String userId;

    private List<String> terminalIds;

    public MerchantMemberCreateDTO() {
    }

    public MerchantMemberCreateDTO(List<String> roleIds,
                                   String userId, List<String> terminalIds) {
        this.roleIds = roleIds;
        this.userId = userId;
        this.terminalIds = terminalIds;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public List<String> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<String> roleIds) {
        this.roleIds = roleIds;
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
