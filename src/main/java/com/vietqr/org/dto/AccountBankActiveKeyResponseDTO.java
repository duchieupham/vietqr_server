package com.vietqr.org.dto;

public class AccountBankActiveKeyResponseDTO extends AccountBankShareResponseDTO {
    private boolean isEmailVerified;
    private boolean isActiveKey;
    private String keyActive;
    private long timeActiveKey;

    public AccountBankActiveKeyResponseDTO() {
    }

    public AccountBankActiveKeyResponseDTO(boolean isActiveKey, String keyActive, long timeActiveKey) {
        this.isActiveKey = isActiveKey;
        this.keyActive = keyActive;
        this.timeActiveKey = timeActiveKey;
    }

    public AccountBankActiveKeyResponseDTO(String id, String bankAccount, String userBankName, String bankShortName, String bankCode, String bankName, String imgId, int type, String bankTypeId, boolean isAuthenticated, String nationalId, String phoneAuthenticated, String userId, boolean isOwner, int bankTypeStatus, String qrCode, String caiValue, String ewalletToken, Integer unlinkedType, boolean isActiveKey, String keyActive, long timeActiveKey) {
        super(id, bankAccount, userBankName, bankShortName, bankCode, bankName, imgId, type, bankTypeId, isAuthenticated, nationalId, phoneAuthenticated, userId, isOwner, bankTypeStatus, qrCode, caiValue, ewalletToken, unlinkedType);
        this.isActiveKey = isActiveKey;
        this.keyActive = keyActive;
        this.timeActiveKey = timeActiveKey;
    }

    public boolean isEmailVerified() {
        return isEmailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        isEmailVerified = emailVerified;
    }

    public void setActiveKey(boolean activeKey) {
        isActiveKey = activeKey;
    }

    public boolean isActiveKey() {
        return isActiveKey;
    }

    public void setIsActiveKey(boolean activeKey) {
        isActiveKey = activeKey;
    }

    public String getKeyActive() {
        return keyActive;
    }

    public void setKeyActive(String keyActive) {
        this.keyActive = keyActive;
    }

    public long getTimeActiveKey() {
        return timeActiveKey;
    }

    public void setTimeActiveKey(long timeActiveKey) {
        this.timeActiveKey = timeActiveKey;
    }
}
