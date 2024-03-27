package com.vietqr.org.dto;

import java.util.List;

public class MerchantRoleSettingDTO {
    private String merchantId;
    private List<RoleSettingDTO> roles;

    public MerchantRoleSettingDTO(String merchantId, List<RoleSettingDTO> roles) {
        this.merchantId = merchantId;
        this.roles = roles;
    }

    public MerchantRoleSettingDTO() {
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public List<RoleSettingDTO> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleSettingDTO> roles) {
        this.roles = roles;
    }
}
