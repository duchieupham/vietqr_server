package com.vietqr.org.dto;

import java.util.List;

public class RoleMemberDTO {
    private String id;
    private String name;
    private String description;
    private int role;
    // 0: blue
    // 1: green
    // 2: red
    private int color;
    private List<String> checkDot;
    private int category;

    public RoleMemberDTO() {
    }

    public RoleMemberDTO(String id, String name, String description, int role,
                         int color, List<String> checkDot, int category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.role = role;
        this.color = color;
        this.checkDot = checkDot;
        this.category = category;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public List<String> getCheckDot() {
        return checkDot;
    }

    public void setCheckDot(List<String> checkDot) {
        this.checkDot = checkDot;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }
}
