package com.vietqr.org.dto;

public class RoleSettingDTO {
    private int category;
    private int role;

    public RoleSettingDTO() {
    }

    public RoleSettingDTO(int category, int role) {
        this.category = category;
        this.role = role;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }
}
