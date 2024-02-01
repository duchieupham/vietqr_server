package com.vietqr.org.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

public class TerminalInsertDTO {

    private List<String> userIds;

    private List<String> bankIds;

    @NotNull
    private String name;

    @NotNull
    private String address;

    @NotBlank
    private String code;

    @NotBlank
    private String userId;

    public TerminalInsertDTO() {
    }

    public TerminalInsertDTO(List<String> userIds, List<String> bankIds, String name, String address, String code, String userId) {
        this.userIds = userIds;
        this.bankIds = bankIds;
        this.name = name;
        this.address = address;
        this.code = code;
        this.userId = userId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public List<String> getBankIds() {
        return bankIds;
    }

    public void setBankIds(List<String> bankIds) {
        this.bankIds = bankIds;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
