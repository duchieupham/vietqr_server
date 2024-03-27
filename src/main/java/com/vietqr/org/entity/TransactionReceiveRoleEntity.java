package com.vietqr.org.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TransactionReceiveRole")
public class TransactionReceiveRoleEntity {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "role")
    private int role;

    // 0: blue
    // 1: green
    // 2: red
    @Column(name = "color",  columnDefinition = "int default 0")
    private int color;

    @Column(name = "description")
    private String description;

    @Column(name = "category")
    private int category;

    @Column(name = "level")
    private int level;

    @Column(name = "checkDot")
    private String checkDot;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getCheckDot() {
        return checkDot;
    }

    public void setCheckDot(String checkDot) {
        this.checkDot = checkDot;
    }
}
