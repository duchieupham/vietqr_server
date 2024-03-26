package com.vietqr.org.dto;

import javax.validation.constraints.NotBlank;

public class InactiveMerchantDTO {
    @NotBlank
    private String merchantId;
    @NotBlank
    private String userId;

    public InactiveMerchantDTO(String merchantId, String userId) {
        this.merchantId = merchantId;
        this.userId = userId;
    }

    public InactiveMerchantDTO() {
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
}
