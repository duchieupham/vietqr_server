package com.vietqr.org.dto;

public class DynamicQRBoxDTO {
    private String notificationType;
    private String amount;
    private String qrCode;
    public DynamicQRBoxDTO() {
    }

    public DynamicQRBoxDTO(String notificationType, String amount, String qrCode) {
        this.notificationType = notificationType;
        this.amount = amount;
        this.qrCode = qrCode;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }
}
