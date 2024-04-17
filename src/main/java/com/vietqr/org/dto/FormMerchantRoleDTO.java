package com.vietqr.org.dto;

import java.util.List;

public class FormMerchantRoleDTO {
    private int level;
    private List<RoleMemberDTO> roles;

    public FormMerchantRoleDTO() {
    }

    public FormMerchantRoleDTO(int level, List<RoleMemberDTO> roles) {
        this.level = level;
        this.roles = roles;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public List<RoleMemberDTO> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleMemberDTO> roles) {
        this.roles = roles;
    }
}
