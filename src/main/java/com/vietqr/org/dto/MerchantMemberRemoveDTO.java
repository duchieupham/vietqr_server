package com.vietqr.org.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

public class MerchantMemberRemoveDTO {
    @NotBlank
    private String userId;
    @NotBlank
    private String merchantId;

    public MerchantMemberRemoveDTO() {
    }

    public MerchantMemberRemoveDTO(String userId, String merchantId) {
        this.userId = userId;
        this.merchantId = merchantId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }
}
