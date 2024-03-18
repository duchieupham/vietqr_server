package com.vietqr.org.dto;

import javax.validation.Valid;

public class MerchantMemberRemoveDTO {
    @Valid
    private String userId;
    @Valid
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
