package com.vietqr.org.dto;

public class EmailDetailDTO {

    private String recipient;
    private String userId;

    public EmailDetailDTO() {
    }

    public EmailDetailDTO(String recipient, String userId) {
        this.recipient = recipient;
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

}
