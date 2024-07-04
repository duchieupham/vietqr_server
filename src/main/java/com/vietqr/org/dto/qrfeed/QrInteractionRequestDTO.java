package com.vietqr.org.dto.qrfeed;

public class QrInteractionRequestDTO {
    private String qrWalletId;
    private String userId;
    private int interactionType;


    // Getters and Setters
    public String getQrWalletId() {
        return qrWalletId;
    }

    public void setQrWalletId(String qrWalletId) {
        this.qrWalletId = qrWalletId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getInteractionType() {
        return interactionType;
    }

    public void setInteractionType(int interactionType) {
        this.interactionType = interactionType;
    }
}
