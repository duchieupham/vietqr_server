package com.vietqr.org.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

public class TerminalInsertV2DTO {

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

    private String merchantId;

    private String merchantName;

    public TerminalInsertV2DTO() {
    }

    public TerminalInsertV2DTO(List<String> userIds, List<String> bankIds, String name, String address, String code, String userId) {
        this.userIds = userIds;
        this.bankIds = bankIds;
        this.name = name;
        this.address = address;
        this.code = code;
        this.userId = userId;
    }

    public List<String> getBankIds() {
        return bankIds;
    }

    public void setBankIds(List<String> bankIds) {
        this.bankIds = bankIds;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public @NotNull String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    public @NotNull String getAddress() {
        return address;
    }

    public void setAddress(@NotNull String address) {
        this.address = address;
    }

    public @NotBlank String getCode() {
        return code;
    }

    public void setCode(@NotBlank String code) {
        this.code = code;
    }

    public @NotBlank String getUserId() {
        return userId;
    }

    public void setUserId(@NotBlank String userId) {
        this.userId = userId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }
}
