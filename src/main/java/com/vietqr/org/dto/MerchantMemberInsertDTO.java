package com.vietqr.org.dto;

import javax.validation.constraints.NotBlank;

public class MerchantMemberInsertDTO {
    @NotBlank
    private String merchantId;
    @NotBlank
    private String userId;
    @NotBlank
    private int role;

    public MerchantMemberInsertDTO(String merchantId, String userId, int role) {
        this.merchantId = merchantId;
        this.userId = userId;
        this.role = role;
    }

    public MerchantMemberInsertDTO() {
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }
}
