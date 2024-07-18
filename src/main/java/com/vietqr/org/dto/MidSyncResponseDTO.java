package com.vietqr.org.dto;

public class MidSyncResponseDTO {
    private String mid;
    private String merchantName;

    public MidSyncResponseDTO() {
    }

    public MidSyncResponseDTO(String mid, String merchantName) {
        this.mid = mid;
        this.merchantName = merchantName;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }
}
