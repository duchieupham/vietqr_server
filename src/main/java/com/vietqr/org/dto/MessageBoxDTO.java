package com.vietqr.org.dto;

public class MessageBoxDTO {
    private String notificationType;
    private String amount;
    private String message;

    public MessageBoxDTO() {
    }

    public MessageBoxDTO(String notificationType, String amount, String message) {
        this.notificationType = notificationType;
        this.amount = amount;
        this.message = message;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
