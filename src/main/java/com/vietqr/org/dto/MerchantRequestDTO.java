package com.vietqr.org.dto;

import javax.validation.constraints.NotBlank;

public class MerchantRequestDTO {
    @NotBlank
    private String name;
    @NotBlank
    private String userId;

    public MerchantRequestDTO() {
    }

    public MerchantRequestDTO(String name,
                              int type, String userId) {
        this.name = name;
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
